package com.pospi.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.print.sdk.PrinterInstance;
import com.gprinter.aidl.GpService;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.newland.jsums.paylib.model.OrderInfo;
import com.pospi.dao.OrderDao;
import com.pospi.pai.pospiflat.login.UserLoginActivity;
import com.pospi.pai.pospiflat.more.LanYaActivity;
import com.pospi.pai.pospiflat.util.DeletFile;
import com.pospi.service.ERPService;
import com.pospi.service.UpLoadService;
import com.pospi.util.constant.URL;
import com.xiasuhuei321.loadingdialog.manager.StyleManager;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.io.File;
import java.util.UUID;

import me.xiaopan.android.preference.PreferencesUtils;

/**
 * Created by huangqi on 2016/7/24.
 */
public class App extends Application {
    public static boolean isAidl;

    public static GpService mGpService = null;
    private static boolean state = false;
    private static Context context;
//    public static String mac = "7C:EC:79:3B:B5:6F";
    public static UUID serviceUuid = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public static UUID characterUuid = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");
    public static UUID characterUuidWrite = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private static boolean isUpLoad = true;
    public  static BluetoothClient mClient;

    public static boolean isHasNoUpLoad() {
        return hasNoUpLoad;
    }

    public static void setHasNoUpLoad(boolean hasNoUpLoad) {
        App.hasNoUpLoad = hasNoUpLoad;
    }

    private static boolean hasNoUpLoad = false;

    public static PrinterInstance mPrinter;
//    public static boolean openLocalPrinter = true;
//    public static boolean openWifiPrinter = false;
//    public static Socket wifiPrinterSocket;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        if (!(new OrderDao(App.getContext()).findOrderERPNOUpLoad().size() > 0) || !(new OrderDao(App.getContext()).findOrderNOUpLoad().size() > 0)) {
            App.hasNoUpLoad = false;
        } else {
            App.hasNoUpLoad = true;
        }
        StyleManager s = new StyleManager();
        //在这里调用方法设置s的属性
        //code here...
        s.Anim(false).repeatTime(0).contentSize(-1).intercept(true);
        LoadingDialog.initStyle(s);

        isUpLoad = PreferencesUtils.getBoolean(App.getContext(), "IsUpLoad", true);
        if (isUpLoad) {//如果打开上传订单开关。则开启上传服务
            Intent i = new Intent(getApplicationContext(), UpLoadService.class);
            startService(i);
            SharedPreferences preferences = this.getSharedPreferences("ERP", Context.MODE_PRIVATE);
            String url = preferences.getString("url", "");//地址
            //检测机器是否为佳博，若是就开启erp服务
            if (!TextUtils.isEmpty(url) && Build.MODEL.equals(URL.MODEL_DT92)) {
                Intent erp = new Intent(context, ERPService.class);
                startService(erp);
            }

        }
        //检测机器是否为d800，若是则初始化米雅支付配置文件
        if (Build.MODEL.equals(URL.MODEL_D800)) {
            initMiYa();
        }

     mClient = new BluetoothClient(this);

//        SharedPreferences sp = getSharedPreferences("receipt_num", Context.MODE_PRIVATE);
//        mac = sp.getString("mac", "");

        isAidl = true;
        AidlUtil.getInstance().connectPrinterService(this);


    }
    public void setAidl(boolean aidl) {
        isAidl = aidl;
    }
    //初始化蓝牙通信
//    private void initBluetooth(){
//        if (mClient == null) {
//            mClient = new BluetoothClient(context);
//            mClient.openBluetooth();
//            if (mClient.openBluetooth()) {
//                BleConnectOptions options = new BleConnectOptions.Builder()
//                        .setConnectRetry(3)   // 连接如果失败重试3次
//                        .setConnectTimeout(30000)   // 连接超时30s
//                        .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
//                        .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
//                        .build();
//                mClient.connect(mac, options, new BleConnectResponse() {
//                    @Override
//                    public void onResponse(int code, BleGattProfile data) {
//                        Log.i("blue", code + "");
//                    }
//                });
//            }
//        }
//
//    }

    private void initMiYa() {
        /**
         * 初始化米雅配置文件
         */
        String filePath = android.os.Environment.getExternalStorageDirectory().getPath() + "/miyajpos/miyaapay/config";
        File f = new File(filePath );
        if (f.exists()){
            DeletFile.deleteAllFiles(f);
        }
        WriteToSDcard wtd = new WriteToSDcard(this);
        wtd.write("config", "miyaConfig.txt");
        wtd.write("config" ,"payConfig.txt");
        wtd.write("config", "printConfig.txt");
        wtd.write("miyalog" , "log.log");
        wtd.write("miyareceitp" , "receitp.txt");
    }

    //获取登录手机的Mac。但是该方法必须要连接上了wifi才可以
    public static String getPhoneMac() {
        String macAddress = "000000000000";
        try {
            //首先得到系统的服务
            WifiManager wifiManager;wifiManager = (WifiManager) getContext().getSystemService(WIFI_SERVICE);
            WifiInfo info = (null == wifiManager ? null : wifiManager.getConnectionInfo());
            //当info 不是为空的时候
            if (info != null) {
                //当info.getMacAddress不为空的时候
                if (!TextUtils.isEmpty(info.getMacAddress())) {
                    macAddress = info.getMacAddress().replace(":", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return macAddress;
    }

    private static OrderInfo lastOrderInfo = null;

    public static OrderInfo getLastOrderInfo() {
        return lastOrderInfo;
    }

    public static void setLastOrderInfo(OrderInfo lastOrderInfo) {
        App.lastOrderInfo = lastOrderInfo;
    }

    public static boolean isUpLoad() {
        return isUpLoad;
    }

    public static void setIsUpLoad(boolean isUpLoad) {
        App.isUpLoad = isUpLoad;
    }

    public static Context getContext() {
        return context;
    }

    public static boolean isState() {
        return state;
    }

    public static void setState(boolean state) {
        App.state = state;
    }
    //    private PrinterServiceConnection conn = null;

    public static GpService getmGpService() {
        return mGpService;
    }

    public static void setmGpService(GpService mGpService) {
        App.mGpService = mGpService;
    }

}
