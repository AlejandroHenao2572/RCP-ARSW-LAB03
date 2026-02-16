import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * Servidor RPC basado en sockets TCP.
 * Atiende múltiples clientes usando threads.
 */
public class RcpServer {

    private final int port;
    private final CalculatorService service;

    public RcpServer(int port, CalculatorService service) {
        this.port = port;
        this.service = service;
    }

    /**
     * Inicia el servidor y espera clientes.
     */
    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[RPC] Server listening on port " + port);

            while (true) {
                Socket client = serverSocket.accept();
                // Cada conexion se atiende en un hilo independiente.
                new Thread(() -> handleClient(client)).start();
            }
        }
    }

    /**
     * Maneja una llamada RPC por cliente.
     */
    private void handleClient(Socket client) {
        try (
            client;
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream()))
        ) {
            String line = in.readLine();
            if (line == null || line.isBlank()) return;

            Map<String, String> request = RpcProtocol.parseLine(line);
            String id = request.getOrDefault("id", "no-id");
            String method = request.get("method");
            String params = request.getOrDefault("params", "");

            String response;
            try {
                // Ejecuta el metodo solicitado y responde con el resultado.
                int result = dispatch(method, params);
                response = RpcProtocol.buildResponse(id, true, String.valueOf(result), null);
            } catch (Exception e) {
                // Reporta el error sin propagarlo al cliente.
                response = RpcProtocol.buildResponse(id, false, null, e.getMessage());
            }

            out.write(response);
            out.newLine();
            out.flush();

        } catch (IOException e) {
            System.out.println("[RPC] Client error: " + e.getMessage());
        }
    }

    /**
     * Dispatcher manual de métodos RPC.
     */
    private int dispatch(String method, String params) {
        if (method == null) {
            throw new IllegalArgumentException("Missing method");
        }

        switch (method) {
            case "add": {
                // add espera dos parametros.
                int[] p = parseInts(params, 2);
                return service.add(p[0], p[1]);
            }
            case "square": {
                // square espera un parametro.
                int[] p = parseInts(params, 1);
                return service.square(p[0]);
            }
            default:
                throw new IllegalArgumentException("Unknown method: " + method);
        }
    }

    /**
     * Convierte parámetros CSV a enteros.
     */
    private int[] parseInts(String csv, int expected) {
        String[] parts = csv.split(",");
        if (parts.length != expected) {
            throw new IllegalArgumentException("Expected " + expected + " params");
        }

        int[] out = new int[expected];
        for (int i = 0; i < expected; i++) {
            out[i] = Integer.parseInt(parts[i].trim());
        }
        return out;
    }

    public static void main(String[] args) throws Exception {
        new RcpServer(5000, new CalculatorServiceImpl()).start();
    }
}
