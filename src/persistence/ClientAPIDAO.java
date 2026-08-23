package persistence;

import business.Client;
import business.OnlineClient;
import business.CompanyClient;
import business.PhoneNumber;
import edu.salle.url.api.ApiHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación API del DAO para gestionar la persistencia de datos de clientes.
 * Obtiene y guarda clientes desde/hacia la API del servidor.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class ClientAPIDAO implements ClientDAO {
    private final ApiHelper apiHelper;
    private final String baseUrl;
    private final String groupId;
    private List<Client> clients;
    
    /**
     * Construye un nuevo ClientAPIDAO.
     * 
     * @param apiHelper el helper para hacer peticiones a la API
     * @param groupId el identificador del grupo para las peticiones
     */
    public ClientAPIDAO(ApiHelper apiHelper, String groupId) {
        this.apiHelper = apiHelper;
        this.baseUrl = "http://localhost/dpoo";
        this.groupId = groupId;
        this.clients = new ArrayList<>();
        try {
            loadAll();
        } catch (IOException e) {
            // Si falla la carga inicial, la lista queda vacía
            this.clients = new ArrayList<>();
        }
    }
    
    /**
     * Carga todos los clientes desde la API.
     * 
     * @throws IOException si hay un error al obtener los clientes
     */
    public void loadAll() throws IOException {
        try {
            String url = baseUrl + "/" + groupId + "/clients";
            String jsonResponse = apiHelper.getFromUrl(url);
            clients = parseClientsFromJSON(jsonResponse);
        } catch (Exception e) {
            throw new IOException("Error getting clients from API: " + e.getMessage(), e);
        }
    }
    
    /**
     * Guarda todos los clientes en la API.
     * 
     * @throws IOException si hay un error al guardar los clientes
     */
    public void saveAll() throws IOException {
        // La API no tiene un endpoint para guardar múltiples clientes a la vez
        // Cada cliente se guarda individualmente cuando se crea
        // Este método se deja para compatibilidad pero no hace nada
    }
    
    /**
     * Parsea una respuesta JSON de clientes y devuelve una lista de Client.
     * 
     * @param json la respuesta JSON de la API
     * @return lista de clientes parseados
     * @throws IOException si el JSON no es válido
     */
    public List<Client> parseClientsFromJSON(String json) throws IOException {
        try {
            java.io.File tempFile = java.io.File.createTempFile("clients", ".json");
            tempFile.deleteOnExit();
            try (java.io.FileWriter writer = new java.io.FileWriter(tempFile)) {
                writer.write(json);
            }
            
            // Parsear clientes base primero
            List<Client> baseClients = JsonParser.leerClientes(tempFile.getAbsolutePath());
            
            // Ahora necesitamos parsear los tipos específicos (OnlineClient, CompanyClient)
            // Esto requiere modificar JsonParser o parsear manualmente
            // Por ahora, devolvemos los clientes base
            // TODO: Implementar parsing de OnlineClient y CompanyClient desde JSON
            
            tempFile.delete();
            return baseClients;
        } catch (Exception e) {
            throw new IOException("Error parsing clients JSON: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Client findById(int clientId) {
        try {
            // Intentamos obtener el cliente específico por ID desde la API
            String url = baseUrl + "/" + groupId + "/clients?client_id=" + clientId;
            String jsonResponse = apiHelper.getFromUrl(url);
            List<Client> foundClients = parseClientsFromJSON(jsonResponse);
            if (!foundClients.isEmpty()) {
                return foundClients.get(0);
            }
        } catch (Exception e) {
            // Si falla, buscar en la lista en memoria
        }
        
        // Buscar en la lista en memoria
        for (Client client : clients) {
            if (client.getClientId() == clientId) {
                return client;
            }
        }
        return null;
    }
    
    @Override
    public List<Client> readAll() {
        // Recargamos desde la API para obtener datos actualizados
        try {
            loadAll();
        } catch (IOException e) {
            // Si falla, devolver la lista en memoria
        }
        return new ArrayList<>(clients);
    }
    
    @Override
    public void save(Client client) {
        try {
            String url = baseUrl + "/" + groupId + "/clients";
            
            // Convertir el cliente a JSON
            String jsonBody = convertClientToJSON(client);
            
            // Hacer POST a la API
            apiHelper.postToUrl(url, jsonBody);
            
            // Recargar la lista
            loadAll();
        } catch (Exception e) {
            // Error saving - can be handled as needed
            System.err.println("Error saving client to API: " + e.getMessage());
        }
    }
    
    /**
     * Convierte un Client a formato JSON para enviarlo a la API.
     * 
     * @param client el cliente a convertir
     * @return el JSON del cliente
     */
    private String convertClientToJSON(Client client) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"client_id\": ").append(client.getClientId()).append(",\n");
        json.append("  \"client_type\": \"");
        
        // Determinar el tipo de cliente
        if (client instanceof OnlineClient) {
            json.append("online");
            OnlineClient onlineClient = (OnlineClient) client;
            json.append("\",\n");
            json.append("  \"shipping_address\": \"").append(escapeJson(onlineClient.getShippingAddress())).append("\",\n");
            json.append("  \"email\": \"").append(escapeJson(onlineClient.getEmail())).append("\",\n");
        } else if (client instanceof CompanyClient) {
            json.append("corporate");
            CompanyClient companyClient = (CompanyClient) client;
            json.append("\",\n");
            json.append("  \"cif\": \"").append(escapeJson(companyClient.getNif())).append("\",\n");
            json.append("  \"contact_name\": \"").append(escapeJson(companyClient.getContactPersonName())).append("\",\n");
            json.append("  \"billing_address\": \"").append(escapeJson(companyClient.getBillingAddress())).append("\",\n");
            json.append("  \"mailing_address\": \"").append(escapeJson(companyClient.getShippingAddress())).append("\",\n");
        } else {
            json.append("standard");
            json.append("\",\n");
        }
        
        json.append("  \"full_name\": \"").append(escapeJson(client.getFullName())).append("\",\n");
        json.append("  \"phone_numbers\": [\n");
        
        List<PhoneNumber> phones = client.getPhoneNumbers();
        for (int i = 0; i < phones.size(); i++) {
            PhoneNumber phone = phones.get(i);
            json.append("    {\n");
            json.append("      \"country_prefix\": \"").append(escapeJson(phone.getCountryPrefix())).append("\",\n");
            json.append("      \"number\": \"").append(escapeJson(phone.getNumber())).append("\"\n");
            json.append("    }");
            if (i < phones.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        
        json.append("  ]\n");
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * Escapa caracteres especiales en JSON.
     * 
     * @param text el texto a escapar
     * @return el texto escapado
     */
    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}

