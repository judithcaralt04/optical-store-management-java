package persistence;

import business.Product;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JSON del DAO para gestionar la persistencia de datos de productos.
 * Maneja la lectura de productos desde el archivo products.json.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class ProductJSONDAO implements ProductDAO {
    private static final String PRODUCTS_FILE = "products.json";
    private final List<Product> products;
    
    /**
     * Construye un nuevo ProductJSONDAO y carga los productos del archivo.
     * 
     * @throws IOException si el archivo no se puede leer o tiene estructura inválida
     */
    public ProductJSONDAO() throws IOException {
        this.products = new ArrayList<>();
        loadAll();
    }
    
    /**
     * Carga todos los productos desde el archivo products.json.
     * 
     * @throws IOException si el archivo no se puede leer o tiene estructura inválida
     */
    public void loadAll() throws IOException {
        products.clear();
        // JsonParser parsea directamente a objetos Product
        products.addAll(JsonParser.leerProductos(PRODUCTS_FILE));
    }
    
    @Override
    public Product findById(String productId) {
        for (Product product : products) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }
        return null;
    }
    
    @Override
    public List<Product> readAll() {
        return new ArrayList<>(products);
    }
    
    /**
     * Busca productos por nombre (búsqueda parcial sin distinguir mayúsculas/minúsculas).
     * 
     * @param searchText el texto a buscar
     * @return lista de productos que coinciden con el criterio de búsqueda
     */
    public List<Product> searchByName(String searchText) {
        return searchProductsByName(products, searchText);
    }
    
    /**
     * Método helper estático para buscar productos por nombre en una lista.
     * Público para que ProductManager pueda reutilizarlo y evitar duplicación de código.
     * 
     * @param productList la lista de productos donde buscar
     * @param searchText el texto a buscar
     * @return lista de productos que coinciden con el criterio de búsqueda
     */
    public static List<Product> searchProductsByName(List<Product> productList, String searchText) {
        List<Product> results = new ArrayList<>();
        String searchLower = searchText.toLowerCase();
        
        for (Product product : productList) {
            if (product.getProductName().toLowerCase().contains(searchLower)) {
                results.add(product);
            }
        }
        
        return results;
    }
}

