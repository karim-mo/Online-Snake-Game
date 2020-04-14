package com.snake.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.snake.game.MainGame;
import com.snake.game.sprites.Apple;
import com.snake.game.sprites.Snake;

public class MultiplayerLCL extends State{
    private Array<Apple> apples = new Array<Apple>();
    private Snake player1;
    private Snake player2;
    private Texture PauseUI;
    private Texture background;
    float timer = 0.05f, timer2 = 0;
    private final int APPLES = 2;
    private boolean started = false;
    boolean isPaused = false;
    private String[] texture = {"rightmouth.png", "leftmouth.png", "upmouth.png", "downmouth.png", "snakeimage.png"};
    private Texture[] mainPlayer = new Texture[5];
    private Texture app = new Texture("enemy.png");
    
    public MultiplayerLCL(GameStateManager gsm) {
        super(gsm);      
        for(int i = 0; i < texture.length; i++){
            mainPlayer[i] = new Texture(texture[i]);
        }
        player1 = new Snake(100, 200, gsm, mainPlayer);
        player2 = new Snake(600, 200, gsm, mainPlayer);
        PauseUI = new Texture("Pause.png"); 
        background = new Texture("bg.jpg");
        for(int i = 0; i < APPLES; i++){
            apples.add(new Apple(app));
        }
        

    }

    @Override
    public void handleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            isPaused = !isPaused;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)   ||  Gdx.input.isKeyPressed(Input.Keys.RIGHT) || 
           Gdx.input.isKeyPressed(Input.Keys.LEFT) ||  Gdx.input.isKeyPressed(Input.Keys.DOWN)  ||
           Gdx.input.isKeyPressed(Input.Keys.W)   ||  Gdx.input.isKeyPressed(Input.Keys.D) || 
           Gdx.input.isKeyPressed(Input.Keys.A) ||  Gdx.input.isKeyPressed(Input.Keys.S)) {
            started = true;
        }
        
    }

    @Override
    public void update(float dt) {
        handleInput();

        if (!isPaused && started){
            for(int i = 0; i < apples.size; i++){
                apples.get(i).update(dt,false);
            }          
            player1.update(dt, true, false, apples, player2);
            player2.update(dt, false, false, apples, player1);
            player1.handleCollision(dt, apples, false);
            player2.handleCollision(dt, apples, false);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, MainGame.WIDTH, MainGame.HEIGHT);
        if(isPaused){
            sb.draw(PauseUI, 0, 0);
        }
        sb.draw(player1.getSnkHead(), player1.getPosition().x, player1.getPosition().y);
        sb.draw(player2.getSnkHead(), player2.getPosition().x, player2.getPosition().y);
        for(int i = 0; i < player1.body.size; i++){
            sb.draw(player1.body.get(i).getSnkBody(), player1.body.get(i).getPosition().x, player1.body.get(i).getPosition().y);
        }
        for(int i = 0; i < player2.body.size; i++){
            sb.draw(player2.body.get(i).getSnkBody(), player2.body.get(i).getPosition().x, player2.body.get(i).getPosition().y);
        }
        if(started){
            for(int i = 0; i < apples.size; i++){
                sb.draw(apples.get(i).getTexture(), apples.get(i).getPosition().x, apples.get(i).getPosition().y);
            }      
        }
        
        sb.end();
    }
    
    

}
