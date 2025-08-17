package net.footy.server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    final Socket socket;
    final ServerMain server;
    public volatile int playerId = 0;
    public final boolean[] inputs = new boolean[6];
    private BufferedReader in;
    private BufferedWriter out;

    public ClientHandler(Socket socket, ServerMain server) throws IOException {
        this.socket = socket; this.server = server;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("INPUT")) {
                    // INPUT <id> <up> <down> <left> <right> <kick>
                    String[] t = line.split(" ");
                    for (int i=0;i<6;i++) inputs[i] = "1".equals(t[i+2]);
                }
            }
        } catch (IOException ignored) {
        } finally {
            close();
            server.onDisconnect(this);
        }
    }

    public synchronized void send(String s) {
        try {
            out.write(s);
            out.flush();
        } catch (IOException ignored) {}
    }

    public void close() {
        try { socket.close(); } catch (IOException ignored) {}
    }
}
