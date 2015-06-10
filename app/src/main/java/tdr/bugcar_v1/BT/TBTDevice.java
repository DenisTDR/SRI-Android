package tdr.bugcar_v1.BT;

import android.bluetooth.BluetoothDevice;

import tdr.bugcar_v1.BT.BTListModel;

/**
 * Created by TDR on 3/7/2015.
 */
public class TBTDevice {

    public BluetoothDevice device;
    boolean discovered;
    public TBTDevice(BluetoothDevice device){
        this.device=device;
        this.discovered=false;
    }
    public TBTDevice(BluetoothDevice device, boolean discovered){
        this.device=device;
        this.discovered=discovered;
    }
    public boolean isDiscovered() {
        return discovered;
    }
    public boolean isPaired() {
        return device.getBondState() == BluetoothDevice.BOND_BONDED;
    }

    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }

    public String getName(){
        return device.getName();
    }
    public String getAddress(){
        return device.getAddress();
    }
    public BTListModel getModel(){
        return new BTListModel(device.getName(), device.getAddress(), this.isDiscovered(), this.isPaired());
    }

    @Override
    public int hashCode() {
        return getAddress().hashCode();
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
