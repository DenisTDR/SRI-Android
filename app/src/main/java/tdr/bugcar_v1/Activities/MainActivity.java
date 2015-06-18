package tdr.bugcar_v1.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import tdr.bugcar_v1.BT.BluetoothChatService;
import tdr.bugcar_v1.Constants;
import tdr.bugcar_v1.R;
import tdr.bugcar_v1.ext;
import tdr.bugcar_v1.extVars;
import tdr.bugcar_v1.utilis;


public class MainActivity extends BaseActivity {

    Intent bluetoothPanelActivity = null;
    TextView btServiceStatusTxt, distTxt, avgVelTxt, timeEnginesOnTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btServiceStatusTxt = (TextView)findViewById(R.id.btServiceStatusTxt);
        distTxt = (TextView)findViewById(R.id.distTxt);
        avgVelTxt = (TextView)findViewById(R.id.avgVelTxt);
        timeEnginesOnTxt = (TextView)findViewById(R.id.timeEnginesOnTxt);

    }

    @Override
    protected void onStart() {
        super.onStart();
        btServiceStatusTxt.setText(R.string.starting);
        switch (utilis.btChatService.getState()) {
            case BluetoothChatService.STATE_CONNECTED:
                //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName))
                btServiceStatusTxt.setText(getString(R.string.connected_to, utilis.btChatService.getConnectedDevice().getName()));
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
        //ext.loadPreferences();
        this.ReceivedInfos(0);

    }

    @Override
    protected void onResume(){
        super.onResume();
        ext.loadPreferences();
    }

    @Override
    public void ReceivedInfos(int what){
        if(what!=0) return;

        if(extVars.DistanceMM < 0)
            return;
        distTxt.setText(String.valueOf(extVars.DistanceMM) + " mm");
        if(extVars.TimeDS==0)
            avgVelTxt.setText(String.valueOf(0));
        else
            avgVelTxt.setText(String.valueOf( utilis.round(1.0*extVars.DistanceMM/extVars.TimeDS / 100, 3))  + " m/s");
        timeEnginesOnTxt.setText(String.valueOf(extVars.TimeDS/10.0) + " s");
    }

    public void openCarInfoActivity(View view){
        startActivity(new Intent(this, CarInfoActivity.class));
    }

    public void openBluetoothPanel (View view){
        if(bluetoothPanelActivity == null)
            bluetoothPanelActivity = new Intent(this, BluetoothPanelActivity.class);
        startActivity(bluetoothPanelActivity);
    }

    public void openCarCommands(View view){
        Intent testActivity = new Intent(this, CarCommandsActivity.class);
        startActivity(testActivity);
    }
    public void disconnectBTServiceIfConnected(View view){
        if(utilis.btChatService.getState() == BluetoothChatService.STATE_CONNECTED)
            utilis.btChatService.stop();
    }

    Intent remoteControlIntent;
    public void openRemoteControlClickAction(View view){
        if(remoteControlIntent == null) {
            remoteControlIntent = new Intent(this, RemoteControlActivity.class);
            Log.d("", "intent created!");
        }
        startActivity(remoteControlIntent);

    }
    public void openMessagesActivity(View view){
        Intent rma = new Intent(this, LogsAndMessagesActivity.class);
        startActivity(rma);
    }

    public void testUnsignedByte(View view){
        byte b[] = new byte[5];
        b[0] = 1;
        b[1] = 55;
        b[2] = 3;
        b[3] = (byte)196;
        b[4] = 5;
        Log.d("", String.valueOf(utilis.byteArrayToInt(b, 1)));
        //ext.loadPreferences();
        final PackageManager pm = getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> appList = pm.queryIntentActivities(mainIntent, 0);
        Collections.sort(appList, new ResolveInfo.DisplayNameComparator(pm));

        for (ResolveInfo temp : appList) {

            Log.v("my logs", "package and activity name = "
                    + temp.activityInfo.packageName + "    "
                    + temp.activityInfo.name);


        }
    }
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            //Log.d("", "Connected to: " + utilis.btChatService.getConnectedDevice().getName());
                            btServiceStatusTxt.setText(getString(R.string.connected_to, utilis.btChatService.getConnectedDevice().getName()));
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            //Log.d("", "Connecting...");
                            btServiceStatusTxt.setText(R.string.connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            //Log.d("", "Not connected!");
                            btServiceStatusTxt.setText(R.string.not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    //byte[] writeBuf = (byte[]) msg.obj;
                    //utilis.LogByteArray("Sentn", writeBuf, (short)writeBuf.length);
                    break;

            }
        }
    };

}
