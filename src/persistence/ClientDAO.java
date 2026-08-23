package persistence;

import business.Client;
import java.util.List;

/**
 * Interfaz para el acceso a datos de clientes.
 */
public interface ClientDAO {
    /**
     * Lee todos los clientes.
     * 
     * @return lista de todos los clientes
     */
    List<Client> readAll();
    
    /**
     * Busca un cliente por su identificador.
     * 
     * @param id el identificador del cliente
     * @return el cliente si existe, null si no existe
     */
    Client findById(int id);
    
    /**
     * Guarda un cliente.
     * 
     * @param c el cliente a guardar
     */
    void save(Client c);
}
