package tdr.bugcar_v1;

import android.location.Address;

/**
 * Created by TDR on 3/6/2015.
 */
public class BTListModel {

    private  String Name="no-name";
    private  String Address="no-adress";
    private  boolean IsAvailable = false;
    private  boolean IsPaired = false;

    public  BTListModel(){}

    public BTListModel(String name, String address, boolean available, boolean paired){
        Name=name;
        Address=address;
        IsAvailable=available;
        IsPaired=paired;
    }

    /*********** Set Methods ******************/
    public void setAddress(String address) {
        Address = address;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setAvailable(boolean isAvailable) {
        this.IsAvailable = isAvailable;
    }

    public void setPaired(boolean isPaired) {
        this.IsPaired = isPaired;
    }

    /*********** Get Methods ****************/
    public String getName() {
        return Name;
    }

    public String getAddress() {
        return Address;
    }

    public boolean isAvailable() {
        return IsAvailable;
    }

    public boolean isPaired() {
        return IsPaired;
    }

    @Override
    public int hashCode() {
        return Address.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BTListModel))
            return false;
        if (obj == this)
            return true;

        BTListModel rhs = (BTListModel) obj;
        return this.getAddress().equals(rhs.getAddress());
    }
}