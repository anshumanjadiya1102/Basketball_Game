package net.footy.server;

import net.footy.shared.GameState;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import static net.footy.shared.GameConstants.*;

public class ServerMain {
    private final int port;
    private volatile ClientHandler c1, c2;
    private final GameState state = new GameState();

    public ServerMain(int port) { this.port = port; }

    public void start() throws IOException {
        System.out.println("Server starting on port " + port);
        try (ServerSocket server = new ServerSocket(port)) {
            ExecutorService pool = Executors.newCachedThreadPool();
            // accept loop in background
            pool.execute(() -> {
                while (!server.isClosed()) {
                    try {
                        Socket s = server.accept();
                        s.setTcpNoDelay(true);
                        ClientHandler ch = new ClientHandler(s, this);
                        assignClient(ch);
                        pool.execute(ch);
                    } catch (IOException e) {
                        break;
                    }
                }
            });

            long lastStateBroadcast = System.nanoTime();
            double broadcastHz = 30.0;
            double broadcastNanos = 1e9 / broadcastHz;
            long lastTick = System.nanoTime();
            while (true) {
                long now = System.nanoTime();
                if (now - lastTick >= (long)(1e9*DT)) {
                    boolean[] in1 = c1 != null ? c1.inputs : new boolean[6];
                    boolean[] in2 = c2 != null ? c2.inputs : new boolean[6];
                    state.update(in1, in2);
                    lastTick = now;
                }
                if (now - lastStateBroadcast >= broadcastNanos) {
                    String msg = String.format("STATE %.2f %.2f %.2f %.2f %.2f %.2f %d %d\n",
                            state.ball.x, state.ball.y,
                            state.p1.x, state.p1.y,
                            state.p2.x, state.p2.y,
                            state.score1, state.score2);
                    if (c1 != null) c1.send(msg);
                    if (c2 != null) c2.send(msg);
                    lastStateBroadcast = now;
                }
                try { Thread.sleep(1); } catch (InterruptedException ignored) {}
            }
        }
    }

    private synchronized void assignClient(ClientHandler ch) throws IOException {
        if (c1 == null) {
            c1 = ch; ch.playerId = 1; ch.send("ASSIGN 1\n");
            System.out.println("Player 1 connected");
        } else if (c2 == null) {
            c2 = ch; ch.playerId = 2; ch.send("ASSIGN 2\n");
            System.out.println("Player 2 connected");
        } else {
            ch.send("FULL\n");
            ch.close();
        }
    }

    public synchronized void onDisconnect(ClientHandler ch) {
        if (c1 == ch) { c1 = null; System.out.println("Player 1 disconnected"); }
        if (c2 == ch) { c2 = null; System.out.println("Player 2 disconnected"); }
    }

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 5555;
        new ServerMain(port).start();
    }
}
