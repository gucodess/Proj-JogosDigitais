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

/** Tela de fim de jogo (vitória ou derrota) com a pontuação final. */
public class GameOverScreen extends ScreenAdapter {
    private final Main game;
    private final boolean won;
    private final int score;
    private final int difficulty;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final GlyphLayout layout = new GlyphLayout();

    public GameOverScreen(Main game, boolean won, int score, int difficulty) {
        this.game = game;
        this.won = won;
        this.score = score;
        this.difficulty = difficulty;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
    }

    /** Desenha um texto centralizado usando a fonte indicada (grande ou padrão). */
    private void center(Object font, String text, float y) {
        if (font == game.fontBig) {
            layout.setText(game.fontBig, text);
            game.fontBig.draw(game.batch, text, (GameConfig.WORLD_WIDTH - layout.width) / 2f, y);
        } else {
            layout.setText(game.font, text);
            game.font.draw(game.batch, text, (GameConfig.WORLD_WIDTH - layout.width) / 2f, y);
        }
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new MenuScreen(game));
            dispose();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            // Reinicia a partida na mesma dificuldade.
            game.setScreen(new GameScreen(game, difficulty));
            dispose();
            return;
        }

        ScreenUtils.clear(0.05f, 0.06f, 0.12f, 1f);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        if (won) {
            game.fontBig.setColor(0.47f, 0.86f, 0.63f, 1f);
            center(game.fontBig, "VOCE VENCEU!", 400f);
        } else {
            game.fontBig.setColor(0.94f, 0.33f, 0.31f, 1f);
            center(game.fontBig, "FIM DE JOGO", 400f);
        }

        game.font.setColor(Color.WHITE);
        center(game.font, "Pontuacao final: " + score, 310f);
        game.font.setColor(0.8f, 0.82f, 1f, 1f);
        center(game.font, "ENTER / ESPACO  ->  Menu", 240f);
        center(game.font, "R  ->  Jogar novamente", 205f);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
