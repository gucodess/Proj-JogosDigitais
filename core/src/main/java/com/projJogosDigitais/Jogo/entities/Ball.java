package com.projJogosDigitais.Jogo.entities;

import com.badlogic.gdx.math.Rectangle;

/** Bola do jogo. Enquanto 'stuck' (presa), acompanha a raquete até o lançamento. */
public class Ball {
    public float x, y;
    public float vx, vy;
    public final float radius;
    public boolean stuck = true;

    private final Rectangle cached = new Rectangle();

    public Ball(float radius) {
        this.radius = radius;
    }

    /** Retângulo envolvente (AABB) reutilizado para as colisões. */
    public Rectangle bounds() {
        cached.set(x - radius, y - radius, radius * 2f, radius * 2f);
        return cached;
    }
}
