/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javagdxdungeon;

//ゲーム内定数群
import static javagdxdungeon.GameConstants.*;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;

//テクスチャ関連のライブラリ
import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//テクスチャアトラス
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//ファイル読み込み
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Interpolation;

//Scene2D用のライブラリ
//import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
//Scene2Dアクション関係
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
//Scene2Dイベント関係
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

//標準図形描画
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

//カメラ＆ビューポート関係
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 *
 * @author hoge
 */
public class MainListener implements ApplicationListener{

    SpriteBatch batch;
    TextureAtlas atlas;
    Texture texture;
    Stage stage;
    
    OrthographicCamera camera;
    Viewport viewport;
    
    ShapeRenderer shapeRenderer;
    
    BitmapFont font;
    
    TerrainCreate terraincreate;
    
    int count;
    
    @Override
    public void create() {
        System.out.println("create");//只のコンソール出力
        
        camera = new OrthographicCamera(VViewLength, HViewLength);
        camera.setToOrtho(false, VViewLength, HViewLength);
        viewport = new FitViewport(VViewLength, HViewLength, camera);
        camera.position.y = 80;
        
        shapeRenderer = new ShapeRenderer();
        
        terraincreate = new TerrainCreate();
        
        font = new BitmapFont();
        
        FileHandle fh = Gdx.files.internal("atlas/colorfulman.txt");
        atlas = new TextureAtlas(fh);
        count = 0;
        
        texture = new Texture("graph/Tile.bmp");
        
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);//イベント導入に必要
        
        Image image = new Image(texture);
        image.setPosition(0, 0);
        image.setOrigin(0, 0);
        
        //stage.addActor(image);
        
        batch = new SpriteBatch();
    }

    @Override
    public void resize(int w, int h) {
        System.out.println("resize"+w+","+h);
        viewport.update(w, h);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);//背景色設定
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.line(-1024, 0, 1024, 0);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.line(0, -1024, 0, 1024);
        shapeRenderer.end();
        
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x -= 2;  // 追加
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += 2;  // 追加
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.position.y += 2;  // 追加
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.y -= 2;  // 追加
        }
        
        // カメラの座標の文字列を作って
        String info = String.format("cam pos(%f,%f)", camera.position.x, camera.position.y);

        
        camera.update(); // 追加 #1
        batch.setProjectionMatrix(camera.combined); // 追加 #2
        
        batch.begin();              //beginとendの間にdrawを入れて描画する
        batch.setColor(1, 1, 1, 1);
        //batch.draw(texture, 0, -3616);
        
        /*TextureAtlas.AtlasRegion region1 = atlas.findRegion("atukuhito_frame_" + String.valueOf(count+1));
        batch.draw(region1, 0, 0);*/
        font.draw(batch, info, 0, 20); // 追加
        
        terraincreate.SectionDraw(camera);//地形生成確認用
        
        batch.end();
        
        stage.setViewport(viewport);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        
        count=(count+1)%40;
    }

    @Override
    public void pause() {
        System.out.println("pause");
    }

    @Override
    public void resume() {
        System.out.println("resume");
    }

    @Override
    public void dispose() {
        System.out.println("dispose");
        batch.dispose();
        atlas.dispose();
        shapeRenderer.dispose();
        //texture.dispose();
    }

}
