package com.projJogosDigitais.Jogo.entities;

import com.badlogic.gdx.math.Rectangle;

/** Bola do jogo. Enquanto 'stuck' acompanha a raquete ate o lancamento. */
public class Ball {
    public float x, y;
    public float vx, vy;
    public final float radius;
    public boolean stuck = true;

    private final Rectangle cached = new Rectangle();

    public Ball(float radius) {
        this.radius = radius;
    }

    /** Retangulo envolvente (AABB) reutilizado para colisoes. */
    public Rectangle bounds() {
        cached.set(x - radius, y - radius, radius * 2f, radius * 2f);
        return cached;
    }
}
