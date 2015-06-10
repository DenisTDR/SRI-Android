package tdr.bugcar_v1.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import tdr.bugcar_v1.BT.BTProtocol;
import tdr.bugcar_v1.Constants;
import tdr.bugcar_v1.R;
import tdr.bugcar_v1.ext;
import tdr.bugcar_v1.extVars;


public class CarSettingsActivity extends BaseActivity {


    CheckBox debuggingChk, readSensorsChk, sendDistTimeChk, autoConnectBtChk, sendSensorsChk, autoSetOnCarConnectChk;
    TextView statusTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_settings);

        debuggingChk = (CheckBox)findViewById(R.id.debuggingChk);
        readSensorsChk = (CheckBox)findViewById(R.id.readSensorsChk);
        sendDistTimeChk = (CheckBox)findViewById(R.id.sendDistTimeChk);
        autoConnectBtChk = (CheckBox)findViewById(R.id.autoConnectBtChk);
        sendSensorsChk = (CheckBox)findViewById(R.id.sendSensorsChk);
        autoSetOnCarConnectChk = (CheckBox)findViewById(R.id.autoSetOnCarConnectChk);
        statusTxt = (TextView)findViewById(R.id.statusTxt);

        this.loadFromExtVars();
    }

    @Override
    protected  void onPause(){
        ext.savePreferences();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_car_settings, menu);
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
    public void first4ChkPressed(View view){
        byte setting = 0;
        setting |= debuggingChk.isChecked()?1:0;
        setting |= readSensorsChk.isChecked()?2:0;
        setting |= sendDistTimeChk.isChecked()?4:0;
        setting |= sendSensorsChk.isChecked()?8:0;
        extVars.Setting=setting;
    }

    public void setSettingsPressed(View view){
        byte setting = 0;
        setting |= debuggingChk.isChecked()?1:0;
        setting |= readSensorsChk.isChecked()?2:0;
        setting |= sendDistTimeChk.isChecked()?4:0;
        setting |= sendSensorsChk.isChecked()?8:0;

        byte[] msg = new byte[5];
        msg[0] = Constants.StartByte;
        msg[1] = (byte)Constants.CarAction.SetSettings.ordinal();
        msg[2] = 1;
        msg[3] = setting;
        msg[4] = Constants.EndByte;
        if(BTProtocol.sendByteArray(msg)){
            statusTxt.setText("Wating confirmation...");
        }
        ext.savePreferences();
    }
    public void readSettingsPressed(View view){
        byte[] msg = new byte[4];
        msg[0] = Constants.StartByte;
        msg[1] = (byte)Constants.CarAction.GetSettings.ordinal();
        msg[2] = 0;
        msg[3] = Constants.EndByte;
        if(BTProtocol.sendByteArray(msg)){
            statusTxt.setText("Wating response...");
        }
    }

    @Override
    public void ReceivedInfos(int what){
        if(what != 1) return;
        this.loadFromExtVars();
        statusTxt.setText("Settings Updated!");
    }
    public void loadFromExtVars(){
        debuggingChk.setChecked((extVars.Setting & 1) == 1);
        readSensorsChk.setChecked((extVars.Setting & 2) == 2);
        sendDistTimeChk.setChecked((extVars.Setting & 4) == 4);
        sendSensorsChk.setChecked((extVars.Setting & 8) == 8);
        statusTxt.setText("...");

        autoConnectBtChk.setChecked(extVars.autoBTConnect);
        autoSetOnCarConnectChk.setChecked(extVars.autoSetOnCarConnect);
    }
    public void autoConnectBTChkPressed(View view){
        ext.setAutoConnectBT(autoConnectBtChk.isChecked());
    }
    public void autoSetOnCarConnectChkPressed(View view){
        extVars.autoSetOnCarConnect = autoSetOnCarConnectChk.isChecked();
    }
}
