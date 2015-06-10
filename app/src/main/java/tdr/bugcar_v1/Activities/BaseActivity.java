package tdr.bugcar_v1.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import tdr.bugcar_v1.NewApp;

/**
 * Created by NMs on 6/7/2015.
 */
public class BaseActivity extends Activity {
    protected NewApp mMyApp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyApp = (NewApp)this.getApplicationContext();
    }
    protected void onResume() {
        super.onResume();
        mMyApp.setCurrentActivity(this);
    }
    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    public void ReceivedInfos(int what){

    }

    private void clearReferences(){
        Activity currActivity = mMyApp.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this))
            mMyApp.setCurrentActivity(null);
    }
}