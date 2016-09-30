/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javagdxdungeon;

//ゲーム内定数群
import static javagdxdungeon.GameConstants.*;
import static javagdxdungeon.GameConstants.Direction.*;
import static javagdxdungeon.GameConstants.DividePosition.*;

import java.util.Random;

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

//Scene2D用のライブラリ
//import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
//Scene2Dアクション関係
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

//標準図形描画
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

//カメラ＆ビューポート関係
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.*;
/**
 *
 * @author hoge
 */
public class NodeMap{
    
    int SectionCount;//何番まで区画を生成したか
    ArrayList<Node> NodeList;//区画リスト
    
    Random rnd;
    
    ShapeRenderer shapeRenderer;
    
    String LOG_TAG = NodeMap.class.getSimpleName();
    
        public class LinkedStatus{

            Node LinkingNode;//接続する区画
            Direction DirectionType;//繋がる方向
            int Weight;//リンクの重み
            boolean IsActive;//ダンジョン生成に使用するリンクか？
            boolean NextDestination;//スパニング木生成時に目印にする
            
            LinkedStatus(Node N,Direction d){
                LinkingNode = N;
                DirectionType = d;
                Weight = 0;
                IsActive =false;
                NextDestination =false;
            }
            LinkedStatus(Node N,Direction d,int W){
                LinkingNode = N;
                DirectionType = d;
                Weight = W;
                IsActive =false;
                NextDestination =false;
            }
            LinkedStatus(Node N,Direction d,int W,boolean A){
                LinkingNode = N;
                DirectionType = d;
                Weight = W;
                IsActive = A;
                NextDestination =false;
            }
            void SetInfinite(){
                Weight =LinkInfinite;
                //Gdx.app.log(LOG_TAG, "Link is Infinite!!");
            }
            LinkedStatus CopyMe(){
                LinkedStatus Ret = new LinkedStatus(LinkingNode,DirectionType,Weight,IsActive);
                return Ret;
            }
        }
        public class Node{
            int SectionNumber;//区画番号
            public int x0,y0;//区画の始点
            public int x1,y1;//区画の終点
            boolean IsActive;//どこかしらの隣接区画と使用するリンクとつながっているか？

            ArrayList<LinkedStatus> LinkedNodeList;//隣接区画リスト
            

            Node(int SN,int X0,int Y0,int X1,int Y1){
                SectionNumber = SN;
                x0 = X0;
                y0 = Y0;
                x1 = X1;
                y1 = Y1;
                IsActive =false;
                
                LinkedNodeList = new ArrayList();
            }
            int NotInfiniteLinks(){
                LinkedStatus linked;
                int ret = 0;
                for(Iterator<LinkedStatus> iterator = LinkedNodeList.iterator(); iterator.hasNext(); ) {
                    linked = iterator.next();
                    if(linked.Weight<LinkInfinite)
                        ret++;
                }
                return ret;
            }
            void AddLink(Node N,Direction d){
                LinkedStatus LS = new LinkedStatus(N,d);
                LinkedNodeList.add(LS);
            }
            ArrayList<LinkedStatus> ReLinkedNode(){
                return LinkedNodeList;
            }
            /*void ExtendLinkedNode(ArrayList<LinkedStatus> list){
                LinkedStatus linked;
                 for(Iterator<LinkedStatus> iterator = list.iterator(); iterator.hasNext(); ) {
                    linked = iterator.next();
                    LinkedNodeList.add(linked);
                 }
            }*/
            void ExtendLinkedNode(Node DividedSection){
                LinkedStatus linked;
                 for(Iterator<LinkedStatus> iterator = DividedSection.LinkedNodeList.iterator(); iterator.hasNext(); ) {
                    linked = iterator.next();
                    LinkedNodeList.add(linked.CopyMe());
                 }
            }
            void LinkUpdate(Node DividedSection,DividePosition DP){
                LinkedStatus UpdateStatus;
                LinkedStatus linked;
                for(Iterator<LinkedStatus> iterator = LinkedNodeList.iterator(); iterator.hasNext(); ) {
                    linked = iterator.next();
                    UpdateStatus = new LinkedStatus(this,linked.DirectionType,linked.Weight,linked.IsActive);
                    
                    if(DP==LEFT && linked.DirectionType==HORIZONTAL && x1 <= linked.LinkingNode.x0){
                        UpdateStatus.SetInfinite();linked.SetInfinite();Gdx.app.log(LOG_TAG, "Inf LE");}
                    if(DP==CENTER && linked.DirectionType==HORIZONTAL){
                        UpdateStatus.SetInfinite();linked.SetInfinite();Gdx.app.log(LOG_TAG, "Inf C");}
                    if(DP==RIGHT && linked.DirectionType==HORIZONTAL && x0 >= linked.LinkingNode.x1){
                        UpdateStatus.SetInfinite();linked.SetInfinite();Gdx.app.log(LOG_TAG, "Inf R");}
                    if(DP==LOWER && linked.DirectionType==VERTICAL && y1 <= linked.LinkingNode.y0){
                        UpdateStatus.SetInfinite();linked.SetInfinite();Gdx.app.log(LOG_TAG, "Inf LO");}
                    if(DP==MIDDLE && linked.DirectionType==VERTICAL){
                        UpdateStatus.SetInfinite();linked.SetInfinite();Gdx.app.log(LOG_TAG, "Inf M");}
                    if(DP==UPPER && linked.DirectionType==VERTICAL && y0 >= linked.LinkingNode.y1){
                        UpdateStatus.SetInfinite();linked.SetInfinite();Gdx.app.log(LOG_TAG, "Inf U");}
                    
                    linked.LinkingNode.LinkedNodeList.add(UpdateStatus);//隣接区画の隣接リストに分割した自分を入れる
                    linked.LinkingNode.RemoveOldLink(DividedSection);//隣接区画の隣接リスト内の分割前の区画隣接を消去する
                }//このfor文で全ての隣接区画に自分の区画を書き込む
                
            }
            void RemoveOldLink(Node DividedSection)//分割前もしくは特定の区画を消去する
            {
                LinkedStatus linked;
                for(Iterator<LinkedStatus> iterator = LinkedNodeList.iterator(); iterator.hasNext(); ) {
                    linked = iterator.next();
                    if(linked.LinkingNode == DividedSection)
                    {
                        LinkedNodeList.remove(linked);//分割前の区画を消去する
                        Gdx.app.log(LOG_TAG, "old section is erased");
                        break;
                    }
                }
            }
            int LinkedStatusActivation(Node Key){
                LinkedStatus linked;
                for(Iterator<LinkedStatus> iterator = LinkedNodeList.iterator(); iterator.hasNext(); ) {
                    linked = iterator.next();
                    if(linked.LinkingNode==Key){
                        linked.IsActive=true;
                        return 0;
                    }
                }
                return -1;
            }
            void LinkActivatioin(Node Destination){
                IsActive=true;
                LinkedStatusActivation(Destination);
            }
            void BreadthFirstSearch(){
                LinkedStatus linked;
                IsActive=true;
                for(Iterator<LinkedStatus> iterator = LinkedNodeList.iterator(); iterator.hasNext(); ) {
                    linked = iterator.next();
                    if(linked.Weight<LinkInfinite && linked.LinkingNode.IsActive==false){
                        linked.IsActive=true;
                        linked.NextDestination =true;
                        linked.LinkingNode.LinkActivatioin(this);
                    }
                }
                for(Iterator<LinkedStatus> iterator = LinkedNodeList.iterator(); iterator.hasNext(); ) {
                    linked = iterator.next();
                    if(linked.NextDestination==true){
                        linked.LinkingNode.BreadthFirstSearch();
                    }
                }
            }
            void DepthFirstSearch(){
                LinkedStatus linked;
                IsActive=true;
                for(Iterator<LinkedStatus> iterator = LinkedNodeList.iterator(); iterator.hasNext(); ) {
                    linked = iterator.next();
                    if(linked.Weight<LinkInfinite && linked.LinkingNode.IsActive==false){
                        linked.IsActive=true;
                        linked.NextDestination =true;
                        linked.LinkingNode.LinkActivatioin(this);
                        linked.LinkingNode.BreadthFirstSearch();
                    }
                }
            }
            String LinkDump(){
                LinkedStatus linked;
                String RetString = "Section" + String.valueOf(SectionNumber) + "'s Links are {";
                for(Iterator<LinkedStatus> iterator = LinkedNodeList.iterator(); iterator.hasNext(); ) {
                    linked = iterator.next();
                    RetString = RetString + String.valueOf(linked.LinkingNode.SectionNumber) + "Weight=" + String.valueOf(linked.Weight) + " , ";
                }
                RetString = RetString + "}";
                return RetString;
            }
        }
        
    NodeMap(){
        SectionCount = 0;
        NodeList = new ArrayList();
        NodeList.add(new Node(SectionCount++,0,0,TerrainLength,TerrainHeight));
        rnd = new Random();
        
        shapeRenderer = new ShapeRenderer();
    }
    void divide2(Direction D,Node DividedSection){
        
        Node a;
        Node b;
        if(D==VERTICAL){
            int border = (int)((DividedSection.y0+DividedSection.y1)/2);
            a = new Node(SectionCount++,DividedSection.x0,DividedSection.y0,DividedSection.x1,border);
            b = new Node(SectionCount++,DividedSection.x0,border+1,DividedSection.x1,DividedSection.y1);
            Gdx.app.log(LOG_TAG, "generate new nodes in vertical");
        }
        else{
            int border = (int)((DividedSection.x0+DividedSection.x1)/2);
            a = new Node(SectionCount++,DividedSection.x0,DividedSection.y0,border,DividedSection.y1);
            b = new Node(SectionCount++,border+1,DividedSection.y0,DividedSection.x1,DividedSection.y1);
            Gdx.app.log(LOG_TAG, "generate new nodes in horizontal");
        }
        NodeList.add(a);
        NodeList.add(b);
        //分割元からの隣接区画継承
        //ArrayList tmpsection = DividedSection.ReLinkedNode();
        //a.ExtendLinkedNode((ArrayList<LinkedStatus>)tmpsection.clone());
        //b.ExtendLinkedNode((ArrayList<LinkedStatus>)tmpsection.clone());
        a.ExtendLinkedNode(DividedSection);
        b.ExtendLinkedNode(DividedSection);
        Gdx.app.log(LOG_TAG, "extending all nodelist...");
        if(D==VERTICAL){
            a.LinkUpdate(DividedSection,LOWER);
            Gdx.app.log(LOG_TAG, b.LinkDump());
            Gdx.app.log(LOG_TAG, "extending a Section"+ String.valueOf(a.SectionNumber) +" nodelist is done");
            b.LinkUpdate(DividedSection,UPPER);
            Gdx.app.log(LOG_TAG, a.LinkDump());
            Gdx.app.log(LOG_TAG, "extending b Section"+ String.valueOf(b.SectionNumber) +" nodelist is done");
        }
        else{
            a.LinkUpdate(DividedSection,LEFT);
            Gdx.app.log(LOG_TAG, b.LinkDump());
            Gdx.app.log(LOG_TAG, "extending a Section"+ String.valueOf(a.SectionNumber) +" nodelist is done");
            b.LinkUpdate(DividedSection,RIGHT);
            Gdx.app.log(LOG_TAG, a.LinkDump());
            Gdx.app.log(LOG_TAG, "extending b Section"+ String.valueOf(b.SectionNumber) +" nodelist is done");
        }//隣接区画の隣接区画リストの更新
        a.AddLink(b, D);//子同士の隣接区画追加
        b.AddLink(a, D);//子同士の隣接区画追加
        Gdx.app.log(LOG_TAG, "conected children");
        NodeList.remove(DividedSection);//存在する区画から分割前の区画を消去する
        Gdx.app.log(LOG_TAG, "parentnode is erased");
    }
    void divide3(Direction D,Node DividedSection){
        Node a;
        Node b;
        Node c;
        if(D==VERTICAL){
            int border1 = (int)((DividedSection.y0+DividedSection.y1)/3);
            int border2 = (int)((DividedSection.y0+DividedSection.y1)*2/3);
            a = new Node(SectionCount++,DividedSection.x0,DividedSection.y0,DividedSection.x1,border1);
            b = new Node(SectionCount++,DividedSection.x0,border1+1,DividedSection.x1,border2);
            c = new Node(SectionCount++,DividedSection.x0,border2+1,DividedSection.x1,DividedSection.y1);
            Gdx.app.log(LOG_TAG, "generate new nodes in vertical");
        }
        else{
            int border1 = (int)((DividedSection.x0+DividedSection.x1)/3);
            int border2 = (int)((DividedSection.x0+DividedSection.x1)*2/3);
            a = new Node(SectionCount++,DividedSection.x0,DividedSection.y0,border1,DividedSection.y1);
            b = new Node(SectionCount++,border1+1,DividedSection.y0,border2,DividedSection.y1);
            c = new Node(SectionCount++,border2+1,DividedSection.y0,DividedSection.x1,DividedSection.y1);
            Gdx.app.log(LOG_TAG, "generate new nodes in horizontal");
        }
        NodeList.add(a);
        NodeList.add(b);
        NodeList.add(c);
        //分割元からの隣接区画継承
        //ArrayList tmpsection = DividedSection.ReLinkedNode();
        //a.ExtendLinkedNode((ArrayList<LinkedStatus>)tmpsection.clone());
        //b.ExtendLinkedNode((ArrayList<LinkedStatus>)tmpsection.clone());
        a.ExtendLinkedNode(DividedSection);
        b.ExtendLinkedNode(DividedSection);
        c.ExtendLinkedNode(DividedSection);
        Gdx.app.log(LOG_TAG, "extending all nodelist...");
        if(D==VERTICAL){
            a.LinkUpdate(DividedSection,LOWER);
            Gdx.app.log(LOG_TAG, b.LinkDump());
            Gdx.app.log(LOG_TAG, "extending a Section"+ String.valueOf(a.SectionNumber) +" nodelist is done");
            b.LinkUpdate(DividedSection,MIDDLE);
            Gdx.app.log(LOG_TAG, a.LinkDump());
            Gdx.app.log(LOG_TAG, "extending b Section"+ String.valueOf(b.SectionNumber) +" nodelist is done");
            c.LinkUpdate(DividedSection,UPPER);
            Gdx.app.log(LOG_TAG, a.LinkDump());
            Gdx.app.log(LOG_TAG, "extending c Section"+ String.valueOf(b.SectionNumber) +" nodelist is done");
        }
        else{
            a.LinkUpdate(DividedSection,LEFT);
            Gdx.app.log(LOG_TAG, b.LinkDump());
            Gdx.app.log(LOG_TAG, "extending a Section"+ String.valueOf(a.SectionNumber) +" nodelist is done");
            b.LinkUpdate(DividedSection,CENTER);
            Gdx.app.log(LOG_TAG, a.LinkDump());
            Gdx.app.log(LOG_TAG, "extending b Section"+ String.valueOf(b.SectionNumber) +" nodelist is done");
            c.LinkUpdate(DividedSection,RIGHT);
            Gdx.app.log(LOG_TAG, a.LinkDump());
            Gdx.app.log(LOG_TAG, "extending b Section"+ String.valueOf(b.SectionNumber) +" nodelist is done");
        }//隣接区画の隣接区画リストの更新
        a.AddLink(b, D);//子同士の隣接区画追加
        b.AddLink(a, D);//子同士の隣接区画追加
        b.AddLink(c, D);//子同士の隣接区画追加
        c.AddLink(b, D);//子同士の隣接区画追加
        Gdx.app.log(LOG_TAG, "conected children");
        NodeList.remove(DividedSection);//存在する区画から分割前の区画を消去する
        Gdx.app.log(LOG_TAG, "parentnode is erased");
    }
    public void divideDebug(Direction D,Node DividedSection){
        Node a;
        Node b;
        if(D==VERTICAL){
            int border = (int)((DividedSection.y0+DividedSection.y1)/2);
            a = new Node(SectionCount++,DividedSection.x0,DividedSection.y0,DividedSection.x1,border);
            b = new Node(SectionCount++,DividedSection.x0,border+1,DividedSection.x1,DividedSection.y1);
            Gdx.app.log(LOG_TAG, "generate new nodes in vertical");
        }
        else{
            int border = (int)((DividedSection.x0+DividedSection.x1)/2);
            a = new Node(SectionCount++,DividedSection.x0,DividedSection.y0,border,DividedSection.y1);
            b = new Node(SectionCount++,border+1,DividedSection.y0,DividedSection.x1,DividedSection.y1);
            Gdx.app.log(LOG_TAG, "generate new nodes in horizontal");
        }
        //ノードリストに追加は行わない
        //分割元からの隣接区画継承
        a.ExtendLinkedNode(DividedSection);
        b.ExtendLinkedNode(DividedSection);
        LinkedStatus TestStatus = new LinkedStatus(DividedSection,D,30);//ここで適当なリンクを追加
        a.LinkedNodeList.add(TestStatus);
        Gdx.app.log(LOG_TAG, a.LinkDump());
        Gdx.app.log(LOG_TAG, b.LinkDump());
    }
    
    public void SpanningTree(int StartNodeNumber){
        if(StartNodeNumber>=NodeList.size() || StartNodeNumber<0)
            StartNodeNumber = rnd.nextInt(NodeList.size());
        Node StartNode = NodeList.get(StartNodeNumber);
        
        //StartNode.BreadthFirstSearch();
        StartNode.DepthFirstSearch();
    }
    
    public void DrawSections(OrthographicCamera camera){
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 1, 0, 1);
        SpriteBatch batch;
        String info;
        BitmapFont font;
        batch = new SpriteBatch();
        font = new BitmapFont();
        batch.setProjectionMatrix(camera.combined); // 追加 #2
        batch.begin();              //beginとendの間にdrawを入れて描画する
        batch.setColor(1, 1, 1, 1);
        float DrawMult = 5.0f;
        for(Iterator<Node> iterator = NodeList.iterator(); iterator.hasNext(); ) {
            Node node = iterator.next();
            info = String.format("section%d(%d,%d,%d,%d)" + node.LinkDump(),node.SectionNumber, node.x0, node.y0,node.x1,node.y1);
            shapeRenderer.box(node.x0*DrawMult, node.y0*DrawMult, 0, (node.x1-node.x0)*DrawMult, (node.y1-node.y0)*DrawMult, 0);
            font.draw(batch, info, 0, -20*NodeList.indexOf(node));
            for(Iterator<LinkedStatus> iterator2 = node.LinkedNodeList.iterator(); iterator2.hasNext(); ) {
                LinkedStatus linked = iterator2.next();
                if(linked.IsActive==true)
                    shapeRenderer.line((node.x0+node.x1)*DrawMult/2,(node.y0+node.y1)*DrawMult/2,(linked.LinkingNode.x0+linked.LinkingNode.x1)*DrawMult/2,(linked.LinkingNode.y0+linked.LinkingNode.y1)*DrawMult/2);
            }
        }
        batch.end();
        shapeRenderer.end();
        batch.dispose();
    }
}
