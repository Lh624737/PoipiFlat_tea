package com.pospi.pai.pospiflat.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.pospi.pai.pospiflat.more.LanYaActivity;
import com.pospi.util.App;

import java.util.UUID;

import static com.inuker.bluetooth.library.Code.REQUEST_SUCCESS;

/**
 * Created by acer on 2017/7/20.
 */

public class BluetoothHelper {
    public static void connect(final Context context) {
        BluetoothClient  client = App.mClient;
        SharedPreferences sp = context.getSharedPreferences("receipt_num", Context.MODE_PRIVATE);
        String mac = sp.getString("mac", "");
        int status = client.getConnectStatus(mac);
        if (status == Constants.STATUS_DEVICE_DISCONNECTED){
            client.openBluetooth();
            if (client.openBluetooth()) {
                BleConnectOptions options = new BleConnectOptions.Builder()
                        .setConnectRetry(3)   // 连接如果失败重试3次
                        .setConnectTimeout(30000)   // 连接超时30s
                        .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
                        .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
                        .build();
                client.connect(mac, options, new BleConnectResponse() {
                    @Override
                    public void onResponse(int code, BleGattProfile data) {
                        Log.i("blue", code + "");
                        write(context);
                    }
                });
            }
        }

    }
    public static void write(final Context context){
        SharedPreferences sp = context.getSharedPreferences("receipt_num", Context.MODE_PRIVATE);
        final String mac = sp.getString("mac", "");
        final BluetoothClient  client = App.mClient;
        final byte[] b ="58 42 57 38".getBytes();
        client.write(mac, App.serviceUuid, App.characterUuidWrite, b, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS)
//                    read(context);
//                    Log.i("onResponse", mac+"----"+client.getConnectStatus(mac));
                    Log.i("onResponse", new String(b)+"写入成功");
                    if (client.getConnectStatus(mac) == 2) {
                        Log.i("onResponse", mac + "----" + "已连接");
                    } else {
                        Log.i("onResponse", mac + "----" + "未连接");
                    }

            }
        });
    }
    public static void read(Context context){
        SharedPreferences sp = context.getSharedPreferences("receipt_num", Context.MODE_PRIVATE);
        final String mac = sp.getString("mac", "");
        final BluetoothClient  client = App.mClient;
        client.read(mac, App.serviceUuid, App.characterUuid, new BleReadResponse() {
            @Override
            public void onResponse(int code, byte[] data) {
                if (code == REQUEST_SUCCESS) {
                    Log.i("onResponse", mac+"----"+client.getConnectStatus(mac));
                    Log.i("onResponse", new String(data));
                }
            }
        });

    }
}
