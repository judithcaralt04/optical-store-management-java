package persistence;

import business.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JSON del DAO para gestionar la persistencia de datos de proveedores.
 * Maneja la lectura y escritura de proveedores desde/hacia el archivo providers.json.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class ProviderJSONDAO implements ProviderDAO {
    private static final String PROVIDERS_FILE = "providers.json";
    private final List<Provider> providers;
    
    /**
     * Construye un nuevo ProviderJSONDAO y carga los proveedores del archivo.
     * 
     * @throws IOException si el archivo no se puede leer o tiene estructura inválida
     */
    public ProviderJSONDAO() throws IOException {
        this.providers = new ArrayList<>();
        loadAll();
    }
    
    /**
     * Carga todos los proveedores desde el archivo providers.json.
     * 
     * @throws IOException si el archivo no se puede leer o tiene estructura inválida
     */
    public void loadAll() throws IOException {
        providers.clear();
        // JsonParser parsea directamente a objetos Provider
        providers.addAll(JsonParser.leerProveedores(PROVIDERS_FILE));
    }
    
    /**
     * Guarda todos los proveedores en el archivo providers.json.
     * 
     * @throws IOException si el archivo no se puede escribir
     */
    public void saveAll() throws IOException {
        // JsonParser escribe directamente desde los objetos Provider
        JsonParser.guardarProveedores(PROVIDERS_FILE, providers);
    }
    
    @Override
    public Provider findById(int providerId) {
        for (Provider provider : providers) {
            if (provider.getProviderId() == providerId) {
                return provider;
            }
        }
        return null;
    }
    
    @Override
    public List<Provider> readAll() {
        return new ArrayList<>(providers);
    }
    
    /**
     * Obtiene todos los proveedores.
     * 
     * @return una copia de la lista de proveedores
     */
    public List<Provider> getAllProviders() {
        return readAll();
    }
    
    /**
     * Actualiza un proveedor en el DAO.
     * 
     * @param provider el proveedor a actualizar
     */
    public void updateProvider(Provider provider) {
        for (int i = 0; i < providers.size(); i++) {
            if (providers.get(i).getProviderId() == provider.getProviderId()) {
                providers.set(i, provider);
                break;
            }
        }
    }
}

