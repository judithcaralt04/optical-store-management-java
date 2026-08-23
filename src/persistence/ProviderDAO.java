package persistence;

import business.Provider;
import java.util.List;

/**
 * Interfaz para el acceso a datos de proveedores.
 */
public interface ProviderDAO {
    /**
     * Lee todos los proveedores.
     * 
     * @return lista de todos los proveedores
     */
    List<Provider> readAll();
    
    /**
     * Busca un proveedor por su identificador.
     * 
     * @param id el identificador del proveedor
     * @return el proveedor si existe, null si no existe
     */
    Provider findById(int id);
}
