package persistence;

import edu.salle.url.api.ApiHelper;
import java.io.File;
import java.io.IOException;

/**
 * Manager que gestiona la persistencia de datos del sistema.
 * Decide si usar la API o archivos locales (JSON/CSV) según la disponibilidad.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class PersistenceManager {
    private static final String BASE_API_URL = "http://localhost/dpoo";
    private static final String GROUP_ID = "P1-Project-112";
    
    private final ApiHelper apiHelper;
    private final boolean useAPI;
    
    /**
     * Construye un nuevo PersistenceManager.
     * Intenta conectar con la API y, si falla, usa persistencia local.
     * 
     * @throws IOException si no hay ninguna fuente de persistencia disponible
     */
    public PersistenceManager() throws IOException {
        // Intentar verificar el estado de la API
        boolean apiAvailable = false;
        ApiHelper helper = null;
        
        try {
            // Intentamos crear ApiHelper (puede fallar si hay problemas de red)
            helper = new ApiHelper();
            // Intentamos verificar el estado de la API
            apiAvailable = checkAPIStatus(helper);
        } catch (Exception e) {
            // Si hay cualquier error al crear ApiHelper o verificar la API, asumimos que no está disponible
            apiAvailable = false;
        }
        
        if (apiAvailable && helper != null) {
            this.apiHelper = helper;
            this.useAPI = true;
            System.out.println("API OK");
        } else {
            // Si la API no está disponible, usar archivos locales
            this.apiHelper = null; // No necesitamos ApiHelper si usamos archivos locales
            this.useAPI = false;
            System.out.println("Error: The API isn't available.");
            System.out.println("Verifying local files...");
            // Verificamos que los archivos locales existen
            boolean filesValid = validateLocalFiles();
            if (!filesValid) {
                // Si los archivos locales tampoco están disponibles, terminar el programa
                throw new IOException("No persistence source available (neither API nor local files)");
            }
            // Si llegamos aquí, los archivos están disponibles
            System.out.println("Files OK.");
        }
    }
    
    /**
     * Verifica el estado de la API intentando hacer una petición simple.
     * 
     * @param helper el ApiHelper a usar para la verificación
     * @return true si la API está disponible, false en caso contrario
     */
    private boolean checkAPIStatus(ApiHelper helper) {
        try {
            // Intentamos obtener los productos compartidos (endpoint que siempre debería existir)
            String url = BASE_API_URL + "/shared/products";
            helper.getFromUrl(url);
            return true;
        } catch (Exception e) {
            // Cualquier excepción indica que la API no está disponible
            // No imprimimos el error aquí, lo haremos en el constructor
            return false;
        }
    }
    
    /**
     * Valida que los archivos locales necesarios existan y sean accesibles.
     * 
     * @return true si los archivos están disponibles, false en caso contrario
     */
    private boolean validateLocalFiles() {
        File productsFile = new File("products.json");
        if (!productsFile.exists() || !productsFile.canRead()) {
            System.out.println("Error: The products.json file can't be accessed.");
            return false;
        }
        
        File providersFile = new File("providers.json");
        if (!providersFile.exists() || !providersFile.canRead()) {
            System.out.println("Error: The providers.json file can't be accessed.");
            return false;
        }
        
        // clients.json y sales.csv son opcionales
        // Si llegamos aquí, ambos archivos existen y son legibles
        return true;
    }
    
    /**
     * Crea un ProductDAO según la fuente de persistencia activa.
     * 
     * @return el ProductDAO correspondiente (API o JSON)
     * @throws IOException si hay un error al crear el DAO
     */
    public ProductDAO createProductDAO() throws IOException {
        if (useAPI && apiHelper != null) {
            return new ProductAPIDAO(apiHelper);
        } else {
            return new ProductJSONDAO();
        }
    }
    
    /**
     * Crea un ClientDAO según la fuente de persistencia activa.
     * 
     * @return el ClientDAO correspondiente (API o JSON)
     * @throws IOException si hay un error al crear el DAO
     */
    public ClientDAO createClientDAO() throws IOException {
        if (useAPI && apiHelper != null) {
            return new ClientAPIDAO(apiHelper, GROUP_ID);
        } else {
            return new ClientJSONDAO();
        }
    }
    
    /**
     * Crea un ProviderDAO según la fuente de persistencia activa.
     * 
     * @return el ProviderDAO correspondiente (API o JSON)
     * @throws IOException si hay un error al crear el DAO
     */
    public ProviderDAO createProviderDAO() throws IOException {
        if (useAPI && apiHelper != null) {
            return new ProviderAPIDAO(apiHelper);
        } else {
            return new ProviderJSONDAO();
        }
    }
    
    /**
     * Crea un SaleDAO según la fuente de persistencia activa.
     * 
     * @return el SaleDAO correspondiente (API o CSV)
     * @throws IOException si hay un error al crear el DAO
     */
    public SaleDAO createSaleDAO() throws IOException {
        if (useAPI && apiHelper != null) {
            return new SaleAPIDAO(apiHelper, GROUP_ID);
        } else {
            return new SaleCSVDAO();
        }
    }
    
    /**
     * Indica si se está usando la API como fuente de persistencia.
     * 
     * @return true si se usa la API, false si se usan archivos locales
     */
    public boolean isUsingAPI() {
        return useAPI;
    }
    
    /**
     * Obtiene el ID del grupo para las peticiones a la API.
     * 
     * @return el ID del grupo
     */
    public String getGroupId() {
        return GROUP_ID;
    }
    
    /**
     * Obtiene la URL base de la API.
     * 
     * @return la URL base
     */
    public String getBaseApiUrl() {
        return BASE_API_URL;
    }
}

