/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javagdxdungeon;

//ゲーム内定数群
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import static javagdxdungeon.GameConstants.*;
import static javagdxdungeon.GameConstants.Direction.*;
import static javagdxdungeon.TerrainType.*;

import java.util.*;

/**
 *
 * @author hoge
 */

public class TerrainCreate {

    
    TerrainType T[][];
    NodeMap nodemap;
    
    TerrainCreate(){
        
        T = new TerrainType[TerrainHeight][TerrainLength];
        this.Reset();
        nodemap = new NodeMap();
        //nodemap.divideDebug(VERTICAL, nodemap.NodeList.get(0));
        nodemap.divide3(HORIZONTAL, nodemap.NodeList.get(0));
        nodemap.divide2(VERTICAL, nodemap.NodeList.get(0));
        nodemap.divide3(VERTICAL, nodemap.NodeList.get(0));
        nodemap.divide2(VERTICAL, nodemap.NodeList.get(0));
        nodemap.SpanningTree(3);
    }
    
    public final void Reset()
    {
        for(int i=0;i<TerrainHeight;i++)
        {
            for(int k=0;k<TerrainLength;k++)
            {
                if((i+1)%TerrainHeight<=1||(k+1)%TerrainLength<=1)
                    T[i][k]=BEDROCK;
                else
                    T[i][k]=WALL;
            }
        }
    }
    
    public void SectionDraw(OrthographicCamera camera){
        nodemap.DrawSections(camera);
    }
    
}
