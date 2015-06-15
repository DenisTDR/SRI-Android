package tdr.bugcar_v1.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import tdr.bugcar_v1.BT.BTProtocol;
import tdr.bugcar_v1.Constants;
import tdr.bugcar_v1.R;
import tdr.bugcar_v1.extVars;
import tdr.bugcar_v1.utilis;


public class LogsAndMessagesActivity extends BaseActivity {

    EditText byte1Txt, byte2Txt, byte3Txt, byte4Txt, byte5Txt;
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
        byte5Txt = (EditText)findViewById(R.id.byte5Txt);

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
        SavePreferences();
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume(){
        LoadPreferences();
        super.onResume();
    }
    private void SavePreferences(){
        if(byte1Txt == null) return;
        SharedPreferences sharedPreferences = getSharedPreferences("sri_app", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("byte1", byte1Txt.getText().toString());
        editor.putString("byte2", byte2Txt.getText().toString());
        editor.putString("byte3", byte3Txt.getText().toString());
        editor.putString("byte4", byte4Txt.getText().toString());
        editor.putString("byte5", byte5Txt.getText().toString());
        editor.putInt("carAction", ((Constants.CarAction)carActionsSpinner.getSelectedItem()).ordinal());

        editor.apply();   // I missed to save the data to preference here,.
    }

    private void LoadPreferences(){
        if(byte1Txt == null) return;
        SharedPreferences sp = getSharedPreferences("sri_app", MODE_PRIVATE);
        byte1Txt.setText(sp.getString("byte1", ""));
        byte2Txt.setText(sp.getString("byte2", ""));
        byte3Txt.setText(sp.getString("byte3", ""));
        byte4Txt.setText(sp.getString("byte4", ""));
        byte5Txt.setText(sp.getString("byte5", ""));
        carActionsSpinner.setSelection(sp.getInt("carAction", 0), true);
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

        int len, cnt=2;
        len = (byte1Txt.getText().length() > 0? 1: 0)
                + (byte2Txt.getText().length() > 0? 1: 0)
                + (byte3Txt.getText().length() > 0? 1: 0)
                + (byte4Txt.getText().length() > 0? 1: 0)
                + (byte5Txt.getText().length() > 0? 1: 0);

       // utilis.displayMessage(String.valueOf(len));

        byte[] msg = new byte[len + 2];
        msg[0] = (byte)((Constants.CarAction)carActionsSpinner.getSelectedItem()).ordinal();
        msg[1] = (byte)len;

        if(byte1Txt.getText().length() > 0)
            msg[cnt++] = (byte)Integer.parseInt(byte1Txt.getText().toString());
        if(byte2Txt.getText().length() > 0)
            msg[cnt++] = (byte)Integer.parseInt(byte2Txt.getText().toString());
        if(byte3Txt.getText().length() > 0)
            msg[cnt++] = (byte)Integer.parseInt(byte3Txt.getText().toString());
        if(byte4Txt.getText().length() > 0)
            msg[cnt++] = (byte)Integer.parseInt(byte4Txt.getText().toString());
        if(byte5Txt.getText().length() > 0)
            msg[cnt++] = (byte)Integer.parseInt(byte5Txt.getText().toString());

        //for(int i=0;i<len+4; i++)
        //    Log.d("RMA", String.valueOf ((int)msg[i]));

        BTProtocol.sendByteArray(msg);
    }
    public void clearCommandBtnClick(View view){
        byte1Txt.setText("");
        byte2Txt.setText("");
        byte3Txt.setText("");
        byte4Txt.setText("");
        byte5Txt.setText("");
    }
}
