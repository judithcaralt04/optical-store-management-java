package persistence;

import business.Client;
import business.CompanyClient;
import business.ContactLens;
import business.Consumable;
import business.Item;
import business.OnlineClient;
import business.PhoneNumber;
import business.Product;
import business.ProductForSale;
import business.Provider;
import business.Service;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.io.File;

/**
 * Clase que lee y escribe archivos JSON sin usar librerías externas.
 *
 * ¿Qué hace esta clase?
 * - LEE archivos JSON (productos.json, proveedores.json, clients.json) y los convierte en objetos Java
 * - ESCRIBE objetos Java de vuelta a archivos JSON
 */

public class JsonParser {
    
    // =========================================================
    // Métodos públicos de LECTURA (JSON -> Objetos de dominio)
    // =========================================================
    
    /**
     * Lee un archivo JSON de productos y devuelve una lista de Product.
     * 
     * @param nombreArchivo el nombre del archivo JSON
     * @return lista de productos
     * @throws IOException si el archivo no se puede leer
     */
    public static List<Product> leerProductos(String nombreArchivo) throws IOException {
        List<Product> productos = new ArrayList<Product>();
        String json = leerArchivo(nombreArchivo);
        String jsonLimpio = json.trim();
        
        if (!jsonLimpio.startsWith("[")) {
            throw new IOException("Formato de array JSON inválido");
        }
        
        List<String> objetosJson = dividirObjetosJson(jsonLimpio);
        
        for (String objetoStr : objetosJson) {
            Product producto = crearProducto(objetoStr);
            if (producto != null) {
                productos.add(producto);
            }
        }
        
        return productos;
    }
    
    /**
     * Lee un archivo JSON de proveedores y devuelve una lista de Provider.
     * 
     * @param nombreArchivo el nombre del archivo JSON
     * @return lista de proveedores
     * @throws IOException si el archivo no se puede leer
     */
    public static List<Provider> leerProveedores(String nombreArchivo) throws IOException {
        List<Provider> proveedores = new ArrayList<Provider>();
        String json = leerArchivo(nombreArchivo);
        String jsonLimpio = json.trim();
        
        if (!jsonLimpio.startsWith("[")) {
            throw new IOException("Formato de array JSON inválido");
        }
        
        List<String> objetosJson = dividirObjetosJson(jsonLimpio);
        
        for (String objetoStr : objetosJson) {
            Provider proveedor = crearProveedor(objetoStr);
            if (proveedor != null) {
                proveedores.add(proveedor);
            }
        }
        
        return proveedores;
    }
    
    /**
     * Lee un archivo JSON de clientes y devuelve una lista de Client.
     * 
     * @param nombreArchivo el nombre del archivo JSON
     * @return lista de clientes
     * @throws IOException si el archivo no se puede leer
     */
    public static List<Client> leerClientes(String nombreArchivo) throws IOException {
        List<Client> clientes = new ArrayList<Client>();
        String json = leerArchivo(nombreArchivo);
        String jsonLimpio = json.trim();
        
        if (!jsonLimpio.startsWith("[")) {
            throw new IOException("Formato de array JSON inválido");
        }
        
        List<String> objetosJson = dividirObjetosJson(jsonLimpio);
        
        for (String objetoStr : objetosJson) {
            Client cliente = crearCliente(objetoStr);
            if (cliente != null) {
                clientes.add(cliente);
            }
        }
        
        return clientes;
    }
    
    // =========================================================
    // Métodos públicos de ESCRITURA (Objetos de dominio -> JSON)
    // =========================================================
    
    /**
     * Guarda una lista de productos a un archivo JSON.
     * 
     * @param nombreArchivo el nombre del archivo
     * @param productos la lista de productos a escribir
     * @throws IOException si el archivo no se puede escribir
     */
    public static void guardarProductos(String nombreArchivo, List<Product> productos) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        
        for (int i = 0; i < productos.size(); i++) {
            Product producto = productos.get(i);
            json.append("  {\n");
            json.append("    \"product_id\": \"").append(producto.getProductId()).append("\",\n");
            json.append("    \"product_name\": \"").append(producto.getProductName()).append("\",\n");
            json.append("    \"brand\": \"").append(producto.getBrand()).append("\",\n");
            json.append("    \"model\": \"").append(producto.getModel()).append("\"\n");
            json.append("  }");
            if (i < productos.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        
        json.append("]");
        
        try (FileWriter writer = new FileWriter(nombreArchivo)) {
            writer.write(json.toString());
            writer.flush();
        }
    }
    
    /**
     * Guarda una lista de proveedores a un archivo JSON.
     * 
     * @param nombreArchivo el nombre del archivo
     * @param proveedores la lista de proveedores a escribir
     * @throws IOException si el archivo no se puede escribir
     */
    public static void guardarProveedores(String nombreArchivo, List<Provider> proveedores) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        
        for (int i = 0; i < proveedores.size(); i++) {
            Provider proveedor = proveedores.get(i);
            json.append("  {\n");
            json.append("    \"provider_id\": ").append(proveedor.getProviderId()).append(",\n");
            json.append("    \"company_name\": \"").append(proveedor.getCompanyName()).append("\",\n");
            json.append("    \"cif\": \"").append(proveedor.getCif()).append("\",\n");
            json.append("    \"contact_name\": \"").append(proveedor.getContactName()).append("\",\n");
            json.append("    \"phone\": \"").append(proveedor.getPhone()).append("\",\n");
            json.append("    \"email\": \"").append(proveedor.getEmail()).append("\",\n");
            json.append("    \"products_for_sale\": [\n");
            
            List<ProductForSale> productos = proveedor.getProductsForSale();
            for (int j = 0; j < productos.size(); j++) {
                ProductForSale producto = productos.get(j);
                json.append("      {\n");
                json.append("        \"product_id\": \"").append(producto.getProductId()).append("\",\n");
                json.append("        \"selling_price\": ").append(producto.getSellingPrice()).append(",\n");
                json.append("        \"units_in_stock\": ").append(producto.getUnitsInStock()).append("\n");
                json.append("      }");
                if (j < productos.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            
            json.append("    ]\n");
            json.append("  }");
            if (i < proveedores.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        
        json.append("]");
        
        try (FileWriter writer = new FileWriter(nombreArchivo)) {
            writer.write(json.toString());
            writer.flush();
        }
    }
    
    /**
     * Guarda una lista de clientes a un archivo JSON.
     * Soporta Client, OnlineClient y CompanyClient.
     * 
     * @param nombreArchivo el nombre del archivo
     * @param clientes la lista de clientes a escribir
     * @throws IOException si el archivo no se puede escribir
     */
    public static void guardarClientes(String nombreArchivo, List<Client> clientes) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        
        for (int i = 0; i < clientes.size(); i++) {
            Client cliente = clientes.get(i);
            json.append("  {\n");
            json.append("    \"client_id\": ").append(cliente.getClientId()).append(",\n");
            
            // Determinamos el tipo de cliente y añadimos campos específicos
            if (cliente instanceof CompanyClient) {
                CompanyClient companyClient = (CompanyClient) cliente;
                json.append("    \"client_type\": \"corporate\",\n");
                json.append("    \"cif\": \"").append(escapeJson(companyClient.getNif())).append("\",\n");
                json.append("    \"contact_name\": \"").append(escapeJson(companyClient.getContactPersonName())).append("\",\n");
                json.append("    \"billing_address\": \"").append(escapeJson(companyClient.getBillingAddress())).append("\",\n");
                json.append("    \"mailing_address\": \"").append(escapeJson(companyClient.getShippingAddress())).append("\",\n");
            } else if (cliente instanceof OnlineClient) {
                OnlineClient onlineClient = (OnlineClient) cliente;
                json.append("    \"client_type\": \"online\",\n");
                json.append("    \"shipping_address\": \"").append(escapeJson(onlineClient.getShippingAddress())).append("\",\n");
                json.append("    \"email\": \"").append(escapeJson(onlineClient.getEmail())).append("\",\n");
            } else {
                json.append("    \"client_type\": \"standard\",\n");
            }
            
            json.append("    \"full_name\": \"").append(escapeJson(cliente.getFullName())).append("\",\n");
            json.append("    \"phone_numbers\": [\n");
            
            List<PhoneNumber> telefonos = cliente.getPhoneNumbers();
            for (int j = 0; j < telefonos.size(); j++) {
                PhoneNumber telefono = telefonos.get(j);
                json.append("      {\n");
                json.append("        \"country_prefix\": \"").append(escapeJson(telefono.getCountryPrefix())).append("\",\n");
                json.append("        \"number\": \"").append(escapeJson(telefono.getNumber())).append("\"\n");
                json.append("      }");
                if (j < telefonos.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            
            json.append("    ]\n");
            json.append("  }");
            if (i < clientes.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        
        json.append("]");
        
        try (FileWriter writer = new FileWriter(nombreArchivo)) {
            writer.write(json.toString());
            writer.flush();
        }
    }
    
    /**
     * Escapa caracteres especiales en JSON.
     * 
     * @param text el texto a escapar
     * @return el texto escapado
     */
    private static String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    // =========================================================
    // Métodos privados: construcción de objetos de dominio
    // =========================================================
    
    /**
     * Crea un objeto Product o Item desde un string JSON.
     * Detecta el tipo de producto según los campos presentes y crea el objeto correspondiente.
     * 
     * @param objetoJson el objeto JSON como string
     * @return el Product o Item creado
     */
    private static Product crearProducto(String objetoJson) {
        String idProducto = obtenerTexto(objetoJson, "product_id");
        String nombreProducto = obtenerTexto(objetoJson, "product_name");
        String marca = obtenerTexto(objetoJson, "brand");
        String modelo = obtenerTexto(objetoJson, "model");
        
        if (idProducto == null || nombreProducto == null) {
            return null;
        }
        
        // Verificamos si es un Service (no tiene brand ni model, pero tiene hoursDuration)
        // Buscar tanto "hours_duration" como "duration_hours" para compatibilidad
        double hoursDuration = obtenerDecimal(objetoJson, "hours_duration");
        if (hoursDuration <= 0) {
            hoursDuration = obtenerDecimal(objetoJson, "duration_hours");
        }
        if (hoursDuration > 0) {
            return new Service(idProducto, nombreProducto, hoursDuration);
        }
        
        // Verificamos si es ContactLens (tiene graduation, numberOfLenses, hasUVFilter)
        if (obtenerDecimal(objetoJson, "graduation") != 0.0 || obtenerNumero(objetoJson, "number_of_lenses") > 0) {
            double graduation = obtenerDecimal(objetoJson, "graduation");
            int numberOfLenses = obtenerNumero(objetoJson, "number_of_lenses");
            // Intentar ambos formatos: "has_uv_filter" y "uv_filter"
            boolean hasUVFilter = obtenerBooleano(objetoJson, "has_uv_filter") || obtenerBooleano(objetoJson, "uv_filter");
            
            if (marca != null && modelo != null) {
                return new ContactLens(idProducto, nombreProducto, marca, modelo, 
                                      graduation, numberOfLenses, hasUVFilter);
            }
        }
        
        // Verificamos si es Consumable (tiene volume, expirationDate, hasPreservatives)
        if (obtenerDecimal(objetoJson, "volume") > 0 || obtenerTexto(objetoJson, "expiration_date") != null) {
            double volume = obtenerDecimal(objetoJson, "volume");
            String expirationDateStr = obtenerTexto(objetoJson, "expiration_date");
            // Intentar ambos formatos: "has_preservatives" y "preservatives"
            boolean hasPreservatives = obtenerBooleano(objetoJson, "has_preservatives") || obtenerBooleano(objetoJson, "preservatives");
            
            Date expirationDate = null;
            if (expirationDateStr != null) {
                try {
                    // Intentar parsear la fecha en formato ISO (YYYY-MM-DD)
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    expirationDate = sdf.parse(expirationDateStr);
                } catch (ParseException e) {
                    // Si falla, usar fecha actual como fallback
                    expirationDate = new Date();
                }
            } else {
                expirationDate = new Date();
            }
            
            if (marca != null && modelo != null) {
                return new Consumable(idProducto, nombreProducto, marca, modelo, 
                                     volume, expirationDate, hasPreservatives);
            }
        }
        
        // Producto normal (Product base)
        if (marca != null && modelo != null) {
            return new Product(idProducto, nombreProducto, marca, modelo);
        }
        
        return null;
    }
    
    /**
     * Obtiene un valor booleano de un objeto JSON.
     * 
     * @param objetoJson el objeto JSON
     * @param clave la clave a buscar
     * @return el valor booleano o false si no se encuentra
     */
    private static boolean obtenerBooleano(String objetoJson, String clave) {
        String claveBusqueda = "\"" + clave + "\":";
        int posicionClave = objetoJson.indexOf(claveBusqueda);
        if (posicionClave == -1) {
            return false;
        }
        
        int inicioValor = posicionClave + claveBusqueda.length();
        // Buscar el siguiente true o false
        while (inicioValor < objetoJson.length() && 
               !Character.isLetter(objetoJson.charAt(inicioValor))) {
            inicioValor++;
        }
        
        if (inicioValor >= objetoJson.length()) {
            return false;
        }
        
        String rest = objetoJson.substring(inicioValor);
        if (rest.startsWith("true")) {
            return true;
        } else if (rest.startsWith("false")) {
            return false;
        }
        
        return false;
    }
    
    /**
     * Crea un objeto Provider desde un string JSON.
     * 
     * @param objetoJson el objeto JSON como string
     * @return el Provider creado
     */
    private static Provider crearProveedor(String objetoJson) {
        int idProveedor = obtenerNumero(objetoJson, "provider_id");
        String nombreEmpresa = obtenerTexto(objetoJson, "company_name");
        String cif = obtenerTexto(objetoJson, "cif");
        String nombreContacto = obtenerTexto(objetoJson, "contact_name");
        String telefono = obtenerTexto(objetoJson, "phone");
        String email = obtenerTexto(objetoJson, "email");
        
        // Leer productos en venta
        List<ProductForSale> productosEnVenta = leerProductosEnVenta(objetoJson);
        
        if (nombreEmpresa != null && cif != null && nombreContacto != null && telefono != null && email != null) {
            return new Provider(idProveedor, nombreEmpresa, cif, nombreContacto, telefono, email, productosEnVenta);
        }
        return null;
    }
    
    /**
     * Lee los productos en venta de un proveedor desde JSON.
     * 
     * @param objetoJson el objeto JSON del proveedor
     * @return lista de ProductForSale
     */
    private static List<ProductForSale> leerProductosEnVenta(String objetoJson) {
        List<ProductForSale> productos = new ArrayList<ProductForSale>();
        
        String contenidoArray = obtenerArray(objetoJson, "products_for_sale");
        if (contenidoArray == null || contenidoArray.trim().equals("[]")) {
            return productos;
        }
        
        List<String> stringsProductos;
        try {
            stringsProductos = dividirObjetosJson(contenidoArray);
        } catch (IOException e) {
            return productos;
        }
        
        for (String productoStr : stringsProductos) {
            String idProducto = obtenerTexto(productoStr, "product_id");
            double precioVenta = obtenerDecimal(productoStr, "selling_price");
            int unidadesStock = obtenerNumero(productoStr, "units_in_stock");
            
            if (idProducto != null) {
                productos.add(new ProductForSale(idProducto, precioVenta, unidadesStock));
            }
        }
        
        return productos;
    }
    
    /**
     * Crea un objeto Client, OnlineClient o CompanyClient desde un string JSON.
     * Detecta el tipo de cliente según los campos presentes y crea el objeto correspondiente.
     * 
     * @param objetoJson el objeto JSON como string
     * @return el Client creado
     */
    private static Client crearCliente(String objetoJson) {
        int idCliente = obtenerNumero(objetoJson, "client_id");
        String nombreCompleto = obtenerTexto(objetoJson, "full_name");
        String clientType = obtenerTexto(objetoJson, "client_type");
        
        // Leer números de teléfono
        List<PhoneNumber> telefonos = leerTelefonos(objetoJson);
        
        if (nombreCompleto == null || telefonos.isEmpty()) {
            return null;
        }
        
        // Verificamos si es CompanyClient (tiene cif, contact_name, billing_address, mailing_address)
        if ("corporate".equalsIgnoreCase(clientType) || 
            obtenerTexto(objetoJson, "cif") != null ||
            obtenerTexto(objetoJson, "contact_name") != null) {
            String nif = obtenerTexto(objetoJson, "cif");
            String contactPersonName = obtenerTexto(objetoJson, "contact_name");
            String billingAddress = obtenerTexto(objetoJson, "billing_address");
            String shippingAddress = obtenerTexto(objetoJson, "mailing_address");
            
            if (nif != null && contactPersonName != null && billingAddress != null && shippingAddress != null) {
                return new CompanyClient(idCliente, nombreCompleto, telefonos, 
                                       nif, contactPersonName, billingAddress, shippingAddress);
            }
        }
        
        // Verificamos si es OnlineClient (tiene shipping_address, email)
        if ("online".equalsIgnoreCase(clientType) ||
            (obtenerTexto(objetoJson, "shipping_address") != null && 
             obtenerTexto(objetoJson, "email") != null)) {
            String shippingAddress = obtenerTexto(objetoJson, "shipping_address");
            String email = obtenerTexto(objetoJson, "email");
            
            if (shippingAddress != null && email != null) {
                return new OnlineClient(idCliente, nombreCompleto, telefonos, 
                                      shippingAddress, email);
            }
        }
        
        // Cliente normal (Client base)
        return new Client(idCliente, nombreCompleto, telefonos);
    }
    
    /**
     * Lee los números de teléfono de un cliente desde JSON.
     * 
     * @param objetoJson el objeto JSON del cliente
     * @return lista de PhoneNumber
     */
    private static List<PhoneNumber> leerTelefonos(String objetoJson) {
        List<PhoneNumber> telefonos = new ArrayList<PhoneNumber>();
        
        String contenidoArray = obtenerArray(objetoJson, "phone_numbers");
        if (contenidoArray == null || contenidoArray.trim().equals("[]")) {
            return telefonos;
        }
        
        List<String> stringsTelefonos;
        try {
            stringsTelefonos = dividirObjetosJson(contenidoArray);
        } catch (IOException e) {
            return telefonos;
        }
        
        for (String telefonoStr : stringsTelefonos) {
            String prefijoPais = obtenerTexto(telefonoStr, "country_prefix");
            String numero = obtenerTexto(telefonoStr, "number");
            
            if (prefijoPais != null && numero != null) {
                telefonos.add(new PhoneNumber(prefijoPais, numero));
            }
        }
        
        return telefonos;
    }
    
    // =========================================================
    // Métodos privados de utilidad para manejar JSON en texto
    // =========================================================
    
    /**
     * Obtiene un valor de texto de un objeto JSON.
     * 
     * @param objetoJson el objeto JSON
     * @param clave la clave a buscar
     * @return el valor texto o null si no se encuentra
     */
    private static String obtenerTexto(String objetoJson, String clave) {
        String claveBusqueda = "\"" + clave + "\":";
        int posicionClave = objetoJson.indexOf(claveBusqueda);
        if (posicionClave == -1) {
            return null;
        }
        
        int inicioValor = objetoJson.indexOf("\"", posicionClave + claveBusqueda.length());
        if (inicioValor == -1) {
            return null;
        }
        
        int finValor = objetoJson.indexOf("\"", inicioValor + 1);
        if (finValor == -1) {
            return null;
        }
        
        return objetoJson.substring(inicioValor + 1, finValor);
    }
    
    /**
     * Obtiene un valor número entero de un objeto JSON.
     * 
     * @param objetoJson el objeto JSON
     * @param clave la clave a buscar
     * @return el valor número o 0 si no se encuentra
     */
    private static int obtenerNumero(String objetoJson, String clave) {
        String claveBusqueda = "\"" + clave + "\":";
        int posicionClave = objetoJson.indexOf(claveBusqueda);
        if (posicionClave == -1) {
            return 0;
        }
        
        int inicioValor = posicionClave + claveBusqueda.length();
        // Buscar el siguiente número
        while (inicioValor < objetoJson.length() && !Character.isDigit(objetoJson.charAt(inicioValor)) && objetoJson.charAt(inicioValor) != '-') {
            inicioValor++;
        }
        
        if (inicioValor >= objetoJson.length()) {
            return 0;
        }
        
        int finValor = inicioValor;
        while (finValor < objetoJson.length() && (Character.isDigit(objetoJson.charAt(finValor)) || objetoJson.charAt(finValor) == '-')) {
            finValor++;
        }
        
        try {
            return Integer.parseInt(objetoJson.substring(inicioValor, finValor));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Obtiene un valor decimal de un objeto JSON.
     * 
     * @param objetoJson el objeto JSON
     * @param clave la clave a buscar
     * @return el valor decimal o 0.0 si no se encuentra
     */
    private static double obtenerDecimal(String objetoJson, String clave) {
        String claveBusqueda = "\"" + clave + "\":";
        int posicionClave = objetoJson.indexOf(claveBusqueda);
        if (posicionClave == -1) {
            return 0.0;
        }
        
        int inicioValor = posicionClave + claveBusqueda.length();
        // Buscar el siguiente número
        while (inicioValor < objetoJson.length() && !Character.isDigit(objetoJson.charAt(inicioValor)) && objetoJson.charAt(inicioValor) != '-' && objetoJson.charAt(inicioValor) != '.') {
            inicioValor++;
        }
        
        if (inicioValor >= objetoJson.length()) {
            return 0.0;
        }
        
        int finValor = inicioValor;
        while (finValor < objetoJson.length() && (Character.isDigit(objetoJson.charAt(finValor)) || objetoJson.charAt(finValor) == '-' || objetoJson.charAt(finValor) == '.')) {
            finValor++;
        }
        
        try {
            return Double.parseDouble(objetoJson.substring(inicioValor, finValor));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Busca el corchete de cierre que coincide con el corchete de apertura en la posición dada.
     * 
     * @param json el string JSON
     * @param posicionInicio la posición del corchete de apertura
     * @return la posición del corchete de cierre o -1 si no se encuentra
     */
    private static int buscarCorcheteCierre(String json, int posicionInicio) {
        if (posicionInicio >= json.length() || json.charAt(posicionInicio) != '[') {
            return -1;
        }
        
        int nivel = 1;
        boolean enTexto = false;
        
        for (int i = posicionInicio + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                enTexto = !enTexto;
            }
            
            if (!enTexto) {
                if (c == '[') {
                    nivel++;
                } else if (c == ']') {
                    nivel--;
                    if (nivel == 0) {
                        return i;
                    }
                }
            }
        }
        
        return -1;
    }
    
    /**
     * Lee un archivo completo y devuelve su contenido como string.
     * Usa Scanner para leer el archivo.
     * 
     * @param nombreArchivo el nombre del archivo
     * @return el contenido del archivo
     * @throws IOException si el archivo no se puede leer
     */
    private static String leerArchivo(String nombreArchivo) throws IOException {
        StringBuilder contenido = new StringBuilder();
        
        try (Scanner scanner = new Scanner(new File(nombreArchivo))) {
            while (scanner.hasNextLine()) {
                contenido.append(scanner.nextLine()).append("\n");
            }
        }
        
        return contenido.toString().trim();
    }
    
    /**
     * Divide un array JSON compuesto por objetos en una lista de strings.
     *
     * @param arrayJson string del array JSON (incluyendo corchetes)
     * @return lista de representaciones de objetos JSON
     * @throws IOException si el formato del array no es válido
     */
    private static List<String> dividirObjetosJson(String arrayJson) throws IOException {
        String limpio = arrayJson.trim();
        if (!limpio.startsWith("[") || !limpio.endsWith("]")) {
            throw new IOException("Formato de array JSON inválido");
        }
        
        String contenido = limpio.substring(1, limpio.length() - 1).trim();
        List<String> objetos = new ArrayList<String>();
        if (contenido.isEmpty()) {
            return objetos;
        }
        
        boolean enTexto = false;
        int nivelLlaves = 0;
        int inicioObjeto = 0;
        
        for (int i = 0; i < contenido.length(); i++) {
            char c = contenido.charAt(i);
            if (c == '"' && (i == 0 || contenido.charAt(i - 1) != '\\')) {
                enTexto = !enTexto;
            }
            if (enTexto) {
                continue;
            }
            
            if (c == '{') {
                if (nivelLlaves == 0) {
                    inicioObjeto = i;
                }
                nivelLlaves++;
            } else if (c == '}') {
                nivelLlaves--;
                if (nivelLlaves == 0) {
                    objetos.add(contenido.substring(inicioObjeto, i + 1).trim());
                }
            }
        }
        
        return objetos;
    }
    
    /**
     * Obtiene un array JSON del objeto indicado por la clave dada.
     *
     * @param objetoJson string del objeto JSON
     * @param clave clave del array a obtener
     * @return substring con el array completo (incluyendo corchetes) o null si no existe
     */
    private static String obtenerArray(String objetoJson, String clave) {
        // Buscar la clave, permitiendo espacios antes del corchete [
        String claveBusqueda = "\"" + clave + "\":";
        int posicionInicio = objetoJson.indexOf(claveBusqueda);
        if (posicionInicio == -1) {
            return null;
        }

        // Buscar el primer '[' después de la clave (puede haber espacios)
        posicionInicio = objetoJson.indexOf("[", posicionInicio);
        if (posicionInicio == -1) {
            return null;
        }

        int posicionFin = buscarCorcheteCierre(objetoJson, posicionInicio);
        if (posicionFin == -1) {
            return null;
        }

        return objetoJson.substring(posicionInicio, posicionFin + 1);
    }
}

