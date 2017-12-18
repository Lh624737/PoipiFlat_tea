package com.pospi.pai.pospiflat.more;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.pospi.pai.pospiflat.R;
import com.pospi.pai.pospiflat.util.BluetoothHelper;
import com.pospi.util.App;

import java.util.UUID;

/**
 * Created by acer on 2017/7/20.
 */

public class LanYaActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lanya);

    }
    //手动连接
    public void connect(View v) {
        SharedPreferences sp = getSharedPreferences("receipt_num", Context.MODE_PRIVATE);
        String mac = sp.getString("mac", "");
        BluetoothHelper.connect(this);
//        App.mClient.notify(mac, App.serviceUuid, App.characterUuid, new BleNotifyResponse() {
//            @Override
//            public void onNotify(UUID service, UUID character, byte[] value) {
//                Log.i("onResponse", new String(value));
//
//            }
//
//            @Override
//            public void onResponse(int code) {
//
//            }
//        });


    }

}
