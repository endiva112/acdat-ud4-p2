import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Scanner;

public class Main {
    // Constantes para la conexión
    private static final String URI = "mongodb+srv://admin:43meehdtvOJLics1@mongodbedv.pvdnhz3.mongodb.net/?retryWrites=true&w=majority";
    private static final String DATABASE_NAME = "MongoDBedv";
    private static final String COLECCION_VIDEOJUEGOS = "videojuegos";
    private static final String COLECCION_VENTAS = "ventas";

    public static void main(String[] args) {
        try (MongoClient client = MongoClients.create(URI)) {
            MongoDatabase db = client.getDatabase(DATABASE_NAME);
            MongoCollection<Document> colVideojuegos = db.getCollection(COLECCION_VIDEOJUEGOS);
            MongoCollection<Document> colVentas = db.getCollection(COLECCION_VENTAS);

            System.out.println("=== CONEXIÓN EXITOSA ===");
            System.out.println("Base de datos: " + db.getName());
            System.out.println();

            // Mostrar menú
            mostrarMenu(colVideojuegos, colVentas);

        } catch (Exception e) {
            System.err.println("(ERROR) Error en la conexión:");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void mostrarMenu(MongoCollection<Document> colVideojuegos,
                                    MongoCollection<Document> colVentas) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n╔========================================╗");
            System.out.println("║   GESTIÓN DE TIENDA DE VIDEOJUEGOS    ║");
            System.out.println("╚========================================╝");
            System.out.println("1. Mostrar todos los videojuegos");
            System.out.println("2. Mostrar todas las ventas");
            System.out.println("3. Buscar videojuegos por género");
            System.out.println("4. Buscar ventas por cliente");
            System.out.println("5. Insertar videojuego");
            System.out.println("6. Insertar venta");
            System.out.println("0. Salir");
            System.out.println("-----------------------------------------");
            System.out.print("Seleccione una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            switch (opcion) {
                case 1:
                    mostrarTodosVideojuegos(colVideojuegos);
                    break;
                case 2:
                    mostrarTodasVentas(colVentas);
                    break;
                case 3:
                    buscarPorGenero(colVideojuegos, scanner);
                    break;
                case 4:
                    buscarVentasPorCliente(colVentas, scanner);
                    break;
                case 5:
                    insertarVideojuego(colVideojuegos, scanner);
                    break;
                case 6:
                    insertarVenta(colVentas, colVideojuegos, scanner);
                    break;
                case 0:
                    System.out.println("\n¡Hasta pronto!");
                    break;
                default:
                    System.out.println("\n(WARNING) Opción no válida");
            }

            if (opcion != 0) {
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
            }

        } while (opcion != 0);

        scanner.close();
    }

    // ========== MÉTODOS DE CONSULTA ==========

    private static void mostrarTodosVideojuegos(MongoCollection<Document> colVideojuegos) {
        System.out.println("\n=======================================");
        System.out.println("        TODOS LOS VIDEOJUEGOS");
        System.out.println("=======================================");

        int contador = 1;
        for (Document doc : colVideojuegos.find()) {
            System.out.println("\n> Videojuego #" + contador++);
            System.out.println("  * ID: " + doc.getString("id_juego"));
            System.out.println("  * Título: " + doc.getString("titulo"));
            System.out.println("  * Plataforma: " + doc.getString("plataforma"));
            System.out.println("  * Género: " + doc.getString("genero"));
            System.out.println("  * Precio: " + doc.getDouble("precio") + " €");
            System.out.println("  * Stock: " + doc.getInteger("stock") + " unidades");
            System.out.println("  * Clasificación: +" + doc.getInteger("clasificacion_edad"));
            System.out.println("  * Desarrolladora: " + doc.getString("desarrolladora"));
            System.out.println("  * Año: " + doc.getInteger("año_lanzamiento"));
            System.out.println("  -----------------------------------");
        }
    }

    private static void mostrarTodasVentas(MongoCollection<Document> colVentas) {
        System.out.println("\n=======================================");
        System.out.println("           TODAS LAS VENTAS");
        System.out.println("=======================================");

        int contador = 1;
        for (Document doc : colVentas.find()) {
            System.out.println("\n> Venta #" + contador++);
            System.out.println("  * ID Venta: " + doc.getString("id_venta"));
            System.out.println("  * ID Juego: " + doc.getString("id_juego"));
            System.out.println("  * Cliente: " + doc.getString("cliente"));
            System.out.println("  * Fecha: " + doc.getString("fecha"));
            System.out.println("  * Cantidad: " + doc.getInteger("cantidad") + " unidad(es)");
            System.out.println("  * Total: " + doc.getDouble("precio_total") + " €");
            System.out.println("  * Método de pago: " + doc.getString("metodo_pago"));
            System.out.println("  -----------------------------------");
        }
    }

    private static void buscarPorGenero(MongoCollection<Document> colVideojuegos, Scanner scanner) {
        System.out.print("\nIntroduce el género a buscar: ");
        String genero = scanner.nextLine();

        System.out.println("\n=======================================");
        System.out.println("    VIDEOJUEGOS DE GÉNERO: " + genero.toUpperCase());
        System.out.println("=======================================");

        Document filtro = new Document("genero", new Document("$regex", genero).append("$options", "i"));

        int contador = 0;
        for (Document doc : colVideojuegos.find(filtro)) {
            contador++;
            System.out.println("\n> Videojuego #" + contador);
            System.out.println("  * ID: " + doc.getString("id_juego"));
            System.out.println("  * Título: " + doc.getString("titulo"));
            System.out.println("  * Plataforma: " + doc.getString("plataforma"));
            System.out.println("  * Género: " + doc.getString("genero"));
            System.out.println("  * Precio: " + doc.getDouble("precio") + " €");
            System.out.println("  * Stock: " + doc.getInteger("stock") + " unidades");
            System.out.println("  * Clasificación: +" + doc.getInteger("clasificacion_edad"));
            System.out.println("  * Desarrolladora: " + doc.getString("desarrolladora"));
            System.out.println("  * Año: " + doc.getInteger("año_lanzamiento"));
            System.out.println("  -----------------------------------");
        }

        if (contador == 0) {
            System.out.println("\n(WARNING) No se encontraron videojuegos del género '" + genero + "'");
        } else {
            System.out.println("\n(OK) Total encontrados: " + contador);
        }
    }

    private static void buscarVentasPorCliente(MongoCollection<Document> colVentas, Scanner scanner) {
        System.out.print("\nIntroduce el nombre del cliente: ");
        String cliente = scanner.nextLine();

        System.out.println("\n=======================================");
        System.out.println("    VENTAS DE: " + cliente.toUpperCase());
        System.out.println("=======================================");

        Document filtro = new Document("cliente", new Document("$regex", cliente).append("$options", "i"));

        int contador = 0;
        double totalGastado = 0;

        for (Document doc : colVentas.find(filtro)) {
            contador++;
            double precioVenta = doc.getDouble("precio_total");
            totalGastado += precioVenta;

            System.out.println("\n> Venta #" + contador);
            System.out.println("  * ID Venta: " + doc.getString("id_venta"));
            System.out.println("  * ID Juego: " + doc.getString("id_juego"));
            System.out.println("  * Cliente: " + doc.getString("cliente"));
            System.out.println("  * Fecha: " + doc.getString("fecha"));
            System.out.println("  * Cantidad: " + doc.getInteger("cantidad") + " unidad(es)");
            System.out.println("  * Total: " + doc.getDouble("precio_total") + " €");
            System.out.println("  * Método de pago: " + doc.getString("metodo_pago"));
            System.out.println("  -----------------------------------");
        }

        if (contador == 0) {
            System.out.println("\n(WARNING) No se encontraron ventas para el cliente '" + cliente + "'");
        } else {
            System.out.println("\n=======================================");
            System.out.println("(OK) Total de compras: " + contador);
            System.out.println("(OK) Total gastado: " + String.format("%.2f", totalGastado) + " €");
        }
    }

    // ========== MÉTODOS DE INSERCIÓN ==========

    private static void insertarVideojuego(MongoCollection<Document> colVideojuegos, Scanner scanner) {
        System.out.println("\n=======================================");
        System.out.println("        INSERTAR NUEVO VIDEOJUEGO");
        System.out.println("=======================================");

        System.out.print("ID del juego: ");
        String idJuego = scanner.nextLine();

        System.out.print("Título: ");
        String titulo = scanner.nextLine();

        System.out.print("Plataforma: ");
        String plataforma = scanner.nextLine();

        System.out.print("Género: ");
        String genero = scanner.nextLine();

        System.out.print("Precio: ");
        double precio = scanner.nextDouble();

        System.out.print("Stock: ");
        int stock = scanner.nextInt();

        System.out.print("Clasificación de edad: ");
        int clasificacion = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        System.out.print("Desarrolladora: ");
        String desarrolladora = scanner.nextLine();

        System.out.print("Año de lanzamiento: ");
        int anio = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        Document nuevoJuego = new Document("id_juego", idJuego)
                .append("titulo", titulo)
                .append("plataforma", plataforma)
                .append("genero", genero)
                .append("precio", precio)
                .append("stock", stock)
                .append("clasificacion_edad", clasificacion)
                .append("desarrolladora", desarrolladora)
                .append("año_lanzamiento", anio);

        colVideojuegos.insertOne(nuevoJuego);
        System.out.println("\n(OK) Videojuego insertado correctamente");
    }

    private static void insertarVenta(MongoCollection<Document> colVentas,
                                      MongoCollection<Document> colVideojuegos,
                                      Scanner scanner) {
        System.out.println("\n=======================================");
        System.out.println("           INSERTAR NUEVA VENTA");
        System.out.println("=======================================");

        System.out.print("ID de la venta: ");
        String idVenta = scanner.nextLine();

        System.out.print("ID del juego: ");
        String idJuego = scanner.nextLine();

        // Verificar que el juego existe
        Document juego = colVideojuegos.find(new Document("id_juego", idJuego)).first();
        if (juego == null) {
            System.out.println("\n(WARNING) No existe ningún videojuego con el ID '" + idJuego + "'");
            return;
        }

        System.out.println("Juego encontrado: " + juego.getString("titulo"));

        System.out.print("Nombre del cliente: ");
        String cliente = scanner.nextLine();

        System.out.print("Fecha (YYYY-MM-DD): ");
        String fecha = scanner.nextLine();

        System.out.print("Cantidad: ");
        int cantidad = scanner.nextInt();

        System.out.print("Precio total: ");
        double precioTotal = scanner.nextDouble();
        scanner.nextLine(); // Limpiar buffer

        System.out.print("Método de pago (Tarjeta/Efectivo/PayPal/Transferencia): ");
        String metodoPago = scanner.nextLine();

        Document nuevaVenta = new Document("id_venta", idVenta)
                .append("id_juego", idJuego)
                .append("cliente", cliente)
                .append("fecha", fecha)
                .append("cantidad", cantidad)
                .append("precio_total", precioTotal)
                .append("metodo_pago", metodoPago);

        colVentas.insertOne(nuevaVenta);
        System.out.println("\n(OK) Venta insertada correctamente");
    }
}