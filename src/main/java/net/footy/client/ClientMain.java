package net.footy.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ClientMain extends Application {
    private static String host = "localhost";
    private static int port = 5555;

    private volatile int playerId = 0;
    private InputModel input = new InputModel();
    private BufferedReader in; private BufferedWriter out;
    private GameView view = new GameView();

    @Override public void start(Stage stage) throws Exception {
        Scene scene = view.createScene();
        stage.setTitle("JavaFX Football — Multiplayer");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // input mapping
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()){
                case W -> input.up = true; case S -> input.down = true; case A -> input.left = true; case D -> input.right = true; case SPACE -> input.kick = true;
                case UP -> input.up = true; case DOWN -> input.down = true; case LEFT -> input.left = true; case RIGHT -> input.right = true; case ENTER -> input.kick = true;
            }
            sendInputs();
        });
        scene.setOnKeyReleased(e -> {
            switch (e.getCode()){
                case W -> input.up = false; case S -> input.down = false; case A -> input.left = false; case D -> input.right = false; case SPACE -> input.kick = false;
                case UP -> input.up = false; case DOWN -> input.down = false; case LEFT -> input.left = false; case RIGHT -> input.right = false; case ENTER -> input.kick = false;
            }
            sendInputs();
        });

        new Thread(this::netLoop, "netLoop").start();
        new Thread(this::inputLoop, "inputLoop").start();
    }

    private void netLoop(){
        try (Socket s = new Socket(host, port)){
            s.setTcpNoDelay(true);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            String line;
            while ((line = in.readLine()) != null){
                if (line.startsWith("ASSIGN")){
                    playerId = Integer.parseInt(line.split(" ")[1]);
                    int id = playerId;
                    Platform.runLater(() -> view.setAssigned(id));
                } else if (line.startsWith("STATE")){
                    // STATE bx by p1x p1y p2x p2y s1 s2
                    String[] t = line.split(" ");
                    float bx = Float.parseFloat(t[1]);
                    float by = Float.parseFloat(t[2]);
                    float p1x = Float.parseFloat(t[3]);
                    float p1y = Float.parseFloat(t[4]);
                    float p2x = Float.parseFloat(t[5]);
                    float p2y = Float.parseFloat(t[6]);
                    int s1 = Integer.parseInt(t[7]);
                    int s2 = Integer.parseInt(t[8]);
                    Platform.runLater(() -> view.applyState(bx,by,p1x,p1y,p2x,p2y,s1,s2));
                } else if (line.startsWith("FULL")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> view.setAssigned(0));
        }
        Platform.exit();
    }

    private void inputLoop(){
        try {
            while (true){
                sendInputs();
                Thread.sleep(50); // 20 Hz
            }
        } catch (InterruptedException ignored) {}
    }

    private synchronized void sendInputs(){
        if (out == null || playerId == 0) return;
        try { out.write(input.toWire(playerId)); out.flush(); } catch (IOException ignored) {}
    }

    public static void main(String[] args){
        if (args.length >= 1) host = args[0];
        if (args.length >= 2) port = Integer.parseInt(args[1]);
        launch(args);
    }
}
