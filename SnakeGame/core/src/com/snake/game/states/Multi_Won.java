package com.snake.game.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.snake.game.MainGame;

public class Multi_Won extends State{
    private Texture go;
    private BitmapFont player1score;
    private BitmapFont player2score;
    private int score1;
    private int score2;
    
    public Multi_Won(GameStateManager gsm, int score1, int score2) {
        super(gsm);
        go = new Texture("gameover.png");
        player1score = new BitmapFont();
        player2score = new BitmapFont();
        player1score.setColor(Color.WHITE);
        player2score.setColor(Color.WHITE);
        player1score.getData().setScale(3);
        player2score.getData().setScale(3);
        this.score1=score1;
        this.score2=score2;
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(go, 0, 0);
        player1score.draw(sb, "Player 1 ate " + score1 + " apples!", (MainGame.WIDTH / 2) - 500, 50);
        player2score.draw(sb, "Player 2 ate " + score2 + " apples!", (MainGame.WIDTH / 2), 50);
        sb.end();
    }
    
    
}
