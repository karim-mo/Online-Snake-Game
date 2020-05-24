package com.snake.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class SnakeBody {
    private Vector2 position;
    private Texture snkBody;
    private Rectangle box;
    
    public SnakeBody(float x, float y, Texture BODYYYYYYYYYYYY){
      snkBody = BODYYYYYYYYYYYY;
      position = new Vector2(x,y);
      box = new Rectangle(x, y, snkBody.getWidth(), snkBody.getHeight());
    }

    public Vector2 getPosition() {
        return position;
    }
    
    public void update(){
        box.setPosition(position.x, position.y);
    }

    public Texture getSnkBody() {
        return snkBody;
    }

    public Rectangle getBox() {
        return box;
    }
    
    public boolean collides(Rectangle x){
        return x.overlaps(box);
    }
}
