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
        NoAction, // 0
        GoFront,  // 1
        GoBack,	  // 2
        GoLeftF,  // 3
        GoRightF, // 4
        GoLeftB,  // 5
        GoRightB, // 6
        RotirePeLocStanga, // 7
        RotirePeLocDreapta, // 8
        RotireSmechera,
        GoM2P1,   // 9
        GoM2P2,   // 10
        GoM2P3,   // 11
        ParkAt,   // 12
        ParallelPark, // 13
        FinalP1,  // 14
        FinalP2,  // 15
        FinalP3,  // 16
        InfoCarStats,     // 19
        Int32Value,    // 20
        ReTransmitLastMsg,  // 21
        DisplayMessage,   // 22
        Led,			  // 23
        ReadSensorValue,  // 24
        StopEngines,      // 25
        GetAverageSpeed,   //26
        ParcurgereDistanta, //27
        ResetThings,		//28
        GetSettings,		//29
        SetSettings,		//29
        EndAction         // 30
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