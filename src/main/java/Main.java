import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║   GESTIÓN DE TIENDA DE VIDEOJUEGOS    ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("1. Mostrar todos los videojuegos");
            System.out.println("2. Mostrar todas las ventas");
            System.out.println("3. Buscar videojuegos por género");
            System.out.println("4. Buscar videojuegos por plataforma");
            System.out.println("5. Mostrar videojuegos con stock bajo (< 30)");
            System.out.println("6. Mostrar ventas de un cliente");
            System.out.println("7. Mostrar información completa de una venta (juego + venta)");
            System.out.println("8. Mostrar estadísticas de ventas por juego");
            System.out.println("0. Salir");
            System.out.println("─────────────────────────────────────────");
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
                    buscarPorPlataforma(colVideojuegos, scanner);
                    break;
                case 5:
                    mostrarStockBajo(colVideojuegos);
                    break;
                case 6:
                    mostrarVentasCliente(colVentas, scanner);
                    break;
                case 7:
                    mostrarInfoCompletaVenta(colVideojuegos, colVentas, scanner);
                    break;
                case 8:
                    mostrarEstadisticasVentas(colVideojuegos, colVentas);
                    break;
                case 0:
                    System.out.println("\n¡Hasta pronto!");
                    break;
                default:
                    System.out.println("\n⚠ Opción no válida");
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
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("        TODOS LOS VIDEOJUEGOS");
        System.out.println("═══════════════════════════════════════");

        int contador = 1;
        for (Document doc : colVideojuegos.find()) {
            System.out.println("\n▸ Videojuego #" + contador++);
            System.out.println("  • Título: " + doc.getString("titulo"));
            System.out.println("  • Plataforma: " + doc.getString("plataforma"));
            System.out.println("  • Género: " + doc.getString("genero"));
            System.out.println("  • Precio: " + doc.getDouble("precio") + " €");
            System.out.println("  • Stock: " + doc.getInteger("stock") + " unidades");
            System.out.println("  • Clasificación: +" + doc.getInteger("clasificacion_edad"));
            System.out.println("  • Desarrolladora: " + doc.getString("desarrolladora"));
            System.out.println("  • Año: " + doc.getInteger("año_lanzamiento"));
            System.out.println("  ───────────────────────────────────");
        }
    }

    private static void mostrarTodasVentas(MongoCollection<Document> colVentas) {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("           TODAS LAS VENTAS");
        System.out.println("═══════════════════════════════════════");

        int contador = 1;
        for (Document doc : colVentas.find()) {
            System.out.println("\n▸ Venta #" + contador++);
            System.out.println("  • ID Venta: " + doc.getString("id_venta"));
            System.out.println("  • ID Juego: " + doc.getString("id_juego"));
            System.out.println("  • Cliente: " + doc.getString("cliente"));
            System.out.println("  • Fecha: " + doc.getString("fecha"));
            System.out.println("  • Cantidad: " + doc.getInteger("cantidad") + " unidad(es)");
            System.out.println("  • Total: " + doc.getDouble("precio_total") + " €");
            System.out.println("  • Método de pago: " + doc.getString("metodo_pago"));
            System.out.println("  ───────────────────────────────────");
        }
    }

    private static void buscarPorGenero(MongoCollection<Document> colVideojuegos, Scanner scanner) {
        System.out.print("\nIntroduce el género a buscar: ");
        String genero = scanner.nextLine();

        System.out.println("\n═══════════════════════════════════════");
        System.out.println("    VIDEOJUEGOS DE GÉNERO: " + genero.toUpperCase());
        System.out.println("═══════════════════════════════════════");

        Document filtro = new Document("genero", new Document("$regex", genero).append("$options", "i"));

        int contador = 0;
        for (Document doc : colVideojuegos.find(filtro)) {
            contador++;
            System.out.println("\n▸ " + doc.getString("titulo"));
            System.out.println("  • Plataforma: " + doc.getString("plataforma"));
            System.out.println("  • Precio: " + doc.getDouble("precio") + " €");
            System.out.println("  • Stock: " + doc.getInteger("stock") + " unidades");
        }

        if (contador == 0) {
            System.out.println("\n⚠ No se encontraron videojuegos del género '" + genero + "'");
        } else {
            System.out.println("\n✓ Total encontrados: " + contador);
        }
    }

    private static void buscarPorPlataforma(MongoCollection<Document> colVideojuegos, Scanner scanner) {
        System.out.print("\nIntroduce la plataforma a buscar: ");
        String plataforma = scanner.nextLine();

        System.out.println("\n═══════════════════════════════════════");
        System.out.println("    VIDEOJUEGOS PARA: " + plataforma.toUpperCase());
        System.out.println("═══════════════════════════════════════");

        Document filtro = new Document("plataforma", new Document("$regex", plataforma).append("$options", "i"));

        int contador = 0;
        for (Document doc : colVideojuegos.find(filtro)) {
            contador++;
            System.out.println("\n▸ " + doc.getString("titulo"));
            System.out.println("  • Género: " + doc.getString("genero"));
            System.out.println("  • Precio: " + doc.getDouble("precio") + " €");
            System.out.println("  • Stock: " + doc.getInteger("stock") + " unidades");
        }

        if (contador == 0) {
            System.out.println("\n⚠ No se encontraron videojuegos para la plataforma '" + plataforma + "'");
        } else {
            System.out.println("\n✓ Total encontrados: " + contador);
        }
    }

    private static void mostrarStockBajo(MongoCollection<Document> colVideojuegos) {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("    VIDEOJUEGOS CON STOCK BAJO (<30)");
        System.out.println("═══════════════════════════════════════");

        Document filtro = new Document("stock", new Document("$lt", 30));

        int contador = 0;
        for (Document doc : colVideojuegos.find(filtro)) {
            contador++;
            System.out.println("\n▸ " + doc.getString("titulo"));
            System.out.println("  • Plataforma: " + doc.getString("plataforma"));
            System.out.println("  • Stock actual: " + doc.getInteger("stock") + " unidades ⚠");
            System.out.println("  • Precio: " + doc.getDouble("precio") + " €");
        }

        if (contador == 0) {
            System.out.println("\n✓ Todos los videojuegos tienen stock suficiente");
        } else {
            System.out.println("\n⚠ Total con stock bajo: " + contador);
        }
    }

    private static void mostrarVentasCliente(MongoCollection<Document> colVentas, Scanner scanner) {
        System.out.print("\nIntroduce el nombre del cliente: ");
        String cliente = scanner.nextLine();

        System.out.println("\n═══════════════════════════════════════");
        System.out.println("    VENTAS DE: " + cliente.toUpperCase());
        System.out.println("═══════════════════════════════════════");

        Document filtro = new Document("cliente", new Document("$regex", cliente).append("$options", "i"));

        int contador = 0;
        double totalGastado = 0;

        for (Document doc : colVentas.find(filtro)) {
            contador++;
            double precioVenta = doc.getDouble("precio_total");
            totalGastado += precioVenta;

            System.out.println("\n▸ Venta " + doc.getString("id_venta"));
            System.out.println("  • Fecha: " + doc.getString("fecha"));
            System.out.println("  • Juego ID: " + doc.getString("id_juego"));
            System.out.println("  • Cantidad: " + doc.getInteger("cantidad"));
            System.out.println("  • Total: " + precioVenta + " €");
            System.out.println("  • Método de pago: " + doc.getString("metodo_pago"));
        }

        if (contador == 0) {
            System.out.println("\n⚠ No se encontraron ventas para el cliente '" + cliente + "'");
        } else {
            System.out.println("\n═══════════════════════════════════════");
            System.out.println("✓ Total de compras: " + contador);
            System.out.println("✓ Total gastado: " + String.format("%.2f", totalGastado) + " €");
        }
    }

    private static void mostrarInfoCompletaVenta(MongoCollection<Document> colVideojuegos,
                                                 MongoCollection<Document> colVentas,
                                                 Scanner scanner) {
        System.out.print("\nIntroduce el ID de la venta: ");
        String idVenta = scanner.nextLine();

        Document venta = colVentas.find(new Document("id_venta", idVenta)).first();

        if (venta == null) {
            System.out.println("\n⚠ No se encontró ninguna venta con el ID '" + idVenta + "'");
            return;
        }

        String idJuego = venta.getString("id_juego");
        Document juego = colVideojuegos.find(new Document("id_juego", idJuego)).first();

        System.out.println("\n═══════════════════════════════════════");
        System.out.println("    INFORMACIÓN COMPLETA DE LA VENTA");
        System.out.println("═══════════════════════════════════════");

        System.out.println("\n┌─ DATOS DE LA VENTA");
        System.out.println("│ • ID Venta: " + venta.getString("id_venta"));
        System.out.println("│ • Cliente: " + venta.getString("cliente"));
        System.out.println("│ • Fecha: " + venta.getString("fecha"));
        System.out.println("│ • Cantidad: " + venta.getInteger("cantidad") + " unidad(es)");
        System.out.println("│ • Total pagado: " + venta.getDouble("precio_total") + " €");
        System.out.println("│ • Método de pago: " + venta.getString("metodo_pago"));

        if (juego != null) {
            System.out.println("│");
            System.out.println("├─ DATOS DEL VIDEOJUEGO");
            System.out.println("│ • Título: " + juego.getString("titulo"));
            System.out.println("│ • Plataforma: " + juego.getString("plataforma"));
            System.out.println("│ • Género: " + juego.getString("genero"));
            System.out.println("│ • Precio unitario: " + juego.getDouble("precio") + " €");
            System.out.println("│ • Desarrolladora: " + juego.getString("desarrolladora"));
            System.out.println("│ • Clasificación: +" + juego.getInteger("clasificacion_edad"));
            System.out.println("└─────────────────────────────────────");
        } else {
            System.out.println("│");
            System.out.println("└─ ⚠ No se encontró información del videojuego");
        }
    }

    private static void mostrarEstadisticasVentas(MongoCollection<Document> colVideojuegos,
                                                  MongoCollection<Document> colVentas) {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("    ESTADÍSTICAS DE VENTAS POR JUEGO");
        System.out.println("═══════════════════════════════════════");

        for (Document juego : colVideojuegos.find()) {
            String idJuego = juego.getString("id_juego");
            String titulo = juego.getString("titulo");

            Document filtro = new Document("id_juego", idJuego);
            int totalVentas = 0;
            int unidadesVendidas = 0;
            double ingresosTotales = 0;

            for (Document venta : colVentas.find(filtro)) {
                totalVentas++;
                unidadesVendidas += venta.getInteger("cantidad");
                ingresosTotales += venta.getDouble("precio_total");
            }

            if (totalVentas > 0) {
                System.out.println("\n▸ " + titulo);
                System.out.println("  • Número de ventas: " + totalVentas);
                System.out.println("  • Unidades vendidas: " + unidadesVendidas);
                System.out.println("  • Ingresos generados: " + String.format("%.2f", ingresosTotales) + " €");
                System.out.println("  • Stock restante: " + juego.getInteger("stock") + " unidades");
            }
        }
    }
}