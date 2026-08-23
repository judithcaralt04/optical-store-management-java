package business;

/**
 * Representa unas lentes de contacto en el marketplace.
 * Las lentes de contacto son productos sanitarios con IVA del 10%.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class ContactLens extends Item {
    /** Porcentaje de IVA para lentes de contacto (10%). */
    private static final double CONTACT_LENS_VAT = 0.10;
    /** Factor multiplicador para calcular precio con IVA. */
    private static final double CONTACT_LENS_VAT_MULTIPLIER = 1.0 + CONTACT_LENS_VAT;
    
    private final double graduation;
    private final int numberOfLenses;
    private final boolean hasUVFilter;
    
    /**
     * Construye unas nuevas lentes de contacto.
     * 
     * @param productId el identificador único del producto
     * @param productName el nombre del producto
     * @param brand la marca de las lentes
     * @param model el modelo de las lentes
     * @param graduation la graduación (entre +8.00 y -12.00)
     * @param numberOfLenses el número de lentes en el paquete
     * @param hasUVFilter true si tienen filtro UV, false en caso contrario
     */
    public ContactLens(String productId, String productName, String brand, String model,
                       double graduation, int numberOfLenses, boolean hasUVFilter) {
        super(productId, productName, brand, model);
        this.graduation = graduation;
        this.numberOfLenses = numberOfLenses;
        this.hasUVFilter = hasUVFilter;
    }
    
    /**
     * Obtiene la graduación de las lentes.
     * 
     * @return la graduación
     */
    public double getGraduation() {
        return graduation;
    }
    
    /**
     * Obtiene el número de lentes en el paquete.
     * 
     * @return el número de lentes
     */
    public int getNumberOfLenses() {
        return numberOfLenses;
    }
    
    /**
     * Indica si las lentes tienen filtro UV.
     * 
     * @return true si tienen filtro UV, false en caso contrario
     */
    public boolean hasUVFilter() {
        return hasUVFilter;
    }
    
    /**
     * Calcula el precio final de las lentes de contacto.
     * Las lentes de contacto tienen un IVA del 10% independientemente del tipo de cliente.
     * 
     * @param basePrice el precio base del producto
     * @param client el cliente (no se usa en este cálculo)
     * @return el precio final con IVA del 10%
     */
    @Override
    public double calculateFinalPrice(double basePrice, Client client) {
        return basePrice * CONTACT_LENS_VAT_MULTIPLIER;
    }
}

