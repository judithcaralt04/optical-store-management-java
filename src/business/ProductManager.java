package business;

import business.Product;
import business.ProductForSale;
import business.Provider;
import persistence.ProductDAO;
import persistence.ProductJSONDAO;
import persistence.ProviderDAO;
import persistence.ProviderJSONDAO;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager que maneja la lógica de negocio relacionada con los productos.
 * Esta clase actúa como "Information Expert" para las operaciones con productos.
 *
 */
public class ProductManager {
    private final ProductDAO productDAO;
    private final ProviderDAO providerDAO;
    
    /**
     * Constructor del manager de productos.
     * 
     * @param productDAO el DAO de productos
     * @param providerDAO el DAO de proveedores
     */
    public ProductManager(ProductDAO productDAO, ProviderDAO providerDAO) {
        this.productDAO = productDAO;
        this.providerDAO = providerDAO;
    }
    
    /**
     * Busca productos por nombre.
     * Si el texto de búsqueda está vacío, devuelve todos los productos.
     * 
     * @param name el texto a buscar (puede estar vacío)
     * @return lista de productos que coinciden con la búsqueda
     */
    public List<Product> findProductsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            // Si no hay texto de búsqueda, devolver todos los productos
            return productDAO.readAll();
        } else {
            // Buscar productos que contengan el texto
            if (productDAO instanceof ProductJSONDAO) {
                return ((ProductJSONDAO) productDAO).searchByName(name);
            } else {
                // Fallback: buscar manualmente usando el mismo método helper
                List<Product> all = productDAO.readAll();
                return ProductJSONDAO.searchProductsByName(all, name);
            }
        }
    }
    
    /**
     * Obtiene un producto por su identificador.
     * 
     * @param id el identificador del producto
     * @return el producto si existe, null si no existe
     */
    public Product getProductById(String id) {
        return productDAO.findById(id);
    }
    
    // Métodos adicionales para mantener compatibilidad con código existente
    public List<Product> buscarProductoNombre(String searchText) {
        return findProductsByName(searchText);
    }
    
    public Product obtenerProducto(String productId) {
        return getProductById(productId);
    }
    
    /**
     * Obtiene todos los proveedores que venden un producto específico.
     * 
     * @param productId el identificador del producto
     * @return lista de proveedores que venden el producto
     */
    public List<Provider> obtenerProveedoresProducto(String productId) {
        List<Provider> proveedoresConProducto = new ArrayList<>();
        List<Provider> todosProveedores;
        
        if (providerDAO instanceof ProviderJSONDAO) {
            todosProveedores = ((ProviderJSONDAO) providerDAO).getAllProviders();
        } else {
            todosProveedores = providerDAO.readAll();
        }
        
        // Recorrer todos los proveedores
        for (Provider proveedor : todosProveedores) {
            // Buscar si este proveedor tiene el producto
            ProductForSale productoEnVenta = proveedor.findProductForSale(productId);
            
            // Añadimos si el proveedor tiene el producto (independientemente del stock)
            if (productoEnVenta != null) {
                proveedoresConProducto.add(proveedor);
            }
        }
        
        return proveedoresConProducto;
    }
}

