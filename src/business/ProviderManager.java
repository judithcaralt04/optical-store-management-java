package business;

import business.ProductForSale;
import business.Provider;
import persistence.ProviderDAO;
import persistence.ProviderJSONDAO;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager que maneja la lógica de negocio relacionada con los proveedores.
 * Esta clase actúa como "Information Expert" para las operaciones con proveedores.
 *
 */
public class ProviderManager {
    private final ProviderDAO providerDAO;
    
    /**
     * Constructor del manager de proveedores.
     * 
     * @param providerDAO el DAO de proveedores
     */
    public ProviderManager(ProviderDAO providerDAO) {
        this.providerDAO = providerDAO;
    }
    
    /**
     * Obtiene todos los proveedores.
     * 
     * @return lista con todos los proveedores
     */
    public List<Provider> getProviders() {
        return providerDAO.readAll();
    }
    
    /**
     * Obtiene un proveedor por su identificador.
     * 
     * @param id el identificador del proveedor
     * @return el proveedor si existe, null si no existe
     */
    public Provider getProviderById(int id) {
        return providerDAO.findById(id);
    }
    
    /**
     * Obtiene todos los productos que vende un proveedor específico.
     * 
     * @param id el identificador del proveedor
     * @return lista de productos en venta del proveedor
     */
    public List<ProductForSale> getProductsFromProvider(int id) {
        Provider proveedor = providerDAO.findById(id);
        
        if (proveedor == null) {
            return new ArrayList<>();
        }
        
        // Devolver todos los productos del proveedor (según el UML)
        return proveedor.getProductsForSale();
    }
    
    // Métodos adicionales para mantener compatibilidad con código existente
    public List<Provider> obtenerProveedor() {
        if (providerDAO instanceof ProviderJSONDAO) {
            return ((ProviderJSONDAO) providerDAO).getAllProviders();
        } else {
            return getProviders();
        }
    }
    
    public Provider obtenerProveedorPorId(int providerId) {
        return getProviderById(providerId);
    }
    
    public List<ProductForSale> obtenerProductoProveedor(int providerId) {
        Provider proveedor = providerDAO.findById(providerId);
        
        if (proveedor == null) {
            return new ArrayList<>();
        }
        
        // Devolver solo productos disponibles (con stock)
        return proveedor.getAvailableProducts();
    }
}

