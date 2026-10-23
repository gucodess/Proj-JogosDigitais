package com.projJogosDigitais.Jogo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.projJogosDigitais.Jogo.GameConfig;
import com.projJogosDigitais.Jogo.Main;
import com.projJogosDigitais.Jogo.entities.Ball;
import com.projJogosDigitais.Jogo.entities.Brick;
import com.projJogosDigitais.Jogo.entities.Paddle;
import com.projJogosDigitais.Jogo.input.KeyboardController;
import com.projJogosDigitais.Jogo.input.PaddleController;

/** Tela principal de jogo: física, colisões, pontuação e vidas. */
public class GameScreen extends ScreenAdapter {

    // Uma cor por linha de blocos.
    private static final Color[] ROW_COLORS = {
            new Color(0.94f, 0.33f, 0.31f, 1f),
            new Color(1f, 0.65f, 0.15f, 1f),
            new Color(1f, 0.93f, 0.35f, 1f),
            new Color(0.40f, 0.73f, 0.42f, 1f),
            new Color(0.26f, 0.65f, 0.96f, 1f)
    };

    private final Main game;
    private final int difficulty;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final PaddleController controller;

    private final Paddle paddle;
    private final Ball ball;
    private final Array<Brick> bricks = new Array<Brick>();

    private final float ballSpeed;
    private int score = 0;
    private int lives = GameConfig.START_LIVES;
    private boolean paused = false;

    public GameScreen(Main game, int difficulty) {
        this.game = game;
        this.difficulty = difficulty;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        this.controller = new KeyboardController();

        float pw = GameConfig.PADDLE_WIDTH[difficulty];
        this.ballSpeed = GameConfig.BALL_SPEED[difficulty];
        this.paddle = new Paddle(pw, GameConfig.PADDLE_HEIGHT, GameConfig.WORLD_WIDTH / 2f, GameConfig.PADDLE_Y);
        this.ball = new Ball(GameConfig.BALL_RADIUS);

        buildBricks();
        resetBall();
    }

    /** Monta a grade de blocos; as linhas de cima valem mais pontos. */
    private void buildBricks() {
        bricks.clear();
        int cols = GameConfig.BRICK_COLS, rows = GameConfig.BRICK_ROWS;
        float gap = GameConfig.BRICK_GAP;
        float totalW = GameConfig.WORLD_WIDTH - 2f * GameConfig.BRICK_AREA_MARGIN;
        float brickW = (totalW - (cols - 1) * gap) / cols;
        float brickH = GameConfig.BRICK_HEIGHT;
        for (int r = 0; r < rows; r++) {
            float y = GameConfig.BRICK_AREA_TOP - r * (brickH + gap);
            Color color = ROW_COLORS[r % ROW_COLORS.length];
            int points = (rows - r) * 10; // linhas de cima valem mais
            for (int c = 0; c < cols; c++) {
                float x = GameConfig.BRICK_AREA_MARGIN + c * (brickW + gap);
                bricks.add(new Brick(x, y, brickW, brickH, color, points));
            }
        }
    }

    /** Prende a bola sobre a raquete, aguardando o lançamento. */
    private void resetBall() {
        ball.stuck = true;
        ball.vx = 0f;
        ball.vy = 0f;
        ball.x = paddle.getCenterX();
        ball.y = paddle.bounds.y + paddle.bounds.height + ball.radius;
    }

    /** Lança a bola para cima com um ângulo aleatório a partir da vertical. */
    private void launchBall() {
        ball.stuck = false;
        float angle = MathUtils.random(-30f, 30f); // graus a partir da vertical
        ball.vx = ballSpeed * MathUtils.sinDeg(angle);
        ball.vy = ballSpeed * MathUtils.cosDeg(angle);
    }

    private void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            dispose();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            paused = !paused;
        }
        if (paused) return;

        // Move a raquete conforme a fonte de controle (teclado por enquanto).
        float newCenter = controller.computeCenterX(delta, GameConfig.WORLD_WIDTH, paddle.getCenterX(), paddle.bounds.width);
        paddle.setCenterX(newCenter);
        paddle.clamp(GameConfig.WORLD_WIDTH);

        if (ball.stuck) {
            // Bola acompanha a raquete até o jogador lançar.
            ball.x = paddle.getCenterX();
            ball.y = paddle.bounds.y + paddle.bounds.height + ball.radius;
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched()) {
                launchBall();
            }
            return;
        }

        ball.x += ball.vx * delta;
        ball.y += ball.vy * delta;

        // Colisão com as paredes (laterais e topo).
        if (ball.x - ball.radius < 0) { ball.x = ball.radius; ball.vx = -ball.vx; }
        if (ball.x + ball.radius > GameConfig.WORLD_WIDTH) { ball.x = GameConfig.WORLD_WIDTH - ball.radius; ball.vx = -ball.vx; }
        if (ball.y + ball.radius > GameConfig.WORLD_HEIGHT) { ball.y = GameConfig.WORLD_HEIGHT - ball.radius; ball.vy = -ball.vy; }

        // Colisão com a raquete: o ângulo de saída depende do ponto de contato.
        Rectangle b = ball.bounds();
        if (ball.vy < 0 && b.overlaps(paddle.bounds)) {
            float rel = MathUtils.clamp((ball.x - paddle.getCenterX()) / (paddle.bounds.width / 2f), -1f, 1f);
            float maxAngle = 60f;
            float angle = rel * maxAngle;
            ball.vx = ballSpeed * MathUtils.sinDeg(angle);
            ball.vy = ballSpeed * MathUtils.cosDeg(angle);
            ball.y = paddle.bounds.y + paddle.bounds.height + ball.radius;
        }

        // Colisão com os blocos (no máximo um por quadro).
        for (int i = 0; i < bricks.size; i++) {
            Brick brick = bricks.get(i);
            if (!brick.alive) continue;
            if (!ball.bounds().overlaps(brick.bounds)) continue;

            brick.alive = false;
            score += brick.points;

            // Decide qual eixo refletir comparando a sobreposição em X e em Y.
            float bcx = brick.bounds.x + brick.bounds.width / 2f;
            float bcy = brick.bounds.y + brick.bounds.height / 2f;
            float dx = ball.x - bcx;
            float dy = ball.y - bcy;
            float combinedHalfW = brick.bounds.width / 2f + ball.radius;
            float combinedHalfH = brick.bounds.height / 2f + ball.radius;
            float overlapX = combinedHalfW - Math.abs(dx);
            float overlapY = combinedHalfH - Math.abs(dy);
            if (overlapX < overlapY) {
                ball.vx = -ball.vx;
                ball.x += Math.signum(dx) * overlapX;
            } else {
                ball.vy = -ball.vy;
                ball.y += Math.signum(dy) * overlapY;
            }
            break;
        }

        // Bola caiu abaixo da tela: perde uma vida.
        if (ball.y + ball.radius < 0) {
            lives--;
            if (lives <= 0) {
                game.setScreen(new GameOverScreen(game, false, score, difficulty));
                dispose();
                return;
            }
            resetBall();
        }

        // Todos os blocos destruídos: vitória.
        if (allBricksCleared()) {
            game.setScreen(new GameOverScreen(game, true, score, difficulty));
            dispose();
        }
    }

    private boolean allBricksCleared() {
        for (int i = 0; i < bricks.size; i++) {
            if (bricks.get(i).alive) return false;
        }
        return true;
    }

    @Override
    public void render(float delta) {
        update(Math.min(delta, 1f / 30f)); // limita o delta para evitar tunneling em quedas de FPS

        ScreenUtils.clear(0.05f, 0.06f, 0.12f, 1f);
        camera.update();

        // Desenho das formas (blocos, raquete e bola).
        game.shapes.setProjectionMatrix(camera.combined);
        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < bricks.size; i++) {
            Brick brick = bricks.get(i);
            if (!brick.alive) continue;
            game.shapes.setColor(brick.color);
            game.shapes.rect(brick.bounds.x, brick.bounds.y, brick.bounds.width, brick.bounds.height);
        }
        game.shapes.setColor(0f, 0.78f, 1f, 1f);
        game.shapes.rect(paddle.bounds.x, paddle.bounds.y, paddle.bounds.width, paddle.bounds.height);
        game.shapes.setColor(Color.WHITE);
        game.shapes.circle(ball.x, ball.y, ball.radius);
        game.shapes.end();

        // HUD (texto): pontuação, vidas e dificuldade.
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.font.setColor(1f, 0.84f, 0.04f, 1f);
        game.font.draw(game.batch, "PONTOS: " + score, 20f, GameConfig.WORLD_HEIGHT - 12f);
        game.font.setColor(0.47f, 0.86f, 0.63f, 1f);
        game.font.draw(game.batch, "VIDAS: " + lives, GameConfig.WORLD_WIDTH - 110f, GameConfig.WORLD_HEIGHT - 12f);
        game.font.setColor(0.6f, 0.71f, 1f, 1f);
        game.font.draw(game.batch, "FASE: " + GameConfig.DIFFICULTY_NAMES[difficulty],
                GameConfig.WORLD_WIDTH / 2f - 50f, GameConfig.WORLD_HEIGHT - 12f);

        if (ball.stuck) {
            game.font.setColor(0.8f, 0.82f, 0.9f, 1f);
            game.font.draw(game.batch, "ESPACO / clique para lancar a bola", GameConfig.WORLD_WIDTH / 2f - 140f, 120f);
        }
        if (paused) {
            game.fontBig.setColor(Color.WHITE);
            game.fontBig.draw(game.batch, "PAUSA (P)", GameConfig.WORLD_WIDTH / 2f - 90f, GameConfig.WORLD_HEIGHT / 2f);
        }
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        controller.dispose();
    }
}
