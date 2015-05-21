package tdr.bugcar_v1;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;


public class goActivity1 extends ActionBarActivity {
    CheckBox leftChk, rightChk, forwardChk, backChk;
    EditText timeTxt, powerTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        utilis.currentActivity = this;
        setContentView(R.layout.activity_go_activity1);
        leftChk = (CheckBox)findViewById(R.id.leftChk);
        rightChk = (CheckBox)findViewById(R.id.rightChk);
        forwardChk = (CheckBox)findViewById(R.id.forwardChk);
        backChk = (CheckBox)findViewById(R.id.backChk);
        timeTxt = (EditText)findViewById(R.id.timeTxt);
        powerTxt = (EditText)findViewById(R.id.powerTxt);

        leftChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(rightChk.isChecked() == leftChk.isChecked() && leftChk.isChecked() )
                    rightChk.setChecked(false);
            }
        });
        rightChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(leftChk.isChecked() == rightChk.isChecked() && rightChk.isChecked())
                    leftChk.setChecked(false);
            }
        });
        forwardChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(backChk.isChecked() == forwardChk.isChecked() && forwardChk.isChecked() )
                    backChk.setChecked(false);
                if(!backChk.isChecked() && !forwardChk.isChecked())
                    backChk.setChecked(true);
            }
        });
        backChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(forwardChk.isChecked() == backChk.isChecked() && backChk.isChecked() )
                    forwardChk.setChecked(false);
                if(!backChk.isChecked() && !forwardChk.isChecked())
                    forwardChk.setChecked(true);
            }
        });


        if(savedInstanceState!=null) {
            forwardChk.setChecked(savedInstanceState.getBoolean("forwardChk"));
            backChk.setChecked(savedInstanceState.getBoolean("backChk"));
            leftChk.setChecked(savedInstanceState.getBoolean("leftChk"));
            rightChk.setChecked(savedInstanceState.getBoolean("rightChk"));
            timeTxt.setText(savedInstanceState.getString("timeTxt"));
            utilis.displayToast("restored!");
        }
    }

    public void startButtonAction(View view){
        byte[] buff = new byte[7];
        buff[0] = (byte)0xAA;
        if(leftChk.isChecked())
            if(forwardChk.isChecked())
                buff[1] = (byte)Constants.CarAction.GoLeftF.ordinal();
            else
                buff[1] = (byte)Constants.CarAction.GoLeftB.ordinal();
        else if(rightChk.isChecked())
                if(forwardChk.isChecked())
                    buff[1] = (byte)Constants.CarAction.GoRightF.ordinal();
                else
                    buff[1] = (byte)Constants.CarAction.GoRightB.ordinal();
        else if(forwardChk.isChecked())
                buff[1] = (byte)Constants.CarAction.GoFront.ordinal();
            else
                buff[1] = (byte)Constants.CarAction.GoBack.ordinal();
        buff[2] = 2;
        buff[3] = (byte) Integer.parseInt(timeTxt.getText().toString());
        buff[4] = (byte) Integer.parseInt(powerTxt.getText().toString());
        buff[5] = 0x55;
        buff[6] = '\0';

        int i;
        for(i=0;i<6;i++)
            Log.d("sent b", String.valueOf(buff[i]));

        BTProtocol.sendByteArray(buff);

        //utilis.receivedAMessage(Constants.CarAction.values()[buff[1]].toString());

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_go_activity1, menu);
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putBoolean("forwardChk", forwardChk.isChecked());
        savedInstanceState.putBoolean("backChk", backChk.isChecked());
        savedInstanceState.putBoolean("leftChk", leftChk.isChecked());
        savedInstanceState.putBoolean("rightChk", rightChk.isChecked());
        savedInstanceState.putString("timeTxt", timeTxt.getText().toString());
        utilis.displayToast("saved!");

    }
    @Override
     public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        forwardChk.setChecked(savedInstanceState.getBoolean("forwardChk"));
        backChk.setChecked(savedInstanceState.getBoolean("backChk"));
        leftChk.setChecked(savedInstanceState.getBoolean("leftChk"));
        rightChk.setChecked(savedInstanceState.getBoolean("rightChk"));
        timeTxt.setText(savedInstanceState.getString("timeTxt"));
        utilis.displayToast("restored!");

    }

}
