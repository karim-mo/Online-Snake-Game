package com.snake.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.snake.game.sprites.Snake;
import com.badlogic.gdx.utils.Array;
import com.snake.game.MainGame;
import com.snake.game.sprites.Apple;
import com.snake.game.sprites.SnakeBody;
import java.util.Random;

public class Playing extends State{

    private Array<Snake> body = new Array<Snake>();
    private Array<Apple> apples = new Array<Apple>();
    private Snake snake;
    private Texture PauseUI;
    private Texture background;
    float timer = 0.05f, timer2 = 0;
    private final int APPLES = 2;
    private boolean started = false;
    private String[] texture = {"rightmouth.png", "leftmouth.png", "upmouth.png", "downmouth.png", "snakeimage.png"};
    private Texture[] mainPlayer = new Texture[5];
    private Texture app = new Texture("enemy.png");

    boolean isPaused = false;
    public Playing(GameStateManager gsm) {
        super(gsm);
        for(int i = 0; i < texture.length; i++){
            mainPlayer[i] = new Texture(texture[i]);
        }
        snake = new Snake(100, 200, gsm, mainPlayer);
        PauseUI = new Texture("Pause.png"); 
        background = new Texture("bg.jpg");
        for(int i = 0; i < APPLES; i++){
            apples.add(new Apple(app));
        }
    }

    @Override
    public void handleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isPaused){
            isPaused = true;
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && isPaused){
            isPaused = false;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)   ||  Gdx.input.isKeyPressed(Input.Keys.RIGHT) || 
           Gdx.input.isKeyPressed(Input.Keys.LEFT) ||  Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            started = true;
        }
        
    }


    @Override
    public void update(float dt) {
        handleInput();

        if (!isPaused && started){
            for(int i = 0; i < apples.size; i++){
                apples.get(i).update(dt, false);
            }
            snake.update(dt, true, false, apples, null);
            snake.handleCollision(dt, apples, false);          
        }
    }
   
    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, MainGame.WIDTH, MainGame.HEIGHT);
        if(isPaused){
            sb.draw(PauseUI, 0, 0);
        }
        sb.draw(snake.getSnkHead(), snake.getPosition().x, snake.getPosition().y);
        for(int i = 0; i < snake.body.size; i++){
            sb.draw(snake.body.get(i).getSnkBody(), snake.body.get(i).getPosition().x, snake.body.get(i).getPosition().y);

        }
        if(started){
            for(int i = 0; i < apples.size; i++){
                sb.draw(apples.get(i).getTexture(), apples.get(i).getPosition().x, apples.get(i).getPosition().y);
            }      
        }
        
        sb.end();
    }

}
