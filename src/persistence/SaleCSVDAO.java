package persistence;

import business.Sale;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación CSV del DAO para gestionar la persistencia de datos de ventas.
 * Maneja la lectura y escritura de ventas desde/hacia el archivo sales.csv.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class SaleCSVDAO implements SaleDAO {
    private static final String SALES_FILE = "sales.csv";
    private static final String CSV_HEADER = "client_id,product_id,price_paid,purchase_date";
    
    /**
     * Construye un nuevo SaleCSVDAO.
     */
    public SaleCSVDAO() {
        // No se necesita inicialización - el archivo se lee/escribe bajo demanda
    }
    
    @Override
    public List<Sale> readAll() throws IOException {
        List<Sale> sales = new ArrayList<>();
        File file = new File(SALES_FILE);
        
        if (!file.exists()) {
            return sales;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(SALES_FILE))) {
            String line = reader.readLine(); // Saltar cabecera
            if (line == null || !line.equals(CSV_HEADER)) {
                // Si el archivo está vacío o falta la cabecera, devolver lista vacía
                return sales;
            }
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int clientId = Integer.parseInt(parts[0].trim());
                    String productId = parts[1].trim();
                    double precioPagado = Double.parseDouble(parts[2].trim());
                    long fechaCompra = Long.parseLong(parts[3].trim());
                    
                    sales.add(new Sale(clientId, productId, precioPagado, fechaCompra));
                }
            }
        }
        
        return sales;
    }
    
    @Override
    public void appendSale(Sale sale) throws IOException {
        File file = new File(SALES_FILE);
        boolean fileExists = file.exists();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SALES_FILE, true))) {
            // Escribir cabecera si el archivo es nuevo
            if (!fileExists) {
                writer.write(CSV_HEADER);
                writer.newLine();
            }
            
            // Escribir datos de la venta
            writer.write(sale.getClientId() + "," + 
                        sale.getProductId() + "," + 
                        sale.getPricePaid() + "," + 
                        sale.getPurchaseDate());
            writer.newLine();
        }
    }
    
    /**
     * Carga todas las ventas desde el archivo sales.csv.
     * Si el archivo no existe, devuelve una lista vacía.
     * 
     * @return lista de todas las ventas
     * @throws IOException si el archivo no se puede leer
     */
    public List<Sale> loadAll() throws IOException {
        return readAll();
    }
    
    /**
     * Obtiene todas las ventas de un cliente específico.
     * 
     * @param clientId el identificador del cliente
     * @return lista de ventas del cliente
     * @throws IOException si el archivo no se puede leer
     */
    public List<Sale> getSalesByClientId(int clientId) throws IOException {
        List<Sale> allSales = readAll();
        List<Sale> clientSales = new ArrayList<>();
        
        for (Sale sale : allSales) {
            if (sale.getClientId() == clientId) {
                clientSales.add(sale);
            }
        }
        
        return clientSales;
    }
}

