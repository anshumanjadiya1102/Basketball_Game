package net.footy.client;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import static net.footy.shared.GameConstants.*;

public class GameView extends BorderPane {
    private final Canvas canvas = new Canvas(FIELD_WIDTH, FIELD_HEIGHT);
    private final Label score = new Label("0 : 0");
    private final Label youAre = new Label("Connecting...");

    // state replicated from server
    public float ballX, ballY, p1x, p1y, p2x, p2y; public int s1, s2;

    public GameView() {
        getStyleClass().add("root");
        HBox hud = new HBox(20, score, youAre);
        hud.getStyleClass().add("hud");
        hud.setPadding(new Insets(10));
        setTop(hud);
        setCenter(canvas);

        score.getStyleClass().add("score");
        youAre.getStyleClass().add("you-are");

        new AnimationTimer(){
            @Override public void handle(long now){ draw(); }
        }.start();
    }

    public Scene createScene() {
        Scene sc = new Scene(this);
        sc.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        return sc;
    }

    public void setAssigned(int id){
        youAre.setText("You are Player " + id + (id==1?" (WASD + SPACE)":" (Arrows + ENTER)"));
    }

    public void applyState(float bx,float by,float a,float b,float c,float d,int s1,int s2){
        this.ballX=bx; this.ballY=by; this.p1x=a; this.p1y=b; this.p2x=c; this.p2y=d; this.s1=s1; this.s2=s2;
        score.setText(s1 + " : " + s2);
    }

    private void draw(){
        GraphicsContext g = canvas.getGraphicsContext2D();
        // field
        g.setFill(Color.web("#0b6623"));
        g.fillRect(0,0,FIELD_WIDTH,FIELD_HEIGHT);
        g.setStroke(Color.web("#d9f7e3"));
        g.setLineWidth(3);
        g.strokeRect(5,5, FIELD_WIDTH-10, FIELD_HEIGHT-10);
        // halfway line and circle
        g.strokeLine(FIELD_WIDTH/2.0, 5, FIELD_WIDTH/2.0, FIELD_HEIGHT-5);
        g.strokeOval(FIELD_WIDTH/2.0-60, FIELD_HEIGHT/2.0-60, 120, 120);
        // goals
        g.setLineWidth(5);
        g.strokeLine(5, FIELD_HEIGHT/2.0-GOAL_WIDTH/2.0, 5, FIELD_HEIGHT/2.0+GOAL_WIDTH/2.0);
        g.strokeLine(FIELD_WIDTH-5, FIELD_HEIGHT/2.0-GOAL_WIDTH/2.0, FIELD_WIDTH-5, FIELD_HEIGHT/2.0+GOAL_WIDTH/2.0);

        // players
        // P1 (blue)
        g.setFill(Color.web("#3b82f6"));
        g.fillOval(p1x-PLAYER_RADIUS, p1y-PLAYER_RADIUS, PLAYER_RADIUS*2, PLAYER_RADIUS*2);
        // P2 (red)
        g.setFill(Color.web("#ef4444"));
        g.fillOval(p2x-PLAYER_RADIUS, p2y-PLAYER_RADIUS, PLAYER_RADIUS*2, PLAYER_RADIUS*2);
        // ball (white)
        g.setFill(Color.WHITE);
        g.fillOval(ballX-BALL_RADIUS, ballY-BALL_RADIUS, BALL_RADIUS*2, BALL_RADIUS*2);
    }
}
