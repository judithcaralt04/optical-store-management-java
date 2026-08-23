package business;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un cliente en el marketplace.
 * Cada cliente tiene un identificador único, nombre completo y al menos un número de teléfono.
 *
 */
public class Client {
    private final int clienteId;
    private final String nombreCompleto;
    private final List<PhoneNumber> phoneNumbers;
    
    /**
     * Construye un nuevo Cliente con los atributos especificados.
     * 
     * @param clienteId el identificador único del cliente
     * @param nombreCompleto el nombre completo del cliente
     * @param phoneNumbers la lista de números de teléfono (debe contener al menos uno)
     */
    public Client(int clienteId, String nombreCompleto, List<PhoneNumber> phoneNumbers) {
        this.clienteId = clienteId;
        this.nombreCompleto = nombreCompleto;
        this.phoneNumbers = new ArrayList<>(phoneNumbers);
    }
    
    /**
     * Obtiene el identificador único del cliente.
     * 
     * @return el ID del cliente
     */
    public int getClientId() {
        return clienteId;
    }
    
    /**
     * Obtiene el nombre completo del cliente.
     * 
     * @return el nombre completo
     */
    public String getFullName() {
        return nombreCompleto;
    }
    
    /**
     * Obtiene la lista de números de teléfono.
     * 
     * @return una copia de la lista de números de teléfono
     */
    public List<PhoneNumber> getPhoneNumbers() {
        return new ArrayList<>(phoneNumbers);
    }
}

