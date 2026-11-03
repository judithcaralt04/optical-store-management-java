package persistence;

import business.Product;
import edu.salle.url.api.ApiHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación API del DAO para gestionar la persistencia de datos de productos.
 * Obtiene los productos desde la API del servidor.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class ProductAPIDAO implements ProductDAO {
    private static final String SHARED_PRODUCTS_ENDPOINT = "/shared/products";
    
    private final ApiHelper apiHelper;
    private final String baseUrl;
    private List<Product> products;
    
    /**
     * Construye un nuevo ProductAPIDAO.
     * 
     * @param apiHelper el helper para hacer peticiones a la API
     */
    public ProductAPIDAO(ApiHelper apiHelper) {
        this.apiHelper = apiHelper;
        this.baseUrl = ApiConfig.baseUrl();
        this.products = new ArrayList<>();
        try {
            loadAll();
        } catch (IOException e) {
            // Si falla la carga inicial, la lista queda vacía
            this.products = new ArrayList<>();
        }
    }
    
    /**
     * Carga todos los productos desde la API.
     * 
     * @throws IOException si hay un error al obtener los productos
     */
    public void loadAll() throws IOException {
        try {
            String url = baseUrl + SHARED_PRODUCTS_ENDPOINT;
            String jsonResponse = apiHelper.getFromUrl(url);
            products = parseProductsFromJSON(jsonResponse);
        } catch (Exception e) {
            throw new IOException("Error getting products from API: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parsea una respuesta JSON de productos y devuelve una lista de Product.
     * 
     * @param json la respuesta JSON de la API
     * @return lista de productos parseados
     * @throws IOException si el JSON no es válido
     */
    public List<Product> parseProductsFromJSON(String json) throws IOException {
        // Usar JsonParser existente para parsear
        // Necesitamos guardar temporalmente el JSON en un archivo o modificar JsonParser
        // Por ahora, usamos un enfoque simple: creamos un archivo temporal
        try {
            java.io.File tempFile = java.io.File.createTempFile("products", ".json");
            tempFile.deleteOnExit();
            try (java.io.FileWriter writer = new java.io.FileWriter(tempFile)) {
                writer.write(json);
            }
            List<Product> parsedProducts = JsonParser.leerProductos(tempFile.getAbsolutePath());
            tempFile.delete();
            return parsedProducts;
        } catch (Exception e) {
            throw new IOException("Error parsing products JSON: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Product findById(String productId) {
        // Recargamos desde la API para obtener datos actualizados
        try {
            loadAll();
        } catch (IOException e) {
            // Si falla, usar la lista en memoria
        }
        
        for (Product product : products) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }
        return null;
    }
    
    @Override
    public List<Product> readAll() {
        // Recargamos desde la API para obtener datos actualizados
        try {
            loadAll();
        } catch (IOException e) {
            // Si falla, devolver la lista en memoria
        }
        return new ArrayList<>(products);
    }
}

