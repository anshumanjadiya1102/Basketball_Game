package net.footy.shared;

public class Vec2 {
    public float x, y;
    public Vec2() { this(0,0); }
    public Vec2(float x, float y) { this.x = x; this.y = y; }
    public Vec2 set(float nx, float ny) { this.x = nx; this.y = ny; return this; }
    public Vec2 add(Vec2 v) { this.x += v.x; this.y += v.y; return this; }
    public Vec2 add(float ax, float ay) { this.x += ax; this.y += ay; return this; }
    public Vec2 sub(Vec2 v) { this.x -= v.x; this.y -= v.y; return this; }
    public Vec2 mul(float s) { this.x *= s; this.y *= s; return this; }
    public float len() { return (float)Math.sqrt(x*x + y*y); }
    public Vec2 nor() { float l = len(); if (l>1e-6f) { x/=l; y/=l; } return this; }
    public Vec2 cpy() { return new Vec2(x,y); }
    public static float dst(Vec2 a, Vec2 b) { float dx=a.x-b.x, dy=a.y-b.y; return (float)Math.sqrt(dx*dx+dy*dy); }
}
