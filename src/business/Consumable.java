package business;

import java.util.Date;
import java.util.Calendar;

/**
 * Representa un producto fungible (consumible) en el marketplace.
 * Los productos fungibles tienen un volumen, fecha de caducidad y pueden contener conservantes.
 * Si la fecha de caducidad es inferior a 3 meses, se aplica un descuento del 40%.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class Consumable extends Item {
    /** Porcentaje de IVA estándar (21%). */
    private static final double STANDARD_VAT = 0.21;
    /** Factor multiplicador para calcular precio con IVA. */
    private static final double STANDARD_VAT_MULTIPLIER = 1.0 + STANDARD_VAT;
    /** Descuento aplicado cuando la caducidad es menor a 3 meses (40%). */
    private static final double EXPIRATION_DISCOUNT = 0.40;
    /** Meses antes de la caducidad para aplicar el descuento. */
    private static final int EXPIRATION_MONTHS_THRESHOLD = 3;
    
    private final double volume;
    private final Date expirationDate;
    private final boolean hasPreservatives;
    
    /**
     * Construye un nuevo producto fungible.
     * 
     * @param productId el identificador único del producto
     * @param productName el nombre del producto
     * @param brand la marca del producto
     * @param model el modelo del producto
     * @param volume el volumen en mililitros
     * @param expirationDate la fecha de caducidad
     * @param hasPreservatives true si contiene conservantes, false en caso contrario
     */
    public Consumable(String productId, String productName, String brand, String model,
                     double volume, Date expirationDate, boolean hasPreservatives) {
        super(productId, productName, brand, model);
        this.volume = volume;
        this.expirationDate = expirationDate;
        this.hasPreservatives = hasPreservatives;
    }
    
    /**
     * Obtiene el volumen del producto en mililitros.
     * 
     * @return el volumen
     */
    public double getVolume() {
        return volume;
    }
    
    /**
     * Obtiene la fecha de caducidad del producto.
     * 
     * @return la fecha de caducidad
     */
    public Date getExpirationDate() {
        return new Date(expirationDate.getTime());
    }
    
    /**
     * Indica si el producto contiene conservantes.
     * 
     * @return true si contiene conservantes, false en caso contrario
     */
    public boolean hasPreservatives() {
        return hasPreservatives;
    }
    
    /**
     * Calcula el precio final del producto fungible.
     * Se aplica IVA del 21% y, si la caducidad es menor a 3 meses, un descuento del 40%.
     * 
     * @param basePrice el precio base del producto
     * @param client el cliente (no se usa en este cálculo)
     * @return el precio final con IVA y posible descuento
     */
    @Override
    public double calculateFinalPrice(double basePrice, Client client) {
        // Calculamos precio con IVA
        double priceWithVAT = basePrice * STANDARD_VAT_MULTIPLIER;
        
        // Verificamos si la caducidad es menor a 3 meses
        if (isExpiringSoon()) {
            // Aplicamos descuento del 40% sobre el precio con IVA
            priceWithVAT = priceWithVAT * (1.0 - EXPIRATION_DISCOUNT);
        }
        
        return priceWithVAT;
    }
    
    /**
     * Verifica si el producto está próximo a caducar (menos de 3 meses).
     * 
     * @return true si caduca en menos de 3 meses, false en caso contrario
     */
    private boolean isExpiringSoon() {
        Calendar expirationCal = Calendar.getInstance();
        expirationCal.setTime(expirationDate);
        
        Calendar nowCal = Calendar.getInstance();
        Calendar thresholdCal = Calendar.getInstance();
        thresholdCal.add(Calendar.MONTH, EXPIRATION_MONTHS_THRESHOLD);
        
        return expirationCal.before(thresholdCal);
    }
}

