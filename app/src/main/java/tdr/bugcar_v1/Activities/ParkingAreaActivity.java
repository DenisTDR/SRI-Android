package tdr.bugcar_v1.Activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

import tdr.bugcar_v1.BT.BTProtocol;
import tdr.bugcar_v1.Constants;
import tdr.bugcar_v1.R;
import tdr.bugcar_v1.TPoint;
import tdr.bugcar_v1.TRect;
import tdr.bugcar_v1.extVars;
import tdr.bugcar_v1.utilis;


public class ParkingAreaActivity extends BaseActivity {

    ImageView imgV;
    TextView selExitTxt, selPpTxt;
    RadioButton selppRBtn, fepRBtn;
    RelativeLayout midRL;
    Button startBtn, stopResetBtn, backBtn;
    TableLayout tableL;

    Bitmap defaultBmp, targetGreenBmp, targetRedBmp, iconBmp;
    ArrayList<TRect> rects;
    ArrayList<TPoint> checkpoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_area);
        makeShits();
        fillMapChekPointsList();
    }

    private void makeShits(){
        selPpTxt = (TextView)findViewById(R.id.selPpTxt);
        selExitTxt = (TextView)findViewById(R.id.selExitTxt);
        selppRBtn = (RadioButton)findViewById(R.id.selppRBtn);
        fepRBtn = (RadioButton)findViewById(R.id.fepRBtn);
        midRL = (RelativeLayout)findViewById(R.id.midRL);
        startBtn = (Button)findViewById(R.id.startBtn);
        stopResetBtn = (Button)findViewById(R.id.stopResetBtn);
        backBtn = (Button)findViewById(R.id.backBtn);
        tableL = (TableLayout)findViewById(R.id.tableL);
        imgV = (ImageView)findViewById(R.id.imgV);

        imgV.setOnTouchListener(imgVOTL);
        rects = new ArrayList<>();
        //parking places
        rects.add(new TRect(4, 225, 220, 172, 115));
        rects.add(new TRect(5, 225, 342, 172, 115));
        rects.add(new TRect(6, 225, 464, 172, 115));
        rects.add(new TRect(1, 403, 464, 172, 115));
        rects.add(new TRect(2, 403, 342, 172, 115));
        rects.add(new TRect(3, 403, 220, 172, 115));

        //exits
        rects.add(new TRect(10, 324, 648, 148, 152));
        rects.add(new TRect(11, 652, 324, 148, 152));
        rects.add(new TRect(12, 324, 0, 148, 152));
        rects.add(new TRect(13, 0, 324, 148, 152));

        getBmpsFromRes();
        scale2 = defaultBmp.getWidth()/800f;
        //Log.d("", "scale2="+String.valueOf(scale2));
    }

    int width = 0;
    float wp8s, scale2;
    int crtPP = -1;
    char crtExit = 'Z';
    char selectedOption = 2;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        //Here you can get the size!
        width = this.getWindow().getDecorView().getWidth();
        wp8s = 1.0f*width / 800;
        //Log.d("", "window W:"+String.valueOf(width)+ " H:"+String.valueOf(this.getWindow().getDecorView().getHeight()));
        //Log.d("","window h/w="+String.valueOf(1.0f*this.getWindow().getDecorView().getHeight() / width));
        if(1.0f*this.getWindow().getDecorView().getHeight() / width < 800f/480){
            float nw = this.getWindow().getDecorView().getHeight()/ (800f/480);
            imgV.getLayoutParams().width=(int)nw;
            imgV.getLayoutParams().height=(int)nw;
            wp8s = nw / 800;
            //Log.d("", "resized 1");
            //Log.d("", String.valueOf((int)nw));
            midRL.getLayoutParams().height = (int)nw;
            selppRBtn.setTextSize(13);
            fepRBtn.setTextSize(13);
            //RelativeLayout.LayoutParams mlp = new RelativeLayout.LayoutParams(
            //        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //mlp.setMargins(0, 0, 0, 0);
            //startBtn.setLayoutParams(mlp);
            //stopResetBtn.setLayoutParams(mlp);
            //backBtn.setLayoutParams(mlp);
            (( RelativeLayout.LayoutParams)startBtn.getLayoutParams()).bottomMargin = 0;
            (( RelativeLayout.LayoutParams)stopResetBtn.getLayoutParams()).bottomMargin = 0;
            (( RelativeLayout.LayoutParams)backBtn.getLayoutParams()).bottomMargin = 0;
        }
        else {
            imgV.getLayoutParams().height = width;
            wp8s = 1.0f*width / 800;
            //Log.d("", "resized 2 (w:"+String.valueOf(width)+")");
        }
        imgV.requestLayout();
    }
    View.OnTouchListener imgVOTL = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            float x, y;
            x = event.getX();
            y = event.getY();
            int hitIndex = 99;
            //Log.d("clicked", String.valueOf(x)+", "+String.valueOf(y));
            for(int i=0;i<rects.size();i++){
                if(rects.get(i).containsPoint(new PointF(x, y), wp8s)){
                    //Log.d("", String.valueOf(rects.get(i).getId()));
                    hitIndex = rects.get(i).getId();
                    break;
                }
            }
            if(hitIndex<99){
                if(hitIndex <= 6){
                    //Log.d("", "hit pp: "+String.valueOf(hitIndex));
                    crtPP = hitIndex;
                }
                else if(hitIndex > 10){
                    //Log.d("", "hit exit: "+String.valueOf((char)(hitIndex - 11 + 'A')));
                    crtExit = (char)(hitIndex - 11 + 'A');
                }
                reDrawImageView();
            }
            return false;
        }
    };

    public void reDrawImageView(){
        Bitmap newBitmap = Bitmap.createBitmap(defaultBmp.getWidth(), defaultBmp.getHeight(),
                defaultBmp.getConfig());
        Canvas newCanvas = new Canvas(newBitmap);
        newCanvas.drawBitmap(defaultBmp, 0, 0, null);

        //Log.d("", "defBmpSize: W:"+String.valueOf(defaultBmp.getWidth())+ " H:"+String.valueOf(defaultBmp.getHeight()));

        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setTextSize(50);
        p.setFakeBoldText(true);

        if(selectedOption == 1) {
            TRect tr = new TRect();
            for (int i = 0; i < rects.size(); i++) {
                if (rects.get(i).getId() == crtPP) {
                    tr = rects.get(i);
                    newCanvas.drawBitmap(targetGreenBmp, (tr.getLeft() + tr.getWidth() / 2) * scale2 - targetGreenBmp.getWidth() / 2,
                            (tr.getTop() + tr.getHeight() / 2) * scale2 - targetGreenBmp.getHeight() / 2, p);
                    selPpTxt.setText(String.valueOf(tr.getId()));
                } else if (rects.get(i).getChar() == crtExit) {
                    tr = rects.get(i);
                    newCanvas.drawBitmap(targetRedBmp, (tr.getLeft() + tr.getWidth() / 2) * scale2 - targetRedBmp.getWidth() / 2,
                            (tr.getTop() + tr.getHeight() / 2) * scale2 - targetRedBmp.getHeight() / 2, p);
                    selExitTxt.setText(String.valueOf(tr.getChar()));
                }
            }
        }
        if(!extVars.crtCheckPoint.equals("")){
            for(int i=0;i<checkpoints.size(); i++)
                if(checkpoints.get(i).getName().equals(extVars.crtCheckPoint)){
                    TPoint tp = checkpoints.get(i);
                    newCanvas.drawBitmap(iconBmp, (tp.getX())*scale2 - iconBmp.getWidth()/2,
                            (tp.getY())*scale2 - iconBmp.getHeight()/2, p);

                    break;
                }
        }
        imgV.setImageBitmap(newBitmap);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_parking_area, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        SavePreferences();
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        iconBmp.recycle();
        targetGreenBmp.recycle();
        targetRedBmp.recycle();
        defaultBmp.recycle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(defaultBmp == null || defaultBmp.isRecycled())
            getBmpsFromRes();
    }
    @Override
    protected void onResume(){
        super.onResume();
        LoadPreferences();
        //setContentView(R.layout.activity_parking_area);
        //makeShits();
        //reDrawImageView();
        //Log.d("", "resumed");
        if(defaultBmp == null || defaultBmp.isRecycled())
            getBmpsFromRes();
        super.onResume();
    }
    private void LoadPreferences(){
        SharedPreferences sp = getSharedPreferences("sri_app", MODE_PRIVATE);
        crtPP = sp.getInt("crtPP", -1);
        crtExit =(char) sp.getInt("crtExit", 'Z');
        selectedOption = (char) sp.getInt("selectedOption", 2);
        if(imgV != null) {
            reDrawImageView();
            selppRBtn.setChecked(selectedOption == 1);
            fepRBtn.setChecked(selectedOption == 2);
            reCheckSelectedLabels();
        }
    }
    private void SavePreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("sri_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("crtPP", crtPP);
        editor.putInt("crtExit", crtExit);
        editor.putInt("selectedOption", selectedOption);
        editor.apply();
    }

    public void paOptionClicked(View view){
        RadioButton trb = (RadioButton)view;
        if(trb == selppRBtn){
            fepRBtn.setChecked(false);
            selectedOption = 1;
            extVars.crtCheckPoint = "";
            reDrawImageView();
        }
        else {
            selppRBtn.setChecked(false);
            selectedOption = 2;
            extVars.crtCheckPoint = "";
            reDrawImageView();
        }
        reDrawImageView();
        reCheckSelectedLabels();
    }
    public void stopResetBtnPressed(View view){
        extVars.crtCheckPoint = "";
        byte[] msg = new byte[1];
        msg[0] = (byte)Constants.CarAction.ResetThings.ordinal();
        BTProtocol.sendByteArray(msg);
        reDrawImageView();
    }
    public void startBtnPressed(View view){
        switch (selectedOption){
            case 1:
                byte msg[] = new byte[4];
                msg[0] = (byte)Constants.CarAction.FSelectedPlaces.ordinal();
                msg[1] = 2;
                msg[2] = (byte)crtPP;
                msg[3] = (byte)crtExit;
                if(BTProtocol.sendByteArray(msg)){

                }
                break;
            default:
                byte msg2[] = new byte[1];
                msg2[0] = (byte)Constants.CarAction.FFindPlaces.ordinal();
                if(BTProtocol.sendByteArray(msg2)){

                }

                break;
        }
    }
    private void reCheckSelectedLabels(){
        if(selectedOption == 2){
            ViewGroup.LayoutParams params = tableL.getLayoutParams();
            params.height = 0;
            tableL.setLayoutParams(params);
            //Log.d("", "==2");
        }
        else{
            ViewGroup.LayoutParams params = tableL.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            tableL.setLayoutParams(params);
            //Log.d("", "==1");
        }
    }
    public void backBtnPressed(View view){
        finish();
    }

    public void teleportBtnPressed(View view){
        EditText txt = (EditText)findViewById(R.id.txttTxt);
        String val = txt.getText().toString();
        extVars.crtCheckPoint = val;
        reDrawImageView();
    }

    @Override
    public void ReceivedInfos(int what){
        if(what!=7) return;
        reDrawImageView();
    }


    private void getBmpsFromRes(){
        defaultBmp = BitmapFactory.decodeResource(getResources(), R.drawable.pa_800);
        targetGreenBmp = BitmapFactory.decodeResource(getResources(), R.drawable.target_green);
        targetRedBmp = BitmapFactory.decodeResource(getResources(), R.drawable.target_red);
        iconBmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_128);
    }
    private void fillMapChekPointsList(){
        checkpoints = new ArrayList<>();
        checkpoints.add(new TPoint(398, 793, "start1"));
        checkpoints.add(new TPoint(399, 736, "start2"));
        checkpoints.add(new TPoint(402, 667, "start3"));
        checkpoints.add(new TPoint(543, 668, "rb1"));
        checkpoints.add(new TPoint(653, 668, "rb2"));
        checkpoints.add(new TPoint(657, 589, "rb3"));
        checkpoints.add(new TPoint(658, 523, "pp1a"));
        checkpoints.add(new TPoint(575, 521, "pp1b"));
        checkpoints.add(new TPoint(488, 523, "pp1c"));
        checkpoints.add(new TPoint(658, 465, "pp12"));
        checkpoints.add(new TPoint(661, 398, "pp2a"));
        checkpoints.add(new TPoint(573, 401, "pp2b"));
        checkpoints.add(new TPoint(489, 401, "pp2c"));
        checkpoints.add(new TPoint(660, 337, "pp23"));
        checkpoints.add(new TPoint(664, 274, "pp3a"));
        checkpoints.add(new TPoint(571, 275, "pp3b"));
        checkpoints.add(new TPoint(488, 275, "pp3c"));
        checkpoints.add(new TPoint(656, 206, "rt3"));
        checkpoints.add(new TPoint(656, 136, "rt2"));
        checkpoints.add(new TPoint(530, 135, "rt1"));
        checkpoints.add(new TPoint(404, 132, "eb1"));
        checkpoints.add(new TPoint(405, 74, "eb2"));
        checkpoints.add(new TPoint(404, 23, "eb3"));
        checkpoints.add(new TPoint(278, 134, "lt1"));
        checkpoints.add(new TPoint(145, 135, "lt2"));
        checkpoints.add(new TPoint(143, 200, "lt3"));
        checkpoints.add(new TPoint(145, 282, "pp4a"));
        checkpoints.add(new TPoint(233, 282, "pp4b"));
        checkpoints.add(new TPoint(315, 281, "pp4c"));
        checkpoints.add(new TPoint(143, 335, "pp45"));
        checkpoints.add(new TPoint(145, 402, "pp5a"));
        checkpoints.add(new TPoint(233, 402, "pp5b"));
        checkpoints.add(new TPoint(316, 401, "pp5c"));
        checkpoints.add(new TPoint(146, 464, "pp56"));
        checkpoints.add(new TPoint(147, 529, "pp6a"));
        checkpoints.add(new TPoint(235, 528, "pp6b"));
        checkpoints.add(new TPoint(319, 526, "pp6c"));
        checkpoints.add(new TPoint(143, 589, "lb3"));
        checkpoints.add(new TPoint(142, 667, "lb2"));
        checkpoints.add(new TPoint(256, 667, "lb1"));
        checkpoints.add(new TPoint(75, 404, "ec1"));
        checkpoints.add(new TPoint(19, 404, "ec2"));
        checkpoints.add(new TPoint(727, 399, "ea1"));
        checkpoints.add(new TPoint(788, 399, "ea2"));
    }
}
