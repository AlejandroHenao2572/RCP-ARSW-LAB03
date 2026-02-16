
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * Nodo P2P que actúa como cliente y servidor.
 */
public class PeerNode {

    private final String peerId;
    private final int listenPort;
    private final TrackerClient tracker;

    public PeerNode(String peerId, int listenPort, TrackerClient tracker) {
        this.peerId = peerId;
        this.listenPort = listenPort;
        this.tracker = tracker;
    }

    public void start() throws IOException {
        tracker.register(peerId, listenPort);
        new Thread(this::listenLoop).start();
        consoleLoop();
    }

    private void listenLoop() {
        try (ServerSocket ss = new ServerSocket(listenPort)) {
            while (true) {
                Socket s = ss.accept();
                new Thread(() -> handleIncoming(s)).start();
            }
        } catch (IOException e) {
            System.out.println("[PEER] Listener error");
        }
    }

    private void handleIncoming(Socket s) {
        try (
            s;
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(s.getInputStream()))
        ) {
            String line = in.readLine();
            if (line != null) {
                System.out.println("[RECV] " + line);
            }
        } catch (IOException ignored) {}
    }

    private void consoleLoop() throws IOException {
        BufferedReader console =
                new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("> ");
            String line = console.readLine();
            if (line == null || line.equals("exit")) return;

            if (line.equals("peers")) {
                Map<String, TrackerClient.HostPort> peers =
                        tracker.listPeers();
                peers.forEach((k, v) ->
                        System.out.println(k + " -> " + v.host + ":" + v.port));
            }
        }
    }
}