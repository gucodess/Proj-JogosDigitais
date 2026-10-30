package com.projJogosDigitais.Jogo.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.fazecast.jSerialComm.SerialPort;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controle da raquete pelo potenciometro de um Arduino, via porta serial (USB).
 *
 * O Arduino envia o valor analogico (0..1023) como texto, uma leitura por linha,
 * a 115200 baud. Uma thread daemon le a porta sem travar a renderizacao e guarda
 * a ultima leitura num AtomicInteger. O valor e mapeado para a posicao horizontal
 * da raquete, com interpolacao (lerp) para suavizar o ruido do sinal analogico.
 */
public class SerialController implements PaddleController {

    private static final int   BAUD      = 115200;
    private static final int   RAW_MIN   = 0;
    private static final int   RAW_MAX   = 1023;
    private static final float SMOOTHING = 0.35f; // 0 = travado, 1 = sem suavizacao

    private SerialPort port;
    private Thread readThread;
    private volatile boolean running;
    private volatile boolean receiving;
    private final AtomicInteger lastRaw = new AtomicInteger((RAW_MIN + RAW_MAX) / 2);
    private String label = "Potenciometro (desconectado)";

    /** Tenta abrir a porta serial; devolve o controlador pronto ou null se nao houver dispositivo. */
    public static SerialController tryOpen() {
        SerialController c = new SerialController();
        return c.open() ? c : null;
    }

    private boolean open() {
        try {
            SerialPort[] all = SerialPort.getCommPorts();
            System.out.println("[SerialController] Portas disponiveis: " + all.length);
            for (SerialPort p : all) {
                System.out.println("  - " + p.getSystemPortName() + " | " + p.getDescriptivePortName());
            }
            SerialPort chosen = selectPort();
            if (chosen == null) {
                System.out.println("[SerialController] Nenhuma porta serial encontrada. O jogo usara o teclado.");
                return false;
            }
            System.out.println("[SerialController] Porta escolhida: " + chosen.getSystemPortName()
                    + " (" + chosen.getDescriptivePortName() + ")");
            chosen.setBaudRate(BAUD);
            chosen.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
            if (!chosen.openPort()) {
                System.out.println("[SerialController] NAO consegui abrir " + chosen.getSystemPortName()
                        + ". A porta esta ocupada? (feche o Serial Monitor do Arduino IDE).");
                return false;
            }
            this.port = chosen;
            this.label = "Potenciometro " + chosen.getSystemPortName();
            System.out.println("[SerialController] Porta " + chosen.getSystemPortName() + " aberta a " + BAUD + " baud.");
            startReader();
            return true;
        } catch (Throwable t) {
            // Erro ao abrir a porta: imprime e segue sem serial (o jogo cai para teclado).
            Gdx.app.error("SerialController", "Falha ao abrir a porta serial", t);
            t.printStackTrace();
            return false;
        }
    }

    /**
     * Escolhe a porta: respeita -Dserial.port=COMx se informado; senao procura uma
     * descricao tipica de Arduino; senao usa a primeira porta disponivel.
     */
    private SerialPort selectPort() {
        SerialPort[] ports = SerialPort.getCommPorts();
        if (ports == null || ports.length == 0) return null;

        String override = System.getProperty("serial.port");
        if (override != null && override.length() > 0) {
            for (SerialPort p : ports) {
                if (p.getSystemPortName().equalsIgnoreCase(override)) return p;
            }
        }
        for (SerialPort p : ports) {
            String d = (p.getDescriptivePortName() + " " + p.getPortDescription()).toLowerCase();
            if (d.contains("arduino") || d.contains("ch340") || d.contains("wch")
                    || d.contains("usb serial") || d.contains("usb-serial")) {
                return p;
            }
        }
        return ports[0];
    }

    private void startReader() {
        running = true;
        readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                byte[] buf = new byte[256];
                InputStream in = port.getInputStream();
                try {
                    int n;
                    while (running && (n = in.read(buf)) >= 0) {
                        for (int i = 0; i < n; i++) {
                            char ch = (char) (buf[i] & 0xFF);
                            if (ch == '\n' || ch == '\r') {
                                if (sb.length() > 0) {
                                    parseLine(sb.toString());
                                    sb.setLength(0);
                                }
                            } else {
                                sb.append(ch);
                                if (sb.length() > 16) sb.setLength(0); // descarta linha anomala
                            }
                        }
                    }
                } catch (Throwable t) {
                    if (running) {
                        Gdx.app.error("SerialController", "Erro na leitura serial", t);
                        t.printStackTrace();
                    }
                }
            }
        }, "serial-reader");
        readThread.setDaemon(true);
        readThread.start();
    }

    private void parseLine(String line) {
        try {
            int v = Integer.parseInt(line.trim());
            if (!receiving) {
                System.out.println("[SerialController] Recebendo dados do potenciometro (primeiro valor: " + v + ").");
            }
            lastRaw.set(MathUtils.clamp(v, RAW_MIN, RAW_MAX));
            receiving = true;
        } catch (NumberFormatException ignore) {
            // linha nao numerica (ruido na inicializacao): ignora
        }
    }

    @Override
    public float computeCenterX(float delta, float worldWidth, float currentCenterX, float paddleWidth) {
        float half = paddleWidth / 2f;
        float t = MathUtils.clamp((lastRaw.get() - RAW_MIN) / (float) (RAW_MAX - RAW_MIN), 0f, 1f);
        float targetCenter = half + t * (worldWidth - paddleWidth);
        return MathUtils.lerp(currentCenterX, targetCenter, SMOOTHING);
    }

    @Override
    public boolean isConnected() {
        return port != null && port.isOpen() && receiving;
    }

    @Override
    public String statusLabel() {
        return label;
    }

    @Override
    public void dispose() {
        running = false;
        if (readThread != null) readThread.interrupt();
        try {
            if (port != null && port.isOpen()) port.closePort();
        } catch (Throwable ignore) {
            // nada a fazer ao fechar
        }
    }
}
