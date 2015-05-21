package tdr.bugcar_v1;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MS2Activity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ms2);
        utilis.currentActivity = this;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ms2, menu);
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

    public void proba2ms2(View view){
        byte[] buff = new byte[]{Constants.StartByte, (byte)Constants.CarAction.GoM2P2.ordinal(), 0,Constants.EndByte};
        BTProtocol.sendByteArray(buff);
    }

    public void proba3ms2(View view){
        byte[] buff = new byte[]{Constants.StartByte, (byte)Constants.CarAction.GoM2P3.ordinal(), 0,Constants.EndByte};
        BTProtocol.sendByteArray(buff);
    }
}
