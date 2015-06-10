package tdr.bugcar_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;

import tdr.bugcar_v1.Activities.CarSettingsActivity;
import tdr.bugcar_v1.Activities.MainActivity;
import tdr.bugcar_v1.BT.BTListAdapter;
import tdr.bugcar_v1.BT.BTProtocol;
import tdr.bugcar_v1.BT.BluetoothChatService;
import tdr.bugcar_v1.BT.TBTDevice;


public final class utilis {

    public static void displayToast(final String msg){
       if(Looper.myLooper() == Looper.getMainLooper()) {
           Toast.makeText(ext.thisApp.getCurrentActivity(), msg, Toast.LENGTH_SHORT).show();
       }
       else
           ext.thisApp.getCurrentActivity().runOnUiThread(new Runnable() {
               public void run() {
                   Toast.makeText(ext.thisApp.getCurrentActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
               }
           });

    }
    public static void displayMessage(final String msg){
        Runnable rnb = new Runnable() {
            public void run() {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ext.thisApp.getCurrentActivity());
                dlgAlert.setMessage(msg);
                dlgAlert.setTitle("");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }};

        if(Looper.myLooper() == Looper.getMainLooper())
            rnb.run();
        else
            ext.thisApp.getCurrentActivity().runOnUiThread(rnb);
    }

    public static ArrayList<String> receivedMessagesList;
    public static ArrayAdapter<String> receivedMessagesAdapter;

    public static BluetoothAdapter mBluetoothAdapter = null;
    static boolean isUnavailable=false;

    public static BluetoothChatService btChatService;
    public static void initOrReinitBTChatService(Handler mHandler){
        if (btChatService == null) {
            btChatService = new BluetoothChatService(mHandler);
            if(checkBlueToothEnabled() == 1)
                btChatService.start();
        }
        else {
            //set handler to receive reponses in this activity
            btChatService.setHandler(mHandler);
        }
    }

    public static void initVars(){
        if(receivedMessagesList == null)
            receivedMessagesList = new ArrayList<String>();
        extVars.SensorsDistance = new int[4];
        utilis.initOrReinitBTChatService(ext.btChatServiceHandler);
        ext.timers = new Timers();
    }
    public static void receivedAMessage(final String msg2){
        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        int miliseconds = c.get(Calendar.MILLISECOND);
        final String msg = String.valueOf(seconds) +"."+ String.valueOf(miliseconds) + ": " + msg2;
        if(Looper.myLooper() == Looper.getMainLooper()) {
            if(receivedMessagesAdapter==null)
                receivedMessagesList.add(msg);
            else
                receivedMessagesAdapter.add(msg);
        }
        else
            ext.thisApp.getCurrentActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (receivedMessagesAdapter == null)
                        receivedMessagesList.add(msg);
                    else
                        receivedMessagesAdapter.add(msg);
                }
            });
        if(msg2.startsWith("x")){
            utilis.playSound();
            utilis.displayMessage(msg2.substring(1));
        }

    }
    public static int byteArrayToInt(byte b[], int offset) {
        int x = 0;
        x |= 0xFFFF & (b[3+offset] & 0xFF);
        x |= (0xFFFF & (b[2+offset] & 0xFF)) << 8;
        x |= (0xFFFF & (b[1+offset] & 0xFF)) << 16;
        x |= (0xFFFF & (b[0+offset] & 0xFF)) << 24;
        return x;
    }
    public static void carStarted(){
        if(extVars.autoSetOnCarConnect){
            byte[] msg = new byte[5];
            msg[0] = Constants.StartByte;
            msg[1] = (byte)Constants.CarAction.SetSettings.ordinal();
            msg[2] = 1;
            msg[3] = (byte)extVars.Setting;
            msg[4] = Constants.EndByte;
            BTProtocol.sendByteArray(msg);
        }
    }
    public static int checkBlueToothEnabled(){
        if(isUnavailable)
            return -1; //unavailable
        if(mBluetoothAdapter==null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (mBluetoothAdapter == null) {
            isUnavailable=true;
            return -1;  //unavailable
        }
        if (!mBluetoothAdapter.isEnabled()) {
            return 0;  //off
        }
        return 1; //on
    }

    public static void playSound(){
        MediaPlayer mPlayer = MediaPlayer.create(ext.thisApp.getCurrentActivity(), R.raw.sunet2);
        mPlayer.start();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static TBTDevice TBTDeviceListFind(ArrayList<TBTDevice> theList, BluetoothDevice device){
        for(int i = 0; i<theList.size(); i++)
            if(theList.get(i).getAddress().equals(device.getAddress()))
                return theList.get(i);
        return null;
    }
    public static BTListAdapter btListAdapterFromBtDeviceList(ArrayList<TBTDevice> theList, Activity a){
        BTListAdapter forRet = new BTListAdapter(a);
        for(int i = 0; i<theList.size(); i++)
            forRet.add(theList.get(i).getModel());
        return forRet;
    }

    public static void LogByteArray(String msg, byte[] ba, short len){
        if(len==0)
            len=(short)ba.length;
        String bytesMsg ="[";
        for(int i=0;i<len-1;i++)
            bytesMsg += String.valueOf((int)ba[i]) + ";";
        Log.d(msg, bytesMsg + String.valueOf(ba[len - 1]) + "]");
    }

}
