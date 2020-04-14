package com.snake.game.states;

import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.Gdx.input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.snake.game.MainGame;

public class ModeSelect extends State {
    
    private Texture bg;
    private Texture singleplayer;
    private Texture multiplayer;
    private Texture onevone;

    public ModeSelect(GameStateManager gsm) {
        super(gsm);
        bg = new Texture("bg.jpg");
        singleplayer = new Texture("singleplayer.png");
        multiplayer = new Texture("multiplayer.png");
        onevone = new Texture("1v1.png");
    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched() && ( input.getX() >= 588 && Gdx.input.getX() <= 692 ) && ( input.getY() >= 302 && input.getY() <= 360 ) ){
            gsm.set(new Playing(gsm));
        }
        if(Gdx.input.justTouched() && ( input.getX() >= 588 && Gdx.input.getX() <= 692 ) && ( input.getY() >= 402 && input.getY() <= 460 ) ){
            gsm.set(new MultiplayerONLINE(gsm));
        }        
        if(Gdx.input.justTouched() && ( input.getX() >= 588 && Gdx.input.getX() <= 692 ) && ( input.getY() >= 502 && input.getY() <= 560 ) ){
            gsm.set(new MultiplayerLCL(gsm));
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(bg, 0, 0, MainGame.WIDTH, MainGame.HEIGHT);
        sb.draw(singleplayer, (MainGame.WIDTH / 2) - (singleplayer.getWidth() / 2), MainGame.HEIGHT / 2);
        sb.draw(multiplayer, (MainGame.WIDTH / 2) - (multiplayer.getWidth() / 2), (MainGame.HEIGHT / 2) - 100);
        sb.draw(onevone, (MainGame.WIDTH / 2) - (onevone.getWidth() / 2), (MainGame.HEIGHT / 2) - 200);
        sb.end();
    }
    
}
