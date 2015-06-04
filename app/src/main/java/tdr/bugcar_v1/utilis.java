package tdr.bugcar_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public final class utilis {
    static void displayToast(final String msg){
       if(Looper.myLooper() == Looper.getMainLooper()) {
           Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show();
       }
       else
           currentActivity.runOnUiThread(new Runnable() {
               public void run() {
                   Toast.makeText(applicationContext, msg,Toast.LENGTH_SHORT).show();
               }
           });

    }
    static void displayMessage(final String msg){
        if(Looper.myLooper() == Looper.getMainLooper()) {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(currentActivity);
            dlgAlert.setMessage(msg);
            dlgAlert.setTitle("");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }
        else
            currentActivity.runOnUiThread(new Runnable() {
                public void run() {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(currentActivity);
                    dlgAlert.setMessage(msg);
                    dlgAlert.setTitle("");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                }
            });
    }

    public static ArrayList<String> receivedMessagesList;
    public static ArrayAdapter<String> receivedMessagesAdapter;

    static BluetoothAdapter mBluetoothAdapter = null;
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

    static void initVars(){
        if(receivedMessagesList==null)
            receivedMessagesList = new ArrayList<String>();

    }
    static void receivedAMessage(final String msg2){
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
            currentActivity.runOnUiThread(new Runnable() {
                public void run() {
                    if(receivedMessagesAdapter==null)
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

    static int checkBlueToothEnabled(){
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
    public static Context applicationContext;
    public static Activity currentActivity;

    public static void updateCarStats(){
        try {
            if (currentActivity instanceof MainActivity) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    ((MainActivity) currentActivity).reloadCarStats();
                } else
                    currentActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (currentActivity instanceof MainActivity)
                                ((MainActivity) currentActivity).reloadCarStats();
                        }
                    });
            }
        }catch(Exception exc) {}
    }
    public static void updateCarSettings(){
        if(currentActivity instanceof CarSettings) {
            if(Looper.myLooper() == Looper.getMainLooper()) {
                ((CarSettings) currentActivity).receivedSettingValues();
            }
            else
                currentActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        ((CarSettings) currentActivity).receivedSettingValues();
                    }
                });
        }
    }

    public static void playSound(){
        MediaPlayer mPlayer = MediaPlayer.create(currentActivity, R.raw.sunet2);
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
    public static class MyTimers extends Handler
    {

        public static final int TIMER_1 = 0;
        public static final int TIMER_2 = 1;

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case TIMER_1:
                    // Do something etc.
                    Log.d("TimerExample", "Timer 1");
                    sendEmptyMessageDelayed(TIMER_1, 1000);
                    break;
                case TIMER_2:
                    // Do another time update etc..
                    Log.d("TimerExample", "Timer 2");
                    sendEmptyMessageDelayed(TIMER_2, 1000);
                    break;
                default:
                    removeMessages(TIMER_1);
                    removeMessages(TIMER_2);
                    break;
            }
        }
    }
}
