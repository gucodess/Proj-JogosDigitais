package com.projJogosDigitais.Jogo.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

/** Bloco destrutível da grade. */
public class Brick {
    public final Rectangle bounds;
    public final Color color;
    public final int points;
    public boolean alive = true;

    public Brick(float x, float y, float w, float h, Color color, int points) {
        this.bounds = new Rectangle(x, y, w, h);
        this.color = color;
        this.points = points;
    }
}
