package tdr.bugcar_v1;

import android.os.Message;
import android.util.Log;

import android.os.Handler;

import tdr.bugcar_v1.BT.BTProtocol;

/**
 * Created by NMs on 6/7/2015.
 */
public class Timers extends Handler {
    public static final char TimerCheckBtQueue = 0;
    public static final char TimerAutoConnectBT = 1;
    public static final char TimerForInstSpeed = 2;
    public static final char TIMER_4 = 3;
    public static int cnt = 0;
    @Override
    public void handleMessage(Message msg)
    {
        switch (msg.what)
        {
            case TimerCheckBtQueue:
                BTProtocol.checkBtQueue();
                sendEmptyMessageDelayed(TimerCheckBtQueue, 50);
                break;
            case TimerAutoConnectBT:
                ext.autoConnectBTSMF();
                sendEmptyMessageDelayed(TimerAutoConnectBT, 2000);
                break;
            case TimerForInstSpeed:
                ext.calcInstSpeedSMF();
                sendEmptyMessageDelayed(TimerForInstSpeed, 800);
                break;
            case TIMER_4:
                // Do another time update etc..
                Log.d("TimerExample", "Timer 4");
                //sendEmptyMessageDelayed(TIMER_2, 1000);

                break;
            default:
                //removeMessages(TIMER_1);
                //removeMessages(TIMER_2);
                break;
        }
    }
}
