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
        GoM2P1,   // 7
        GoM2P2,   // 8
        GoM2P3,   // 9
        ParkAt,   // 10
        ParallelPark, // 11
        FinalP1,  // 12
        FinalP2,  // 13
        FinalP3,  // 14
        IVelocityAvg, // 15
        IVelocityInst, // 16
        IDistance,     // 17
        Int32Value,    // 18
        ReTransmitLastMsg,  // 19
        DisplayMessage,   // 20
        Led,			  // 21
        ReadSensorValue, //22
        StopEngines,     //23
        RotirePeLocStanga, // 24
        RotirePeLocDreapta, // 25
        ResetThings,		//26
        EndAction         // 27
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