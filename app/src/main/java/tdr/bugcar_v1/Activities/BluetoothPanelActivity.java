package tdr.bugcar_v1.Activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import tdr.bugcar_v1.BT.BTListAdapter;
import tdr.bugcar_v1.BT.BluetoothChatService;
import tdr.bugcar_v1.Constants;
import tdr.bugcar_v1.R;
import tdr.bugcar_v1.BT.TBTDevice;
import tdr.bugcar_v1.utilis;


public class BluetoothPanelActivity extends BaseActivity {

    private ListView btDevicesListView;
    private TextView btServiceStatusTxt, bluetoothStatusTxt;
    private ProgressBar discoveringPB;
    private Button restartDiscoveryBtn;


    private Activity thisActivity = this;

    private BTListAdapter allBTDevicesArrayAdapter;
    private ArrayList<TBTDevice> allBTDevices;


    private static final String TAG = "BluetoothChatFragment";
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("", "onCreate");

        setContentView(R.layout.activity_bluetooth_panel);

        bluetoothStatusTxt=(TextView)findViewById(R.id.bluetoothStatusTxt);
        btServiceStatusTxt=(TextView)findViewById(R.id.btServiceStatusTxt);

        discoveringPB=(ProgressBar)findViewById(R.id.discoveringPB);
        restartDiscoveryBtn=(Button)findViewById(R.id.restartDiscoveryBtn);

        btDevicesListView = (ListView) findViewById(R.id.btDevicesListView);

        allBTDevicesArrayAdapter = new BTListAdapter(this);
        allBTDevices = new ArrayList<>();

        btDevicesListView.setAdapter(allBTDevicesArrayAdapter);

        btDevicesListView.setOnItemClickListener(mDeviceClickListener);


        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        startCheckBTTimer();
        isForeground = true;
        Log.d("", "onStart");
        if (utilis.checkBlueToothEnabled() != 1) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            initBTServicesAndDoShits();
        }
    }
    protected void initBTServicesAndDoShits(){

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

        if(utilis.btChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            utilis.btChatService.start();
            allBTDevicesArrayAdapter.clear();
            allBTDevices.clear();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        isForeground=true;
        Log.d("", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground=false;
        Log.d("", "onPause");
        this.unregisterReceiver(mReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (utilis.mBluetoothAdapter != null) {
            utilis.mBluetoothAdapter.cancelDiscovery();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth_panel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void listPairedBTs(){
        Log.d("", "Listing paired BTs...");

        if(utilis.checkBlueToothEnabled()!=1){
            utilis.displayToast("Bluetooth not enabled!!!");
            return;
        }
        Log.d("", "bt status: " + utilis.checkBlueToothEnabled());
        Log.d("", "Listing paired devices...");

        Set<BluetoothDevice> pairedDevices = utilis.mBluetoothAdapter.getBondedDevices();
        // If there are paired devices

        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                Log.d("", "paired with: " + device.getName() + " -> " + device.getAddress());

                TBTDevice tDevice = new TBTDevice(device);
                allBTDevices.add(tDevice);
                allBTDevicesArrayAdapter.add(tDevice.getModel());
            }
        }
        Log.d("", "-----");

    }

    Timer checkBTTimer;
    final Handler handler = new Handler();
    boolean isForeground=true;
    public void startCheckBTTimer() {
        if(checkBTTimer!=null){
            //utilis.displayToast("already timered");;
            return;
        }
        checkBTTimer = new Timer();
        checkBTTimer.schedule(new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        if(!isForeground)
                            return;
                        int bTval = utilis.checkBlueToothEnabled();
                        bluetoothStatusTxt.setBackgroundResource(bTval==-1?R.color.aGray:bTval==0?R.color.red1:R.color.lightGreen);
                        bluetoothStatusTxt.setText(bTval==-1?R.string.unavailable:bTval==0?R.string.off:R.string.on);
                    }
                });
            }
        }, 500, 2500);
    }

    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View clickedItem, int position, long id) {


            if(allBTDevices.size()<=0){
                //doDiscovery(null);
                if(!utilis.mBluetoothAdapter.isDiscovering())
                    doDiscovery(null);
                else
                    utilis.displayToast("Already discovering...");
                return;
            }

            utilis.mBluetoothAdapter.cancelDiscovery();
            Log.d("", "clicked: #"+String.valueOf(position));
            if(utilis.btChatService.getState()!=BluetoothChatService.STATE_NONE)
                utilis.btChatService.stop();
            utilis.btChatService.connect(allBTDevices.get(position).device, true);

        }
    };

    public void disconnectBTServiceIfConnected(View view){
        if(utilis.btChatService.getState()==BluetoothChatService.STATE_CONNECTED)
            utilis.btChatService.stop();
    }

    public void doDiscovery(View view) {
        allBTDevicesArrayAdapter.clear();
        allBTDevices.clear();
        allBTDevicesArrayAdapter.notifyDataSetChanged();
        listPairedBTs();
        Log.d("", "doDiscovery()ing...");
        discoveringPB.setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (utilis.mBluetoothAdapter.isDiscovering()) {
            utilis.mBluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        utilis.mBluetoothAdapter.startDiscovery();
        restartDiscoveryBtn.setEnabled(false);
    }

    public void sendRandomString(View view){
        // Check that we're actually connected before trying anything
        if (utilis.btChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            utilis.displayToast("nu esti conectat la nicun dispozitiv BT");
            return;
        }

        // Check that there's actually something to send

        // Get the message bytes and tell the BluetoothChatService to write
        String message = UUID.randomUUID().toString();
        byte[] send = message.getBytes();
        utilis.btChatService.write(send);

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.d("", "Found device:" + device.getName() + "  -"+device.getAddress()+"-");

                TBTDevice tBTDevice;
                if((tBTDevice=utilis.TBTDeviceListFind(allBTDevices, device)) == null){
                    tBTDevice = new TBTDevice(device, true);
                    allBTDevices.add(tBTDevice);
                    allBTDevicesArrayAdapter.add(tBTDevice.getModel());
                    Log.d("", "listFind = null");
                }
                else{
                    Log.d("", "listFind = st");
                    if(tBTDevice.isDiscovered()) {
                        return;
                    }
                    tBTDevice.setDiscovered(true);
                    allBTDevicesArrayAdapter.find(tBTDevice.getModel()).setAvailable(true);
                }
                allBTDevicesArrayAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                discoveringPB.setVisibility(View.GONE);
                restartDiscoveryBtn.setEnabled(true);
                Log.d("", "discovery ended!");
                if(utilis.btChatService.getState()==BluetoothChatService.STATE_NONE){
                    utilis.btChatService.start();
                }
            }
        }
    };


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("", "selected st shit for secure connection");
                    //connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("", "selected st shit for insecure connection");
                    //connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    initBTServicesAndDoShits();
                    doDiscovery(null);
                    //setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    this.finish();
                }
        }
    }


    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            btServiceStatusTxt.setText(getString(R.string.connected_to, utilis.btChatService.getConnectedDevice().getName()));
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            btServiceStatusTxt.setText(R.string.connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            btServiceStatusTxt.setText(R.string.not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    break;
                case Constants.MESSAGE_READ:
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    break;
                case Constants.MESSAGE_TOAST:
                    break;
            }
        }
    };

}
