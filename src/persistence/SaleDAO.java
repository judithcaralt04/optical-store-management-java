package persistence;

import business.Sale;
import java.io.IOException;
import java.util.List;

/**
 * Interfaz para el acceso a datos de ventas.
 */
public interface SaleDAO {
    /**
     * Lee todas las ventas.
     * 
     * @return lista de todas las ventas
     * @throws IOException si hay un error al leer las ventas
     */
    List<Sale> readAll() throws IOException;
    
    /**
     * Añade una nueva venta.
     * 
     * @param s la venta a añadir
     * @throws IOException si hay un error al escribir la venta
     */
    void appendSale(Sale s) throws IOException;
}
