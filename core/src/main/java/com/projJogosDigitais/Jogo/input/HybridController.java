package com.projJogosDigitais.Jogo.input;

/**
 * Controle hibrido com fallback automatico.
 * Usa o potenciometro (SerialController) enquanto ele estiver enviando dados;
 * caso contrario, usa o teclado/mouse. Assim o jogo funciona com ou sem o
 * Arduino conectado, sem precisar mudar nada no GameScreen.
 */
public class HybridController implements PaddleController {

    private final SerialController serial;      // pode ser null se nao houver dispositivo
    private final KeyboardController keyboard = new KeyboardController();

    public HybridController() {
        this.serial = SerialController.tryOpen();
    }

    @Override
    public float computeCenterX(float delta, float worldWidth, float currentCenterX, float paddleWidth) {
        if (serial != null && serial.isConnected()) {
            return serial.computeCenterX(delta, worldWidth, currentCenterX, paddleWidth);
        }
        return keyboard.computeCenterX(delta, worldWidth, currentCenterX, paddleWidth);
    }

    @Override
    public boolean isConnected() {
        return serial != null && serial.isConnected();
    }

    @Override
    public String statusLabel() {
        if (serial != null && serial.isConnected()) {
            return serial.statusLabel();
        }
        if (serial != null) {
            return keyboard.statusLabel() + " (aguardando potenciometro)";
        }
        return keyboard.statusLabel();
    }

    @Override
    public void dispose() {
        if (serial != null) serial.dispose();
        keyboard.dispose();
    }
}
