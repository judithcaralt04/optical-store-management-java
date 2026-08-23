package persistence;

import business.Product;
import java.util.List;

/**
 * Interfaz para el acceso a datos de productos.
 */
public interface ProductDAO {
    /**
     * Lee todos los productos.
     * 
     * @return lista de todos los productos
     */
    List<Product> readAll();
    
    /**
     * Busca un producto por su identificador.
     * 
     * @param id el identificador del producto
     * @return el producto si existe, null si no existe
     */
    Product findById(String id);
}
