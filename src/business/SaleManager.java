package business;

import business.Sale;
import business.ShoppingCart;
import persistence.ProviderDAO;
import persistence.ProviderJSONDAO;
import persistence.SaleDAO;
import persistence.SaleCSVDAO;
import java.io.IOException;
import java.util.List;

/**
 * Manager que maneja la lógica de negocio relacionada con las ventas.
 * Esta clase actúa como "Information Expert" para las operaciones de venta.
 */
public class SaleManager {
    private final SaleDAO saleDAO;
    private final ProviderDAO providerDAO;
    
    /**
     * Constructor del manager de ventas.
     * 
     * @param saleDAO el DAO de ventas
     * @param providerDAO el DAO de proveedores (para actualizar stock)
     */
    public SaleManager(SaleDAO saleDAO, ProviderDAO providerDAO) {
        this.saleDAO = saleDAO;
        this.providerDAO = providerDAO;
    }
    
    /**
     * Registra una venta.
     * 
     * @param clientId el identificador del cliente
     * @param productId el identificador del producto
     * @param price el precio pagado
     * @throws IOException si hay un error al guardar la venta
     */
    public void registerSale(int clientId, String productId, double price) throws IOException {
        long fechaActual = System.currentTimeMillis() / 1000;
        Sale venta = new Sale(clientId, productId, price, fechaActual);
        saleDAO.appendSale(venta);
    }
    
    /**
     * Obtiene todas las ventas de un cliente.
     * 
     * @param clientId el identificador del cliente
     * @return lista de ventas del cliente
     * @throws IOException si hay un error al leer las ventas
     */
    public List<Sale> getSalesOfClient(int clientId) throws IOException {
        if (saleDAO instanceof SaleCSVDAO) {
            return ((SaleCSVDAO) saleDAO).getSalesByClientId(clientId);
        } else {
            // Fallback: filtrar manualmente
            List<Sale> allSales = saleDAO.readAll();
            List<Sale> clientSales = new java.util.ArrayList<>();
            for (Sale sale : allSales) {
                if (sale.getClientId() == clientId) {
                    clientSales.add(sale);
                }
            }
            return clientSales;
        }
    }
    
    // Métodos adicionales para mantener compatibilidad con código existente
    /**
     * Crea ventas a partir de un carrito de compra.
     * Usa el cálculo de precios estándar (método legacy).
     * 
     * @param clientId el identificador del cliente
     * @param carrito el carrito de compra
     * @return lista de ventas creadas
     * @throws IOException si hay un error al guardar las ventas
     */
    public List<Sale> crearVenta(int clientId, ShoppingCart carrito) throws IOException {
        return crearVenta(clientId, carrito, null, null);
    }
    
    /**
     * Crea ventas a partir de un carrito de compra usando el nuevo sistema de cálculo de precios.
     * 
     * @param clientId el identificador del cliente
     * @param carrito el carrito de compra
     * @param productManager el manager de productos (para obtener información de productos)
     * @param client el cliente (para calcular precios según tipo)
     * @return lista de ventas creadas
     * @throws IOException si hay un error al guardar las ventas
     */
    public List<Sale> crearVenta(int clientId, ShoppingCart carrito, 
                                 ProductManager productManager, Client client) throws IOException {
        List<Sale> ventasCreadas = new java.util.ArrayList<>();
        List<business.CartItem> items = carrito.getItems();
        
        // Obtenemos la fecha actual como timestamp Unix
        long fechaActual = System.currentTimeMillis() / 1000;
        
        // Procesar cada item del carrito
        for (business.CartItem item : items) {
            business.ProductForSale producto = item.getProductForSale();
            business.Provider proveedor = item.getProvider();
            int cantidad = item.getQuantity();
            
            // Calculamos el precio pagado usando el nuevo sistema si está disponible
            double precioPagado;
            if (productManager != null && client != null) {
                Product product = productManager.getProductById(producto.getProductId());
                precioPagado = item.getTotalPrice(product, client);
            } else {
                // Fallback al cálculo estándar
                precioPagado = producto.getPriceWithVAT() * cantidad;
            }
            
            // Creamos la venta
            Sale venta = new Sale(clientId, producto.getProductId(), precioPagado, fechaActual);
            ventasCreadas.add(venta);
            
            // Guardar la venta en el archivo
            saleDAO.appendSale(venta);
            
            // Actualizar el stock del producto en el proveedor
            producto.reduceStock(cantidad);
            
            // Actualizar el proveedor en el DAO
            if (providerDAO instanceof ProviderJSONDAO) {
                ((ProviderJSONDAO) providerDAO).updateProvider(proveedor);
            }
        }
        
        // Guardar los cambios de stock en el archivo de proveedores
        if (providerDAO instanceof ProviderJSONDAO) {
            ((ProviderJSONDAO) providerDAO).saveAll();
        }
        
        return ventasCreadas;
    }
    
    public List<Sale> obtenerVentasCliente(int clientId) throws IOException {
        return getSalesOfClient(clientId);
    }
    
    public Sale obtenerUltimaVenta(int clientId) throws IOException {
        List<Sale> ventas = getSalesOfClient(clientId);
        
        if (ventas.isEmpty()) {
            return null;
        }
        
        // Buscar la venta más reciente (mayor timestamp)
        Sale ultimaVenta = ventas.get(0);
        for (Sale venta : ventas) {
            if (venta.getPurchaseDate() > ultimaVenta.getPurchaseDate()) {
                ultimaVenta = venta;
            }
        }
        
        return ultimaVenta;
    }
}

