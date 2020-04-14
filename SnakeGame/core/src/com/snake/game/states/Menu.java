package com.snake.game.states;

import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.Gdx.input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.snake.game.MainGame;

public class Menu extends State{

    
    private Texture bg;
    private Texture playBtn;
    public Menu(GameStateManager gsm) {
        super(gsm);
        bg = new Texture("bg.jpg");
        playBtn = new Texture("playbtn.png");
    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched() && ( input.getX() >= 588 && Gdx.input.getX() <= 692 ) && ( input.getY() >= 302 && input.getY() <= 360 ) ){
            gsm.set(new ModeSelect(gsm));
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
        sb.draw(playBtn, (MainGame.WIDTH / 2) - (playBtn.getWidth() / 2), MainGame.HEIGHT / 2);
        sb.end();
    }
    
}
