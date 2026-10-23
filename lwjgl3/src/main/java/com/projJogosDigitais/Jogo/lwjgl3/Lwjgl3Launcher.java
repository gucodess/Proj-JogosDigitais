package com.projJogosDigitais.Jogo.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.projJogosDigitais.Jogo.Main;

/** Inicia a aplicacao desktop (LWJGL3). */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        try {
            if (StartupHelper.startNewJvmIfRequired()) return; // Garante suporte ao macOS e ajuda no Windows.
            createApplication();
        } catch (Throwable t) {
            // Imprime qualquer erro de inicializacao da aplicacao no console (ex.: falha ao criar a janela/OpenGL).
            System.err.println("Falha ao iniciar a aplicacao desktop (Breakout Rehab):");
            t.printStackTrace();
        }
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Breakout Rehab");
        // O Vsync limita os quadros por segundo ao que o hardware consegue exibir e ajuda a eliminar
        // o tearing (rasgo) da imagem. Esse ajuste nem sempre funciona no Linux, por isso a linha seguinte e uma protecao.
        configuration.useVsync(true);
        // Limita o FPS a taxa de atualizacao do monitor ativo, +1 para tentar casar com taxas fracionarias.
        // O Vsync acima ja deve limitar o FPS real para acompanhar o monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        // Se remover a linha acima e deixar o Vsync em false, obtem-se FPS ilimitado, util para testes de desempenho,
        // mas que pode sobrecarregar o hardware e causar tearing.

        configuration.setWindowedMode(800, 600);
        // Estes arquivos podem ser trocados; ficam em lwjgl3/src/main/resources/ (ou na raiz de assets/).
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}
