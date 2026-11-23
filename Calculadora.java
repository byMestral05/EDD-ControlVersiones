import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Calculadora - backend en Java.
 *
 * Servidor HTTP que sirve el frontend (index.html) y resuelve exclusivamente
 * suma, resta, multiplicacion y potencias de numeros enteros en el rango
 * [-99999, 99999].
 *
 * Uso: java Calculadora.java  y abrir http://localhost:8080
 */
public class Calculadora {

    /** Puerto en el que escucha el servidor. */
    private static final int PUERTO = 8080;

    /**
     * Punto de entrada: arranca el servidor HTTP.
     *
     * @param args argumentos de linea de comandos (no se usan)
     * @throws IOException si no se puede abrir el puerto
     */
    public static void main(String[] args) throws IOException {
        HttpServer servidor = HttpServer.create(new InetSocketAddress(PUERTO), 0);
        servidor.createContext("/", Calculadora::servirFrontend);
        servidor.start();
        System.out.println("Calculadora escuchando en http://localhost:" + PUERTO);
    }

    /**
     * Devuelve la pagina index.html.
     *
     * @param peticion peticion HTTP recibida
     * @throws IOException si no se puede leer el archivo o responder
     */
    private static void servirFrontend(HttpExchange peticion) throws IOException {
        byte[] html = Files.readAllBytes(Paths.get("index.html"));
        responder(peticion, 200, "text/html; charset=utf-8", html);
    }

    /**
     * Envia la respuesta HTTP al cliente.
     *
     * @param peticion peticion HTTP
     * @param codigo codigo de estado HTTP
     * @param tipo valor de Content-Type
     * @param cuerpo contenido de la respuesta
     * @throws IOException si falla el envio
     */
    private static void responder(HttpExchange peticion, int codigo, String tipo, byte[] cuerpo)
            throws IOException {
        peticion.getResponseHeaders().set("Content-Type", tipo);
        peticion.sendResponseHeaders(codigo, cuerpo.length);
        try (OutputStream salida = peticion.getResponseBody()) {
            salida.write(cuerpo);
        }
    }
}
