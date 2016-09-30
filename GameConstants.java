/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javagdxdungeon;

/**
 *
 * @author hoge
 */
public class GameConstants {
    
    public static final int VViewLength = 640;
    public static final int HViewLength = 480;
    
    public static final int TerrainHeight = 60;
    public static final int TerrainLength = 100;
    
    public static final int TileSize = 32;
    
    public static final int LinkInfinite = 100;
    
    public enum Direction{VERTICAL,HORIZONTAL};
    public enum DividePosition{LEFT,CENTER,RIGHT,UPPER,MIDDLE,LOWER};//分割した左，中央，右，上，中央，下　の部屋
}
