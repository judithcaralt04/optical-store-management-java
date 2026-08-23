package business;

import java.util.List;

/**
 * Representa un cliente empresa en el marketplace.
 * Las empresas tienen un NIF, persona de contacto, dirección de facturación y dirección de envío.
 * Las empresas con dirección de facturación fuera de España están exentas de IVA.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class CompanyClient extends Client {
    private final String nif;
    private final String contactPersonName;
    private final String billingAddress;
    private final String shippingAddress;
    
    /**
     * Construye un nuevo cliente empresa.
     * 
     * @param clientId el identificador único del cliente
     * @param nombreCompleto el nombre completo de la empresa
     * @param phoneNumbers la lista de números de teléfono
     * @param nif el número de identificación fiscal
     * @param contactPersonName el nombre de la persona de contacto
     * @param billingAddress la dirección de facturación
     * @param shippingAddress la dirección de envío
     */
    public CompanyClient(int clientId, String nombreCompleto, List<PhoneNumber> phoneNumbers,
                        String nif, String contactPersonName, String billingAddress, 
                        String shippingAddress) {
        super(clientId, nombreCompleto, phoneNumbers);
        this.nif = nif;
        this.contactPersonName = contactPersonName;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
    }
    
    /**
     * Obtiene el NIF (número de identificación fiscal) de la empresa.
     * 
     * @return el NIF
     */
    public String getNif() {
        return nif;
    }
    
    /**
     * Obtiene el nombre de la persona de contacto.
     * 
     * @return el nombre de contacto
     */
    public String getContactPersonName() {
        return contactPersonName;
    }
    
    /**
     * Obtiene la dirección de facturación.
     * 
     * @return la dirección de facturación
     */
    public String getBillingAddress() {
        return billingAddress;
    }
    
    /**
     * Obtiene la dirección de envío.
     * 
     * @return la dirección de envío
     */
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    /**
     * Verifica si la empresa está exenta de IVA.
     * Una empresa está exenta de IVA si su dirección de facturación
     * está fuera de España.
     * 
     * @return true si está exenta de IVA, false en caso contrario
     */
    public boolean isExemptFromVAT() {
        if (billingAddress == null || billingAddress.trim().isEmpty()) {
            return false;
        }
        
        // Verificamos si la dirección contiene indicadores de que está fuera de España
        String addressLower = billingAddress.toLowerCase();
        
        // Lista de países comunes (simplificado)
        String[] nonSpainIndicators = {
            "france", "francia", "portugal", "italy", "italia", "germany", "alemania",
            "uk", "united kingdom", "reino unido", "usa", "united states", "estados unidos",
            "mexico", "méxico", "argentina", "chile", "colombia"
        };
        
        for (String indicator : nonSpainIndicators) {
            if (addressLower.contains(indicator)) {
                return true;
            }
        }
        
        // Verificamos códigos postales de otros países (simplificado)
        // Si no empieza con código postal español típico (5 dígitos), podría ser de otro país
        // Pero esto es una simplificación - en un sistema real se usaría una API de geolocalización
        
        return false;
    }
}

