package business;

/**
 * Representa un número de teléfono con su prefijo de país.
 **/
public class PhoneNumber {
    private final String prefijoPais;
    private final String numero;
    
    /**
     * Construye un nuevo PhoneNumber con el prefijo y número especificados.
     * 
     * @param prefijoPais el prefijo internacional del país (ej: "+34")
     * @param numero el número de teléfono
     */
    public PhoneNumber(String prefijoPais, String numero) {
        this.prefijoPais = prefijoPais;
        this.numero = numero;
    }
    
    /**
     * Obtiene el prefijo del país.
     * 
     * @return el prefijo del país
     */
    public String getCountryPrefix() {
        return prefijoPais;
    }
    
    /**
     * Obtiene el número de teléfono.
     * 
     * @return el número de teléfono
     */
    public String getNumber() {
        return numero;
    }
    
    /**
     * Devuelve una representación formateada del número de teléfono.
     * Formato: (+prefijo) número
     * 
     * @return string del número de teléfono formateado
     */
    public String getFormattedNumber() {
        return "(" + prefijoPais + ") " + numero;
    }
}

