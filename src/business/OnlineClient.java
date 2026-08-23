package business;

import java.util.List;

/**
 * Representa un cliente online en el marketplace.
 * Los clientes online tienen una dirección de envío y un correo electrónico.
 * Al precio del carrito se les añade el coste de envío calculado según la distancia.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class OnlineClient extends Client {
    private final String shippingAddress;
    private final String email;
    
    /** Latitud del campus de La Salle (origen de envíos). */
    private static final double CAMPUS_LAT = 41.408879;
    /** Longitud del campus de La Salle (origen de envíos). */
    private static final double CAMPUS_LON = 2.130103;
    /** Coste base de envío en euros. */
    private static final double BASE_SHIPPING_COST = 2.0;
    /** Coste por kilómetro en euros. */
    private static final double COST_PER_KM = 0.01;
    
    /**
     * Construye un nuevo cliente online.
     * 
     * @param clientId el identificador único del cliente
     * @param nombreCompleto el nombre completo del cliente
     * @param phoneNumbers la lista de números de teléfono
     * @param shippingAddress la dirección de envío
     * @param email el correo electrónico
     */
    public OnlineClient(int clientId, String nombreCompleto, List<PhoneNumber> phoneNumbers,
                       String shippingAddress, String email) {
        super(clientId, nombreCompleto, phoneNumbers);
        this.shippingAddress = shippingAddress;
        this.email = email;
    }
    
    /**
     * Obtiene la dirección de envío del cliente.
     * 
     * @return la dirección de envío
     */
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    /**
     * Obtiene el correo electrónico del cliente.
     * 
     * @return el email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Calcula el coste de envío basado en la distancia desde el campus de La Salle
     * hasta la dirección de envío del cliente.
     * 
     * @param geolocationHelper el helper para obtener geolocalización y calcular distancias
     * @return el coste de envío en euros, o 0.0 si no se puede calcular
     */
    public double calculateShippingCost(Object geolocationHelper) {
        try {
            // Parsear la dirección del String a un objeto Address
            // Intentamos extraer componentes básicos de la dirección
            Object address = parseAddressFromString(shippingAddress);
            if (address == null) {
                return 0.0;
            }
            
            // Usar reflexión para llamar a los métodos de GeolocationApiManager
            // El método getGeolocationFromAddress recibe un Address, no un String
            java.lang.reflect.Method getGeolocationMethod = geolocationHelper.getClass()
                .getMethod("getGeolocationFromAddress", address.getClass());
            Object geolocation = getGeolocationMethod.invoke(geolocationHelper, address);
            
            if (geolocation == null) {
                return 0.0;
            }
            
            // Obtenemos latitud y longitud de la geolocalización
            java.lang.reflect.Method getLatMethod = geolocation.getClass().getMethod("getLatitude");
            java.lang.reflect.Method getLonMethod = geolocation.getClass().getMethod("getLongitude");
            double lat = (Double) getLatMethod.invoke(geolocation);
            double lon = (Double) getLonMethod.invoke(geolocation);
            
            // Creamos geolocalización del campus
            Object campusGeolocation = geolocation.getClass().getConstructor(double.class, double.class)
                .newInstance(CAMPUS_LAT, CAMPUS_LON);
            
            // Calculamos distancia
            java.lang.reflect.Method calculateDistanceMethod = geolocationHelper.getClass()
                .getMethod("calculateDistance", geolocation.getClass(), geolocation.getClass());
            double distance = (Double) calculateDistanceMethod.invoke(geolocationHelper, 
                campusGeolocation, geolocation);
            
            // Calculamos coste: 2€ + 0.01€ por km
            return BASE_SHIPPING_COST + (distance * COST_PER_KM);
            
        } catch (Exception e) {
            // Si hay algún error, retornar 0.0
            return 0.0;
        }
    }
    
    /**
     * Parsea un String de dirección a un objeto Address.
     * Intenta extraer los componentes básicos de la dirección.
     * 
     * @param addressString la dirección como String
     * @return un objeto Address o null si no se puede parsear
     */
    private Object parseAddressFromString(String addressString) {
        try {
            // Cargar la clase Address usando reflexión
            Class<?> addressClass = Class.forName("geolocationAPI.Address");
            
            // Intentar parsear la dirección
            // Formato esperado: "Calle/Nombre, Número, Ciudad, País" o similar
            // Por simplicidad, intentamos extraer componentes básicos
            
            String[] parts = addressString.split(",");
            String street = "";
            int number = 0;
            String city = "";
            String country = "Spain";
            String postalCode = "";
            
            if (parts.length >= 1) {
                // Intentar extraer calle y número del primer componente
                String firstPart = parts[0].trim();
                // Buscar números en el string
                java.util.regex.Pattern numberPattern = java.util.regex.Pattern.compile("\\d+");
                java.util.regex.Matcher matcher = numberPattern.matcher(firstPart);
                if (matcher.find()) {
                    try {
                        number = Integer.parseInt(matcher.group());
                        street = firstPart.substring(0, matcher.start()).trim();
                    } catch (Exception e) {
                        street = firstPart;
                    }
                } else {
                    street = firstPart;
                }
            }
            
            if (parts.length >= 2) {
                city = parts[1].trim();
            }
            
            if (parts.length >= 3) {
                country = parts[2].trim();
            }
            
            // Creamos el objeto Address usando reflexión
            return addressClass.getConstructor(String.class, int.class, String.class, String.class, String.class)
                .newInstance(street, number, city, country, postalCode);
                
        } catch (Exception e) {
            // Si falla el parsing, retornar null
            return null;
        }
    }
}

