package persistence;

import business.Provider;
import business.ProductForSale;
import edu.salle.url.api.ApiHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación API del DAO para gestionar la persistencia de datos de proveedores.
 * Obtiene los proveedores desde la API del servidor.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class ProviderAPIDAO implements ProviderDAO {
    private static final String SHARED_PROVIDERS_ENDPOINT = "/shared/providers";
    
    private final ApiHelper apiHelper;
    private final String baseUrl;
    private List<Provider> providers;
    
    /**
     * Construye un nuevo ProviderAPIDAO.
     * 
     * @param apiHelper el helper para hacer peticiones a la API
     */
    public ProviderAPIDAO(ApiHelper apiHelper) {
        this.apiHelper = apiHelper;
        this.baseUrl = "http://localhost/dpoo";
        this.providers = new ArrayList<>();
        try {
            loadAll();
        } catch (IOException e) {
            // Si falla la carga inicial, la lista queda vacía
            this.providers = new ArrayList<>();
        }
    }
    
    /**
     * Carga todos los proveedores desde la API.
     * 
     * @throws IOException si hay un error al obtener los proveedores
     */
    public void loadAll() throws IOException {
        try {
            String url = baseUrl + SHARED_PROVIDERS_ENDPOINT;
            String jsonResponse = apiHelper.getFromUrl(url);
            providers = parseProvidersFromJSON(jsonResponse);
        } catch (Exception e) {
            throw new IOException("Error getting providers from API: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parsea una respuesta JSON de proveedores y devuelve una lista de Provider.
     * 
     * @param json la respuesta JSON de la API
     * @return lista de proveedores parseados
     * @throws IOException si el JSON no es válido
     */
    public List<Provider> parseProvidersFromJSON(String json) throws IOException {
        try {
            java.io.File tempFile = java.io.File.createTempFile("providers", ".json");
            tempFile.deleteOnExit();
            try (java.io.FileWriter writer = new java.io.FileWriter(tempFile)) {
                writer.write(json);
            }
            List<Provider> parsedProviders = JsonParser.leerProveedores(tempFile.getAbsolutePath());
            tempFile.delete();
            return parsedProviders;
        } catch (Exception e) {
            throw new IOException("Error parsing providers JSON: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Provider findById(int providerId) {
        try {
            // Intentamos obtener el proveedor específico por ID desde la API
            String url = baseUrl + SHARED_PROVIDERS_ENDPOINT + "/" + providerId;
            String jsonResponse = apiHelper.getFromUrl(url);
            List<Provider> foundProviders = parseProvidersFromJSON("[" + jsonResponse + "]");
            if (!foundProviders.isEmpty()) {
                return foundProviders.get(0);
            }
        } catch (Exception e) {
            // Si falla, buscar en la lista en memoria
        }
        
        // Buscar en la lista en memoria
        for (Provider provider : providers) {
            if (provider.getProviderId() == providerId) {
                return provider;
            }
        }
        return null;
    }
    
    @Override
    public List<Provider> readAll() {
        // Recargamos desde la API para obtener datos actualizados
        try {
            loadAll();
        } catch (IOException e) {
            // Si falla, devolver la lista en memoria
        }
        return new ArrayList<>(providers);
    }
}

