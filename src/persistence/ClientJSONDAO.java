package persistence;

import business.Client;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JSON del DAO para gestionar la persistencia de datos de clientes.
 * Maneja la lectura y escritura de clientes desde/hacia el archivo clients.json.
 */
public class ClientJSONDAO implements ClientDAO {
    private static final String CLIENTS_FILE = "clients.json";
    private final List<Client> clients;
    
    /**
     * Construye un nuevo ClientJSONDAO.
     * Si el archivo existe, carga los clientes desde él. Si no, inicializa una lista vacía.
     * 
     * @throws IOException si el archivo no se puede leer (cuando existe) o tiene estructura inválida
     */
    public ClientJSONDAO() throws IOException {
        this.clients = new ArrayList<>();
        File file = new File(CLIENTS_FILE);
        if (file.exists()) {
            loadAll();
        }
    }
    
    /**
     * Carga todos los clientes desde el archivo clients.json.
     * 
     * @throws IOException si el archivo no se puede leer o tiene estructura inválida
     */
    public void loadAll() throws IOException {
        clients.clear();
        // JsonParser parsea directamente a objetos Client
        clients.addAll(JsonParser.leerClientes(CLIENTS_FILE));
    }
    
    /**
     * Guarda todos los clientes en el archivo clients.json.
     * Crea el archivo si no existe.
     * 
     * @throws IOException si el archivo no se puede escribir
     */
    public void saveAll() throws IOException {
        // JsonParser escribe directamente desde los objetos Client
        JsonParser.guardarClientes(CLIENTS_FILE, clients);
    }
    
    @Override
    public Client findById(int clientId) {
        for (Client client : clients) {
            if (client.getClientId() == clientId) {
                return client;
            }
        }
        return null;
    }
    
    @Override
    public List<Client> readAll() {
        return new ArrayList<>(clients);
    }
    
    @Override
    public void save(Client c) {
        addClient(c);
        try {
            saveAll();
        } catch (IOException e) {
            // Error al guardar - se puede manejar según necesidad
        }
    }
    
    /**
     * Obtiene todos los clientes.
     * 
     * @return una copia de la lista de clientes
     */
    public List<Client> getAllClients() {
        return readAll();
    }
    
    /**
     * Añade un nuevo cliente al DAO.
     * 
     * @param client el cliente a añadir
     */
    public void addClient(Client client) {
        clients.add(client);
    }
    
    /**
     * Obtiene el siguiente identificador de cliente disponible.
     * 
     * @return el siguiente ID disponible
     */
    public int getNextClientId() {
        int maxId = 0;
        for (Client client : clients) {
            if (client.getClientId() > maxId) {
                maxId = client.getClientId();
            }
        }
        return maxId + 1;
    }
}

