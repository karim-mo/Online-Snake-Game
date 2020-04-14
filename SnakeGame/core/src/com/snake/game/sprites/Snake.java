/********************************************************
 ** Program Filename: Snake.java
 ** Author: Karim & Omar
 ** Project Start Date: 28.04.2019
 ** Description: Controls snake movements and interactions
 ********************************************************/


package com.snake.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.snake.game.MainGame;
import com.snake.game.states.GameOver;
import com.snake.game.states.GameStateManager;
import com.snake.game.states.Multi_Won;
import io.socket.client.Socket;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class Snake {
    private Texture snkHead, headleft, headup, headdown, headright;
    private int score;
    private Vector2 position;
    private Vector2 previousPosition;
    private boolean left,right = true,up,down;
    private Rectangle box;
    private float timer = 0.03f, timer2 = 1f, timer3 = 1.5f;
    public Array<SnakeBody> body = new Array<SnakeBody>();
    private GameStateManager gsm;

       
/*********************************************************************************
** Constructor: Snake
** Description: Initializations at call.
** Pre-Conditions: Manual or pre-defined parameters.
** Post-Conditions: Variables and such are initialized.
     * @param x
     * @param y
     * @param gsm
     * @param texture
*********************************************************************************/
    
    public Snake(float x, float y, GameStateManager gsm, Texture[] texture){    
        snkHead = texture[0];
        body.add(new SnakeBody(x - 25, y, texture[4]));

        headright = texture[0];
        headleft = texture[1];
        headup = texture[2];
        headdown = texture[3];
        box = new Rectangle(x, y, snkHead.getWidth(), snkHead.getHeight());

        position = new Vector2(x,y);
        previousPosition = new Vector2(x, y);
        this.gsm = gsm;
    }

    
    
    
/*********************************************************************************
** Function: handleInput
** Description: Handles player 1 or 2 inputs
** Pre-Conditions: Manual or pre-defined parameter.
** Post-Conditions: Booleans are set accordingly.
     * @param switchPlayer
*********************************************************************************/
    public void handleInput(boolean switchPlayer){
        if(switchPlayer){
            if(Gdx.input.isKeyPressed(Input.Keys.UP) && !down){
                up = true;
                snkHead = headup;
                left = right = down = false;            
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !left){
                right = true;
                snkHead = headright;
                left = up = down = false;
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && !right){
                left = true;
                snkHead = headleft;
                right = up = down = false;
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.DOWN) && !up){
                down = true;
                snkHead = headdown;
                left = up = right = false;
            }
        }
        else{
            if(Gdx.input.isKeyPressed(Input.Keys.W) && !down){
                up = true;
                snkHead = headup;
                left = right = down = false;            
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.D) && !left){
                right = true;
                snkHead = headright;
                left = up = down = false;
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.A) && !right){
                left = true;
                snkHead = headleft;
                right = up = down = false;
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.S) && !up){
                down = true;
                snkHead = headdown;
                left = up = right = false;
            }
        }
        
    }
    
    
/*********************************************************************************
** Function: update
** Description: A main loop for the class.
** Pre-Conditions: Manual or pre-defined parameter.
** Post-Conditions: Snake is updated accordingly.
     * @param dt
     * @param switchPlayer
     * @param enemy
     * @param apples
     * @param other
*********************************************************************************/
    public void update(float dt, boolean switchPlayer, boolean enemy, Array<Apple> apples, Snake other){
        if(!enemy){
            handleInput(switchPlayer);
            handleCollision(dt, apples, false);
            if(other != null){
                this.collides(dt, other);
            }
            timer -= dt;
            if(timer <= 0){
                handleBodyFollow(dt); 
                handleMovement(dt); 
                timer = 0.03f;
            }
        }
        else{
           //timer -= dt;
           //if(timer <= 0){
            handleBodyFollow(dt);
            //timer = 0.03f;
          // }
        }
        box.setPosition(position.x, position.y);
        
    }
    
    
/*********************************************************************************
** Function: handleCollision
** Description: Handles head to tail and apple collisions
** Pre-Conditions: Manual or pre-defined parameter.
** Post-Conditions: State transition on failure or snake update on success.
     * @param dt
     * @param apples
     * @param online
*********************************************************************************/  
    
    
    public void handleCollision(float dt, Array<Apple> apples, boolean online){
        if(!online){
            timer2 -= dt;
            if(timer2 <= 0){
                for(int i = 0; i < apples.size; i++){
                    if(apples.get(i).collides(this.getBox())){
                        score++;
                        apples.get(i).dispose(null);
                        this.body.add(new SnakeBody(this.body.get(this.body.size - 1).getPosition().x, 
                                this.body.get(this.body.size - 1).getPosition().y, this.body.get(this.body.size - 1).getSnkBody()));
                    }
                }
            }

            //Head to Tail
            for(int i = 0; i < this.body.size; i++){
                if(this.body.get(i).collides(this.getBox())){
                    gsm.set(new GameOver(gsm, score));
                }
            }
        }
    }
    
    public JSONObject handleOnlineCollision(float dt, HashMap<String, HashMap<String, Apple>> apples, Socket socket, Apple k, String id, String sid){
        JSONObject data = new JSONObject();
        timer2 -= dt;
        for(HashMap.Entry<String, HashMap<String, Apple>> ent : apples.entrySet()){
            for(HashMap.Entry<String, Apple> entry : ent.getValue().entrySet()){
                entry.getValue().getBox().setPosition(entry.getValue().getPosition());
                
                if(entry.getValue().collides(box)){
                    if(timer2 <= 0){
                        this.body.add(new SnakeBody(this.body.get(this.body.size - 1).getPosition().x, 
                                        this.body.get(this.body.size - 1).getPosition().y, this.body.get(this.body.size - 1).getSnkBody()));
                        timer2 = 0.3f;
                    }
                    try{
                        data.put("x", entry.getValue().getPosition().x);
                        data.put("y", entry.getValue().getPosition().y);
                        data.put("id", entry.getKey());
                        data.put("session", ent.getKey());
                    } catch(JSONException e){
                        System.out.println(e);
                    }
                    return data;
                }
                 
            }
        }   
        return null;
    }
    
    
    public HashMap<String, HashMap<String, Snake>> handleOnlineSnakeCollision(float dt, HashMap<String, HashMap<String, Snake>> snakes, Socket socket){
        timer3 -= dt;
        HashMap<String, HashMap<String, Snake>> ret = new HashMap<String, HashMap<String, Snake>>();
        HashMap<String, Snake> ret2 = new HashMap<String, Snake>();
        for(HashMap.Entry<String, HashMap<String, Snake>> ent : snakes.entrySet()){
            for(HashMap.Entry<String, Snake> entry : ent.getValue().entrySet()){
                entry.getValue().getBox().setPosition(entry.getValue().getPosition());
                if(onlineCollision(dt, entry.getValue())){               
                    ret2.put(entry.getKey(), entry.getValue());
                    ret.put(ent.getKey(), ret2);
                    return ret;
                }
            }
        }
        return null;
    }
/*********************************************************************************
** Function: handleMovement
** Description: Handles snake boundaries and movements.
** Parameters: float
** Pre-Conditions: Manual or pre-defined parameter.
** Post-Conditions: Snake update.
*********************************************************************************/     
    public void handleMovement(float dt){
        if(position.x <= 0){
            position.x = MainGame.WIDTH;
        }
        else if(position.x >= MainGame.WIDTH){
            position.x = 0;
        }
        if(position.y >= MainGame.HEIGHT){
            position.y = 0;
        }
        else if(position.y <= 0){
            position.y = MainGame.HEIGHT;
        }
//--------------------------------------------------------
        if(right){
            position.add(25,0);
        }     
        if(left){
            position.add(-25, 0);
        }
        if(up){
            position.add(0, 25);
        }
        if(down){
            position.add(0, -25);
        }
    }
    
    
/*********************************************************************************
** Function: handleBodyFollow
** Description: Handles body following head movement.
** Parameters: float
** Pre-Conditions: Manual or pre-defined parameter.
** Post-Conditions: Body follows head perfectly.
*********************************************************************************/    
    
    
    public void handleBodyFollow(float dt){
        float prevx = body.get(0).getPosition().x;
        float prevy = body.get(0).getPosition().y;
        float prev2x, prev2y;
        body.get(0).getPosition().x = position.x;
        body.get(0).getPosition().y = position.y;
        body.get(0).update();
        for(int i = 1; i < body.size; i++){
            prev2x = body.get(i).getPosition().x;
            prev2y = body.get(i).getPosition().y;
            body.get(i).getPosition().x = prevx;
            body.get(i).getPosition().y = prevy;
            body.get(i).update();
            prevx = prev2x;
            prevy = prev2y;
        }
    }
/*********************************************************************************
** Function: collides
** Description: Handles snake to snake and snake to other snake tail collision.
** Parameters: float, Snake
** Pre-Conditions: Manual or pre-defined parameter.
** Post-Conditions: State transition on failure or snake update on success.
*********************************************************************************/    
    public void collides(float dt, Snake other){
        if(other.getBox().overlaps(box)){
            gsm.set(new Multi_Won(gsm, other.getScore(), score));
        }
        
        
        timer3 -= dt;
        for(int i = 0; i < other.body.size; i++){
            if(other.body.get(i).collides(this.box)){
                if(timer3 <= 0){
                    timer3 = 1.5f;
                    if(other.body.size >= 1)
                        other.body.removeIndex(other.body.size - 1);
                    else gsm.set(new Multi_Won(gsm, other.getScore(), score));


                    this.body.add(new SnakeBody(this.body.get(this.body.size - 1).getPosition().x, this.body.get(this.body.size - 1).getPosition().y, this.body.get(i).getSnkBody()));
                }
            }
        }
    }
    
    public boolean onlineCollision(float dt, Snake other){
        for(int i = 0; i < other.body.size; i++){
            if(box.overlaps(other.body.get(i).getBox())){
                return true;
                }
            }
        if(box.overlaps(other.getBox())){
            return true;
        }
        return false;
    }
      
    public boolean hasMoved(){
        if(previousPosition.x != getPosition().x || previousPosition.y != getPosition().y){
            previousPosition.x = getPosition().x;
            previousPosition.y = getPosition().y;
            return true;
        }
        return false;
    }

    public Texture getSnkHead() {
        return snkHead;
    }

    public void setSnkHead(Texture snkHead) {
        this.snkHead = snkHead;
    }
    
    
    public int getScore(){
        return score;
    }
    
    public Rectangle getBox(){
        return box;
    }

    public Vector2 getPosition() {
        return position;
    }    

    public void setPosition(Vector2 position) {
        this.position = position;
    }
    
}