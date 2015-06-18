package tdr.bugcar_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import tdr.bugcar_v1.Activities.BluetoothPanelActivity;
import tdr.bugcar_v1.Activities.MainActivity;
import tdr.bugcar_v1.BT.BluetoothChatService;
import tdr.bugcar_v1.BT.TBTDevice;

/**
 * Created by NMs on 6/7/2015.
 */
public final class ext {
    public static NewApp thisApp;
    public static MainActivity mainActivity;
    public static Timers timers;

    public static void ReceivedInfos(final int what){
        if(thisApp != null)
            if(thisApp.getCurrentActivity() != null) {
                Runnable rnb = new Runnable() {
                    public void run() {
                        if(thisApp.getCurrentActivity()!=null)
                            thisApp.getCurrentActivity().ReceivedInfos(what);
                    }
                };

                if (Looper.myLooper() == Looper.getMainLooper())
                    rnb.run();
                else
                    ext.thisApp.getCurrentActivity().runOnUiThread(rnb);
            }
    }
    public static void loadPreferences(){
        SharedPreferences sp = thisApp.getCurrentActivity(). getSharedPreferences("sri_app", Activity.MODE_PRIVATE);

        extVars.autoBTConnect = sp.getBoolean("autoBTConnect", false);
        ext.setAutoConnectBT(extVars.autoBTConnect);

        extVars.autoSetOnCarConnect = sp.getBoolean("autoSetOnCarConnect", false);
        extVars.Setting = sp.getInt("Setting", 2);

    }
    public static void savePreferences(){
        SharedPreferences sp = thisApp.getCurrentActivity(). getSharedPreferences("sri_app", Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();

        edit.putBoolean("autoBTConnect", extVars.autoBTConnect);
        edit.putBoolean("autoSetOnCarConnect", extVars.autoSetOnCarConnect);
        edit.putInt("Setting",  extVars.Setting);

        edit.apply();
    }
    public static void setAutoConnectBT(boolean val){
        extVars.autoBTConnect = val;
        if(val){
            if(!ext.timers.hasMessages(Timers.TimerAutoConnectBT))
                ext.timers.sendEmptyMessage(Timers.TimerAutoConnectBT);
        }
        else{
            if(ext.timers.hasMessages(Timers.TimerAutoConnectBT))
                ext.timers.removeMessages(Timers.TimerAutoConnectBT);
        }
    }
    static boolean receiverRegistered = false;
    static BluetoothDevice desideredBTDevice = null;
    static int clocksToWait=0;
    public static void autoConnectBTSMF(){
        if(!extVars.autoBTConnect)
            return;
        if(utilis.mBluetoothAdapter.isDiscovering())
            return;
        if(utilis.btChatService.getState() == BluetoothChatService.STATE_CONNECTED
                || utilis.btChatService.getState() == BluetoothChatService.STATE_CONNECTING)
            return;
        if(utilis.checkBlueToothEnabled() == 1) {

            if(!receiverRegistered){
                receiverRegistered = true;
                thisApp.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                thisApp.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
            }
            if(!utilis.mBluetoothAdapter.isDiscovering()) {
                if(clocksToWait>0){
                    clocksToWait --;
                    return;
                }
                if(utilis.btChatService.getState() == BluetoothChatService.STATE_LISTEN)
                    utilis.btChatService.stop();
                if(desideredBTDevice != null){
                    utilis.btChatService.connect(desideredBTDevice, true);
                    desideredBTDevice = null;
                }
                else {
                    utilis.mBluetoothAdapter.startDiscovery();
                    Log.d("", "started descopering from ext");
                }
            }
        }
    }
    public static int lastDist;
    public static void calcInstSpeedSMF(){
        if(extVars.DistanceMM == lastDist)
            extVars.InstSpeed = 0;
        else{
            extVars.InstSpeed = (extVars.DistanceMM - lastDist) / 500.0f;
            lastDist = extVars.DistanceMM;
        }
        ext.ReceivedInfos(0);
    }

    private static final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.d("ext:", "Found device:" + device.getName() + "  -"+device.getAddress()+"-");
                if(device.getAddress().equals(extVars.desideredBtMacAddress)){
                    desideredBTDevice = device;
                    utilis.mBluetoothAdapter.cancelDiscovery();
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("", "discovery ended!");
                clocksToWait = 2;
            }
        }
    };
    public static Handler btChatServiceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(ext.thisApp.getCurrentActivity() instanceof BluetoothPanelActivity){
                ((BluetoothPanelActivity)ext.thisApp.getCurrentActivity()).mHandler.handleMessage(msg);
            }
            else if(ext.thisApp.getCurrentActivity() instanceof MainActivity){
                ((MainActivity)ext.thisApp.getCurrentActivity()).mHandler.handleMessage(msg);
            }

            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            Log.d("", "Connected to: " + utilis.btChatService.getConnectedDevice().getName());
                            if(utilis.mBluetoothAdapter.isDiscovering()){
                                utilis.mBluetoothAdapter.cancelDiscovery();
                            }
                            utilis.carStarted();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Log.d("", "Connecting...");

                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            Log.d("", "Not connected!");

                            break;
                    }

                    ext.ReceivedInfos(3);
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String bytesMsg ="[";
                    for(int i=0;i<writeBuf.length-1;i++)
                        bytesMsg += String.valueOf(0xFFFF&(writeBuf[i]&0xFF)) + ",";
                    bytesMsg += String.valueOf(0xFFFF&(writeBuf[writeBuf.length-1]&0xFF))+"]";
                    Log.d("message sent", bytesMsg+"]");
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    break;
                case Constants.MESSAGE_TOAST:
                    break;
            }
        }
    };
}
