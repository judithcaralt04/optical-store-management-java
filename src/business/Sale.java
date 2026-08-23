package business;

/**
 * Representa una transacción de venta.
 * Cada venta representa un producto comprado por un cliente en un momento específico.
 */
public class Sale {
    private final int clientId;
    private final String productId;
    private final double precioPagado;
    private final long fechaCompra;
    
    /**
     * Construye una nueva Venta con los atributos especificados.
     * 
     * @param clientId el ID del cliente que realizó la compra
     * @param productId el ID del producto comprado
     * @param precioPagado el precio pagado (incluyendo IVA)
     * @param fechaCompra la fecha de compra como timestamp Unix
     */
    public Sale(int clientId, String productId, double precioPagado, long fechaCompra) {
        this.clientId = clientId;
        this.productId = productId;
        this.precioPagado = precioPagado;
        this.fechaCompra = fechaCompra;
    }
    
    /**
     * Obtiene el ID del cliente.
     * 
     * @return el ID del cliente
     */
    public int getClientId() {
        return clientId;
    }
    
    /**
     * Obtiene el ID del producto.
     * 
     * @return el ID del producto
     */
    public String getProductId() {
        return productId;
    }
    
    /**
     * Obtiene el precio pagado.
     * 
     * @return el precio pagado
     */
    public double getPricePaid() {
        return precioPagado;
    }
    
    /**
     * Obtiene la fecha de compra como timestamp Unix.
     * 
     * @return la fecha de compra
     */
    public long getPurchaseDate() {
        return fechaCompra;
    }
}

