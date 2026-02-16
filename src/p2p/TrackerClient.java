
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Cliente que interactúa con el tracker.
 */
public class TrackerClient {

    private final String host;
    private final int port;

    public TrackerClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void register(String peerId, int peerPort) {
        String resp = request("REGISTER " + peerId + " " + peerPort);
        if (!resp.startsWith("OK")) {
            throw new RuntimeException("Register failed: " + resp);
        }
    }

    public Map<String, HostPort> listPeers() {
        String resp = request("LIST");
        Map<String, HostPort> out = new HashMap<>();

        if (!resp.startsWith("PEERS")) return out;
        String payload = resp.substring("PEERS".length()).trim();
        if (payload.isBlank()) return out;

        for (String e : payload.split(",")) {
            String[] a = e.split("@");
            String[] hp = a[1].split(":");
            out.put(a[0], new HostPort(hp[0], Integer.parseInt(hp[1])));
        }
        return out;
    }

    private String request(String line) {
        try (
            Socket s = new Socket(host, port);
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(s.getOutputStream()));
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(s.getInputStream()))
        ) {
            out.write(line);
            out.newLine();
            out.flush();
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Tracker unreachable", e);
        }
    }

    public static class HostPort {
        public final String host;
        public final int port;

        public HostPort(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }
}