package tdr.bugcar_v1;

/**
 * Created by TDR on 3/6/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class BTListAdapter extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList<BTListModel> data;
    private static LayoutInflater inflater=null;
    public Resources res;
    BTListModel tempValues=null;
    int i=0;

    /*************  CustomAdapter Constructor *****************/
    public BTListAdapter(Activity a) {

        /********** Take passed values **********/
        activity = a;

        data=new ArrayList<>();
        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public BTListModel getItem(int position) {
        return (BTListModel)data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void clear(){
        data.clear();
    }
    public void add(BTListModel btModel){
        data.add(btModel);
    }
    public void remove(BTListModel btListModel){
        data.remove(btListModel);
    }
    public BTListModel find(BTListModel btListModel){
        return data.get(data.indexOf(btListModel));
    }
    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView nameTextView;
        public TextView addressTextView;
        public ImageView availableImageView;
        public ImageView pairedImageView;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate bt_row.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.bt_row, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.nameTextView = (TextView) vi.findViewById(R.id.nameTextView);
            holder.addressTextView = (TextView) vi.findViewById(R.id.addressTextView);
            holder.availableImageView = (ImageView) vi.findViewById(R.id.availableImageView);
            holder.pairedImageView = (ImageView) vi.findViewById(R.id.pairedImageView);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.nameTextView.setText("No Data");
            holder.addressTextView.setText("Click to refresh.");
            holder.availableImageView.setVisibility(View.GONE);
            holder.pairedImageView.setVisibility(View.GONE);
        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues =  data.get( position );

            /************  Set Model values in Holder elements ***********/

            holder.nameTextView.setText(tempValues.getName());
            holder.addressTextView.setText(tempValues.getAddress());
            holder.availableImageView.setVisibility(tempValues.isAvailable() ? View.VISIBLE : View.GONE);
            holder.pairedImageView.setVisibility(tempValues.isPaired() ? View.VISIBLE : View.GONE);

        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

}