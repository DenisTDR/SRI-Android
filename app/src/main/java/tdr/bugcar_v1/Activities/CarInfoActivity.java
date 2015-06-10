package tdr.bugcar_v1.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import tdr.bugcar_v1.R;
import tdr.bugcar_v1.extVars;
import tdr.bugcar_v1.utilis;


public class CarInfoActivity extends BaseActivity {

    TextView distTxt, avgVelTxt, instVelTxt, timeEnginesOnTxt;
    TextView sensor0Txt, sensor1Txt, sensor2Txt, sensor3Txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);

        distTxt = (TextView)findViewById(R.id.distTxt);
        avgVelTxt = (TextView)findViewById(R.id.avgVelTxt);
        instVelTxt = (TextView)findViewById(R.id.instVelTxt);
        timeEnginesOnTxt = (TextView)findViewById(R.id.timeEnginesOnTxt);

        sensor0Txt = (TextView)findViewById(R.id.sensor0Txt);
        sensor1Txt = (TextView)findViewById(R.id.sensor1Txt);
        sensor2Txt = (TextView)findViewById(R.id.sensor2Txt);
        sensor3Txt = (TextView)findViewById(R.id.sensor3Txt);
        this.ReceivedInfos(0);
        this.ReceivedInfos(2);
    }

    @Override
    public void ReceivedInfos(int what) {
        if(what == 0) {

            distTxt.setText(String.valueOf(extVars.DistanceMM / 10.0) + " cm");
            if (extVars.TimeDS == 0)
                avgVelTxt.setText(String.valueOf(0));
            else
                avgVelTxt.setText(String.valueOf(utilis.round(1.0 * extVars.DistanceMM / extVars.TimeDS, 2)) + " cm/s");
            timeEnginesOnTxt.setText(String.valueOf(extVars.TimeDS / 10.0) + " s");
        }
        if(what == 2) {
            sensor0Txt.setText(String.valueOf(extVars.SensorsDistance[0] / 10.0) + " cm");
            sensor1Txt.setText(String.valueOf(extVars.SensorsDistance[1] / 10.0) + " cm");
            sensor2Txt.setText(String.valueOf(extVars.SensorsDistance[2] / 10.0) + " cm");
            sensor3Txt.setText(String.valueOf(extVars.SensorsDistance[3] / 10.0) + " cm");
        }
    }
}
