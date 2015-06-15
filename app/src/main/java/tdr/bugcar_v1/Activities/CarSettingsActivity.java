package tdr.bugcar_v1.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import tdr.bugcar_v1.BT.BTProtocol;
import tdr.bugcar_v1.Constants;
import tdr.bugcar_v1.R;
import tdr.bugcar_v1.ext;
import tdr.bugcar_v1.extVars;


public class CarSettingsActivity extends BaseActivity {


    CheckBox debuggingChk, readSensorsChk, sendDistTimeChk, autoConnectBtChk, sendSensorsChk, autoSetOnCarConnectChk;
    TextView statusTxt;
    RadioGroup neonLRG;

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
        neonLRG = (RadioGroup)findViewById(R.id.neonLRG);

        neonLRG.setOnCheckedChangeListener(rgoccl);

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
        setting |= neonSetting << 4;
        extVars.Setting = setting;
        Log.d("", "first4ChkPressed=> Setting:"+String.valueOf(setting));
    }

    public void setSettingsPressed(View view){
        byte setting = 0;
        setting |= debuggingChk.isChecked()?1:0;
        setting |= readSensorsChk.isChecked()?2:0;
        setting |= sendDistTimeChk.isChecked()?4:0;
        setting |= sendSensorsChk.isChecked()?8:0;
        setting |= neonSetting << 4;

        byte[] msg = new byte[3];
        msg[0] = (byte)Constants.CarAction.SetSettings.ordinal();
        msg[1] = 1;
        msg[2] = setting;
        if(BTProtocol.sendByteArray(msg)){
            statusTxt.setText("Wating confirmation...");
        }
        ext.savePreferences();
    }
    public void readSettingsPressed(View view){
        byte[] msg = new byte[1];
        msg[0] = (byte)Constants.CarAction.GetSettings.ordinal();
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
        neonSetting = (byte)((extVars.Setting >> 4) & 0x7);

        ((RadioButton)findViewById(R.id.neonTypeOffRB)).setChecked(neonSetting == 0);
        ((RadioButton)findViewById(R.id.neonType1RB)).setChecked(neonSetting == 1);
        ((RadioButton)findViewById(R.id.neonType2RB)).setChecked(neonSetting == 2);
        ((RadioButton)findViewById(R.id.neonType3RB)).setChecked(neonSetting == 3);
        ((RadioButton)findViewById(R.id.neonType4RB)).setChecked(neonSetting == 4);

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

    public byte neonSetting = 0;
    RadioGroup.OnCheckedChangeListener rgoccl = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // find which radio button is selected
           switch (checkedId){
               case R.id.neonTypeOffRB:
                   neonSetting = 0;
                   break;
               case R.id.neonType1RB:
                   Log.d("", "n1");
                   neonSetting = 1;
                   break;
               case R.id.neonType2RB:
                   Log.d("", "n2");
                   neonSetting = 2;
                   break;
               case R.id.neonType3RB:
                   Log.d("", "n3");
                   neonSetting = 3;
                   break;
               case R.id.neonType4RB:
                   Log.d("", "n4");
                   neonSetting = 4;
                   break;
               default:
                   Log.d("", "default");
                   break;
           }
           if(ext.thisApp.getCurrentActivity() instanceof  CarSettingsActivity)
                ((CarSettingsActivity)ext.thisApp.getCurrentActivity()).first4ChkPressed(null);

        }

    };

}
