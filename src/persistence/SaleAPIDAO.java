package persistence;

import business.Sale;
import edu.salle.url.api.ApiHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación API del DAO para gestionar la persistencia de datos de ventas.
 * Obtiene y guarda ventas desde/hacia la API del servidor.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class SaleAPIDAO implements SaleDAO {
    private final ApiHelper apiHelper;
    private final String baseUrl;
    private final String groupId;
    private List<Sale> sales;
    
    /**
     * Construye un nuevo SaleAPIDAO.
     * 
     * @param apiHelper el helper para hacer peticiones a la API
     * @param groupId el identificador del grupo para las peticiones
     */
    public SaleAPIDAO(ApiHelper apiHelper, String groupId) {
        this.apiHelper = apiHelper;
        this.baseUrl = "http://localhost/dpoo";
        this.groupId = groupId;
        this.sales = new ArrayList<>();
        try {
            loadAll();
        } catch (IOException e) {
            // Si falla la carga inicial, la lista queda vacía
            this.sales = new ArrayList<>();
        }
    }
    
    /**
     * Carga todas las ventas desde la API.
     * 
     * @return lista de ventas cargadas
     * @throws IOException si hay un error al obtener las ventas
     */
    public List<Sale> loadAll() throws IOException {
        try {
            String url = baseUrl + "/" + groupId + "/sales";
            String csvResponse = apiHelper.getFromUrl(url);
            sales = parseSalesFromCSV(csvResponse);
            return new ArrayList<>(sales);
        } catch (Exception e) {
            throw new IOException("Error getting sales from API: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene todas las ventas de un cliente específico desde la API.
     * 
     * @param clientId el identificador del cliente
     * @return lista de ventas del cliente
     * @throws IOException si hay un error al obtener las ventas
     */
    public List<Sale> getSalesByClientId(int clientId) throws IOException {
        try {
            String url = baseUrl + "/" + groupId + "/sales?client_id=" + clientId;
            String csvResponse = apiHelper.getFromUrl(url);
            return parseSalesFromCSV(csvResponse);
        } catch (Exception e) {
            throw new IOException("Error getting client sales from API: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parsea una respuesta CSV de ventas y devuelve una lista de Sale.
     * 
     * @param csv la respuesta CSV de la API
     * @return lista de ventas parseadas
     * @throws IOException si el CSV no es válido
     */
    public List<Sale> parseSalesFromCSV(String csv) throws IOException {
        List<Sale> salesList = new ArrayList<>();
        
        if (csv == null || csv.trim().isEmpty()) {
            return salesList;
        }
        
        String[] lines = csv.split("\n");
        boolean isFirstLine = true;
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            
            // Saltar la cabecera
            if (isFirstLine && line.startsWith("client_id")) {
                isFirstLine = false;
                continue;
            }
            isFirstLine = false;
            
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                try {
                    int clientId = Integer.parseInt(parts[0].trim());
                    String productId = parts[1].trim();
                    double pricePaid = Double.parseDouble(parts[2].trim());
                    long purchaseDate = Long.parseLong(parts[3].trim());
                    
                    salesList.add(new Sale(clientId, productId, pricePaid, purchaseDate));
                } catch (NumberFormatException e) {
                    // Ignorar líneas con formato incorrecto
                }
            }
        }
        
        return salesList;
    }
    
    @Override
    public List<Sale> readAll() throws IOException {
        // Recargamos desde la API para obtener datos actualizados
        return loadAll();
    }
    
    @Override
    public void appendSale(Sale sale) throws IOException {
        try {
            String url = baseUrl + "/" + groupId + "/sales";
            
            // Convertir la venta a JSON según la especificación de la API
            String jsonBody = convertSaleToJSON(sale);
            
            // Hacer POST a la API
            apiHelper.postToUrl(url, jsonBody);
            
            // Añadimos a la lista local
            sales.add(sale);
        } catch (Exception e) {
            throw new IOException("Error saving sale to API: " + e.getMessage(), e);
        }
    }
    
    /**
     * Convierte un Sale a formato JSON para enviarlo a la API.
     * 
     * @param sale la venta a convertir
     * @return el JSON de la venta
     */
    private String convertSaleToJSON(Sale sale) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"client_id\": ").append(sale.getClientId()).append(",\n");
        json.append("  \"product_id\": \"").append(escapeJson(sale.getProductId())).append("\",\n");
        json.append("  \"price_paid\": ").append(sale.getPricePaid()).append(",\n");
        json.append("  \"purchase_date\": ").append(sale.getPurchaseDate()).append("\n");
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

