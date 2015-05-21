package tdr.bugcar_v1;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class remote_control_activity extends ActionBarActivity {

    Button goBackBtn, goFrontBtn, turnLeftBtn, turnRightBtn, rotateLeftBtn, rotateRightBtn;
    SeekBar powerSeekBar;
    EditText timeTxt;
    TextView powerTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);

        goBackBtn = (Button) findViewById(R.id.goBackBtn);
        goFrontBtn = (Button) findViewById(R.id.goFrontBtn);
        turnLeftBtn = (Button) findViewById(R.id.turnLeftBtn);
        turnRightBtn = (Button) findViewById(R.id.turnRightBtn);
        rotateLeftBtn = (Button) findViewById(R.id.rotateLeftBtn);
        rotateRightBtn = (Button) findViewById(R.id.rotateRightBtn);
        timeTxt = (EditText) findViewById(R.id.timeTxt);
        powerTxt = (TextView) findViewById(R.id.powerTxt);

        goFrontBtn.setOnTouchListener(goFrontTouchListener);
        goBackBtn.setOnTouchListener(goBackTouchListener);
        turnLeftBtn.setOnTouchListener(turnLeftTouchListener);
        turnRightBtn.setOnTouchListener(turnRightTouchListener);
        rotateLeftBtn.setOnTouchListener(rotateLeftTouchListener);
        rotateRightBtn.setOnTouchListener(rotateRightTouchListener);

        powerSeekBar = (SeekBar) findViewById(R.id.powerSeekBar);


        powerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                powerTxt.setText("Power: "+ (String.format("%.0f", (double)progresValue/seekBar.getMax()*100)) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        powerSeekBar.setProgress(200);
    }

    @Override
    protected void onStart() {
        super.onStart();
        utilis.currentActivity = this;
        utilis.applicationContext = getApplicationContext();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_remote_control_activity, menu);
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

    View.OnTouchListener goFrontTouchListener=new View.OnTouchListener(){
        public boolean onTouch(    View v,    MotionEvent event){
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCmdWithAction(Constants.CarAction.GoFront);
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                sendCmdWithAction(Constants.CarAction.StopEngines);
            }
            return false;
        }
    };
    View.OnTouchListener goBackTouchListener=new View.OnTouchListener(){
        public boolean onTouch(    View v,    MotionEvent event){
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCmdWithAction(Constants.CarAction.GoBack);
            }
            else if(event.getAction() == MotionEvent.ACTION_UP)
                sendCmdWithAction(Constants.CarAction.StopEngines);
            return false;
        }
    };
    View.OnTouchListener turnLeftTouchListener=new View.OnTouchListener(){
        public boolean onTouch(    View v,    MotionEvent event){
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCmdWithAction(Constants.CarAction.GoLeftF);
            }
            else if(event.getAction() == MotionEvent.ACTION_UP)
                sendCmdWithAction(Constants.CarAction.StopEngines);
            return false;
        }
    };
    View.OnTouchListener turnRightTouchListener=new View.OnTouchListener(){
        public boolean onTouch(    View v,    MotionEvent event){
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCmdWithAction(Constants.CarAction.GoRightF);
            }
            else if(event.getAction() == MotionEvent.ACTION_UP)
                sendCmdWithAction(Constants.CarAction.StopEngines);
            return false;
        }
    };
    View.OnTouchListener rotateLeftTouchListener=new View.OnTouchListener(){
        public boolean onTouch(    View v,    MotionEvent event){
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCmdWithAction(Constants.CarAction.RotirePeLocStanga);
            }
            else if(event.getAction() == MotionEvent.ACTION_UP)
                sendCmdWithAction(Constants.CarAction.StopEngines);
            return false;
        }
    };
    View.OnTouchListener rotateRightTouchListener=new View.OnTouchListener(){
        public boolean onTouch(    View v,    MotionEvent event){
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCmdWithAction(Constants.CarAction.RotirePeLocDreapta);
            }
            else if(event.getAction() == MotionEvent.ACTION_UP)
                sendCmdWithAction(Constants.CarAction.StopEngines);
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
