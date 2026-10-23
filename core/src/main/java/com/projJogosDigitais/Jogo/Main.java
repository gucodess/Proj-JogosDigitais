package com.projJogosDigitais.Jogo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.projJogosDigitais.Jogo.screens.MenuScreen;

/**
 * Breakout Rehab - ponto de entrada compartilhado por todas as plataformas.
 * Mantém os recursos globais (batch, shapes, fontes) e gerencia a troca de telas.
 * Centraliza também o tratamento de erros: qualquer exceção lançada durante a
 * atualização ou a renderização de uma tela é capturada e impressa aqui.
 */
public class Main extends Game {
    private static final String TAG = "BreakoutRehab";

    public SpriteBatch batch;
    public ShapeRenderer shapes;
    public BitmapFont font;     // texto do HUD
    public BitmapFont fontBig;  // títulos

    @Override
    public void create() {
        try {
            batch = new SpriteBatch();
            shapes = new ShapeRenderer();
            font = new BitmapFont();
            fontBig = new BitmapFont();
            fontBig.getData().setScale(2f);
            setScreen(new MenuScreen(this));
        } catch (Exception e) {
            // Falha na inicialização: imprime o erro para facilitar o diagnóstico.
            Gdx.app.error(TAG, "Falha ao inicializar o jogo", e);
            e.printStackTrace();
        }
    }

    @Override
    public void render() {
        try {
            // Delega para a tela ativa; o try/catch cobre todas as telas de uma vez.
            super.render();
        } catch (Exception e) {
            // Exibe no console o erro ocorrido em tempo de execução, sem derrubar o processo silenciosamente.
            Gdx.app.error(TAG, "Erro durante a atualização/renderização da tela", e);
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        try {
            if (getScreen() != null) getScreen().dispose();
            if (batch != null) batch.dispose();
            if (shapes != null) shapes.dispose();
            if (font != null) font.dispose();
            if (fontBig != null) fontBig.dispose();
        } catch (Exception e) {
            // Erros ao liberar recursos não devem mascarar a causa real; apenas registramos.
            Gdx.app.error(TAG, "Erro ao liberar os recursos do jogo", e);
            e.printStackTrace();
        }
    }
}
