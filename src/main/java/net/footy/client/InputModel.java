package net.footy.client;

public class InputModel {
    public boolean up, down, left, right, kick;
    public String toWire(int playerId) {
        return String.format("INPUT %d %d %d %d %d %d\n", playerId,
                up?1:0, down?1:0, left?1:0, right?1:0, kick?1:0);
    }
}
