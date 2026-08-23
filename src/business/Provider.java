package business;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un proveedor (suministrador) en el marketplace.
 * Cada proveedor tiene un identificador único y ofrece múltiples productos en venta.
 */
public class Provider {
    private final int proveedorId;
    private final String nombreComercial;
    private final String cif;
    private final String nombreContacto;
    private final String telefono;
    private final String email;
    private final List<ProductForSale> productsForSale;
    
    /**
     * Construye un nuevo Proveedor con los atributos especificados.
     * 
     * @param proveedorId el identificador único del proveedor
     * @param nombreComercial el nombre de la empresa
     * @param cif el código de identificación fiscal
     * @param nombreContacto el nombre de la persona de contacto
     * @param telefono el número de teléfono
     * @param email la dirección de correo electrónico
     * @param productsForSale la lista de productos en venta
     */
    public Provider(int proveedorId, String nombreComercial, String cif, String nombreContacto, 
                   String telefono, String email, List<ProductForSale> productsForSale) {
        this.proveedorId = proveedorId;
        this.nombreComercial = nombreComercial;
        this.cif = cif;
        this.nombreContacto = nombreContacto;
        this.telefono = telefono;
        this.email = email;
        this.productsForSale = new ArrayList<>(productsForSale);
    }
    
    /**
     * Obtiene el identificador único del proveedor.
     * 
     * @return el ID del proveedor
     */
    public int getProviderId() {
        return proveedorId;
    }
    
    /**
     * Obtiene el nombre de la empresa.
     * 
     * @return el nombre de la empresa
     */
    public String getCompanyName() {
        return nombreComercial;
    }
    
    /**
     * Obtiene el CIF (código de identificación fiscal).
     * 
     * @return el CIF
     */
    public String getCif() {
        return cif;
    }
    
    /**
     * Obtiene el nombre de la persona de contacto.
     * 
     * @return el nombre de contacto
     */
    public String getContactName() {
        return nombreContacto;
    }
    
    /**
     * Obtiene el número de teléfono.
     * 
     * @return el número de teléfono
     */
    public String getPhone() {
        return telefono;
    }
    
    /**
     * Obtiene la dirección de correo electrónico.
     * 
     * @return el email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Obtiene la lista de productos en venta.
     * 
     * @return una copia de la lista de productos en venta
     */
    public List<ProductForSale> getProductsForSale() {
        return new ArrayList<>(productsForSale);
    }
    
    /**
     * Obtiene los productos en venta que están disponibles (tienen stock).
     * 
     * @return lista de productos disponibles
     */
    public List<ProductForSale> getAvailableProducts() {
        List<ProductForSale> available = new ArrayList<>();
        for (ProductForSale product : productsForSale) {
            if (product.isAvailable()) {
                available.add(product);
            }
        }
        return available;
    }
    
    /**
     * Busca un producto en venta por su identificador de producto.
     * 
     * @param productId el ID del producto a buscar
     * @return el ProductForSale si se encuentra, null si no existe
     */
    public ProductForSale findProductForSale(String productId) {
        for (ProductForSale product : productsForSale) {
            if (product.getProductId() != null && productId != null &&
                product.getProductId().trim().equalsIgnoreCase(productId.trim())) {
                return product;
            }
        }
        return null;
    }
}

