package business;

/**
 * Representa un producto que está siendo vendido por un proveedor específico.
 * Contiene el ID del producto, precio de venta (sin IVA) y unidades en stock.
 */
public class ProductForSale {
    /** Porcentaje de IVA aplicado (21%). */
    private static final double VAT_PERCENTAGE = 0.21;
    /** Factor multiplicador para calcular precio con IVA. */
    private static final double VAT_MULTIPLIER = 1.0 + VAT_PERCENTAGE;
    /** Stock mínimo para considerar un producto disponible. */
    private static final int MIN_STOCK_AVAILABLE = 0;
    
    private final String productId;
    private final double precioVenta;
    private int unidadStock;
    
    /**
     * Construye un nuevo ProductForSale con los atributos especificados.
     * 
     * @param productId el identificador del producto
     * @param precioVenta el precio de venta sin IVA
     * @param unidadStock el número de unidades disponibles en stock
     */
    public ProductForSale(String productId, double precioVenta, int unidadStock) {
        this.productId = productId;
        this.precioVenta = precioVenta;
        this.unidadStock = unidadStock;
    }
    
    /**
     * Obtiene el identificador del producto.
     * 
     * @return el ID del producto
     */
    public String getProductId() {
        return productId;
    }
    
    /**
     * Obtiene el precio de venta sin IVA.
     * 
     * @return el precio de venta
     */
    public double getSellingPrice() {
        return precioVenta;
    }
    
    /**
     * Obtiene el número de unidades en stock.
     * 
     * @return las unidades en stock
     */
    public int getUnitsInStock() {
        return unidadStock;
    }
    
    /**
     * Calcula el precio de venta final incluyendo IVA (21%).
     * 
     * @return el precio con IVA
     */
    public double getPriceWithVAT() {
        return precioVenta * VAT_MULTIPLIER;
    }
    
    /**
     * Reduce el stock en la cantidad especificada.
     * 
     * @param quantity la cantidad a reducir
     */
    public void reduceStock(int quantity) {
        if (quantity > 0 && unidadStock >= quantity) {
            unidadStock -= quantity;
        }
    }
    
    /**
     * Comprueba si el producto está disponible (tiene stock).
     * 
     * @return true si unidades en stock > MIN_STOCK_AVAILABLE, false en caso contrario
     */
    public boolean isAvailable() {
        return unidadStock > MIN_STOCK_AVAILABLE;
    }
}

