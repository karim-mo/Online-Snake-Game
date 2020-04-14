package com.snake.game.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.snake.game.MainGame;

public class GameOver extends State{
    private Texture go;
    private BitmapFont playerscore;
    private int score;
    
    public GameOver(GameStateManager gsm, int score) {
        super(gsm);
        go = new Texture("gameover.png");
        playerscore = new BitmapFont();
        playerscore.setColor(Color.WHITE);
        playerscore.getData().setScale(3);
        this.score = score;
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
        playerscore.draw(sb, "You ate " + score + " apples!", (MainGame.WIDTH / 2) - 200, 50);
        sb.end();
    }
    
}
