package presentation;

import business.Client;
import business.CompanyClient;
import business.OnlineClient;
import business.PhoneNumber;
import business.Product;
import business.Provider;
import java.util.List;
import java.util.Scanner;

/**
 * Clase que maneja la interfaz de usuario.
 * Según el UML, esta clase se encarga de mostrar información y pedir datos al usuario.
 */
public class MainUI {
    private final Scanner scanner;
    
    /**
     * Constructor de MainUI.
     * 
     * @param scanner el scanner para entrada de usuario
     */
    public MainUI(Scanner scanner) {
        this.scanner = scanner;
    }
    
    /**
     * Muestra el menú de usuario.
     */
    public void showUserMenu() {
        System.out.println();
        System.out.println(" 1) User profile");
        System.out.println(" 2) Find products by name");
        System.out.println(" 3) Find products by provider");
        System.out.println(" 4) Shopping cart");
        System.out.println(" 0) Logout");
        System.out.println();
    }
    
    /**
     * Muestra el menú del programa (login/registro).
     */
    public void showProgramMenu() {
        System.out.println();
        System.out.println(" 1) Login");
        System.out.println(" 2) Sign up");
        System.out.println();
        System.out.println(" 0) Exit");
        System.out.println();
    }
    
    /**
     * Imprime el perfil del usuario.
     * 
     * @param info lista con información del perfil (cliente, teléfonos, ventas)
     */
    public void printUserProfile(List<?> info) {
        // Este método se puede usar para mostrar información del perfil
        // La implementación actual está en ControladorPerfilCliente
    }
    
    /**
     * Imprime una lista de productos.
     * 
     * @param list lista de productos
     */
    public void printProductList(List<Product> list) {
        for (int i = 0; i < list.size(); i++) {
            Product producto = list.get(i);
            System.out.println((i + 1) + ") " + producto.getProductId() + " - " + producto.getProductName());
        }
    }
    
    /**
     * Imprime una lista de proveedores.
     * 
     * @param list lista de proveedores
     */
    public void printProviderList(List<Provider> list) {
        for (int i = 0; i < list.size(); i++) {
            Provider proveedor = list.get(i);
            System.out.println((i + 1) + ") " + proveedor.getCompanyName());
        }
    }
    
    /**
     * Imprime el carrito de compra.
     * 
     * @param list lista de items del carrito
     */
    public void printShoppingCart(List<?> list) {
        // Este método se puede usar para mostrar el carrito
        // La implementación actual está en ControladorCarrito
    }
    
    /**
     * Pide una cadena de texto al usuario.
     * 
     * @param msg el mensaje a mostrar
     * @return la cadena introducida por el usuario
     */
    public String askForString(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }
    
    /**
     * Pide un entero al usuario.
     * 
     * @param msg el mensaje a mostrar
     * @return el entero introducido por el usuario
     */
    public int askForInt(String msg) {
        System.out.print(msg);
        int value = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer
        return value;
    }
    
    /**
     * Pide un entero al usuario con validación.
     * Repite la petición hasta que se introduzca un número válido.
     * 
     * @param msg el mensaje a mostrar
     * @return el entero introducido por el usuario
     */
    public int askForIntValidated(String msg) {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }
    
    /**
     * Pide un entero al usuario con validación y rango.
     * Repite la petición hasta que se introduzca un número válido dentro del rango.
     * 
     * @param msg el mensaje a mostrar
     * @param min el valor mínimo permitido (inclusive)
     * @param max el valor máximo permitido (inclusive)
     * @return el entero introducido por el usuario
     */
    public int askForIntInRange(String msg, int min, int max) {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Error: Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }
    
    /**
     * Pide confirmación al usuario (sí/no).
     * 
     * @param msg el mensaje a mostrar
     * @return true si el usuario confirma, false en caso contrario
     */
    public boolean askForConfirmation(String msg) {
        System.out.print(msg);
        String respuesta = scanner.nextLine().toLowerCase();
        return respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí") || 
               respuesta.equals("yes") || respuesta.equals("y");
    }
    
    /**
     * Pide un número de teléfono al usuario.
     * 
     * @return el número de teléfono introducido
     */
    public PhoneNumber askForPhone() {
        System.out.print("Country prefix (e.g.: 34): ");
        String prefijo = scanner.nextLine().trim();
        if (prefijo.matches("\\d{2}")) {
            prefijo = "+" + prefijo;
        }
        
        System.out.print("Number: ");
        String numero = scanner.nextLine();
        
        return new PhoneNumber(prefijo, numero);
    }
    
    /**
     * Pide un número de teléfono al usuario con validación.
     * Valida que el prefijo tenga 2 dígitos y el número solo contenga dígitos.
     * 
     * @return el número de teléfono introducido
     */
    public PhoneNumber askForPhoneValidated() {
        String prefijo;
        while (true) {
            System.out.print("Country prefix (e.g.: 34): ");
            prefijo = scanner.nextLine().trim();
            
            // Validar que solo contenga dígitos
            if (!prefijo.matches("\\d+")) {
                System.out.println("Error: The country prefix must contain only numbers.");
                continue;
            }
            
            // Validar que tenga exactamente 2 dígitos
            if (prefijo.length() != 2) {
                System.out.println("Error: The country prefix must have exactly 2 digits.");
                continue;
            }
            
            prefijo = "+" + prefijo;
            break;
        }
        
        String numero;
        while (true) {
            System.out.print("Number: ");
            numero = scanner.nextLine().trim();
            
            // Validar que no esté vacío
            if (numero.isEmpty()) {
                System.out.println("Error: The phone number cannot be empty.");
                continue;
            }
            
            // Validar que solo contenga dígitos
            if (!numero.matches("\\d+")) {
                System.out.println("Error: The phone number must contain only numbers.");
                continue;
            }
            
            break;
        }
        
        return new PhoneNumber(prefijo, numero);
    }
    
    /**
     * Pide el nombre completo al usuario.
     * 
     * @return el nombre completo introducido
     */
    public String askForFullName() {
        System.out.print("Enter your full name: ");
        return scanner.nextLine();
    }
    
    /**
     * Pide el nombre completo al usuario con validación.
     * Valida que el nombre contenga al menos una letra y no esté vacío.
     * 
     * @return el nombre completo introducido
     */
    public String askForFullNameValidated() {
        String nombreCompleto;
        while (true) {
            System.out.print("Enter your full name: ");
            nombreCompleto = scanner.nextLine().trim();
            
            // Validar que no esté vacío
            if (nombreCompleto.isEmpty()) {
                System.out.println("Error: The name cannot be empty.");
                continue;
            }
            
            // Validar que contenga al menos una letra (no solo números o caracteres especiales)
            if (!nombreCompleto.matches(".*[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ].*")) {
                System.out.println("Error: The name must contain at least one letter.");
                continue;
            }
            
            break;
        }
        return nombreCompleto;
    }
    
    /**
     * Pide una dirección de envío al usuario.
     * 
     * @return la dirección de envío introducida
     */
    public String askForShippingAddress() {
        System.out.print("Enter shipping address: ");
        return scanner.nextLine().trim();
    }
    
    /**
     * Pide un correo electrónico al usuario con validación básica.
     * 
     * @return el correo electrónico introducido
     */
    public String askForEmail() {
        String email;
        while (true) {
            System.out.print("Enter email address: ");
            email = scanner.nextLine().trim();
            
            // Validación básica de email
            if (email.isEmpty()) {
                System.out.println("Error: The email cannot be empty.");
                continue;
            }
            
            if (!email.contains("@") || !email.contains(".")) {
                System.out.println("Error: Please enter a valid email address.");
                continue;
            }
            
            break;
        }
        return email;
    }
    
    /**
     * Pide un NIF (número de identificación fiscal) al usuario.
     * 
     * @return el NIF introducido
     */
    public String askForNIF() {
        System.out.print("Enter NIF (tax identification number): ");
        return scanner.nextLine().trim();
    }
    
    /**
     * Pide el nombre de la persona de contacto al usuario.
     * 
     * @return el nombre de contacto introducido
     */
    public String askForContactPersonName() {
        System.out.print("Enter contact person name: ");
        return scanner.nextLine().trim();
    }
    
    /**
     * Pide una dirección de facturación al usuario.
     * 
     * @return la dirección de facturación introducida
     */
    public String askForBillingAddress() {
        System.out.print("Enter billing address: ");
        return scanner.nextLine().trim();
    }
}
