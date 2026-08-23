package business;

/**
 * Representa un producto disponible en el marketplace.
 * Cada producto tiene un identificador único, nombre, marca y modelo.
 */
public class Product {
    private final String productoId;
    private final String productoNombre;
    private final String marca;
    private final String modelo;
    
    /**
     * Construye un nuevo Producto con los atributos especificados.
     * 
     * @param productoId el identificador único del producto
     * @param productoNombre el nombre del producto
     * @param marca la marca del producto
     * @param modelo el modelo del producto
     */
    public Product(String productoId, String productoNombre, String marca, String modelo) {
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.marca = marca;
        this.modelo = modelo;
    }
    
    /**
     * Obtiene el identificador único del producto.
     * 
     * @return el ID del producto
     */
    public String getProductId() {
        return productoId;
    }
    
    /**
     * Obtiene el nombre del producto.
     * 
     * @return el nombre del producto
     */
    public String getProductName() {
        return productoNombre;
    }
    
    /**
     * Obtiene la marca del producto.
     * 
     * @return la marca
     */
    public String getBrand() {
        return marca;
    }
    
    /**
     * Obtiene el modelo del producto.
     * 
     * @return el modelo
     */
    public String getModel() {
        return modelo;
    }
}

