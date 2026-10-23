package com.projJogosDigitais.Jogo;

/** Constantes globais do jogo (resolucao virtual e parametros de dificuldade). */
public final class GameConfig {
    public static final float WORLD_WIDTH = 800f;
    public static final float WORLD_HEIGHT = 600f;

    // Dificuldade: indice 0 = Facil, 1 = Normal, 2 = Dificil
    public static final String[] DIFFICULTY_NAMES = { "Facil", "Normal", "Dificil" };
    public static final float[] PADDLE_WIDTH      = { 170f, 120f, 90f };
    public static final float[] BALL_SPEED        = { 320f, 400f, 500f };

    public static final float PADDLE_HEIGHT = 18f;
    public static final float PADDLE_Y      = 40f;
    public static final float BALL_RADIUS   = 9f;

    public static final int   START_LIVES = 3;

    // Grade de blocos
    public static final int   BRICK_COLS  = 10;
    public static final int   BRICK_ROWS  = 5;
    public static final float BRICK_GAP   = 6f;
    public static final float BRICK_HEIGHT = 24f;
    public static final float BRICK_AREA_MARGIN = 40f;
    public static final float BRICK_AREA_TOP    = 560f;

    private GameConfig() {}
}
