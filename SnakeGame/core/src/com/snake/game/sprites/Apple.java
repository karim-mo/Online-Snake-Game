package com.snake.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.snake.game.MainGame;
import com.snake.game.states.MultiplayerONLINE;
import io.socket.client.Socket;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;


public class Apple {
    private Vector2 position;
    private Vector2 previousPosition;
    private Texture apple;
    private float timer = 0f;
    private float timer2 = 0f;
    private Rectangle box;
    Random randx = new Random();
    Random randy = new Random();
    Random rand = new Random();
    boolean move=false;
    
    
/*********************************************************************************
** Constructor: Apple
** Description: Initializations at call.
** Parameters: None.
** Pre-Conditions: None.
** Post-Conditions: Variables and such are initialized.
*********************************************************************************/
    public Apple(Texture app){
        position = new Vector2(randx.nextInt(1000),randy.nextInt(600)); 
        previousPosition = new Vector2(getPosition().x, getPosition().y);
        apple = app;
        box = new Rectangle(position.x, position.y, apple.getWidth(), apple.getHeight());
    }
    
        
/*********************************************************************************
** Function: update
** Description: Handles spawning of apples randomly
** Parameters: float
** Pre-Conditions: Manual or pre-defined parameters.
** Post-Conditions: Apple is spawned randomly.
*********************************************************************************/
    public void update(float dt, boolean online){
        if(!online){
            timer -= dt;
            if( timer <= 0 ){
                position.x = randx.nextInt(MainGame.WIDTH/25) * 25;
                position.y = randy.nextInt(MainGame.HEIGHT/25) * 25;
                
                timer = 5f;
            }  
        }
    
        box.setPosition(position.x, position.y);      
    }
    
    public Rectangle getBox(){
        return box;
    }
    
/*********************************************************************************
** Function: collides
** Description: Checks for collision with the apple object
** Parameters: Rectangle
** Pre-Conditions: Manual or pre-defined parameter.
** Post-Conditions: returns success on collision and failure on none.
*********************************************************************************/  
    public boolean collides(Rectangle snk){
        return snk.overlaps(box);
        
    }
    public boolean hasMoved(){
        if(previousPosition.x != getPosition().x || previousPosition.y != getPosition().y){
            previousPosition.x = getPosition().x;
            previousPosition.y = getPosition().y;
            return true;
        }
        return false;
    }

/*********************************************************************************
** Function: dispose
** Description: Just a re-location under disguise of a dispose lol
** Parameters: None.
** Pre-Conditions: None.
** Post-Conditions: Apple location changed randomly.
*********************************************************************************/   
    public void dispose(Socket socket){
        position.x = randx.nextInt(MainGame.WIDTH/25) * 25;
        position.y = randy.nextInt(MainGame.HEIGHT/25) * 25;
        //timer = timer2 = 5f;
        
        box.setPosition(position.x, position.y);
    }

    public float getTimer() {
        return timer;
    }

    public void setTimer(float timer) {
        this.timer = timer;
    }

    public float getTimer2() {
        return timer2;
    }

    public void setTimer2(float timer2) {
        this.timer2 = timer2;
    }
    
    public Texture getTexture(){
        return apple;
    }
    
    public Vector2 getPosition(){
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
    
    public boolean getMove(){
        
        return move;
    }
    public void setMove(boolean move){
        this.move = move;
    }
    
}