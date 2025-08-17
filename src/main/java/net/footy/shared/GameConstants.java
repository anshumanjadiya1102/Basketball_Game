package net.footy.shared;

public final class GameConstants {
    private GameConstants() {}
    public static final int FIELD_WIDTH = 960;
    public static final int FIELD_HEIGHT = 540;
    public static final int GOAL_WIDTH = 140;
    public static final float DT = 1f / 60f; // server tick

    public static final float PLAYER_RADIUS = 18f;
    public static final float PLAYER_SPEED = 220f; // px/s
    public static final float PLAYER_FRICTION = 8f;

    public static final float BALL_RADIUS = 12f;
    public static final float BALL_FRICTION = 2.2f;

    public static final float KICK_RANGE = 34f;
    public static final float KICK_IMPULSE = 340f;
}
