package tdr.bugcar_v1;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;


public class TestActivity extends ActionBarActivity {

    private ListView theListView;
    private BTListAdapter theBTListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        theListView=(ListView)findViewById(R.id.theListView);
        theBTListAdapter=new BTListAdapter(this);
        theListView.setAdapter(theBTListAdapter);

        populateArrayList();

        utilis.currentActivity = this;
    }

    private void populateArrayList(){
        for(int i=0;i<20;i++){
            final BTListModel oneModel = new BTListModel();
            oneModel.setName("BlackTooth #"+String.valueOf(i));
            oneModel.setAddress("68:05:71:DD:6D:FB:" + String.valueOf(i));
            oneModel.setAvailable(i%2==1);
            oneModel.setPaired(i % 3 != 1);
            theBTListAdapter.add(oneModel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_test, menu);
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
}
