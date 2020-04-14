/********************************************************
 ** Program Filename: MainGame.java
 ** Author: Karim & Omar
 ** Project Start Date: 28.04.2019
 ** Description: Main launcher for game
 ********************************************************/

package com.snake.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.snake.game.states.GameStateManager;
import com.snake.game.states.Menu;
import io.socket.client.IO;
import io.socket.client.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainGame extends ApplicationAdapter {
    public static final int HEIGHT = 720;
    public static final int WIDTH = 1280;
    
    private SpriteBatch sb;

    private GameStateManager gsm;

    
/*********************************************************************************
** Function: create
** Description: Initializations at start of game.
** Parameters: None.
** Pre-Conditions: None.
** Post-Conditions: Variables and such are initialized.
*********************************************************************************/
    @Override
    public void create(){
        gsm = new GameStateManager();
        gsm.push(new Menu(gsm));
        sb = new SpriteBatch();
        Gdx.gl.glClearColor(1, 0, 0, 1);

    }
/*********************************************************************************
** Function: render
** Description: Main loop for the game.
** Parameters: None.
** Pre-Conditions: None.
** Post-Conditions: Objects are drawn on the screen continously.
*********************************************************************************/
    @Override
    public void render(){
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gsm.render(sb);
        gsm.update(Gdx.graphics.getDeltaTime());
        
        
    }
/*********************************************************************************
** Function: dispose
** Description: Disposing of textures and such to avoid memory leakage
** Parameters: None.
** Pre-Conditions: None.
** Post-Conditions: None. [Unused]
*********************************************************************************/   
    @Override
    public void dispose(){
        
    }
    
    

}
