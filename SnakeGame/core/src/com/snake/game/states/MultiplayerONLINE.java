package com.snake.game.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.snake.game.MainGame;
import com.snake.game.sprites.Apple;
import com.snake.game.sprites.Snake;
import com.snake.game.sprites.SnakeBody;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MultiplayerONLINE extends State{
    
    private Socket socket;
    private String id;
    private String sessid;
    public static boolean appp=false;
    private String appleid;
    private Snake player;
    private Apple apple;
    private HashMap<String, HashMap<String, Snake>> Enemies;
    private String[] Player = {"rightmouth.png", "leftmouth.png", "upmouth.png", "downmouth.png", "snakeimage.png"};
    private String[] enemies = {"rightmouth2.png", "leftmouth2.png", "upmouth2.png", "downmouth2.png", "snakeimage2.png"};
    private Texture[] mainPlayer = new Texture[5];
    private Texture[] mainEnemy = new Texture[5];
    private Texture appleTexture = new Texture("enemy.png");
    private HashMap<String, HashMap<String, Apple>> apples ;
    private Array<Apple> playerApples = new Array<Apple>();
    private Texture background;
    float timer = 0.05f, timer2 = 1/10f;
    private final int APPLES = 2;
    private final float UPDATE_TIME = 1/10f;
    private boolean started = false;
    
    HashMap<String, Apple> a = new HashMap<String, Apple>();
    HashMap<String, Snake> p = new HashMap<String, Snake>();
    
    public MultiplayerONLINE(GameStateManager gsm) {
        super(gsm);
        Enemies = new HashMap<String, HashMap<String, Snake>>();
        apples = new HashMap<String, HashMap<String, Apple>>();
        connectSocket();
        configSocketEvents();
        background = new Texture("bg.jpg");

        for(int i = 0; i < Player.length; i++){
            mainPlayer[i] = new Texture(Player[i]);
        }
        for(int i = 0; i < Player.length; i++){
            mainEnemy[i] = new Texture(enemies[i]);
        }
        
    }

    @Override
    public void handleInput() {
    }
    
    
    @Override
    public void update(float dt) {
        handleInput();
        updateServer(dt);
        if(player != null){
            player.update(dt, true, false, playerApples, null);
            
        }
    }
    
    
    public void updateServer(float dt){
        timer2 += dt;
        if(player != null && player.hasMoved()){
            JSONObject data = new JSONObject();
            try{
                data.put("x", player.getPosition().x);
                data.put("y", player.getPosition().y);
                socket.emit("playerMoved", data);
            } catch(JSONException e){
                Gdx.app.log("SocketIO", "Error sending update data");
            }
        }
        if(player != null){
            JSONObject x = player.handleOnlineCollision(dt, apples, socket, apple, id, sessid);
            if(x != null) {
                JSONObject data = new JSONObject();
                JSONObject data2 = new JSONObject();
                try{
                    data.put("session", x.getString("session"));
                    data.put("appid", x.getString("id"));
                    data.put("x", x.getString("x"));
                    data.put("y", x.getString("y"));
                    
                    data2.put("x", player.getPosition().x);
                    data2.put("y", player.getPosition().y);
                    data2.put("len", player.body.size);
                    
                    socket.emit("TailUpdate", data2);
                    socket.emit("appleMovedM", data);
                } catch(JSONException e){
                    Gdx.app.log("SocketIO", "Error sending update data");
                }
            }
        }
        if(player != null){
            Snake x = new Snake(0, 0, gsm, mainPlayer);
            String key = "";
            String skey = "";
            HashMap<String, HashMap<String, Snake>> k = player.handleOnlineSnakeCollision(dt, Enemies, socket);
            if(k != null){
                for(HashMap.Entry<String, HashMap<String, Snake>> ent : k.entrySet()){
                    for(HashMap.Entry<String, Snake> entry : ent.getValue().entrySet()){
                        key = entry.getKey();
                        x = entry.getValue();
                        skey = ent.getKey();
                        break;
                    }
                }

                if(x != null) {
                    JSONObject data = new JSONObject();
                    try{
                        //data.put("session", skey);
                        data.put("id", key);
                        data.put("x", x.getPosition().x);
                        data.put("y", x.getPosition().y);
                        data.put("len", (x.body.size));

                        socket.emit("TailUpdateM", data);
                        socket.disconnect();
                    } catch(JSONException e){
                        Gdx.app.log("SocketIO", "Error sending update data");
                    }
                }
            }
        }
    
    }

    
    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, MainGame.WIDTH, MainGame.HEIGHT);
        //System.out.println(id);
        
        for(HashMap.Entry<String, HashMap<String, Apple>> entry : apples.entrySet()){
            if(entry.getKey().equals(sessid)){
                for(HashMap.Entry<String, Apple> ent : entry.getValue().entrySet()){
                    sb.draw(ent.getValue().getTexture(), ent.getValue().getPosition().x, 
                            ent.getValue().getPosition().y);
                    //System.out.println(ent.getValue().getPosition());
                }
            }
        }
        
        if(player != null){
            sb.draw(player.getSnkHead(), player.getPosition().x, player.getPosition().y);
            for(int i = 0; i < player.body.size; i++){
                sb.draw(player.body.get(i).getSnkBody(), player.body.get(i).getPosition().x, 
                        player.body.get(i).getPosition().y);
            }
        }

        for(HashMap.Entry<String, HashMap<String, Snake>> entry : Enemies.entrySet()){
            if(entry.getKey().equals(sessid)){
                for(HashMap.Entry<String, Snake> ent : entry.getValue().entrySet()){
                    sb.draw(ent.getValue().getSnkHead(), ent.getValue().getPosition().x, 
                            ent.getValue().getPosition().y);
                    for(int i = 0; i < ent.getValue().body.size; i++){
                        sb.draw(ent.getValue().body.get(i).getSnkBody(), ent.getValue().body.get(i).getPosition().x, 
                                ent.getValue().body.get(i).getPosition().y);
                    }
                    //System.out.println("big " + Enemies.size() + "small " + entry.getValue().size());
                }
            }
        }
        sb.end();
    }
    
    
    public final void connectSocket(){
      try {
          socket = IO.socket("http://localhost:9000");
          socket.connect();
      } catch(Exception e){
          System.out.println(e);
      }
    }

    
    public void configSocketEvents(){
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                        Gdx.app.log("SocketIO", "Connected");
                        player = new Snake(100, 200, gsm, mainPlayer);
                        //apple = new Apple(appleTexture);
                }
        }).on("socketID", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject data = (JSONObject) args[0];
                    JSONObject data2 = (JSONObject) args[1];
                    JSONObject data3 = (JSONObject) args[2];
                    try {     
                        id = data.getString("id");
                        appleid = data2.getString("appid");
                        sessid = data3.getString("session");
                        Gdx.app.log("SocketIO", "My ID: " + id + "\nMy Session ID: " + sessid);
                    } catch (JSONException e) {
                        Gdx.app.log("SocketIO", "Error getting ID");
                    }
                }
        }).on("newPlayer", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject data = (JSONObject) args[0];
                    JSONObject data2 = (JSONObject) args[1];
                    try {
                        String pid = data.getString("id");
                        String sid = data2.getString("session");
                        Gdx.app.log("SocketIO", "New Player Connect: " + id);
                        p.put(pid, new Snake(100, 100, gsm, mainEnemy));
                        Enemies.put(sid, p);
                    }catch(JSONException e){
                        Gdx.app.log("SocketIO", "Error getting New PlayerID");
                    }
                }
        }).on("newApple", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject data2 = (JSONObject) args[0];
                    JSONObject data3 = (JSONObject) args[1];
                    try {
                        Apple x = new Apple(appleTexture);
                        x.setPosition(new Vector2(
                                (float)data2.getDouble("x"),
                                (float)data2.getDouble("y")                              
                        ));                      
                        String appid = data2.getString("appid");
                        String sid = data3.getString("session");
                        Gdx.app.log("SocketIO", "New Apple: " + appid);    
                        a.put(appid, x);
                        apples.put(sid, a);                      
                    }catch(JSONException e){
                        Gdx.app.log("SocketIO", "Error getting New AppleID");
                    }
                }
        }).on("playerDisconnected", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject data = (JSONObject) args[0];
                    JSONObject appdata = (JSONObject) args[1];
                    JSONObject session = (JSONObject) args[2];
                    try {
                        String pid = data.getString("id");
                        String session_id = session.getString("session");
                        if(Enemies.get(session_id) != null) 
                            Enemies.get(session_id).remove(pid);

                        String appid = appdata.getString("appid");
                        if(apples.get(session_id) != null) 
                            apples.get(session_id).remove(appid);
                    }catch(JSONException e){
                        Gdx.app.log("SocketIO", "Error getting disconnected PlayerID");
                    }
                }
        }).on("getPlayers", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                        JSONArray data = (JSONArray) args[0];
                        try {
                            for(int i = 0; i < data.length(); i++){
                                Snake enemyPlayer = new Snake(0, 0, gsm, mainEnemy);
                                
                                enemyPlayer.setPosition(new Vector2(
                                        (float)data.getJSONObject(i).getDouble("x"),
                                        (float)data.getJSONObject(i).getDouble("y")                              
                                ));
                                if(!(data.getJSONObject(i).getString("id").equals(id))){
                                    p.put(data.getJSONObject(i).getString("id"), enemyPlayer);
                                    Enemies.put(data.getJSONObject(i).getString("session"), p);
                                }
                            }
                        } catch(JSONException e){
                            Gdx.app.log("SocketIO", "Error getting players");
                        }
                }
        }).on("getApples", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                        JSONArray data = (JSONArray) args[0];
                        try {     
                            for(int i = 0; i < data.length(); i++){
                                Apple x = new Apple(appleTexture);
                                x.setPosition(new Vector2(
                                        (float)data.getJSONObject(i).getDouble("x"),
                                        (float)data.getJSONObject(i).getDouble("y")                              
                                ));
                                String appid = data.getJSONObject(i).getString("appid");
                                String sid = data.getJSONObject(i).getString("session");
                                a.put(appid, x);
                                apples.put(sid, a);  
                                
                            }
                        } catch(JSONException e){
                            Gdx.app.log("SocketIO", "Error getting apples");
                        }
                }
        }).on("playerMoved", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String pid = data.getString("id");
                        String sid = data.getString("session");
                        float x = (float)data.getDouble("x");
                        float y = (float)data.getDouble("y");
                        if(Enemies.get(sid) != null && Enemies.get(sid).get(pid) != null){
                            Enemies.get(sid).get(pid).update(Gdx.graphics.getDeltaTime(), true, true, playerApples, null);                          
                            if(Enemies.get(sid).get(pid).getPosition().x == x && Enemies.get(sid).get(pid).getPosition().y < y){
                                Enemies.get(sid).get(pid).setSnkHead(mainEnemy[2]);
                            }
                            else if(Enemies.get(sid).get(pid).getPosition().x == x && Enemies.get(sid).get(pid).getPosition().y > y){
                                Enemies.get(sid).get(pid).setSnkHead(mainEnemy[3]);
                            }
                            else if(Enemies.get(sid).get(pid).getPosition().x > x && Enemies.get(sid).get(pid).getPosition().y == y){
                                Enemies.get(sid).get(pid).setSnkHead(mainEnemy[1]);
                            }
                            else if(Enemies.get(sid).get(pid).getPosition().x < x && Enemies.get(sid).get(pid).getPosition().y == y){
                                Enemies.get(sid).get(pid).setSnkHead(mainEnemy[0]);
                            }
                            Enemies.get(sid).get(pid).setPosition(new Vector2(x,y));
                        }
                    }catch(JSONException e){
                        Gdx.app.log("SocketIO", "Error getting player coords");
                    }
                }
        }).on("appleMoved", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String appid = data.getString("appid");
                        String sid = data.getString("session");
                        float x = (float)data.getDouble("x");
                        float y = (float)data.getDouble("y");
                        if(apples.get(sid) != null && apples.get(sid).get(appid) != null){
                            //apples.get(sid).get(appid).update(Gdx.graphics.getDeltaTime(), true);
                            apples.get(sid).get(appid).setPosition(new Vector2(x,y));
                        }
                    }catch(JSONException e){
                        System.out.println(e);
                        Gdx.app.log("SocketIO", "Error getting apple coords");
                    }
                }
        }).on("TailUpdate", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String pid = data.getString("id");
                        String sid = data.getString("session");
                        float x = (float)data.getDouble("x");
                        float y = (float)data.getDouble("y");
                        int z   = (int)data.getInt("len");
                        
                        if(Enemies.get(sid) != null && Enemies.get(sid).get(pid) != null){
                            Enemies.get(sid).get(pid).body.clear();
                            Enemies.get(sid).get(pid).body.add(new SnakeBody(Enemies.get(sid).get(pid).getPosition().x - 25,
                                                            Enemies.get(sid).get(pid).getPosition().y,
                                                            mainEnemy[4]
                                ));
                            for(int i = 1; i < z; i++){
                                Enemies.get(sid).get(pid).body.add(new SnakeBody(Enemies.get(sid).get(pid).body.get(Enemies.get(sid).get(pid).body.size - 1).getPosition().x,
                                                            Enemies.get(sid).get(pid).body.get(Enemies.get(sid).get(pid).body.size - 1).getPosition().y,
                                                            Enemies.get(sid).get(pid).body.get(Enemies.get(sid).get(pid).body.size - 1).getSnkBody()
                                ));
                            }
                        }
                    }catch(JSONException e){
                        Gdx.app.log("SocketIO", "Error getting player coords");
                    }
                }
        });
    }
    
}