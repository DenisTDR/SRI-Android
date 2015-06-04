package tdr.bugcar_v1;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;


public class ReceivedMessagesActivity extends ActionBarActivity {

    EditText byte1Txt, byte2Txt, byte3Txt, byte4Txt;
    Spinner carActionsSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_messages);
        messagesListView = (ListView) findViewById(R.id.messagesListView);
        utilis.receivedMessagesAdapter = new ArrayAdapter<String>(this, R.layout.row,  utilis.receivedMessagesList);
        messagesListView.setAdapter( utilis.receivedMessagesAdapter);
        //utilis.receivedMessagesAdapter.add("oncreating...");

        carActionsSpinner = (Spinner) findViewById(R.id.carActionsSpinner);
        carActionsSpinner.setAdapter(new ArrayAdapter<Constants.CarAction>(this, R.layout.spinner_item, Constants.CarAction.values()));

        byte1Txt = (EditText)findViewById(R.id.byte1Txt);
        byte2Txt = (EditText)findViewById(R.id.byte2Txt);
        byte3Txt = (EditText)findViewById(R.id.byte3Txt);
        byte4Txt = (EditText)findViewById(R.id.byte4Txt);

        utilis.currentActivity = this;

        byte1Txt.setText(extVars.byteTxtValues[1]);
        byte2Txt.setText(extVars.byteTxtValues[2]);
        byte3Txt.setText(extVars.byteTxtValues[3]);
        byte4Txt.setText(extVars.byteTxtValues[4]);
        carActionsSpinner.setSelection(extVars.selectedCarAction.ordinal(), true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    ListView messagesListView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_received_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        extVars.byteTxtValues[1]=byte1Txt.getText().toString();
        extVars.byteTxtValues[2]=byte2Txt.getText().toString();
        extVars.byteTxtValues[3]=byte3Txt.getText().toString();
        extVars.byteTxtValues[4]=byte4Txt.getText().toString();
        extVars.selectedCarAction = (Constants.CarAction)carActionsSpinner.getSelectedItem();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void clearMessages(View view){
        utilis.receivedMessagesAdapter.clear();
        //utilis.receivedMessagesAdapter.add("clicked...");
    }
    public void resetBtnProtocolAction(View view){
        BTProtocol.state = Constants.BTState.WaitingStartByte;
        utilis.receivedAMessage("BTProtocol state changed to 'WaitingStartByte'");
    }
    public void sendMessageBtnClick(View view){

        int len, cnt=3;
        len = (byte1Txt.getText().length() > 0? 1: 0)
            + (byte2Txt.getText().length() > 0? 1: 0)
            + (byte3Txt.getText().length() > 0? 1: 0)
            + (byte4Txt.getText().length() > 0? 1: 0);

       // utilis.displayMessage(String.valueOf(len));

        byte[] msg = new byte[len + 4];
        msg[0] = Constants.StartByte;
        msg[1] = (byte)((Constants.CarAction)carActionsSpinner.getSelectedItem()).ordinal();
        msg[2] = (byte)len;

        if(byte1Txt.getText().length() > 0)
            msg[cnt++] = (byte)Integer.parseInt(byte1Txt.getText().toString());
        if(byte2Txt.getText().length() > 0)
            msg[cnt++] = (byte)Integer.parseInt(byte2Txt.getText().toString());
        if(byte3Txt.getText().length() > 0)
            msg[cnt++] = (byte)Integer.parseInt(byte3Txt.getText().toString());
        if(byte4Txt.getText().length() > 0)
            msg[cnt++] = (byte)Integer.parseInt(byte4Txt.getText().toString());
        msg[cnt]=Constants.EndByte;

        //for(int i=0;i<len+4; i++)
        //    Log.d("RMA", String.valueOf ((int)msg[i]));

        BTProtocol.sendByteArray(msg);
    }
}
