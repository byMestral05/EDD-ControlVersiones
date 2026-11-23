import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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

    /** Valor minimo permitido para los operandos. */
    private static final int MINIMO = -99999;

    /** Valor maximo permitido para los operandos. */
    private static final int MAXIMO = 99999;

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
        servidor.createContext("/calcular", Calculadora::atenderCalculo);
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
     * Atiende /calcular?a=..&b=..&op=.. y devuelve el resultado en texto plano.
     *
     * @param peticion peticion HTTP recibida
     * @throws IOException si no se puede responder
     */
    private static void atenderCalculo(HttpExchange peticion) throws IOException {
        Map<String, String> parametros = leerParametros(peticion.getRequestURI().getRawQuery());
        int codigo = 200;
        String resultado;
        try {
            int a = validar(parametros.get("a"));
            int b = validar(parametros.get("b"));
            resultado = calcular(a, b, parametros.get("op"));
        } catch (IllegalArgumentException | ArithmeticException e) {
            codigo = 400;
            resultado = "Error: " + e.getMessage();
        }
        responder(peticion, codigo, "text/plain; charset=utf-8",
                resultado.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Ejecuta la operacion pedida. Solo se admiten suma, resta,
     * multiplicacion y potencia.
     *
     * @param a primer operando
     * @param b segundo operando
     * @param operacion "suma", "resta", "multiplicacion" o "potencia"
     * @return resultado en texto
     * @throws IllegalArgumentException si la operacion no esta permitida
     */
    static String calcular(int a, int b, String operacion) {
        if (operacion == null) {
            throw new IllegalArgumentException("falta la operacion");
        }
        switch (operacion) {
            case "suma":
                return String.valueOf((long) a + b);
            case "resta":
                return String.valueOf((long) a - b);
            case "multiplicacion":
                return String.valueOf((long) a * b);
            case "potencia":
                return potencia(a, b);
            default:
                throw new IllegalArgumentException("operacion no permitida");
        }
    }

    /**
     * Calcula base elevado a exponente. Se usa BigInteger porque el resultado
     * puede ser enorme. El exponente debe ser mayor o igual que 0 para que el
     * resultado sea entero.
     *
     * @param base base de la potencia
     * @param exponente exponente (mayor o igual que 0)
     * @return resultado en texto
     * @throws IllegalArgumentException si el exponente es negativo
     */
    static String potencia(int base, int exponente) {
        if (exponente < 0) {
            throw new IllegalArgumentException("el exponente debe ser mayor o igual que 0");
        }
        return BigInteger.valueOf(base).pow(exponente).toString();
    }

    /**
     * Comprueba que el texto sea un numero entero en el rango [-99999, 99999].
     *
     * @param texto valor recibido
     * @return el valor convertido a entero
     * @throws IllegalArgumentException si no es un entero valido o esta fuera de rango
     */
    static int validar(String texto) {
        if (texto == null || !texto.matches("-?\\d+")) {
            throw new IllegalArgumentException("solo se admiten numeros enteros");
        }
        if (texto.replace("-", "").length() > 5) {
            throw new IllegalArgumentException("el numero debe estar entre " + MINIMO + " y " + MAXIMO);
        }
        int valor = Integer.parseInt(texto);
        if (valor < MINIMO || valor > MAXIMO) {
            throw new IllegalArgumentException("el numero debe estar entre " + MINIMO + " y " + MAXIMO);
        }
        return valor;
    }

    /**
     * Convierte la query string (a=1&amp;b=2&amp;op=suma) en un mapa.
     *
     * @param query query string de la peticion (puede ser null)
     * @return mapa nombre -&gt; valor
     */
    private static Map<String, String> leerParametros(String query) {
        Map<String, String> parametros = new HashMap<>();
        if (query == null) {
            return parametros;
        }
        for (String par : query.split("&")) {
            String[] partes = par.split("=", 2);
            if (partes.length == 2) {
                parametros.put(partes[0], partes[1]);
            }
        }
        return parametros;
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
