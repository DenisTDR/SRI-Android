package tdr.bugcar_v1;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class remote_control_activity extends ActionBarActivity {

    ImageButton goBackBtn, goFrontBtn, turnLeftFrontBtn, turnRightFrontBtn, turnLeftBackBtn, turnRightBackBtn, rotateLeftBtn, rotateRightBtn;

    SeekBar powerSeekBar;
    EditText timeTxt;
    TextView powerTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);

        goBackBtn = (ImageButton) findViewById(R.id.goBackBtn);
        goFrontBtn = (ImageButton) findViewById(R.id.goFrontBtn);
        turnLeftBackBtn = (ImageButton) findViewById(R.id.turnBackLeftBtn);
        turnLeftFrontBtn = (ImageButton) findViewById(R.id.turnFrontLeftBtn);
        turnRightBackBtn = (ImageButton) findViewById(R.id.turnBackRightBtn);
        turnRightFrontBtn = (ImageButton) findViewById(R.id.turnFrontRightBtn);
        rotateLeftBtn = (ImageButton) findViewById(R.id.rotateLeftBtn);
        rotateRightBtn = (ImageButton) findViewById(R.id.rotateRightBtn);

        timeTxt = (EditText) findViewById(R.id.timeTxt);
        powerTxt = (TextView) findViewById(R.id.powerTxt);

        //imageButtonTouchEventListener
        goBackBtn.setOnTouchListener(imageButtonTouchEventListener);
        goFrontBtn.setOnTouchListener(imageButtonTouchEventListener);
        turnLeftBackBtn.setOnTouchListener(imageButtonTouchEventListener);
        turnLeftFrontBtn.setOnTouchListener(imageButtonTouchEventListener);
        turnRightBackBtn.setOnTouchListener(imageButtonTouchEventListener);
        turnRightFrontBtn.setOnTouchListener(imageButtonTouchEventListener);
        rotateLeftBtn.setOnTouchListener(imageButtonTouchEventListener);
        rotateRightBtn.setOnTouchListener(imageButtonTouchEventListener);
//        goFrontBtn.setOnTouchListener(goFrontTouchListener);
//        goBackBtn.setOnTouchListener(goBackTouchListener);
//        turnLeftBtn.setOnTouchListener(turnLeftTouchListener);
//        turnRightBtn.setOnTouchListener(turnRightTouchListener);
//        rotateLeftBtn.setOnTouchListener(rotateLeftTouchListener);
//        rotateRightBtn.setOnTouchListener(rotateRightTouchListener);

        powerSeekBar = (SeekBar) findViewById(R.id.powerSeekBar);


        powerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                if(powerTxt!=null)
                    powerTxt.setText("Power: "+ (String.format("%.0f", (double)progresValue/seekBar.getMax()*100)) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        utilis.currentActivity = this;
        utilis.applicationContext = getApplicationContext();
    }

    @Override
    protected void onPause(){
        SavePreferences();
        super.onPause();
    }

    @Override
    protected void onResume(){
        LoadPreferences();
        super.onResume();
    }
    private void SavePreferences(){
        if(powerSeekBar == null || timeTxt==null) return;
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("time", Integer.parseInt(timeTxt.getText().toString()));
        editor.putInt("power", powerSeekBar.getProgress());
        editor.apply();   // I missed to save the data to preference here,.
    }

    private void LoadPreferences(){
        if(powerSeekBar == null || timeTxt==null) return;
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        powerSeekBar.setProgress(sharedPreferences.getInt("power", 75*255/100));
        timeTxt.setText(String.valueOf(sharedPreferences.getInt("time", 3)));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_remote_control_activity, menu);
        return true;
    }
    public void backBtnPressed(View view){
        finish();
    }
    public void stopBtnPressed(View view){
        sendCmdWithAction(Constants.CarAction.StopEngines);
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

    View.OnTouchListener imageButtonTouchEventListener=new View.OnTouchListener(){
        public boolean onTouch(    View v,    MotionEvent event){
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
               ImageButton pressed = (ImageButton)v;
                if(pressed == goFrontBtn){
                    Log.d("", "go front");
                    sendCmdWithAction(Constants.CarAction.GoFront);
                }else if(pressed == goBackBtn){
                    Log.d("", "go back");
                    sendCmdWithAction(Constants.CarAction.GoBack);
                }else if(pressed == turnLeftFrontBtn){
                    Log.d("", "turn left front");
                    sendCmdWithAction(Constants.CarAction.GoLeftF);
                }else if(pressed == turnRightFrontBtn){
                    Log.d("", "turn right front");
                    sendCmdWithAction(Constants.CarAction.GoRightF);
                }else if(pressed == turnLeftBackBtn){
                    Log.d("", "turn left back");
                    sendCmdWithAction(Constants.CarAction.GoLeftB);
                }else if(pressed == turnRightBackBtn){
                    Log.d("", "turn right back");
                    sendCmdWithAction(Constants.CarAction.GoRightB);
                }else if(pressed == rotateLeftBtn){
                    Log.d("", "rotate left");
                    sendCmdWithAction(Constants.CarAction.RotirePeLocStanga);
                }else if(pressed == rotateRightBtn){
                    Log.d("", "rotate right");
                    sendCmdWithAction(Constants.CarAction.RotirePeLocDreapta);
                }
        }
        else if(event.getAction() == MotionEvent.ACTION_UP) {
            Log.d("", "up");
            sendCmdWithAction(Constants.CarAction.StopEngines);
        }
        return false;
        }
    };


    void sendCmdWithAction(Constants.CarAction cAction){
        byte[] buff = new byte[6];
        buff[0] = (byte)0xAA;
        buff[1] = (byte)cAction.ordinal();
        buff[2] = 2;
        buff[3] = (byte) Integer.parseInt(timeTxt.getText().toString());
        buff[4] = (byte) powerSeekBar.getProgress();
        buff[5] = 0x55;
        BTProtocol.sendByteArray(buff);
    }
}
