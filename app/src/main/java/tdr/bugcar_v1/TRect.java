package tdr.bugcar_v1;


import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;

import java.sql.SQLInvalidAuthorizationSpecException;

/**
 * Created by NMs on 6/14/2015.
 */
public class TRect{
    public TRect(){
        left = top = width = height = id = 0;

    }
    public TRect(int id, int left, int top, int width, int height){
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.id = id;
        this.Char = (char)(id - 11 + 'A');
    }
    private int left, top, id, width, height;
    private char Char;
    public char getChar(){
        return Char;
    }
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public int getLeft(){
        return left;
    }
    public void setLeft(int left){
        this.left = left;
    }
    public int getTop(){
        return top;
    }
    public void setTop(int top){
        this.top = top;
    }
    public int getRight(){
        return left + width;
    }
    public int getBottom(){
        return top + height;
    }
    public int getHeight(){
        return height;
    }
    public void setHeight(int height){
        this.height = height;
    }
    public int getWidth(){
        return width;
    }
    public void setWidth(int width){
        this.width = width;
    }
    public boolean containsPoint(PointF point, float scaleFactor){
        return (point.x>scaleFactor*getLeft() && point.x<scaleFactor*getRight()
                && point.y > scaleFactor * getTop() && point.y < scaleFactor *getBottom());
    }
}
