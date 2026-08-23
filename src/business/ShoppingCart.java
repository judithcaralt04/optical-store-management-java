package business;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un carrito de compra para un cliente.
 * Contiene una lista de items del carrito que el cliente quiere comprar.
 */
public class ShoppingCart {
    private final List<CartItem> items;
    
    /**
     * Construye un nuevo ShoppingCart vacío.
     */
    public ShoppingCart() {
        this.items = new ArrayList<>();
    }
    
    /**
     * Añade un producto al carrito.
     * Si el producto del mismo proveedor ya existe, aumenta la cantidad.
     * 
     * @param productForSale el producto a añadir
     * @param provider el proveedor que vende el producto
     */
    public void addItem(ProductForSale productForSale, Provider provider) {
        // Comprobar si el item ya existe
        for (CartItem item : items) {
            if (item.getProductForSale().getProductId().equals(productForSale.getProductId()) &&
                item.getProvider().getProviderId() == provider.getProviderId()) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        // Añadimos nuevo item
        items.add(new CartItem(productForSale, provider));
    }
    
    /**
     * Elimina un item del carrito por índice.
     * 
     * @param index el índice del item a eliminar
     */
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }
    
    /**
     * Obtiene todos los items del carrito.
     * 
     * @return una copia de la lista de items
     */
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }
    
    /**
     * Comprueba si el carrito está vacío.
     * 
     * @return true si está vacío, false en caso contrario
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    /**
     * Calcula el precio total de todos los items del carrito.
     * Si se proporciona el cliente y es OnlineClient, incluye el coste de envío.
     * 
     * @param productManager el manager de productos para obtener información de los productos
     * @param client el cliente que está comprando (puede ser null)
     * @param geolocationHelper el helper de geolocalización para calcular envíos (puede ser null)
     * @return el precio total incluyendo envío si aplica
     */
    public double getTotalPrice(ProductManager productManager, Client client, Object geolocationHelper) {
        double total = 0.0;
        
        // Calculamos el total de los items
        for (CartItem item : items) {
            Product product = productManager.getProductById(item.getProductForSale().getProductId());
            total += item.getTotalPrice(product, client);
        }
        
        // Si el cliente es OnlineClient, añadimos coste de envío
        if (client instanceof OnlineClient && geolocationHelper != null) {
            OnlineClient onlineClient = (OnlineClient) client;
            double shippingCost = onlineClient.calculateShippingCost(geolocationHelper);
            total += shippingCost;
        }
        
        return total;
    }
    
    /**
     * Calcula el precio total de todos los items del carrito (método legacy para compatibilidad).
     * 
     * @return el precio total
     */
    public double getTotalPrice() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }
    
    /**
     * Limpia todos los items del carrito.
     */
    public void clear() {
        items.clear();
    }
}

