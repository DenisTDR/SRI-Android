package tdr.bugcar_v1.BT;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import tdr.bugcar_v1.Constants;
import tdr.bugcar_v1.Timers;
import tdr.bugcar_v1.ext;
import tdr.bugcar_v1.extVars;
import tdr.bugcar_v1.utilis;

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


    public static void  readByte(byte theByte){
        shouldWait = true;
        //Log.d("BTProtocol", "Read byte: "+String.valueOf(0xFFFF&(theByte&0xFF))+" -> '"+String.valueOf((char)theByte)+"'");
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
                    state = Constants.BTState.WaitingEndByte;
                    break;
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
                 if(!ext.timers.hasMessages(Timers.TimerCheckBtQueue))
                    ext.timers.sendEmptyMessageDelayed(Timers.TimerCheckBtQueue, 150);
            } else
                sendingQueue.add(array);
            shouldWait = true;
            return true;
        }
        else {
            utilis.displayMessage("Nu esti conectat la masina sau alt dispozitiv!");
            return false;
        }
    }
    static void reTransmit(){
        reTransmit("");
    }
    static void reTransmit(String msg){
        String msgTmp = "Un mesaj primit de la masina nu a putut fi procesat!" +( msg.equals("")?"":(" ("+msg+")"));
        //utilis.displayToast(msgTmp);
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

                if(extVars.DistanceMM >=0) {
                    //Log.d("", "received time and distance");
                    ext.ReceivedInfos(0);

                    if(!ext.timers.hasMessages(Timers.TimerForInstSpeed))
                        ext.timers.sendEmptyMessageDelayed(Timers.TimerForInstSpeed, 500);
                }

                break;
            case ICarSettings:
                extVars.Setting = date[0];
                ext.ReceivedInfos(1);
                Log.d("", "settings received from car: "+String.valueOf(date[0]));
                break;
            case ISensorsValues:
                extVars.SensorsDistance[0] = utilis.byteArrayToInt(date, 0);
                extVars.SensorsDistance[1] = utilis.byteArrayToInt(date, 4);
                extVars.SensorsDistance[2] = utilis.byteArrayToInt(date, 8);
                extVars.SensorsDistance[3] = utilis.byteArrayToInt(date, 12);
                ext.ReceivedInfos(2);
                break;
            case CarStarted:
                utilis.carStarted();
                break;
            case CRCSumFailed:
                Log.d("", "CRC sum error! cmdId: "+String.valueOf(0xFFFF&(date[0]&0xFF)));
                for(int i=0;i<10;i++)
                    utilis.btChatService.write(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00});
                break;
            case ICheckPoint:
                String str="";
                for(int i=0;i<len;i++){
                    str+=String.valueOf((char)date[i]);
                }
                extVars.crtCheckPoint = str;
                ext.ReceivedInfos(7);
                Log.d("am primit checkpoint", str);
                break;
        }
    }

    public static byte crtCmdIndex = 0;
    public static void checkBtQueue(){
        if(shouldWait){
            shouldWait = false;
            return;
        }
        if(sendingQueue.size()>0){
            byte[] array = sendingQueue.get(0);
            if(array.length == 1)
                array = new byte[]{array[0], 0};
            byte crc = 0;
            for(byte c : array)
                crc += c;
            byte[] newArray = new byte[array.length + 3];
            newArray[0] = Constants.StartByte;
            for(byte i=0; i<array.length; i++)
                 newArray[i+1] = array[i];
            newArray[array.length + 1] = crtCmdIndex;
            newArray[array.length + 2] = crc;
            utilis.btChatService.write(newArray);

            sendingQueue.remove(0);
            crtCmdIndex++;
        }
    }

    public static boolean shouldWait = false;

}
