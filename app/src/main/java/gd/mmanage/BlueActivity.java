package gd.mmanage;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zkteco.android.IDReader.IDPhotoHelper;
import com.zkteco.android.IDReader.WLTService;
import com.zkteco.id3xx.IDCardReader;
import com.zkteco.id3xx.meta.IDCardInfo;
import com.zkteco.id3xx.util.LogHelper;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/11/22.
 */

public class BlueActivity extends AppCompatActivity {
    ArrayAdapter<String> mAdapter;
    BluetoothAdapter mBluetoothAdapter;
    AlertDialog dialog;
    ProgressDialog pDialog;

    IDCardReader idCardReader = null;
    private WorkThread workThread = null;
    private TextView textView = null;

    private EditText infoName;
    private EditText infoSex;
    private EditText infoNation;
    private EditText infoBirth;
    private EditText infoAddress;
    private EditText infoIdcard;
    private EditText infoCertifying;
    private EditText infoData;
    private ImageView image;

    boolean isRead = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        initUI();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        IntentFilter mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, mFilter);
        // 注册搜索完时的receiver
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, mFilter);
        pDialog = new ProgressDialog(BlueActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setTitle("正在读卡……");
        View view = LayoutInflater.from(BlueActivity.this).inflate(R.layout.dialog, null);
        dialog = new AlertDialog.Builder(BlueActivity.this).setTitle("蓝牙列表").setView(view).setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        dialog.dismiss();
                    }
                }).create();
    }

    public void onResume() {
        super.onResume();
        checkPermission();
    }

    //检测权限
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {

        }
    }

    //权限管理
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
        } else if (requestCode == 1) {

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //搜索结果处理
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String action = arg1.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = arg1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getAddress().equals("DC:0D:30:04:20:D9")) {
                    try {
                        connect(device, "");
                    } catch (Exception e) {
                        Toast.makeText(BlueActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                // setProgressBarIndeterminateVisibility(false);
                setTitle("搜索蓝牙设备");
            }
        }
    };

    //搜索设备操作
    public void OnBnSearch(View view) {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
        //dialog.show();
    }

    //设备版本
    public void OnBnFirmVer(View view) {
        if (null != idCardReader) {
            String strFirmVersion = idCardReader.getFirmwareVersion();
            Toast.makeText(BlueActivity.this, "设备版本：" + strFirmVersion, Toast.LENGTH_SHORT).show();
        }
    }

    //联网操作
    private void connect(BluetoothDevice device, String bluetooth) {
        try {
            idCardReader = new IDCardReader();
            idCardReader.setDevice(device.getAddress());
            int ret = 0;
            if (IDCardReader.ERROR_SUCC == (ret = idCardReader.openDevice())) {
                textView.setText(bluetooth);
                dialog.dismiss();
                Toast.makeText(BlueActivity.this, "连接成功" + bluetooth, Toast.LENGTH_SHORT).show();
            } else {
                idCardReader = null;
                Toast.makeText(BlueActivity.this, "连接失败，错误码:" + ret, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // TODO: handle exception
            idCardReader = null;
            Toast.makeText(BlueActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
        }
    }

    private class WorkThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (isRead) {
                if (!ReadCardInfo()) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("请放卡...");
                        }
                    });

                } else {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("读卡成功，请放入下一张卡");
                        }
                    });

                }
            }
        }
    }

    //重置页面
    private void resetContent() {
        infoName.setText("");
        infoSex.setText("");
        infoNation.setText("");
        infoBirth.setText("");
        infoAddress.setText("");
        infoIdcard.setText("");
        infoCertifying.setText("");
        infoData.setText("");
        image.setImageBitmap(null);
    }

    private void initUI() {
        textView = (TextView) findViewById(R.id.textView);
        infoName = (EditText) findViewById(R.id.infoName);
        infoSex = (EditText) findViewById(R.id.infoSex);
        infoNation = (EditText) findViewById(R.id.infoNation);
        infoBirth = (EditText) findViewById(R.id.infoBirth);
        infoAddress = (EditText) findViewById(R.id.infoAddress);
        infoIdcard = (EditText) findViewById(R.id.infoIdcard);
        infoCertifying = (EditText) findViewById(R.id.infoCertifying);
        infoData = (EditText) findViewById(R.id.infoData);
        image = (ImageView) findViewById(R.id.image);
    }

    //读卡操作
    private boolean ReadCardInfo() {
        if (!idCardReader.sdtFindCard()) {
            return false;
        } else {
            if (!idCardReader.sdtSelectCard()) {
                return false;
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText("正在读卡...");
                resetContent();
            }
        });
        final IDCardInfo idCardInfo = new IDCardInfo();
        if (idCardReader.sdtReadCard(1, idCardInfo)) {
            long time = System.currentTimeMillis();
            final Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTimeInMillis(time);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    infoName.setText(idCardInfo.getName());
                    infoSex.setText(idCardInfo.getSex());
                    infoNation.setText(idCardInfo.getNation());
                    infoBirth.setText(idCardInfo.getBirth());
                    infoAddress.setText(idCardInfo.getAddress());
                    infoIdcard.setText(idCardInfo.getId());
                    infoCertifying.setText(idCardInfo.getDepart());
                    infoData.setText(idCardInfo.getValidityTime());
                    isRead = false;
                }
            });


            if (idCardInfo.getPhoto() != null) {
                byte[] buf = new byte[WLTService.imgLength];
                if (1 == WLTService.wlt2Bmp(idCardInfo.getPhoto(), buf)) {
                    final Bitmap bitmap = IDPhotoHelper.Bgr2Bitmap(buf);
                    if (null != bitmap) {
                        image.post(new Runnable() {
                            @Override
                            public void run() {
                                image.setImageBitmap(bitmap);
                            }
                        });

                    }
                }
            }
            return true;
        } else {
            //playSound(9, 0);
        }
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText("读卡失败...");
            }
        });

        return false;
    }

    //设备连接
    public void OnBnRead(View view) {
        if (null == idCardReader) {
            Toast.makeText(BlueActivity.this, "请先连接设备", Toast.LENGTH_SHORT).show();
            return;
        }
        workThread = new WorkThread();
        workThread.start();// 线程启动
        isRead = true;
    }

    //断开连接
    public void OnBnDisconn() {
        if (null == idCardReader) {
            return;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        idCardReader.closeDevice();
        idCardReader = null;
        Toast.makeText(BlueActivity.this, "断开设备成功", Toast.LENGTH_SHORT).show();
        textView.setText("");
    }

    //页面关闭处理
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        OnBnDisconn();
    }

}

