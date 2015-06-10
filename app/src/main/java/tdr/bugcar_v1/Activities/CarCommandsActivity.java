package tdr.bugcar_v1.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import tdr.bugcar_v1.BT.BTProtocol;
import tdr.bugcar_v1.Constants;
import tdr.bugcar_v1.R;


public class CarCommandsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_commands);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_car_commands, menu);
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

    public void openCarSettings(View view){
        Intent testActivity = new Intent(this, CarSettingsActivity.class);
        startActivity(testActivity);
    }
    public void doPPBtnPressed(View view){
        byte[] msg = new byte[4];
        msg[0] = Constants.StartByte;
        msg[1] = (byte)Constants.CarAction.ParallelPark.ordinal();
        msg[2] = 0;
        msg[3] = Constants.EndByte;
        BTProtocol.sendByteArray(msg);
    }
    public void resetBtnPressed(View view){
        byte[] msg = new byte[4];
        msg[0] = Constants.StartByte;
        msg[1] = (byte)Constants.CarAction.ResetThings.ordinal();
        msg[2] = 0;
        msg[3] = Constants.EndByte;
        BTProtocol.sendByteArray(msg);
    }
    public void backBtnPressed(View view){
        finish();
    }
}
