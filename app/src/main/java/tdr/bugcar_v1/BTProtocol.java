package tdr.bugcar_v1;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by NMs on 4/20/2015.
 */

public final class BTProtocol {

    public static Constants.BTState state = Constants.BTState.WaitingStartByte;
    static Constants.CarAction actiune;
    static byte len;
    static byte[] date = new byte[250];
    static byte dateCrtIndex;

    static byte[] tmpBuff = new byte[50];
    static char tmpBuffC=0;
    public static ArrayList<byte[]> sendingQueue;

    static void  readByte(byte theByte){
        shouldWait = true;
        Log.d("BTProtocol", "Read byte: "+String.valueOf((int)theByte)+" -> "+String.valueOf((char)theByte));
        tmpBuff[tmpBuffC++] = theByte;
        if(tmpBuffC>=20) {
            //utilis.LogByteArray("Rec", tmpBuff, (short) tmpBuffC);
            tmpBuffC = 0;
        }
        //Log.d("rb", String.valueOf((int)theByte));


        switch (state){
            case WaitingStartByte:
                if(theByte == Constants.StartByte)
                    state = Constants.BTState.WaitingCarAction;
                break;

            case WaitingCarAction:
                if(theByte >= Constants.CarAction.NoAction.ordinal() && theByte < Constants.CarAction.EndAction.ordinal()){
                    state = Constants.BTState.WaitingDataLength;
                    actiune = Constants.CarAction.values()[theByte];
                }
                else{
                    state = Constants.BTState.WaitingStartByte;
                    reTransmit("invalid carAction"); // error ocurred, send retransmit signal, invalid carAction
                }
                break;

            case WaitingDataLength:
                if(theByte == 0){
                    state = Constants.BTState. WaitingEndByte;
                }
                state = Constants.BTState.ReadingData;
                len = theByte;
                dateCrtIndex = 0;
                if(len>100 || len < 0){
                    state = Constants.BTState.WaitingStartByte;
                    reTransmit("invalid len"); // len este invalid
                }
                len = theByte;
                dateCrtIndex = 0;
                break;

            case ReadingData:

                date[dateCrtIndex++] = theByte;
                if(dateCrtIndex >= len)
                    state = Constants.BTState.WaitingEndByte;
                break;

            case WaitingEndByte:
                if(theByte != 0x55){
                    reTransmit("expected end byte"); // error ocurred, send retransmit signal
                }
                else{
                    //Log.d("BTProtocol", "a relatively valid command received!");
                    prelucreazaDatele();
                }
                state = Constants.BTState.WaitingStartByte;
                break;

        }

        //printf("\nstare noua %d: ", state);
    }

    public static boolean sendByteArray(byte[] array){
        if(utilis.btChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
            if (sendingQueue == null) {
                (sendingQueue = new ArrayList<>()).add(array);
                timer = new MyTimers();
                timer.sendEmptyMessageDelayed(MyTimers.TIMER_1, 1000);
            } else
                sendingQueue.add(array);
            return true;
        }
        else {
            utilis.displayMessage("Nu esti conectat la masina/ alt dispozitiv!");
            return false;
        }
    }
    static void reTransmit(){
        reTransmit("");
    }
    static void reTransmit(String msg){
        String msgTmp = "Un mesaj primit de la masina nu a putut fi procesat!" +( msg.equals("")?"":(" ("+msg+")"));
        utilis.displayToast(msgTmp);
        Log.d("BTProtocol", msgTmp);
        utilis.receivedAMessage("[T]:"+msgTmp);
        //nu ii pot cere sa imi trimita din nou acel mesaj pentru ca nu l-a stocat
    }
    static void prelucreazaDatele(){
        switch (actiune){
            case Int32Value:
                int value;
                value = (0xFF & date[0])<<24 | (0xFF & date[1])<<16 | (0xFF & date[2])<<8 | (0xFF & date[3]);
                //Log.d("BTProtocol", "Rec int32: "+String.valueOf(value));
                utilis.receivedAMessage("rec int32 " + value);

                break;
            case DisplayMessage:
                String msg="";
                for(int i=0;i<len;i++)
                    msg += (char)date[i];
                utilis.receivedAMessage(msg);
                Log.d("BTProtocol", "display msg: "+msg);
                break;
            case ReTransmitLastMsg:
                Log.d("BTProtocol", "Am primit comanda pentru a retransmite ultimul mesaj!");
                utilis.displayToast("Am primit comanda pentru a retransmite ultimul mesaj!");
                break;
            case InfoCarStats:

                int tmpDist = 0, tmpTime = 0;
                tmpDist |= date[0] << 24;
                tmpDist |= date[1] << 16;
                tmpDist |= date[2] << 8;
                tmpDist |= date[3] & 0xFF;

                tmpTime |= date[4] << 24;
                tmpTime |= date[5] << 16;
                tmpTime |= date[6] << 8;
                tmpTime |= date[7] & 0xFF;

                extVars.TimeDS = tmpTime;
                extVars.DistanceMM = tmpDist;
                Log.d("", "received time and distance");
                utilis.updateCarStats();

                break;
            case SetSettings:
                int setting = date[0];
                extVars.Setting = setting;
                utilis.updateCarSettings();
                Log.d("", "settings received from car: "+String.valueOf(setting));
                break;
            case GetSettings:

                break;
        }
    }


    static void checkBtQueue(){
        if(shouldWait){
            shouldWait = false;
            return;
        }
        if(sendingQueue.size()>0){
            utilis.btChatService.write(sendingQueue.get(0));
            sendingQueue.remove(0);
        }
    }

    public static boolean shouldWait = false;
    static MyTimers timer;
    public static class MyTimers extends Handler
    {

        public static final char TIMER_1 = 0;
        public static final char TIMER_2 = 1;

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case TIMER_1:

                    checkBtQueue();
                    //Log.d("", "Checked sending queue");
                    sendEmptyMessageDelayed(TIMER_1, 200);
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
