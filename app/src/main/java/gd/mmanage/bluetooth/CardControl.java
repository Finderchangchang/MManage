package gd.mmanage.bluetooth;

import android.bluetooth.BluetoothAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 */

public class CardControl {
    BluetoothAdapter mBluetoothAdapter;

    public List getDivers() {
        List list = new ArrayList<>();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
        return list;
    }
}
