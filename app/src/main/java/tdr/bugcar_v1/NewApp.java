package tdr.bugcar_v1;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import tdr.bugcar_v1.Activities.BaseActivity;

/**
 * Created by NMs on 6/7/2015.
 */
public class NewApp extends Application {
    public void onCreate() {
        super.onCreate();
        ext.thisApp = this;
        utilis.initVars();
    }




    private BaseActivity mCurrentActivity = null;
    public BaseActivity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(BaseActivity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }
}
