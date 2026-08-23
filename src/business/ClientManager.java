package business;

import business.Client;
import business.CompanyClient;
import business.OnlineClient;
import business.PhoneNumber;
import business.Sale;
import persistence.ClientDAO;
import persistence.ClientJSONDAO;
import persistence.SaleDAO;
import persistence.SaleCSVDAO;
import java.io.IOException;
import java.util.List;

/**
 * Manager que maneja la lógica de negocio relacionada con los clientes.
 * Esta clase actúa como "Information Expert" para las operaciones con clientes.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class ClientManager {
    private final ClientDAO clientDAO;
    
    /**
     * Constructor del manager de clientes.
     * 
     * @param clientDAO el DAO de clientes
     */
    public ClientManager(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }
    
    /**
     * Registra un nuevo cliente en el sistema.
     * Asigna automáticamente un ID único al cliente.
     * 
     * @param fullName el nombre completo del cliente
     * @param phones la lista de números de teléfono del cliente
     * @return el cliente registrado
     * @throws IOException si hay un error al guardar el cliente
     */
    public Client register(String fullName, List<PhoneNumber> phones) throws IOException {
        // Obtenemos el siguiente ID disponible sin colisionar con sales.csv
        int maxIdClientes = 0;
        List<Client> allClients;
        if (clientDAO instanceof ClientJSONDAO) {
            allClients = ((ClientJSONDAO) clientDAO).getAllClients();
        } else {
            allClients = clientDAO.readAll();
        }
        
        for (Client client : allClients) {
            if (client.getClientId() > maxIdClientes) {
                maxIdClientes = client.getClientId();
            }
        }

        int maxIdVentas = 0;
        try {
            SaleCSVDAO saleDAO = new SaleCSVDAO();
            for (Sale sale : saleDAO.readAll()) {
                if (sale.getClientId() > maxIdVentas) {
                    maxIdVentas = sale.getClientId();
                }
            }
        } catch (IOException e) {
            // Si hay error leyendo ventas, continuar con maxIdVentas = 0
        }

        int nuevoId = Math.max(maxIdClientes, maxIdVentas) + 1;
        
        // Creamos el nuevo cliente
        Client nuevoCliente = new Client(nuevoId, fullName, phones);
        
        // Guardar el cliente
        if (clientDAO instanceof ClientJSONDAO) {
            ((ClientJSONDAO) clientDAO).addClient(nuevoCliente);
            ((ClientJSONDAO) clientDAO).saveAll();
        } else {
            clientDAO.save(nuevoCliente);
        }
        
        return nuevoCliente;
    }
    
    /**
     * Auténtica un cliente por su identificador.
     * 
     * @param id el identificador del cliente
     * @return true si el cliente existe, false en caso contrario
     */
    public boolean login(int id) {
        Client client = clientDAO.findById(id);
        return client != null;
    }
    
    /**
     * Obtiene un cliente por su identificador.
     * 
     * @param id el identificador del cliente
     * @return el cliente si existe, null si no existe
     */
    public Client getClientById(int id) {
        return clientDAO.findById(id);
    }
    
    /**
     * Registra un nuevo cliente en el sistema.
     * Método de compatibilidad que delega en register.
     * 
     * @param nombreCompleto el nombre completo del cliente
     * @param phoneNumbers la lista de números de teléfono del cliente
     * @return el cliente registrado
     * @throws IOException si hay un error al guardar el cliente
     */
    public Client registrarCliente(String nombreCompleto, List<PhoneNumber> phoneNumbers) throws IOException {
        return register(nombreCompleto, phoneNumbers);
    }
    
    /**
     * Registra un nuevo cliente online en el sistema.
     * 
     * @param nombreCompleto el nombre completo del cliente
     * @param phoneNumbers la lista de números de teléfono
     * @param shippingAddress la dirección de envío
     * @param email el correo electrónico
     * @return el cliente online registrado
     * @throws IOException si hay un error al guardar el cliente
     */
    public OnlineClient registerOnlineClient(String nombreCompleto, List<PhoneNumber> phoneNumbers,
                                            String shippingAddress, String email) throws IOException {
        int nuevoId = getNextClientId();
        OnlineClient nuevoCliente = new OnlineClient(nuevoId, nombreCompleto, phoneNumbers, 
                                                     shippingAddress, email);
        
        if (clientDAO instanceof ClientJSONDAO) {
            ((ClientJSONDAO) clientDAO).addClient(nuevoCliente);
            ((ClientJSONDAO) clientDAO).saveAll();
        } else {
            clientDAO.save(nuevoCliente);
        }
        
        return nuevoCliente;
    }
    
    /**
     * Registra un nuevo cliente empresa en el sistema.
     * 
     * @param nombreCompleto el nombre completo de la empresa
     * @param phoneNumbers la lista de números de teléfono
     * @param nif el número de identificación fiscal
     * @param contactPersonName el nombre de la persona de contacto
     * @param billingAddress la dirección de facturación
     * @param shippingAddress la dirección de envío
     * @return el cliente empresa registrado
     * @throws IOException si hay un error al guardar el cliente
     */
    public CompanyClient registerCompanyClient(String nombreCompleto, List<PhoneNumber> phoneNumbers,
                                               String nif, String contactPersonName,
                                               String billingAddress, String shippingAddress) throws IOException {
        int nuevoId = getNextClientId();
        CompanyClient nuevoCliente = new CompanyClient(nuevoId, nombreCompleto, phoneNumbers,
                                                      nif, contactPersonName, billingAddress, shippingAddress);
        
        if (clientDAO instanceof ClientJSONDAO) {
            ((ClientJSONDAO) clientDAO).addClient(nuevoCliente);
            ((ClientJSONDAO) clientDAO).saveAll();
        } else {
            clientDAO.save(nuevoCliente);
        }
        
        return nuevoCliente;
    }
    
    /**
     * Obtiene el siguiente ID de cliente disponible.
     * 
     * @return el siguiente ID disponible
     */
    private int getNextClientId() {
        int maxIdClientes = 0;
        List<Client> allClients;
        if (clientDAO instanceof ClientJSONDAO) {
            allClients = ((ClientJSONDAO) clientDAO).getAllClients();
        } else {
            allClients = clientDAO.readAll();
        }
        
        for (Client client : allClients) {
            if (client.getClientId() > maxIdClientes) {
                maxIdClientes = client.getClientId();
            }
        }

        int maxIdVentas = 0;
        try {
            SaleCSVDAO saleDAO = new SaleCSVDAO();
            for (Sale sale : saleDAO.readAll()) {
                if (sale.getClientId() > maxIdVentas) {
                    maxIdVentas = sale.getClientId();
                }
            }
        } catch (IOException e) {
            // Si hay error leyendo ventas, continuar con maxIdVentas = 0
        }

        return Math.max(maxIdClientes, maxIdVentas) + 1;
    }
    
    /**
     * Auténtica un cliente por su identificador.
     * Método de compatibilidad que obtiene el cliente por ID.
     * 
     * @param clientId el identificador del cliente
     * @return el cliente autenticado si existe, null si no existe
     */
    public Client autenticarCliente(int clientId) {
        Client client = getClientById(clientId);
        return client;
    }
    
    /**
     * Obtiene un cliente por su identificador.
     * Método de compatibilidad que delega en getClientById.
     * 
     * @param clientId el identificador del cliente
     * @return el cliente si existe, null si no existe
     */
    public Client obtenerCliente(int clientId) {
        return getClientById(clientId);
    }
}

