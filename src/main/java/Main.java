import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Cadena de conexión a MongoDB Atlas
        String uri = "mongodb+srv://admin:43meehdtvOJLics1@mongodbedv.pvdnhz3.mongodb.net/?retryWrites=true&w=majority";

        try (MongoClient client = MongoClients.create(uri)) {
            // Obtener la base de datos
            MongoDatabase db = client.getDatabase("MongoDBedv");

            // Obtener la colección
            MongoCollection<Document> col = db.getCollection("tutorial");

            System.out.println("=== CONEXIÓN EXITOSA ===");
            System.out.println("Base de datos: " + db.getName());
            System.out.println("Colección: tutorial");
            System.out.println();

            // ========== CONSULTA ANTES DE INSERTAR ==========
            System.out.println("=== DOCUMENTOS ANTES DE LA INSERCIÓN ===");
            long countAntes = col.countDocuments();
            System.out.println("Total de documentos: " + countAntes);

            if (countAntes > 0) {
                System.out.println("\nDocumentos existentes:");
                for (Document doc : col.find()) {
                    System.out.println(doc.toJson());
                }
            } else {
                System.out.println("La colección está vacía.");
            }
            System.out.println();

            // ========== CREAR E INSERTAR DOCUMENTO ==========
            System.out.println("=== INSERTANDO NUEVO DOCUMENTO ===");

            Document nuevoDoc = new Document("titulo", "Como testear MongoDB")
                    .append("autor", "Enrique Díaz")
                    .append("anyo", 2026)
                    .append("publicado", true)
                    .append("paginas", 112)
                    .append("tags", List.of("java", "mongodb", "nosql", "base de datos"))
                    .append("editorial", new Document("nombre", "TechBooks")
                            .append("pais", "España"));

            col.insertOne(nuevoDoc);
            System.out.println("(OK) Documento insertado correctamente");
            System.out.println();

            // ========== CONSULTA DESPUÉS DE INSERTAR ==========
            System.out.println("=== DOCUMENTOS DESPUÉS DE LA INSERCIÓN ===");
            long countDespues = col.countDocuments();
            System.out.println("Total de documentos: " + countDespues);
            System.out.println("\nTodos los documentos en la colección:");

            for (Document doc : col.find()) {
                System.out.println("---");
                System.out.println("Título: " + doc.getString("titulo"));
                System.out.println("Autor: " + doc.getString("autor"));
                System.out.println("Año: " + doc.getInteger("anyo"));
                System.out.println("Publicado: " + doc.getBoolean("publicado"));
                System.out.println("Documento completo (JSON):");
                System.out.println(doc.toJson());
            }

        } catch (Exception e) {
            System.err.println("(ERROR) Error en la conexión o inserción:");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}