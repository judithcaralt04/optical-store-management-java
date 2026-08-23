package presentation;

import business.Client;
import business.CompanyClient;
import business.OnlineClient;
import business.ProductManager;
import business.ProviderManager;
import business.ClientManager;
import business.SaleManager;
import business.ShoppingCartManager;
import business.ShoppingCart;
import business.PhoneNumber;
import business.Product;
import business.ProductForSale;
import business.Provider;
import business.CartItem;
import business.Sale;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Controller principal que gestiona toda la lógica de presentación de la aplicación.
 * Coordina los menús y las interacciones con el usuario.
 */
public class Controller {

    private final SaleManager saleManager;
    private final ShoppingCartManager shoppingCartManager;
    private final ProductManager productManager;
    private final ProviderManager providerManager;
    private final ClientManager clientManager;
    private final MainUI mainUI;
    private final Scanner scanner;
    private Client clienteActual;
    private ShoppingCart carritoActual;

    /**
     * Constructor de Controller.
     * Inicializa todos los managers necesarios.
     *
     * @param productManager el manager de productos
     * @param providerManager el manager de proveedores
     * @param clientManager el manager de clientes
     * @param saleManager el manager de ventas
     * @param shoppingCartManager el manager del carrito
     */
    public Controller(ProductManager productManager, ProviderManager providerManager,
                  ClientManager clientManager, SaleManager saleManager, ShoppingCartManager shoppingCartManager) {
        this.scanner = new Scanner(System.in);
        this.saleManager = saleManager;
        this.shoppingCartManager = shoppingCartManager;
        this.productManager = productManager;
        this.providerManager = providerManager;
        this.clientManager = clientManager;
        this.mainUI = new MainUI(scanner);

        this.clienteActual = null;
        this.carritoActual = shoppingCartManager.crearCarrito();
    }

    /**
     * Método principal que inicia la aplicación.
     */
    public void run() {
        boolean continuar = true;
        while (continuar) {
            if (clienteActual == null) {
                continuar = mostrarMenuLogin();
                if (!continuar) {
                    break; // Salir del loop si el usuario eligió Exit
                }
            } else {
                mostrarMenuUsuario();
            }
        }

        System.out.println();
        System.out.println("We hope to see you again!");
        scanner.close();
    }

    /**
     * Muestra el menú principal (sin login).
     * Permite buscar productos y proveedores sin estar autenticado.
     */
    public void mostrarMenuPrincipal() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Search products by name");
        System.out.println("2. Search products by brand");
        System.out.println("3. View product details");
        System.out.println("4. Search products by provider");
        System.out.println("5. View provider product");
        System.out.println("6. Login / Sign up");
        System.out.println("0. Exit");

        int opcion = mainUI.askForIntInRange("\nSelect an option: ", 0, 6);

        switch (opcion) {
            case 1:
                buscarPorNombre();
                break;
            case 2:
                buscarPorMarca();
                break;
            case 3:
                verDetalleProducto();
                break;
            case 4:
                buscarProductoDeProveedor();
                break;
            case 5:
                verProductoDeProveedor();
                break;
            case 6:
                mostrarMenuLogin();
                break;
            case 0:
                System.out.println();
                System.out.println("We hope to see you again!");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    /**
     * Muestra el menú de login y registro.
     * 
     * @return false si el usuario quiere salir, true para continuar
     */
    public boolean mostrarMenuLogin() {
        System.out.println();
        System.out.println(" 1) Login");
        System.out.println(" 2) Sign up");
        System.out.println();
        System.out.println(" 0) Exit");
        System.out.println();

        int opcion = mainUI.askForIntInRange("Choose an option: ", 0, 2);

        switch (opcion) {
            case 1:
                clienteActual = loginCliente();
                if (clienteActual != null) {
                    carritoActual = shoppingCartManager.crearCarrito();
                }
                break;
            case 2:
                clienteActual = registrarCliente();
                if (clienteActual != null) {
                    carritoActual = shoppingCartManager.crearCarrito();
                }
                break;
            case 0:
                return false;
        }
        return true;
    }
    
    /**
     * Pide confirmación al usuario (sí/no) con validación.
     * 
     * @param msg el mensaje a mostrar
     * @return true si el usuario confirma, false si niega
     */
    private boolean pedirConfirmacion(String msg) {
        while (true) {
            System.out.print(msg);
            String respuesta = scanner.nextLine().toLowerCase().trim();
            
            // Validamos respuestas afirmativas
            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí") || 
                respuesta.equals("yes") || respuesta.equals("y")) {
                return true;
            }
            // Validamos respuestas negativas
            else if (respuesta.equals("n") || respuesta.equals("no")) {
                return false;
            }
            // Si no es ninguna respuesta válida, mostramos error
            else {
                System.out.println("Error: Please enter 'y' for yes or 'n' for no.");
            }
        }
    }
    
    /**
     * Pide un número entero al usuario con validación.
     * 
     * @param msg el mensaje a mostrar
     * @return el número entero introducido
     */
    private int pedirEntero(String msg) {
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
     * Pide un número entero al usuario con validación y rango.
     * 
     * @param msg el mensaje a mostrar
     * @param min el valor mínimo permitido (inclusive)
     * @param max el valor máximo permitido (inclusive)
     * @return el número entero introducido
     */
    private int pedirEnteroEnRango(String msg, int min, int max) {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine().trim();
            try {
                int opcion = Integer.parseInt(input);
                if (opcion >= min && opcion <= max) {
                    return opcion;
                } else {
                    System.out.println("Error: Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    /**
     * Muestra el menú del usuario autenticado.
     * Incluye todas las funcionalidades disponibles para usuarios logueados:
     * perfil, búsqueda de productos, búsqueda por proveedor y carrito de compra.
     * Si el usuario no está autenticado, redirige al menú de login.
     */
    private void mostrarMenuUsuario() {
        if (clienteActual == null) {
            mostrarMenuLogin();
            return;
        }

        if (carritoActual == null) {
            carritoActual = shoppingCartManager.crearCarrito();
        }

        System.out.println();
        System.out.println("Welcome " + clienteActual.getFullName() + "!");
        System.out.println();
        System.out.println();
        System.out.println(" 1) User profile");
        System.out.println(" 2) Find products by name");
        System.out.println(" 3) Find products by provider");
        System.out.println(" 4) Shopping cart");
        System.out.println(" 0) Logout");
        System.out.println();

        int opcion = mainUI.askForIntInRange("Choose an option: ", 0, 4);

        switch (opcion) {
            case 1:
                verPerfilCliente(clienteActual.getClientId());
                break;
            case 2:
                buscarPorNombre(carritoActual, shoppingCartManager);
                break;
            case 3:
                buscarProductoDeProveedor(carritoActual, shoppingCartManager);
                break;
            case 4:
                // Menú del carrito
                boolean volverCarrito = false;
                while (!volverCarrito) {
                    verCarrito(carritoActual);
                    if (!carritoActual.isEmpty()) {
                        System.out.println();
                        System.out.println("1) Proceed with purchase");
                        System.out.println("2) Remove product from cart");
                        System.out.println("0) Back");
                        System.out.println();
                        
                        int opcionCarrito = pedirEnteroEnRango("Choose an option: ", 0, 2);
                        
                        switch (opcionCarrito) {
                            case 1:
                                // Mostramos resumen de compra antes de confirmar
                                mostrarResumenCompra(carritoActual);
                                if (pedirConfirmacion("Do you want to proceed with the purchase? ")) {
                                    try {
                                        // Calculamos el precio total final
                                        Object geolocationHelper = loadGeolocationHelper();
                                        double totalPrice = carritoActual.getTotalPrice(productManager, clienteActual, geolocationHelper);
                                        
                                        // Usamos el nuevo método que acepta ProductManager y Client
                                        saleManager.crearVenta(clienteActual.getClientId(), carritoActual,
                                                              productManager, clienteActual);
                                        System.out.println("Thank you for your purchase!");
                                        System.out.println("Total amount paid: " + String.format("%.2f", totalPrice).replace(".", ",") + "€");
                                        carritoActual.clear();
                                        System.out.println();
                                        volverCarrito = true;
                                    } catch (IOException e) {
                                        System.out.println("Error processing the purchase: " + e.getMessage());
                                    }
                                }
                                break;
                            case 2:
                                eliminarProductoDelCarrito(carritoActual);
                                break;
                            case 0:
                                volverCarrito = true;
                                break;
                        }
                    } else {
                        volverCarrito = true;
                    }
                }
                break;
            case 0:
                clienteActual = null;
                carritoActual = null;
                System.out.println();
                System.out.println("We hope to see you again!");
                break;
        }
    }

    // ========== MÉTODOS DE LOGIN Y REGISTRO ==========

    /**
     * Realiza el login de un cliente existente.
     *
     * @return el cliente autenticado, o null si no se pudo autenticar
     */
    private Client loginCliente() {
        System.out.println("\n=== Login ===");
        int clientId = pedirEntero("Enter your client ID: ");

        Client cliente = clientManager.autenticarCliente(clientId);

        if (cliente == null) {
            System.out.println("Client not found. Please sign up first.");
            return null;
        }

        return cliente;
    }

    /**
     * Registra un nuevo cliente en el sistema.
     * Permite elegir entre cliente estándar, online o empresa.
     *
     * @return el cliente registrado, o null si hubo un error
     */
    private Client registrarCliente() {
        System.out.println("\n=== New Client Registration ===");
        
        // Elegimos tipo de cliente
        System.out.println("Select client type:");
        System.out.println("1) Standard client");
        System.out.println("2) Online client");
        System.out.println("3) Company client");
        int tipoCliente = mainUI.askForIntInRange("Choose an option: ", 1, 3);
        
        String nombreCompleto = mainUI.askForFullNameValidated();

        // Solicitamos números de teléfono
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        System.out.println("\nEnter at least one phone number:");

        boolean continuar = true;
        int contador = 1;

        while (continuar) {
            System.out.print("Phone " + contador + " - ");
            PhoneNumber phone = mainUI.askForPhoneValidated();
            phoneNumbers.add(phone);

            if (phoneNumbers.size() >= 1) {
                continuar = pedirConfirmacion("Do you want to add another phone number? (y/n): ");
            }

            contador++;
        }

        try {
            Client nuevoCliente;
            
            switch (tipoCliente) {
                case 2: // Online client
                    String shippingAddress = mainUI.askForShippingAddress();
                    String email = mainUI.askForEmail();
                    nuevoCliente = clientManager.registerOnlineClient(nombreCompleto, phoneNumbers, 
                                                                     shippingAddress, email);
                    break;
                case 3: // Company client
                    String nif = mainUI.askForNIF();
                    String contactPersonName = mainUI.askForContactPersonName();
                    String billingAddress = mainUI.askForBillingAddress();
                    String shippingAddressCompany = mainUI.askForShippingAddress();
                    nuevoCliente = clientManager.registerCompanyClient(nombreCompleto, phoneNumbers,
                                                                      nif, contactPersonName,
                                                                      billingAddress, shippingAddressCompany);
                    break;
                default: // Standard client
                    nuevoCliente = clientManager.registrarCliente(nombreCompleto, phoneNumbers);
                    break;
            }
            
            System.out.println("\nRegistration successful!");
            System.out.println("Your client ID is: " + nuevoCliente.getClientId());
            System.out.println("Save this ID to log in in the future.");
            return nuevoCliente;
        } catch (IOException e) {
            System.out.println("Error registering the client: " + e.getMessage());
            return null;
        }
    }

    // ========== MÉTODOS DE BÚSQUEDA DE PRODUCTOS ==========

    /**
     * Busca productos por nombre.
     * Muestra los resultados al usuario y permite seleccionar un producto para ver detalles.
     * Versión sin login que no permite añadir productos al carrito.
     */
    private void buscarPorNombre() {
        System.out.println("\n=== Search Products by Name ===");
        System.out.print("Enter the product name (or Enter to see all): ");
        String nombre = scanner.nextLine();

        List<Product> productos = productManager.buscarProductoNombre(nombre);

        if (productos.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        System.out.println("\nProducts found:");
        for (int i = 0; i < productos.size(); i++) {
            Product producto = productos.get(i);
            System.out.println((i + 1) + ". " + producto.getProductId() + " - " + producto.getProductName());
        }
        System.out.println("0. Back");

        int opcion = pedirEnteroEnRango("\nSelect a product (number) or 0 to go back: ", 0, productos.size());
        if (opcion == 0) {
            return;
        }

        Product productoSeleccionado = productos.get(opcion - 1);
        mostrarDetalleProducto(productoSeleccionado.getProductId());
    }

    /**
     * Busca productos por nombre con opción de añadir al carrito.
     * Versión sobrecargada que permite añadir productos al carrito si se proporciona.
     *
     * @param carrito el carrito de compra (opcional)
     * @param shoppingCartManager el manager del carrito (opcional)
     */
    private void buscarPorNombre(ShoppingCart carrito, ShoppingCartManager shoppingCartManager) {
        System.out.print("Search criteria: ");
        String nombre = scanner.nextLine();

        List<Product> productos = productManager.buscarProductoNombre(nombre);

        if (productos.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        // Mostramos lista numerada: GLS-0019C - OptiLux Premium
        for (int i = 0; i < productos.size(); i++) {
            Product producto = productos.get(i);
            System.out.println((i + 1) + ") " + producto.getProductId() + " - " + producto.getProductName());
        }
        System.out.println("0) Back");
        System.out.println();

        int opcion = pedirEnteroEnRango("Choose an option: ", 0, productos.size());
        if (opcion == 0) {
            return;
        }

        Product productoSeleccionado = productos.get(opcion - 1);
        mostrarDetalleProducto(productoSeleccionado.getProductId(), carrito, shoppingCartManager);
    }

    /**
     * Busca productos por marca.
     * Muestra los resultados al usuario filtrados por la marca especificada.
     * 
     * @implNote Si la marca está vacía, muestra un mensaje de error.
     */
    private void buscarPorMarca() {
        System.out.println("\n=== Search Products by Brand ===");
        System.out.print("Enter the product brand: ");
        String marca = scanner.nextLine();

        if (marca.trim().isEmpty()) {
            System.out.println("The brand cannot be empty.");
            return;
        }

        // Obtenemos todos los productos y filtramos por marca
        List<Product> todosProductos = productManager.buscarProductoNombre("");
        List<Product> productosFiltrados = todosProductos.stream()
                .filter(p -> p.getBrand().equalsIgnoreCase(marca))
                .toList();

        if (productosFiltrados.isEmpty()) {
            System.out.println("No products found for brand: " + marca);
        } else {
            System.out.println("\nProducts found for brand " + marca + ":");
            for (int i = 0; i < productosFiltrados.size(); i++) {
                Product producto = productosFiltrados.get(i);
                System.out.println((i + 1) + ". " + producto.getProductName() +
                        " - " + producto.getBrand() + " " + producto.getModel() +
                        " (ID: " + producto.getProductId() + ")");
            }
        }
    }

    /**
     * Muestra el detalle de un producto específico, incluyendo proveedores disponibles.
     * Versión sin login que no permite añadir productos al carrito.
     */
    private void verDetalleProducto() {
        System.out.println("\n=== Product Details ===");
        System.out.print("Enter the product ID: ");
        String productId = scanner.nextLine();

        mostrarDetalleProducto(productId);
    }

    /**
     * Muestra el detalle de un producto específico por su ID.
     * Método auxiliar usado tanto por verDetalleProducto como por buscarPorNombre.
     * Versión sin carrito que no permite añadir productos.
     *
     * @param productId el ID del producto a mostrar
     */
    private void mostrarDetalleProducto(String productId) {
        mostrarDetalleProducto(productId, null, null);
    }

    /**
     * Muestra el detalle de un producto específico por su ID.
     * Si se proporcionan el carrito y shoppingCartManager, permite añadir el producto al carrito.
     *
     * @param productId el ID del producto a mostrar
     * @param carrito el carrito de compra (opcional, null si el usuario no está logueado)
     * @param shoppingCartManager el manager del carrito (opcional, null si el usuario no está logueado)
     */
    private void mostrarDetalleProducto(String productId, ShoppingCart carrito, ShoppingCartManager shoppingCartManager) {
        Product producto = productManager.obtenerProducto(productId);

        if (producto == null) {
            System.out.println("Product not found.");
            return;
        }

        // Formato según imágenes: Product ID: GLS-0052R, Name: EyeCraft Premium, etc.
        System.out.println();
        System.out.println("Product ID: " + producto.getProductId() + ",");
        System.out.println("Name: " + producto.getProductName() + ",");
        System.out.println("Brand: " + producto.getBrand() + ",");
        System.out.println("Model: " + producto.getModel() + ",");
        System.out.println();

        // Obtenemos proveedores que venden este producto directamente desde todos los proveedores
        List<Provider> proveedores = new ArrayList<>();
        List<ProductForSale> productosDisponibles = new ArrayList<>();
        for (Provider proveedor : providerManager.obtenerProveedor()) {
            for (ProductForSale pfs : proveedor.getProductsForSale()) {
                if (pfs.getProductId() != null && productId != null &&
                        pfs.getProductId().trim().equalsIgnoreCase(productId.trim())) {
                    proveedores.add(proveedor);
                    productosDisponibles.add(pfs);
                    break;
                }
            }
        }

        if (proveedores.isEmpty()) {
            System.out.println("Providers:");
            System.out.println("No providers available for this product.");
            return;
        }

        System.out.println("Providers:");
        for (int i = 0; i < proveedores.size(); i++) {
            Provider proveedor = proveedores.get(i);
            ProductForSale productoEnVenta = productosDisponibles.get(i);
            System.out.println((i + 1) + ") " + proveedor.getCompanyName());
            System.out.println("- Sale price: " + String.format("%.2f", productoEnVenta.getSellingPrice()) + "€,");
            System.out.println("- Available stock: " + productoEnVenta.getUnitsInStock());
        }
        System.out.println();

        // Si el usuario está logueado (tiene carrito), preguntamos si quiere añadir al carrito
        if (carrito != null && shoppingCartManager != null) {
            if (pedirConfirmacion("Do you want to add this product to the shopping cart? ")) {
                System.out.println();
                System.out.println();
                int opcion = pedirEnteroEnRango("Choose a provider: ", 1, proveedores.size());

                Provider proveedorSeleccionado = proveedores.get(opcion - 1);
                ProductForSale productoEnVenta = productosDisponibles.get(opcion - 1);
                
                // Verificamos si es un servicio y si puede ser vendido al cliente actual
                // Usar la variable producto que ya existe en el método
                if (producto instanceof business.Service) {
                    business.Service service = (business.Service) producto;
                    if (clienteActual != null && !service.canBeSoldTo(clienteActual)) {
                        System.out.println("Error: Services can only be sold to presential clients, not to online or company clients.");
                        System.out.println();
                        return;
                    }
                }

                shoppingCartManager.agregarProducto(carrito, productoEnVenta, proveedorSeleccionado);
                System.out.println("Product added to shopping cart.");
                System.out.println();
            }
        }
    }

    // ========== MÉTODOS DE BÚSQUEDA DE PROVEEDORES ==========

    /**
     * Busca y muestra los productos que vende un proveedor específico.
     * Permite seleccionar proveedor por número y luego seleccionar producto para ver detalles.
     * Versión sin login que no permite añadir productos al carrito.
     */
    private void buscarProductoDeProveedor() {
        buscarProductoDeProveedor(null, null);
    }

    /**
     * Busca y muestra los productos que vende un proveedor específico.
     * Si se proporcionan el carrito y shoppingCartManager, permite añadir productos al carrito.
     *
     * @param carrito el carrito de compra (opcional)
     * @param shoppingCartManager el manager del carrito (opcional)
     */
    private void buscarProductoDeProveedor(ShoppingCart carrito, ShoppingCartManager shoppingCartManager) {
        // Mostramos lista de proveedores disponibles
        List<Provider> proveedores = providerManager.obtenerProveedor();

        if (proveedores.isEmpty()) {
            System.out.println("No providers available.");
            return;
        }

        // Formato según imágenes: lista numerada sin IDs
        for (int i = 0; i < proveedores.size(); i++) {
            Provider proveedor = proveedores.get(i);
            System.out.println((i + 1) + ") " + proveedor.getCompanyName());
        }
        System.out.println("0) Back");
        System.out.println();

        int opcion = pedirEnteroEnRango("Choose an option: ", 0, proveedores.size());
        if (opcion == 0) {
            return;
        }

        Provider proveedorSeleccionado = proveedores.get(opcion - 1);

        // Mostramos información completa del proveedor con formato según imágenes
        System.out.println();
        System.out.println("Name: " + proveedorSeleccionado.getCompanyName() + ",");
        System.out.println("CIF: " + proveedorSeleccionado.getCif() + ",");
        System.out.println("Contact person: " + proveedorSeleccionado.getContactName() + ",");
        System.out.println("Phone number: " + proveedorSeleccionado.getPhone() + ",");
        System.out.println("Contact email: " + proveedorSeleccionado.getEmail() + ",");
        System.out.println();

        // Mostramos productos del proveedor
        List<ProductForSale> productos = providerManager.obtenerProductoProveedor(proveedorSeleccionado.getProviderId());

        if (productos.isEmpty()) {
            System.out.println("Provider products:");
            System.out.println("This provider has no products available.");
            return;
        }

        System.out.println("Provider products:");
        for (int i = 0; i < productos.size(); i++) {
            ProductForSale producto = productos.get(i);
            Product productInfo = productManager.obtenerProducto(producto.getProductId());
            String nombreProducto = (productInfo != null) ? productInfo.getProductName() : producto.getProductId();
            System.out.println((i + 1) + ") " + producto.getProductId() + " - " + nombreProducto);
            System.out.println("- Sale price: " + String.format("%.2f", producto.getSellingPrice()) + "€,");
            System.out.println("- Available stock: " + producto.getUnitsInStock());
        }
        System.out.println("0) Back");
        System.out.println();

        int opcionProducto = pedirEnteroEnRango("Choose an option: ", 0, productos.size());
        if (opcionProducto == 0) {
            return;
        }

        ProductForSale productoSeleccionado = productos.get(opcionProducto - 1);
        mostrarDetalleProductoProveedor(productoSeleccionado.getProductId(), proveedorSeleccionado, carrito, shoppingCartManager);
    }

    /**
     * Muestra el detalle de un producto de un proveedor específico.
     *
     * @param productId el ID del producto
     * @param proveedor el proveedor
     * @param carrito el carrito (opcional)
     * @param shoppingCartManager el manager del carrito (opcional)
     */
    private void mostrarDetalleProductoProveedor(String productId, Provider proveedor, ShoppingCart carrito, ShoppingCartManager shoppingCartManager) {
        Product producto = productManager.obtenerProducto(productId);
        ProductForSale productoEnVenta = proveedor.findProductForSale(productId);

        if (producto == null || productoEnVenta == null) {
            System.out.println("Product not found.");
            return;
        }

        // Usamos el mismo formato que mostrarDetalleProducto
        System.out.println();
        System.out.println("Product ID: " + producto.getProductId() + ",");
        System.out.println("Name: " + producto.getProductName() + ",");
        System.out.println("Brand: " + producto.getBrand() + ",");
        System.out.println("Model: " + producto.getModel() + ",");
        System.out.println();
        System.out.println("Providers:");
        System.out.println("1) " + proveedor.getCompanyName());
        System.out.println("- Sale price: " + String.format("%.2f", productoEnVenta.getSellingPrice()) + "€,");
        System.out.println("- Available stock: " + productoEnVenta.getUnitsInStock());
        System.out.println();

        // Si el usuario está logueado, preguntamos si quiere añadir al carrito
        if (carrito != null && shoppingCartManager != null && productoEnVenta.isAvailable()) {
            if (pedirConfirmacion("Do you want to add this product to the shopping cart? ")) {
                // Verificamos si es un servicio y si puede ser vendido al cliente actual
                // Usar la variable producto que ya existe en el método
                if (producto instanceof business.Service) {
                    business.Service service = (business.Service) producto;
                    if (clienteActual != null && !service.canBeSoldTo(clienteActual)) {
                        System.out.println("Error: Services can only be sold to presential clients, not to online or company clients.");
                        System.out.println();
                        return;
                    }
                }
                
                // Solo hay un proveedor (el seleccionado), añadimos directamente
                shoppingCartManager.agregarProducto(carrito, productoEnVenta, proveedor);
                System.out.println();
                System.out.println("Product added to shopping cart.");
                System.out.println();
            }
        }
    }

    /**
     * Muestra el detalle de un producto específico de un proveedor.
     * Permite seleccionar un proveedor y un producto para ver información detallada
     * incluyendo precios con y sin IVA, stock disponible e información del proveedor.
     */
    private void verProductoDeProveedor() {
        System.out.println("\n=== Provider Product Details ===");

        // Mostramos lista de proveedores
        List<Provider> proveedores = providerManager.obtenerProveedor();

        if (proveedores.isEmpty()) {
            System.out.println("No providers available.");
            return;
        }

        System.out.println("\nAvailable providers:");
        for (int i = 0; i < proveedores.size(); i++) {
            Provider proveedor = proveedores.get(i);
            System.out.println((i + 1) + ". " + proveedor.getCompanyName() +
                    " (ID: " + proveedor.getProviderId() + ")");
        }

        int providerId = pedirEntero("\nEnter the provider ID: ");

        Provider proveedor = providerManager.obtenerProveedorPorId(providerId);

        if (proveedor == null) {
            System.out.println("Provider not found.");
            return;
        }

        System.out.print("Enter the product ID: ");
        String productId = scanner.nextLine();

        ProductForSale producto = proveedor.findProductForSale(productId);

        if (producto == null) {
            System.out.println("This provider does not sell this product.");
        } else {
            System.out.println("\n--- Product Details ---");
            System.out.println("Product ID: " + producto.getProductId());
            System.out.println("Price without VAT: " + String.format("%.2f", producto.getSellingPrice()) + "€");
            System.out.println("Price with VAT (21%): " + String.format("%.2f", producto.getPriceWithVAT()) + "€");
            System.out.println("Units in Stock: " + producto.getUnitsInStock());
            System.out.println("Available: " + (producto.isAvailable() ? "Yes" : "No"));

            System.out.println("\n--- Provider Information ---");
            System.out.println("Name: " + proveedor.getCompanyName());
            System.out.println("CIF: " + proveedor.getCif());
            System.out.println("Contact: " + proveedor.getContactName());
            System.out.println("Phone: " + proveedor.getPhone());
            System.out.println("Email: " + proveedor.getEmail());
        }
    }

    // ========== MÉTODOS DEL CARRITO ==========

    /**
     * Muestra el contenido actual del carrito.
     * Usa el nuevo sistema de cálculo de precios que considera el tipo de producto y cliente.
     *
     * @param carrito el carrito de compra
     */
    private void verCarrito(ShoppingCart carrito) {
        System.out.println("Shopping cart:");
        if (carrito.isEmpty()) {
            System.out.println("The shopping cart is empty.");
            return;
        }

        List<CartItem> items = carrito.getItems();
        double totalItems = 0.0;

        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            ProductForSale productoEnVenta = item.getProductForSale();
            Provider proveedor = item.getProvider();
            
            // Obtenemos información completa del producto
            Product producto = productManager.obtenerProducto(productoEnVenta.getProductId());
            String nombreProducto = (producto != null) ? producto.getProductName() : productoEnVenta.getProductId();
            
            // Calculamos precio usando el nuevo sistema
            double precioItem = item.getTotalPrice(producto, clienteActual);
            totalItems += precioItem;
            
            // Formatear precio con coma como separador decimal
            String precioFormateado = String.format("%.2f", precioItem).replace(".", ",");
            
            // Formato según requisitos: + GLS-0011W - Lensify Trend (VisionPro S.A.) 170,03€
            System.out.println("+ " + productoEnVenta.getProductId() + " - " + nombreProducto + 
                             " (" + proveedor.getCompanyName() + ") " + precioFormateado + "€");
        }

        // Calculamos total incluyendo envío si es OnlineClient
        Object geolocationHelper = loadGeolocationHelper();
        double total = carrito.getTotalPrice(productManager, clienteActual, geolocationHelper);
        
        // Si hay envío, lo mostramos por separado
        if (clienteActual instanceof OnlineClient && geolocationHelper != null) {
            OnlineClient onlineClient = (OnlineClient) clienteActual;
            double shippingCost = onlineClient.calculateShippingCost(geolocationHelper);
            if (shippingCost > 0) {
                String shippingFormateado = String.format("%.2f", shippingCost).replace(".", ",");
                System.out.println(" Shipping cost: " + shippingFormateado + "€");
            }
        }
        
        String totalFormateado = String.format("%.2f", total).replace(".", ",");
        System.out.println(" TOTAL PRICE: " + totalFormateado + "€");
    }
    
    /**
     * Muestra un resumen detallado de la compra antes de confirmar.
     * El resumen se adapta específicamente al tipo de cliente.
     * 
     * @param carrito el carrito de compra
     */
    private void mostrarResumenCompra(ShoppingCart carrito) {
        System.out.println();
        System.out.println("=== Purchase Summary ===");
        System.out.println();
        
        // Mostramos información específica del cliente
        mostrarInfoCliente();
        System.out.println();
        
        // Adaptamos el resumen según el tipo de cliente
        if (clienteActual instanceof OnlineClient) {
            mostrarResumenOnlineClient(carrito);
        } else if (clienteActual instanceof CompanyClient) {
            mostrarResumenCompanyClient(carrito);
        } else {
            mostrarResumenStandardClient(carrito);
        }
    }
    
    /**
     * Muestra el resumen de compra adaptado para OnlineClient.
     * Incluye información de envío y cálculo de coste de envío.
     */
    private void mostrarResumenOnlineClient(ShoppingCart carrito) {
        OnlineClient onlineClient = (OnlineClient) clienteActual;
        Object geolocationHelper = loadGeolocationHelper();
        
        System.out.println("=== Products ===");
        List<CartItem> items = carrito.getItems();
        double subtotalProductos = 0.0;
        
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            ProductForSale productoEnVenta = item.getProductForSale();
            Provider proveedor = item.getProvider();
            Product producto = productManager.obtenerProducto(productoEnVenta.getProductId());
            String nombreProducto = (producto != null) ? producto.getProductName() : productoEnVenta.getProductId();
            
            double precioFinal = item.getTotalPrice(producto, clienteActual);
            subtotalProductos += precioFinal;
            
            String precioFormateado = String.format("%.2f", precioFinal).replace(".", ",");
            System.out.println("  " + (i + 1) + ". " + productoEnVenta.getProductId() + " - " + nombreProducto);
            System.out.println("     Provider: " + proveedor.getCompanyName() + " | Quantity: " + item.getQuantity());
            System.out.println("     Price: " + precioFormateado + "€");
            System.out.println();
        }
        
        String subtotalFormateado = String.format("%.2f", subtotalProductos).replace(".", ",");
        System.out.println("-----------------------------------");
        System.out.println("Products subtotal: " + subtotalFormateado + "€");
        System.out.println();
        
        // Información de envío específica para OnlineClient
        System.out.println("=== Shipping Information ===");
        System.out.println("Shipping address: " + onlineClient.getShippingAddress());
        
        if (geolocationHelper != null) {
            double shippingCost = onlineClient.calculateShippingCost(geolocationHelper);
            if (shippingCost > 0) {
                String shippingFormateado = String.format("%.2f", shippingCost).replace(".", ",");
                System.out.println("Shipping cost: " + shippingFormateado + "€");
                System.out.println("(Base: 2,00€ + 0,01€/km from La Salle campus)");
            } else {
                System.out.println("Shipping cost: Unable to calculate (0,00€)");
            }
        } else {
            System.out.println("Shipping cost: Geolocation service unavailable (0,00€)");
        }
        System.out.println();
        
        // Total final
        double shippingCost = (geolocationHelper != null) ? onlineClient.calculateShippingCost(geolocationHelper) : 0.0;
        double total = subtotalProductos + shippingCost;
        String totalFormateado = String.format("%.2f", total).replace(".", ",");
        System.out.println("-----------------------------------");
        System.out.println("TOTAL (products + shipping): " + totalFormateado + "€");
        System.out.println();
    }
    
    /**
     * Muestra el resumen de compra adaptado para CompanyClient.
     * Incluye información de exención de IVA si aplica.
     */
    private void mostrarResumenCompanyClient(ShoppingCart carrito) {
        CompanyClient companyClient = (CompanyClient) clienteActual;
        boolean isExempt = companyClient.isExemptFromVAT();
        
        System.out.println("=== Products ===");
        List<CartItem> items = carrito.getItems();
        double subtotalSinIVA = 0.0;
        double totalIVA = 0.0;
        double subtotalConIVA = 0.0;
        
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            ProductForSale productoEnVenta = item.getProductForSale();
            Provider proveedor = item.getProvider();
            Product producto = productManager.obtenerProducto(productoEnVenta.getProductId());
            String nombreProducto = (producto != null) ? producto.getProductName() : productoEnVenta.getProductId();
            
            double precioFinal = item.getTotalPrice(producto, clienteActual);
            
            // Calculamos desglose según tipo de producto
            double precioBase = 0.0;
            double ivaItem = 0.0;
            double descuentoAplicado = 0.0;
            String infoIVA = "";
            
            if (producto instanceof business.ContactLens) {
                precioBase = productoEnVenta.getSellingPrice() * item.getQuantity();
                ivaItem = precioBase * 0.10;
                infoIVA = "VAT 10%";
            } else if (producto instanceof business.Consumable) {
                business.Consumable consumable = (business.Consumable) producto;
                precioBase = productoEnVenta.getSellingPrice() * item.getQuantity();
                
                // Verificamos si tiene descuento por caducidad
                java.util.Calendar expirationCal = java.util.Calendar.getInstance();
                expirationCal.setTime(consumable.getExpirationDate());
                java.util.Calendar thresholdCal = java.util.Calendar.getInstance();
                thresholdCal.add(java.util.Calendar.MONTH, 3);
                boolean tieneDescuento = expirationCal.before(thresholdCal);
                
                if (!isExempt) {
                    double precioConIVA = precioBase * 1.21;
                    if (tieneDescuento) {
                        descuentoAplicado = precioConIVA * 0.40;
                        infoIVA = "VAT 21% (with 40% expiration discount)";
                    } else {
                        infoIVA = "VAT 21%";
                    }
                    ivaItem = precioBase * 0.21;
                } else {
                    // Si está exento, no hay IVA pero puede haber descuento
                    if (tieneDescuento) {
                        // Descuento del 40% sobre precio base (sin IVA)
                        descuentoAplicado = precioBase * 0.40;
                        infoIVA = "VAT EXEMPT (with 40% expiration discount)";
                    } else {
                        infoIVA = "VAT EXEMPT";
                    }
                    ivaItem = 0.0;
                }
            } else if (producto instanceof business.Service) {
                business.Service service = (business.Service) producto;
                precioBase = productoEnVenta.getSellingPrice() * service.getHoursDuration() * item.getQuantity();
                if (!isExempt) {
                    ivaItem = precioBase * 0.21;
                    infoIVA = "VAT 21%";
                } else {
                    infoIVA = "VAT EXEMPT";
                }
            } else {
                // Producto normal
                precioBase = productoEnVenta.getSellingPrice() * item.getQuantity();
                if (!isExempt) {
                    ivaItem = precioBase * 0.21;
                    infoIVA = "VAT 21%";
                } else {
                    ivaItem = 0.0;
                    infoIVA = "VAT EXEMPT";
                }
            }
            
            subtotalSinIVA += precioBase;
            totalIVA += ivaItem;
            subtotalConIVA += precioFinal;
            
            String precioFormateado = String.format("%.2f", precioFinal).replace(".", ",");
            System.out.println("  " + (i + 1) + ". " + productoEnVenta.getProductId() + " - " + nombreProducto);
            System.out.println("     Provider: " + proveedor.getCompanyName() + " | Quantity: " + item.getQuantity());
            System.out.println("     " + infoIVA);
            if (descuentoAplicado > 0) {
                String descuentoFormateado = String.format("%.2f", descuentoAplicado).replace(".", ",");
                System.out.println("     Discount: -" + descuentoFormateado + "€");
            }
            System.out.println("     Price: " + precioFormateado + "€");
            System.out.println();
        }
        
        // Calculamos total de descuentos
        double totalDescuentos = 0.0;
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            Product producto = productManager.obtenerProducto(item.getProductForSale().getProductId());
            if (producto instanceof business.Consumable) {
                business.Consumable consumable = (business.Consumable) producto;
                java.util.Calendar expirationCal = java.util.Calendar.getInstance();
                expirationCal.setTime(consumable.getExpirationDate());
                java.util.Calendar thresholdCal = java.util.Calendar.getInstance();
                thresholdCal.add(java.util.Calendar.MONTH, 3);
                if (expirationCal.before(thresholdCal)) {
                    double precioBaseItem = item.getProductForSale().getSellingPrice() * item.getQuantity();
                    if (!isExempt) {
                        double precioConIVA = precioBaseItem * 1.21;
                        totalDescuentos += precioConIVA * 0.40;
                    } else {
                        totalDescuentos += precioBaseItem * 0.40;
                    }
                }
            }
        }
        
        // Resumen de totales
        String subtotalSinIVAFormateado = String.format("%.2f", subtotalSinIVA).replace(".", ",");
        String totalIVAFormateado = String.format("%.2f", totalIVA).replace(".", ",");
        String totalDescuentosFormateado = totalDescuentos > 0 ? String.format("%.2f", totalDescuentos).replace(".", ",") : "0,00";
        String subtotalConIVAFormateado = String.format("%.2f", subtotalConIVA).replace(".", ",");
        
        System.out.println("-----------------------------------");
        System.out.println("Subtotal (without VAT): " + subtotalSinIVAFormateado + "€");
        
        if (isExempt) {
            // Las lentes de contacto siempre tienen VAT 10%, incluso para empresas exentas
            // Calculamos solo el VAT de las lentes
            double vatLentes = 0.0;
            for (CartItem item : items) {
                Product producto = productManager.obtenerProducto(item.getProductForSale().getProductId());
                if (producto instanceof business.ContactLens) {
                    double precioBaseLente = item.getProductForSale().getSellingPrice() * item.getQuantity();
                    vatLentes += precioBaseLente * 0.10;
                }
            }
            
            if (vatLentes > 0) {
                String vatLentesFormateado = String.format("%.2f", vatLentes).replace(".", ",");
                System.out.println("VAT: EXEMPT (Company billing address outside Spain)");
                System.out.println("Note: Contact lenses always have 10% VAT (not exempt)");
                System.out.println("Total VAT: " + vatLentesFormateado + "€ (contact lenses only)");
                totalIVA = vatLentes; // Actualizar totalIVA para que sea correcto
                totalIVAFormateado = vatLentesFormateado; // Actualizar también el formateado
            } else {
                System.out.println("VAT: EXEMPT (Company billing address outside Spain)");
                System.out.println("Total VAT: 0,00€");
            }
        } else {
            System.out.println("Total VAT: " + totalIVAFormateado + "€");
        }
        
        if (totalDescuentos > 0) {
            System.out.println("Total discounts: -" + totalDescuentosFormateado + "€");
        }
        
        System.out.println("Subtotal (with VAT): " + subtotalConIVAFormateado + "€");
        System.out.println();
        
        System.out.println("=== Billing Information ===");
        System.out.println("Billing address: " + companyClient.getBillingAddress());
        System.out.println("NIF: " + companyClient.getNif());
        System.out.println();
        
        System.out.println("-----------------------------------");
        System.out.println("TOTAL: " + subtotalConIVAFormateado + "€");
        System.out.println();
    }
    
    /**
     * Muestra el resumen de compra adaptado para Client estándar.
     * Muestra IVA normal sin exenciones ni envío.
     */
    private void mostrarResumenStandardClient(ShoppingCart carrito) {
        System.out.println("=== Products ===");
        List<CartItem> items = carrito.getItems();
        double subtotalSinIVA = 0.0;
        double totalIVA = 0.0;
        double subtotalConIVA = 0.0;
        
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            ProductForSale productoEnVenta = item.getProductForSale();
            Provider proveedor = item.getProvider();
            Product producto = productManager.obtenerProducto(productoEnVenta.getProductId());
            String nombreProducto = (producto != null) ? producto.getProductName() : productoEnVenta.getProductId();
            
            double precioFinal = item.getTotalPrice(producto, clienteActual);
            
            // Calculamos desglose según tipo de producto
            double precioBase = 0.0;
            double ivaItem = 0.0;
            double descuentoAplicado = 0.0;
            String infoIVA = "";
            
            if (producto instanceof business.ContactLens) {
                precioBase = productoEnVenta.getSellingPrice() * item.getQuantity();
                ivaItem = precioBase * 0.10;
                infoIVA = "VAT 10%";
            } else if (producto instanceof business.Consumable) {
                business.Consumable consumable = (business.Consumable) producto;
                precioBase = productoEnVenta.getSellingPrice() * item.getQuantity();
                
                // Verificamos si tiene descuento por caducidad
                java.util.Calendar expirationCal = java.util.Calendar.getInstance();
                expirationCal.setTime(consumable.getExpirationDate());
                java.util.Calendar thresholdCal = java.util.Calendar.getInstance();
                thresholdCal.add(java.util.Calendar.MONTH, 3);
                boolean tieneDescuento = expirationCal.before(thresholdCal);
                
                if (tieneDescuento) {
                    double precioConIVA = precioBase * 1.21;
                    descuentoAplicado = precioConIVA * 0.40;
                    infoIVA = "VAT 21% (with 40% expiration discount)";
                } else {
                    infoIVA = "VAT 21%";
                }
                ivaItem = precioBase * 0.21;
            } else if (producto instanceof business.Service) {
                business.Service service = (business.Service) producto;
                precioBase = productoEnVenta.getSellingPrice() * service.getHoursDuration() * item.getQuantity();
                ivaItem = precioBase * 0.21;
                infoIVA = "VAT 21%";
            } else {
                precioBase = productoEnVenta.getSellingPrice() * item.getQuantity();
                ivaItem = precioBase * 0.21;
                infoIVA = "VAT 21%";
            }
            
            subtotalSinIVA += precioBase;
            totalIVA += ivaItem;
            subtotalConIVA += precioFinal;
            
            String precioFormateado = String.format("%.2f", precioFinal).replace(".", ",");
            System.out.println("  " + (i + 1) + ". " + productoEnVenta.getProductId() + " - " + nombreProducto);
            System.out.println("     Provider: " + proveedor.getCompanyName() + " | Quantity: " + item.getQuantity());
            System.out.println("     " + infoIVA);
            if (descuentoAplicado > 0) {
                String descuentoFormateado = String.format("%.2f", descuentoAplicado).replace(".", ",");
                System.out.println("     Discount: -" + descuentoFormateado + "€");
            }
            System.out.println("     Price: " + precioFormateado + "€");
            System.out.println();
        }
        
        // Calculamos total de descuentos
        double totalDescuentos = 0.0;
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            Product producto = productManager.obtenerProducto(item.getProductForSale().getProductId());
            if (producto instanceof business.Consumable) {
                business.Consumable consumable = (business.Consumable) producto;
                java.util.Calendar expirationCal = java.util.Calendar.getInstance();
                expirationCal.setTime(consumable.getExpirationDate());
                java.util.Calendar thresholdCal = java.util.Calendar.getInstance();
                thresholdCal.add(java.util.Calendar.MONTH, 3);
                if (expirationCal.before(thresholdCal)) {
                    double precioBaseItem = item.getProductForSale().getSellingPrice() * item.getQuantity();
                    double precioConIVA = precioBaseItem * 1.21;
                    totalDescuentos += precioConIVA * 0.40;
                }
            }
        }
        
        // Resumen de totales
        String subtotalSinIVAFormateado = String.format("%.2f", subtotalSinIVA).replace(".", ",");
        String totalIVAFormateado = String.format("%.2f", totalIVA).replace(".", ",");
        String totalDescuentosFormateado = totalDescuentos > 0 ? String.format("%.2f", totalDescuentos).replace(".", ",") : "0,00";
        String subtotalConIVAFormateado = String.format("%.2f", subtotalConIVA).replace(".", ",");
        
        System.out.println("-----------------------------------");
        System.out.println("Subtotal (without VAT): " + subtotalSinIVAFormateado + "€");
        System.out.println("Total VAT: " + totalIVAFormateado + "€");
        if (totalDescuentos > 0) {
            System.out.println("Total discounts: -" + totalDescuentosFormateado + "€");
        }
        System.out.println("Subtotal (with VAT): " + subtotalConIVAFormateado + "€");
        System.out.println();
        
        System.out.println("-----------------------------------");
        System.out.println("TOTAL: " + subtotalConIVAFormateado + "€");
        System.out.println();
    }
    
    /**
     * Muestra información relevante del cliente para el resumen de compra.
     */
    private void mostrarInfoCliente() {
        System.out.println("Client Information:");
        System.out.println("  Name: " + clienteActual.getFullName());
        System.out.println("  Client ID: " + clienteActual.getClientId());
        
        if (clienteActual instanceof OnlineClient) {
            OnlineClient onlineClient = (OnlineClient) clienteActual;
            System.out.println("  Type: Online Client");
            System.out.println("  Shipping Address: " + onlineClient.getShippingAddress());
            System.out.println("  Email: " + onlineClient.getEmail());
        } else if (clienteActual instanceof CompanyClient) {
            CompanyClient companyClient = (CompanyClient) clienteActual;
            System.out.println("  Type: Company Client");
            System.out.println("  NIF: " + companyClient.getNif());
            System.out.println("  Billing Address: " + companyClient.getBillingAddress());
            if (companyClient.isExemptFromVAT()) {
                System.out.println("  VAT Exempt: Yes (billing address outside Spain)");
            } else {
                System.out.println("  VAT Exempt: No");
            }
        } else {
            System.out.println("  Type: Standard Client");
            System.out.println("  VAT Status: Subject to VAT (21%)");
        }
    }
    
    /**
     * Intenta cargar GeolocationApiManager dinámicamente usando reflexión.
     * 
     * @return la instancia de GeolocationApiManager o null si no está disponible
     */
    private Object loadGeolocationHelper() {
        try {
            Class<?> helperClass = Class.forName("geolocationAPI.GeolocationApiManager");
            return helperClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // Si no está disponible, retornar null
            return null;
        }
    }

    /**
     * Permite al usuario eliminar un producto del carrito.
     * 
     * @param carrito el carrito de compra
     */
    private void eliminarProductoDelCarrito(ShoppingCart carrito) {
        if (carrito.isEmpty()) {
            System.out.println("The shopping cart is empty.");
            return;
        }
        
        List<CartItem> items = carrito.getItems();
        
        System.out.println();
        System.out.println("Select a product to remove:");
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            ProductForSale productoEnVenta = item.getProductForSale();
            Provider proveedor = item.getProvider();
            Product producto = productManager.obtenerProducto(productoEnVenta.getProductId());
            String nombreProducto = (producto != null) ? producto.getProductName() : productoEnVenta.getProductId();
            System.out.println((i + 1) + ") " + productoEnVenta.getProductId() + " - " + nombreProducto + 
                             " (" + proveedor.getCompanyName() + ")");
        }
        System.out.println("0) Cancel");
        System.out.println();
        
        int opcion = pedirEnteroEnRango("Choose an option: ", 0, items.size());
        if (opcion == 0) {
            return;
        }
        
        carrito.removeItem(opcion - 1);
        System.out.println("Product removed from shopping cart.");
        System.out.println();
    }

    // ========== MÉTODOS DEL PERFIL DE CLIENTE ==========

    /**
     * Muestra el perfil completo del cliente.
     *
     * @param clientId el ID del cliente
     */
    private void verPerfilCliente(int clientId) {
        Client cliente = clientManager.obtenerCliente(clientId);

        if (cliente == null) {
            System.out.println("Client not found.");
            return;
        }

        System.out.println();
        System.out.println("--| User Profile |--");
        System.out.println();
        System.out.println();
        System.out.println("Client ID: " + cliente.getClientId() + ",");
        System.out.println("Full name: " + cliente.getFullName());

        // Mostramos información específica según el tipo de cliente
        if (cliente instanceof OnlineClient) {
            OnlineClient onlineClient = (OnlineClient) cliente;
            System.out.println("Client type: Online");
            System.out.println("Shipping address: " + onlineClient.getShippingAddress());
            System.out.println("Email: " + onlineClient.getEmail());
        } else if (cliente instanceof CompanyClient) {
            CompanyClient companyClient = (CompanyClient) cliente;
            System.out.println("Client type: Company");
            System.out.println("NIF: " + companyClient.getNif());
            System.out.println("Contact person: " + companyClient.getContactPersonName());
            System.out.println("Billing address: " + companyClient.getBillingAddress());
            System.out.println("Shipping address: " + companyClient.getShippingAddress());
            if (companyClient.isExemptFromVAT()) {
                System.out.println("VAT Status: EXEMPT (billing address outside Spain)");
            } else {
                System.out.println("VAT Status: Subject to VAT (21%)");
            }
        } else {
            System.out.println("Client type: Standard");
        }

        List<PhoneNumber> telefonos = cliente.getPhoneNumbers();
        System.out.println();
        System.out.println("Phone numbers:");
        if (telefonos.isEmpty()) {
            System.out.println("  No phone numbers registered.");
        } else {
            for (PhoneNumber telefono : telefonos) {
                System.out.println(" (" + telefono.getCountryPrefix() + ") " + telefono.getNumber());
            }
        }

        // Mostramos historial de compra (10 últimos productos)
        try {
            List<Sale> ventas = saleManager.obtenerVentasCliente(clientId);
            
            // Ordenar por fecha descendente (más reciente primero)
            ventas.sort(Collections.reverseOrder(Comparator.comparing(Sale::getPurchaseDate)));
            
            // Limitar a 10 últimos
            int limite = Math.min(10, ventas.size());
            
            System.out.println();
            System.out.println("Shopping history:");
            if (ventas.isEmpty()) {
                System.out.println("No purchases registered.");
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy – HH:mm");
                
                for (int i = 0; i < limite; i++) {
                    Sale venta = ventas.get(i);
                    Product producto = productManager.obtenerProducto(venta.getProductId());
                    
                    if (producto != null) {
                        Date fecha = new Date(venta.getPurchaseDate() * 1000);
                        // Formato: NovaFrame Elite – NovaFrame (NF-TR900) - € 142.17 – 24/10/2025 – 12:35
                        System.out.println(producto.getProductName() + " – " + 
                                         producto.getBrand() + " (" + producto.getModel() + ") - € " +
                                         String.format("%.2f", venta.getPricePaid()) + " – " +
                                         dateFormat.format(fecha));
                    } else {
                        // Si no se encuentra el producto, mostramos solo el ID
                        Date fecha = new Date(venta.getPurchaseDate() * 1000);
                        System.out.println("Product ID: " + venta.getProductId() + " - € " +
                                         String.format("%.2f", venta.getPricePaid()) + " – " +
                                         dateFormat.format(fecha));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("\nError retrieving purchase history: " + e.getMessage());
        }
    }

    // ========== MÉTODOS PÚBLICOS PARA COMPATIBILIDAD ==========

    /**
     * Muestra el menú del usuario autenticado.
     * Incluye todas las funcionalidades disponibles para usuarios logueados.
     */
    public void showUserMenu() {
        mostrarMenuUsuario();
    }
    
    /**
     * Muestra el menú del programa (login/registro).
     */
    public void showProgramMenu() {
        mostrarMenuLogin();
    }
    
    /**
     * Realiza el login de un cliente.
     * 
     * @return true si el login fue exitoso, false en caso contrario
     */
    public boolean login() {
        if (clienteActual == null) {
            Client cliente = loginCliente();
            if (cliente != null) {
                clienteActual = cliente;
                carritoActual = shoppingCartManager.crearCarrito();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Registra un nuevo cliente.
     */
    public void register() {
        if (clienteActual == null) {
            clienteActual = registrarCliente();
            if (clienteActual != null) {
                carritoActual = shoppingCartManager.crearCarrito();
            }
        }
    }
    
    /**
     * Muestra el perfil del usuario.
     * 
     * @param clientId el ID del cliente
     */
    public void userProfile(int clientId) {
        verPerfilCliente(clientId);
    }
    
    /**
     * Busca productos por nombre.
     */
    public void findProductsByName() {
        if (clienteActual != null && carritoActual != null) {
            buscarPorNombre(carritoActual, shoppingCartManager);
        } else {
            buscarPorNombre();
        }
    }
    
    /**
     * Busca productos por proveedor.
     */
    public void findProductsByProvider() {
        if (clienteActual != null && carritoActual != null) {
            buscarProductoDeProveedor(carritoActual, shoppingCartManager);
        } else {
            buscarProductoDeProveedor();
        }
    }
    
    /**
     * Muestra el carrito de compra.
     */
    public void seeShoppingCart() {
        if (carritoActual != null) {
            verCarrito(carritoActual);
        }
    }
    
    /**
     * Realiza el checkout del carrito.
     * Calcula el precio final usando el nuevo sistema que considera tipos de producto y cliente.
     */
    public void checkout() {
        if (clienteActual != null && carritoActual != null) {
            try {
                // Calculamos el precio total usando el nuevo sistema
                Object geolocationHelper = loadGeolocationHelper();
                double totalPrice = carritoActual.getTotalPrice(productManager, clienteActual, geolocationHelper);
                
                // Creamos las ventas usando el nuevo método que acepta ProductManager y Client
                saleManager.crearVenta(clienteActual.getClientId(), carritoActual, 
                                      productManager, clienteActual);
                
                System.out.println("Thank you for your purchase!");
                System.out.println("Total amount: " + String.format("%.2f", totalPrice) + "€");
                carritoActual.clear();
            } catch (IOException e) {
                System.out.println("Error processing the purchase: " + e.getMessage());
            }
        }
    }
    
    /**
     * Cierra la sesión del usuario actual.
     */
    public void logout() {
        clienteActual = null;
        carritoActual = null;
        System.out.println();
        System.out.println("We hope to see you again!");
    }
}
