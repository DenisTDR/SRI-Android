package tdr.bugcar_v1;

/**
 * Created by NMs on 6/16/2015.
 */
public class TPoint {
    public TPoint(){

    }
    public TPoint(int id, int x, int y){
        this.x = x;
        this.y = y;
        this.id = id;
        this.name = "none";
    }
    public TPoint(String name, int x, int y){
        this.x = x;
        this.y = y;
        this.name = name;
        this.id = -1;
    }
    public TPoint(int x, int y, String name){
        this.x = x;
        this.y = y;
        this.name = name;
        this.id = -1;
    }
    private int x;
    private int y;
    private int id;
    private String name;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
