/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javagdxdungeon;

//ゲーム内定数群
import static javagdxdungeon.GameConstants.*;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

/**
 *
 * @author hoge
 */


public class JavaGDXDungeon {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        // TODO code application logic here
        new LwjglApplication(new MainListener(),"Dungeon",VViewLength, HViewLength);
    }
    
}
