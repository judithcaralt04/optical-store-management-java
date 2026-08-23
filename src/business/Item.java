package business;

/**
 * Clase abstracta que representa un item (producto o servicio) en el marketplace.
 * Todos los items extienden de Product y añaden funcionalidades específicas.
 * Esta clase define el contrato para calcular el precio final según el tipo de cliente.
 * 
 * @author Aras y Judith - Nude Eye Project
 */
public abstract class Item extends Product {
    
    /**
     * Construye un nuevo Item con los atributos básicos de un Product.
     * 
     * @param itemId el identificador único del item
     * @param itemName el nombre del item
     * @param brand la marca del item (puede ser null para servicios)
     * @param model el modelo del item (puede ser null para servicios)
     */
    public Item(String itemId, String itemName, String brand, String model) {
        super(itemId, itemName, brand, model);
    }
    
    /**
     * Calcula el precio final del item según el tipo de cliente.
     * Este método debe ser implementado por cada subclase para aplicar
     * las reglas de negocio específicas de cada tipo de item.
     * 
     * @param basePrice el precio base del producto (selling_price del proveedor)
     * @param client el cliente que está comprando
     * @return el precio final calculado según las reglas del item y cliente
     */
    public abstract double calculateFinalPrice(double basePrice, Client client);
}

