package net.footy.shared;

import static net.footy.shared.GameConstants.*;

public class GameState {
    public Vec2 ball = new Vec2(FIELD_WIDTH/2f, FIELD_HEIGHT/2f);
    public Vec2 ballVel = new Vec2();

    public Vec2 p1 = new Vec2(FIELD_WIDTH*0.2f, FIELD_HEIGHT/2f);
    public Vec2 p1Vel = new Vec2();

    public Vec2 p2 = new Vec2(FIELD_WIDTH*0.8f, FIELD_HEIGHT/2f);
    public Vec2 p2Vel = new Vec2();

    public int score1 = 0, score2 = 0;

    public void update(boolean[] in1, boolean[] in2) {
        // players move
        stepPlayer(p1, p1Vel, in1);
        stepPlayer(p2, p2Vel, in2);

        // kicking
        if (in1[5] && Vec2.dst(p1, ball) <= KICK_RANGE) {
            Vec2 dir = ball.cpy().sub(p1).nor();
            ballVel.add(dir.mul(KICK_IMPULSE));
        }
        if (in2[5] && Vec2.dst(p2, ball) <= KICK_RANGE) {
            Vec2 dir = ball.cpy().sub(p2).nor();
            ballVel.add(dir.mul(KICK_IMPULSE));
        }

        // ball physics
        ball.add(ballVel.cpy().mul(DT));
        // friction
        ballVel.mul((float)Math.exp(-BALL_FRICTION * DT));

        // collisions with walls
        if (ball.x < BALL_RADIUS) { ball.x = BALL_RADIUS; ballVel.x = -ballVel.x*0.6f; }
        if (ball.x > FIELD_WIDTH - BALL_RADIUS) { ball.x = FIELD_WIDTH - BALL_RADIUS; ballVel.x = -ballVel.x*0.6f; }
        if (ball.y < BALL_RADIUS) { ball.y = BALL_RADIUS; ballVel.y = -ballVel.y*0.6f; }
        if (ball.y > FIELD_HEIGHT - BALL_RADIUS) { ball.y = FIELD_HEIGHT - BALL_RADIUS; ballVel.y = -ballVel.y*0.6f; }

        // simple player-ball repulsion
        repel(p1, ball);
        repel(p2, ball);

        // goals (left/right center)
        boolean goalLeft = ball.x < 10 && Math.abs(ball.y - FIELD_HEIGHT/2f) < GOAL_WIDTH/2f;
        boolean goalRight = ball.x > FIELD_WIDTH-10 && Math.abs(ball.y - FIELD_HEIGHT/2f) < GOAL_WIDTH/2f;
        if (goalLeft) { score2++; resetAfterGoal(-1); }
        if (goalRight) { score1++; resetAfterGoal(1); }
    }

    private void stepPlayer(Vec2 p, Vec2 v, boolean[] in) {
        float ax = 0, ay = 0;
        if (in[0]) ay -= 1; // up
        if (in[1]) ay += 1; // down
        if (in[2]) ax -= 1; // left
        if (in[3]) ax += 1; // right
        Vec2 a = new Vec2(ax, ay);
        if (a.len() > 0) a.nor().mul(PLAYER_SPEED);
        // integrate
        p.add(a.mul(DT));
        // clamp inside field
        p.x = Math.max(PLAYER_RADIUS, Math.min(FIELD_WIDTH-PLAYER_RADIUS, p.x));
        p.y = Math.max(PLAYER_RADIUS, Math.min(FIELD_HEIGHT-PLAYER_RADIUS, p.y));
        // (simple frictional damping on pseudo-velocity not kept here)
    }

    private void repel(Vec2 player, Vec2 b) {
        float minDist = PLAYER_RADIUS + BALL_RADIUS;
        float dx = b.x - player.x;
        float dy = b.y - player.y;
        float d2 = dx*dx + dy*dy;
        if (d2 < minDist*minDist && d2 > 0.0001f) {
            float d = (float)Math.sqrt(d2);
            float overlap = (minDist - d);
            float nx = dx/d, ny = dy/d;
            // push ball out and add a bit of impulse
            b.x += nx * overlap;
            b.y += ny * overlap;
            bVelImpulse(nx * overlap * 50f, ny * overlap * 50f);
        }
    }

    private void bVelImpulse(float ix, float iy) {
        ballVel.x += ix; ballVel.y += iy;
    }

    private void resetAfterGoal(int dir) {
        ball.set(FIELD_WIDTH/2f, FIELD_HEIGHT/2f);
        ballVel.set(220f * dir, 0);
        p1.set(FIELD_WIDTH*0.2f, FIELD_HEIGHT/2f);
        p2.set(FIELD_WIDTH*0.8f, FIELD_HEIGHT/2f);
    }
}
