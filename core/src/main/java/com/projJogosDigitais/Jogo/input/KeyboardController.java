package com.projJogosDigitais.Jogo.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Controle por teclado (setas ou A/D) e também pelo mouse.
 * É o controle padrão e o fallback quando o dispositivo IoT não está presente.
 */
public class KeyboardController implements PaddleController {

    private static final float SPEED = 650f; // pixels por segundo

    @Override
    public float computeCenterX(float delta, float worldWidth, float currentCenterX, float paddleWidth) {
        // O mouse/toque tem prioridade quando o botão está pressionado.
        if (Gdx.input.isTouched()) {
            float worldX = Gdx.input.getX() * (worldWidth / Gdx.graphics.getWidth());
            return worldX;
        }
        float x = currentCenterX;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += SPEED * delta;
        }
        return x;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public String statusLabel() {
        return "Teclado (setas / A-D) ou mouse";
    }

    @Override
    public void dispose() {
        // nada a liberar
    }
}
