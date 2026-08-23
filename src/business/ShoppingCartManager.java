package business;

import business.CartItem;
import business.ProductForSale;
import business.Provider;
import business.ShoppingCart;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager que maneja la lógica de negocio relacionada con el carrito de compra.
 * Esta clase actúa como "Information Expert" para las operaciones del carrito.
 *
 */
public class ShoppingCartManager {
    private final Map<Integer, ShoppingCart> carts;
    
    /**
     * Constructor del manager del carrito.
     */
    public ShoppingCartManager() {
        this.carts = new HashMap<>();
    }
    
    /**
     * Obtiene el carrito de un cliente.
     * Si no existe, crea uno nuevo.
     * 
     * @param clientId el identificador del cliente
     * @return el carrito del cliente
     */
    public ShoppingCart getCart(int clientId) {
        if (!carts.containsKey(clientId)) {
            carts.put(clientId, new ShoppingCart());
        }
        return carts.get(clientId);
    }
    
    /**
     * Añade un item al carrito de un cliente.
     * 
     * @param clientId el identificador del cliente
     * @param item el item a añadir
     */
    public void addItem(int clientId, CartItem item) {
        ShoppingCart cart = getCart(clientId);
        cart.addItem(item.getProductForSale(), item.getProvider());
    }
    
    /**
     * Elimina un item del carrito de un cliente.
     * 
     * @param clientId el identificador del cliente
     * @param item el item a eliminar
     */
    public void removeItem(int clientId, CartItem item) {
        ShoppingCart cart = getCart(clientId);
        List<CartItem> items = cart.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(item)) {
                cart.removeItem(i);
                break;
            }
        }
    }
    
    /**
     * Realiza el checkout del carrito de un cliente.
     * Limpia el carrito después del checkout.
     * 
     * @param clientId el identificador del cliente
     */
    public void checkout(int clientId) {
        ShoppingCart cart = getCart(clientId);
        cart.clear();
    }
    
    // Métodos adicionales para mantener compatibilidad con código existente
    public ShoppingCart crearCarrito() {
        return new ShoppingCart();
    }
    
    public void eliminarItem(ShoppingCart carrito, int index) {
        carrito.removeItem(index);
    }
    
    public double calcularTotal(ShoppingCart carrito) {
        return carrito.getTotalPrice();
    }
    
    public void agregarProducto(ShoppingCart carrito, ProductForSale productoEnVenta, Provider proveedor) {
        carrito.addItem(productoEnVenta, proveedor);
    }
    
    public List<CartItem> obtenerItems(ShoppingCart carrito) {
        return carrito.getItems();
    }
    
    public boolean estaVacio(ShoppingCart carrito) {
        return carrito.isEmpty();
    }
    
    public void limpiarCarrito(ShoppingCart carrito) {
        carrito.clear();
    }
}

