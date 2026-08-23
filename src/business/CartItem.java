package business;

/**
 * Representa un item en el carrito de compra.
 * Cada item contiene un producto en venta y su cantidad.
 * Calcula el precio final según el tipo de producto (Item) y cliente.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public class CartItem {
    private final ProductForSale productForSale;
    private final Provider provider;
    private int cantidad;
    
    /**
     * Construye un nuevo CartItem con el producto y proveedor especificados.
     * 
     * @param productForSale el producto en venta
     * @param provider el proveedor que vende este producto
     */
    public CartItem(ProductForSale productForSale, Provider provider) {
        this.productForSale = productForSale;
        this.provider = provider;
        this.cantidad = 1;
    }
    
    /**
     * Obtiene el producto en venta.
     * 
     * @return el producto en venta
     */
    public ProductForSale getProductForSale() {
        return productForSale;
    }
    
    /**
     * Obtiene el proveedor.
     * 
     * @return el proveedor
     */
    public Provider getProvider() {
        return provider;
    }
    
    /**
     * Obtiene la cantidad de este item en el carrito.
     * 
     * @return la cantidad
     */
    public int getQuantity() {
        return cantidad;
    }
    
    /**
     * Establece la cantidad de este item.
     * 
     * @param quantity la nueva cantidad (debe ser positiva)
     */
    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.cantidad = quantity;
        }
    }
    
    /**
     * Calcula el precio total para este item del carrito.
     * Si el producto es un Item (ContactLens, Consumable, Service), usa su método calculateFinalPrice.
     * Si no, usa el cálculo estándar con IVA del 21%.
     * 
     * @param product el Product asociado (puede ser un Item)
     * @param client el cliente que está comprando (puede ser null)
     * @return el precio total para la cantidad especificada
     */
    public double getTotalPrice(Product product, Client client) {
        double basePrice = productForSale.getSellingPrice();
        double finalPrice;
        
        // Si el producto es un Item, verificamos si el cliente está exento de IVA
        if (product instanceof Item) {
            Item item = (Item) product;
            
            // ContactLens siempre tiene IVA del 10%, incluso para CompanyClient exento
            if (product instanceof ContactLens) {
                finalPrice = item.calculateFinalPrice(basePrice, client);
            } 
            // Para Consumable y Service, verificamos exención de IVA
            else if (client instanceof CompanyClient) {
                CompanyClient companyClient = (CompanyClient) client;
                if (companyClient.isExemptFromVAT()) {
                    // Si está exento, calculamos sin IVA
                    if (product instanceof Consumable) {
                        Consumable consumable = (Consumable) product;
                        double priceWithoutVAT = basePrice;
                        // Verificamos descuento por caducidad
                        java.util.Calendar expirationCal = java.util.Calendar.getInstance();
                        expirationCal.setTime(consumable.getExpirationDate());
                        java.util.Calendar thresholdCal = java.util.Calendar.getInstance();
                        thresholdCal.add(java.util.Calendar.MONTH, 3);
                        if (expirationCal.before(thresholdCal)) {
                            // Descuento del 40% sobre precio base (sin IVA)
                            priceWithoutVAT = priceWithoutVAT * (1.0 - 0.40);
                        }
                        finalPrice = priceWithoutVAT;
                    } else if (product instanceof Service) {
                        // Para servicios, precio = precioHora * horas (sin IVA)
                        Service service = (Service) product;
                        finalPrice = basePrice * service.getHoursDuration();
                    } else {
                        // Otros Items sin IVA
                        finalPrice = basePrice;
                    }
                } else {
                    // No está exento, usar cálculo normal
                    finalPrice = item.calculateFinalPrice(basePrice, client);
                }
            } else {
                // Cliente normal, usar cálculo estándar
                finalPrice = item.calculateFinalPrice(basePrice, client);
            }
        } else {
            // Para productos normales, usar el cálculo estándar
            // Pero considerar si el cliente es CompanyClient y está exento de IVA
            if (client instanceof CompanyClient) {
                CompanyClient companyClient = (CompanyClient) client;
                if (companyClient.isExemptFromVAT()) {
                    // Sin IVA para empresas exentas
                    finalPrice = basePrice;
                } else {
                    // IVA estándar del 21%
                    finalPrice = productForSale.getPriceWithVAT();
                }
            } else {
                // IVA estándar del 21%
                finalPrice = productForSale.getPriceWithVAT();
            }
        }
        
        return finalPrice * cantidad;
    }
    
    /**
     * Calcula el precio total para este item del carrito (método legacy para compatibilidad).
     * Usa el cálculo estándar con IVA del 21%.
     * 
     * @return el precio total
     */
    public double getTotalPrice() {
        return productForSale.getPriceWithVAT() * cantidad;
    }
}

