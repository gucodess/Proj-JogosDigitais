package com.projJogosDigitais.Jogo.input;

/**
 * Abstração da fonte de controle da raquete.
 * Implementações: KeyboardController (padrão / fallback) e, no Milestone 2,
 * SerialController (potenciômetro via Arduino).
 */
public interface PaddleController {

    /**
     * Calcula a nova posição do CENTRO da raquete, em pixels do mundo virtual.
     *
     * @param delta          tempo desde o último quadro (s)
     * @param worldWidth     largura do mundo virtual
     * @param currentCenterX centro atual da raquete
     * @param paddleWidth    largura da raquete
     * @return novo centro X desejado
     */
    float computeCenterX(float delta, float worldWidth, float currentCenterX, float paddleWidth);

    /** @return true se a fonte de controle está ativa (ex.: dispositivo conectado). */
    boolean isConnected();

    /** Rótulo curto para exibir no HUD/menu (ex.: "Teclado" ou "Potenciometro COM3"). */
    String statusLabel();

    void dispose();
}
