package com.projJogosDigitais.Jogo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.projJogosDigitais.Jogo.screens.MenuScreen;

/**
 * Breakout Rehab - ponto de entrada compartilhado por todas as plataformas.
 * Mantem recursos globais (batch, shapes, fontes) e gerencia a troca de telas.
 */
public class Main extends Game {
    public SpriteBatch batch;
    public ShapeRenderer shapes;
    public BitmapFont font;     // HUD
    public BitmapFont fontBig;  // titulos

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        font = new BitmapFont();
        fontBig = new BitmapFont();
        fontBig.getData().setScale(2f);
        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        if (getScreen() != null) getScreen().dispose();
        batch.dispose();
        shapes.dispose();
        font.dispose();
        fontBig.dispose();
    }
}
