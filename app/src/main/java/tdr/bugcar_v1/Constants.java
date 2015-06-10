package tdr.bugcar_v1;

/**
 * Created by TDR on 3/7/2015.
 */
public interface Constants {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public enum CarAction {
        NoAction,
        GoFront,
        GoBack,
        GoLeftF,
        GoRightF,
        GoLeftB,
        GoRightB,
        RotirePeLocStanga,
        RotirePeLocDreapta,
        RotireSmechera,
        GoM2P1,
        GoM2P2,
        GoM2P3,
        ParkAt,
        ParallelPark,
        FinalP1,
        FinalP2,
        FinalP3,
        InfoCarStats,
        ISensorsValues,
        ICarSettings,
        Int32Value,
        ReTransmitLastMsg,
        DisplayMessage,
        Led,
        ReadSensorValue,
        StopEngines,
        GetAverageSpeed,
        ParcurgereDistanta,
        ResetThings,
        GetSettings,
        SetSettings,
        CarStarted,
        EndAction
    }
    public enum BTState{
        WaitingStartByte,  //0
        WaitingCarAction,  //1
        WaitingDataLength, //2
        ReadingData,        //3
        WaitingEndByte      //4
    }
    byte StartByte = (byte)0xAA; // -86   170
    byte EndByte = (byte)0x55;   //  85   85
}