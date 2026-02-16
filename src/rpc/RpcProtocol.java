
import java.util.HashMap;
import java.util.Map;

/**
 * Protocolo RPC basado en texto plano.
 * Se encarga de parsear requests y construir responses.
 */
public final class RpcProtocol {

    private RpcProtocol() {}

    /**
     * Convierte una línea de texto en un mapa clave=valor.
     * Ejemplo: id=1;method=add;params=2,3
     */
    public static Map<String, String> parseLine(String line) {
        Map<String, String> map = new HashMap<>();
        // Separador de campos: ';' y clave/valor con '='.
        String[] parts = line.split(";");

        for (String part : parts) {
            int eq = part.indexOf('=');
            if (eq > 0) {
                String key = part.substring(0, eq).trim();
                String value = part.substring(eq + 1).trim();
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * Construye una respuesta RPC estándar.
     */
    public static String buildResponse(String id, boolean ok, String result, String error) {
        if (ok) {
            // Respuesta exitosa incluye el resultado.
            return "id=" + id + ";ok=true;result=" + result;
        }
        // Respuesta con error incluye el mensaje sanitizado.
        return "id=" + id + ";ok=false;error=" + sanitize(error);
    }

    /**
     * Evita romper el protocolo si hay caracteres inválidos.
     */
    private static String sanitize(String s) {
        if (s == null) return "";
        return s.replace(";", ",");
    }
}
