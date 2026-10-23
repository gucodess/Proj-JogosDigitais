package com.projJogosDigitais.Jogo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.projJogosDigitais.Jogo.GameConfig;
import com.projJogosDigitais.Jogo.Main;

/** Tela de menu: titulo, selecao de dificuldade e inicio. */
public class MenuScreen extends ScreenAdapter {
    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final GlyphLayout layout = new GlyphLayout();

    private int difficulty = 1; // 0 Facil, 1 Normal, 2 Dificil

    public MenuScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            difficulty = (difficulty + GameConfig.DIFFICULTY_NAMES.length - 1) % GameConfig.DIFFICULTY_NAMES.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            difficulty = (difficulty + 1) % GameConfig.DIFFICULTY_NAMES.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game, difficulty));
            dispose();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void center(String text, float y, Color color) {
        game.font.setColor(color);
        layout.setText(game.font, text);
        game.font.draw(game.batch, text, (GameConfig.WORLD_WIDTH - layout.width) / 2f, y);
    }

    @Override
    public void render(float delta) {
        handleInput();

        ScreenUtils.clear(0.05f, 0.06f, 0.12f, 1f);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Titulo
        game.fontBig.setColor(0f, 0.78f, 1f, 1f);
        layout.setText(game.fontBig, "BREAKOUT REHAB");
        game.fontBig.draw(game.batch, "BREAKOUT REHAB",
                (GameConfig.WORLD_WIDTH - layout.width) / 2f, 500f);

        center("Reabilitacao de punho e mao assistida por jogo", 440f, new Color(0.7f, 0.72f, 0.85f, 1f));

        center("ENTER / ESPACO  ->  Iniciar", 330f, new Color(0.47f, 0.86f, 0.63f, 1f));
        center("CIMA / BAIXO    ->  Dificuldade: " + GameConfig.DIFFICULTY_NAMES[difficulty],
                285f, new Color(0.8f, 0.82f, 1f, 1f));
        center("ESC             ->  Sair", 240f, new Color(0.6f, 0.63f, 0.78f, 1f));

        center("Controle: teclado (setas / A-D) ou mouse.  IoT (potenciometro) no Milestone 2.",
                90f, new Color(0.45f, 0.72f, 0.52f, 1f));

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
