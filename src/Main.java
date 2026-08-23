import business.ProductManager;
import business.ProviderManager;
import business.ClientManager;
import business.SaleManager;
import business.ShoppingCartManager;
import persistence.PersistenceManager;
import persistence.ClientDAO;
import persistence.ProductDAO;
import persistence.ProviderDAO;
import persistence.SaleDAO;
import presentation.Controller;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase principal del programa Nude Eye Project.
 * Punto de entrada de la aplicación que valida la conexión con la API o archivos locales
 * e inicializa el sistema.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class Main {
    
    /**
     * Método principal que inicia la aplicación.
     * 
     * @param args argumentos de la línea de comandos (no utilizados)
     */
    static void main(String[] args) {
        // Configuramos logging para ocultar mensajes técnicos de librerías externas
        // Suprimimos logs INFO de geolocationAPI para que no aparezcan en la consola
        Logger geolocationLogger = Logger.getLogger("geolocationAPI");
        geolocationLogger.setLevel(Level.WARNING); // Solo mostramos WARNING y ERROR, no INFO
        
        // También suprimimos logs del root logger si es necesario
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.WARNING);
        
        try {
            // Mostramos mensaje de bienvenida
            mostrarBienvenida();
            
            // Verificamos estado de la API
            System.out.println("Checking API status...");
            
            // Creamos PersistenceManager que intentará conectar con la API
            // Si falla, usaremos archivos locales automáticamente
            PersistenceManager persistenceManager;
            try {
                persistenceManager = new PersistenceManager();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Shutting down...");
                System.exit(1);
                return; // Nunca se ejecutará, pero evitamos warnings
            }
            
            System.out.println();
            System.out.println("Starting program...");
            System.out.println();
            
            // Inicializamos DAOs usando PersistenceManager
            ProductDAO productDAO = persistenceManager.createProductDAO();
            ProviderDAO providerDAO = persistenceManager.createProviderDAO();
            ClientDAO clientDAO = persistenceManager.createClientDAO();
            SaleDAO saleDAO = persistenceManager.createSaleDAO();
            
            // Inicializamos managers
            ProductManager productManager = new ProductManager(productDAO, providerDAO);
            ProviderManager providerManager = new ProviderManager(providerDAO);
            ClientManager clientManager = new ClientManager(clientDAO);
            SaleManager saleManager = new SaleManager(saleDAO, providerDAO);
            ShoppingCartManager shoppingCartManager = new ShoppingCartManager();
            
            // Creamos y ejecutamos el controlador principal
            Controller controller = new Controller(productManager, providerManager, clientManager, saleManager, shoppingCartManager);
            controller.run();
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Shutting down...");
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Unexpected error occurred", e);
            System.out.println("Shutting down...");
            System.exit(1);
        }
    }
    
    /**
     * Muestra el mensaje ASCII de bienvenida del proyecto.
     */
    private static void mostrarBienvenida() {
        System.out.println("_  _         _       ___           ___          _        _    ");
        System.out.println("| \\| |_  _ _| |__  | _|  _ ___  | _ \\ _ ___ ()__ _| | ");
        System.out.println("| .` | || / ` / -) | | || / -) |  / '/ _ \\| / -_) _|  _|");
        System.out.println("||\\|\\,\\,\\| |\\, \\| || || \\// \\\\|\\_|");
        System.out.println("                        |/                 |/            ");
        System.out.println();
        System.out.println("Welcome to Nude Eye Project.");
        System.out.println("\"by glass wearers for glass wearers\"");
        System.out.println();
    }
    
}

