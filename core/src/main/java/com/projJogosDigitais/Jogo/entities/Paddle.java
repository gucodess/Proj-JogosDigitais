package com.projJogosDigitais.Jogo.entities;

import com.badlogic.gdx.math.Rectangle;

/** Raquete controlada pelo jogador (teclado ou potenciometro). */
public class Paddle {
    public final Rectangle bounds;

    public Paddle(float width, float height, float centerX, float y) {
        bounds = new Rectangle(centerX - width / 2f, y, width, height);
    }

    public void setCenterX(float centerX) {
        bounds.x = centerX - bounds.width / 2f;
    }

    public float getCenterX() {
        return bounds.x + bounds.width / 2f;
    }

    public void clamp(float worldWidth) {
        if (bounds.x < 0) bounds.x = 0;
        if (bounds.x + bounds.width > worldWidth) bounds.x = worldWidth - bounds.width;
    }
}
