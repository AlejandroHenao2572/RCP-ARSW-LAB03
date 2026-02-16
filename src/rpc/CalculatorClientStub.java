import java.io.*;
import java.net.Socket;
import java.util.UUID;

/**
 * Stub del cliente RPC.
 * Simula llamadas locales pero envía requests por la red.
 */
public class CalculatorClientStub implements CalculatorService {

    private final String host;
    private final int port;

    public CalculatorClientStub(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public int add(int a, int b) {
        String id = UUID.randomUUID().toString();
        // Request compacto con id, metodo y parametros.
        String request = "id=" + id + ";method=add;params=" + a + "," + b;
        String response = send(request);
        return parseResultOrThrow(id, response);
    }

    @Override
    public int square(int n) {
        String id = UUID.randomUUID().toString();
        // Mantiene el mismo formato de protocolo para todos los metodos.
        String request = "id=" + id + ";method=square;params=" + n;
        String response = send(request);
        return parseResultOrThrow(id, response);
    }

    /**
     * Envía el request RPC y recibe la respuesta.
     */
    private String send(String request) {
        try (
            Socket socket = new Socket(host, port);
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()))
        ) {
            out.write(request);
            out.newLine();
            out.flush();
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException("RPC connection failed: " + e.getMessage(), e);
        }
    }

    /**
     * Procesa la respuesta RPC.
     */
    private int parseResultOrThrow(String id, String responseLine) {
        if (responseLine == null) {
            throw new RuntimeException("Empty response");
        }

        var resp = RpcProtocol.parseLine(responseLine);

        if (!id.equals(resp.get("id"))) {
            throw new RuntimeException("Mismatched response id");
        }

        // La respuesta debe indicar si la ejecucion fue exitosa.
        boolean ok = "true".equalsIgnoreCase(resp.get("ok"));
        if (!ok) {
            throw new RuntimeException("RPC error: " +
                    resp.getOrDefault("error", "unknown"));
        }

        return Integer.parseInt(resp.get("result"));
    }
}
