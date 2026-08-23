package business;

/**
 * Representa un servicio ofrecido por las ópticas.
 * Los servicios solo se pueden vender a clientes presenciales (no online ni empresas).
 * El precio se calcula multiplicando el precio por hora por las horas de duración.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class Service extends Item {
    /** Porcentaje de IVA estándar (21%). */
    private static final double STANDARD_VAT = 0.21;
    /** Factor multiplicador para calcular precio con IVA. */
    private static final double STANDARD_VAT_MULTIPLIER = 1.0 + STANDARD_VAT;
    
    private final String serviceId;
    private final String serviceName;
    private final double hoursDuration;
    
    /**
     * Construye un nuevo servicio.
     * 
     * @param serviceId el identificador único del servicio
     * @param serviceName el nombre del servicio
     * @param hoursDuration la duración del servicio en horas
     */
    public Service(String serviceId, String serviceName, double hoursDuration) {
        // Los servicios no tienen marca ni modelo
        super(serviceId, serviceName, null, null);
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.hoursDuration = hoursDuration;
    }
    
    /**
     * Obtiene el identificador único del servicio.
     * 
     * @return el ID del servicio
     */
    public String getServiceId() {
        return serviceId;
    }
    
    /**
     * Obtiene el nombre del servicio.
     * 
     * @return el nombre del servicio
     */
    public String getServiceName() {
        return serviceName;
    }
    
    /**
     * Obtiene la duración del servicio en horas.
     * 
     * @return las horas de duración
     */
    public double getHoursDuration() {
        return hoursDuration;
    }
    
    /**
     * Calcula el precio final del servicio.
     * El precio base es el precio por hora, que se multiplica por las horas de duración.
     * Luego se aplica el IVA del 21%.
     * 
     * @param pricePerHour el precio por hora del servicio
     * @param client el cliente (no se usa en este cálculo)
     * @return el precio final con IVA
     */
    @Override
    public double calculateFinalPrice(double pricePerHour, Client client) {
        // Calculamos precio total (precio por hora * horas de duración)
        double totalPrice = pricePerHour * hoursDuration;
        
        // Aplicamos IVA del 21%
        return totalPrice * STANDARD_VAT_MULTIPLIER;
    }
    
    /**
     * Verifica si el servicio puede ser vendido a un cliente específico.
     * Los servicios solo se pueden vender a clientes presenciales (Client base),
     * no a clientes online ni empresas.
     * 
     * @param client el cliente a verificar
     * @return true si puede ser vendido al cliente, false en caso contrario
     */
    public boolean canBeSoldTo(Client client) {
        // Los servicios solo se pueden vender a clientes presenciales (Client base)
        // No a OnlineClient ni CompanyClient
        return client != null && !(client instanceof OnlineClient) && !(client instanceof CompanyClient);
    }
}

