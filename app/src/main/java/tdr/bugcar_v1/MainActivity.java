package tdr.bugcar_v1;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    Intent bluetoothPanelActivity = null;
    Activity thisActivity = this;
    TextView btServiceStatusTxt;
    EditText theByteTxt;
    Button sendThisByteBtn;
    Spinner sendAsTypesSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btServiceStatusTxt = (TextView)findViewById(R.id.btServiceStatusTxt);
        sendThisByteBtn=(Button)findViewById(R.id.sendThisByteBtn);
        theByteTxt=(EditText)findViewById(R.id.theByteTxt);
        sendAsTypesSpinner=(Spinner)findViewById(R.id.sendAsTypesSpinner);
        theByteTxt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        //Intent intent = new Intent(this, DisplayMessageActivity.class);
        //startActivity(intent);
        utilis.initVars();
    }

    @Override
    protected void onStart() {
        super.onStart();
        utilis.currentActivity = this;
        utilis.applicationContext=getApplicationContext();
        utilis.initOrReinitBTChatService(mHandler);
        btServiceStatusTxt.setText(R.string.starting);
        sendThisByteBtn.setEnabled(false);
        switch (utilis.btChatService.getState()) {
            case BluetoothChatService.STATE_CONNECTED:
                //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName))
                btServiceStatusTxt.setText(getString(R.string.connected_to, utilis.btChatService.getConnectedDevice().getName()));
                sendThisByteBtn.setEnabled(true);
                //mConversationArrayAdapter.clear();
                break;
            case BluetoothChatService.STATE_CONNECTING:
                btServiceStatusTxt.setText(R.string.connecting);
                //setStatus(R.string.title_connecting);
                break;
            case BluetoothChatService.STATE_LISTEN:
            case BluetoothChatService.STATE_NONE:
                btServiceStatusTxt.setText(R.string.not_connected);
                //setStatus(R.string.title_not_connected);
                break;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void openBluetoothPanel (View view){
        if(bluetoothPanelActivity == null)
            bluetoothPanelActivity = new Intent(this, BluetoothPanelActivity.class);
        startActivity(bluetoothPanelActivity);
    }

    public void openTestActivity(View view){
        Intent testActivity = new Intent(this, TestActivity.class);
        startActivity(testActivity);
    }
    public void disconnectBTServiceIfConnected(View view){
        if(utilis.btChatService.getState() == BluetoothChatService.STATE_CONNECTED)
            utilis.btChatService.stop();
    }
    public void sendThisByteBtnAction(View view){
        String s = theByteTxt.getText().toString();
        //utilis.displayToast(s);
        utilis.btChatService.write(s.getBytes());
        utilis.displayToast("Sent!");
    }
    Intent controlPanel1;
    public void openControlPanel1(View view){
        if(controlPanel1 == null)
            controlPanel1 = new Intent(this, remote_control_activity.class);
        startActivity(controlPanel1);
    }
    public void openControlPanel2(View view){
        Intent testActivity = new Intent(this, goActivity2.class);
        startActivity(testActivity);
    }
    public void openMessagesActivity(View view){
        Intent rma = new Intent(this, ReceivedMessagesActivity.class);
        startActivity(rma);
    }
    public void openMs2(View view){
        //utilis.displayToast(String.valueOf((int)Constants.CarAction.IVelocityAvg.ordinal()));
        Intent ms2a = new Intent(this, MS2Activity.class);
        startActivity(ms2a);
    }
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Activity activity = thisActivity;
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            Log.d("", "Connected to: " + utilis.btChatService.getConnectedDevice().getName());
                            btServiceStatusTxt.setText(getString(R.string.connected_to, utilis.btChatService.getConnectedDevice().getName()));
                            sendThisByteBtn.setEnabled(true);
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Log.d("", "Connecting...");
                            btServiceStatusTxt.setText(R.string.connecting);
                            sendThisByteBtn.setEnabled(false);
                            //setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            sendThisByteBtn.setEnabled(false);
                            Log.d("", "Not connected!");
                            btServiceStatusTxt.setText(R.string.not_connected);
                            //setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    utilis.LogByteArray("Sent", writeBuf, (short)writeBuf.length);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //Log.d("message received", readMessage);
                    //utilis.displayToast(utilis.btChatService.getConnectedDevice().getName()+": "+readMessage);
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    //do nothing, we can get this from utilis.btChatService.getConnDevi().name. ...
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        //Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                        //        Toast.LENGTH_SHORT).show();
                        Log.d("As Toast", msg.getData().getString(Constants.TOAST));
                    }
                    break;
            }
        }
    };

}
