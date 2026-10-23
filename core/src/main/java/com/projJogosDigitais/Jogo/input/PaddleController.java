package com.projJogosDigitais.Jogo.input;

/**
 * Abstracao da fonte de controle da raquete.
 * Implementacoes: KeyboardController (padrao / fallback) e, no Milestone 2,
 * SerialController (potenciometro via Arduino).
 */
public interface PaddleController {

    /**
     * Calcula a nova posicao do CENTRO da raquete, em pixels do mundo virtual.
     *
     * @param delta          tempo desde o ultimo quadro (s)
     * @param worldWidth     largura do mundo virtual
     * @param currentCenterX centro atual da raquete
     * @param paddleWidth    largura da raquete
     * @return novo centro X desejado
     */
    float computeCenterX(float delta, float worldWidth, float currentCenterX, float paddleWidth);

    /** @return true se a fonte de controle esta ativa (ex.: dispositivo conectado). */
    boolean isConnected();

    /** Rotulo curto para exibir no HUD/menu (ex.: "Teclado" ou "Potenciometro COM3"). */
    String statusLabel();

    void dispose();
}
