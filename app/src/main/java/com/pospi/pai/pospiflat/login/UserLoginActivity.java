package com.pospi.pai.pospiflat.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pax.api.PortException;
import com.pax.api.PortManager;
import com.pospi.adapter.SelectStoreAdapter;
import com.pospi.callbacklistener.HttpCallBackListener;
import com.pospi.dao.GoodsDao;
import com.pospi.dao.MemberDao;
import com.pospi.dao.ShopDao;
import com.pospi.dialog.MyDialog;
import com.pospi.dto.GoodsDto;
import com.pospi.dto.LoginReturnDto;
import com.pospi.dto.MemberDto;
import com.pospi.dto.ShopDto;
import com.pospi.dto.StoreMsgDto;
import com.pospi.http.HttpConnection;
import com.pospi.http.Server;
import com.pospi.pai.pospiflat.Main_Login;
import com.pospi.pai.pospiflat.R;
import com.pospi.pai.pospiflat.base.BaseActivity;
import com.pospi.pai.pospiflat.util.UrlSettingActivity;
import com.pospi.util.constant.URL;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserLoginActivity extends BaseActivity {

    private static final String TAG = "UserLoginActivity";
    private Button btn_login;
    private EditText et_email;
    private EditText et_password;
    private ProgressBar bar;

    public static final int RETURN = 2;
    public static final int  FINSH= 123;

    private HttpConnection httpConnection;
    private HttpCallBackListener listener;

    private LoginReturnDto loginReturnDto;
    private StoreMsgDto msgDto;
    private WifiManager wifiManager;
    String goodsMsg = "";
    String token;

    private LoadingDialog dialog;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RETURN:
                    closeDialog();
                    if (msg.obj.toString().equals("error")) {
//                        showToast("网络请求失败");
                        getNetFromSelf();
                    } else {

                        bar.setVisibility(View.INVISIBLE);
                        loginReturnDto = httpConnection.parseJson(msg.obj.toString());
                        int result = loginReturnDto.getCode();

                        if (result == 200) {
                            SharedPreferences.Editor sp = getSharedPreferences("syy", MODE_PRIVATE).edit();
                            sp.clear();
                            sp.putString("countNum", et_email.getText().toString().trim());
                            sp.putString("countPass", et_password.getText().toString().trim());
                            sp.commit();
                            Toast.makeText(UserLoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
//                            showDialog("正在下载商品");
                            parseGoodsmsg(goodsMsg);
//                            closeDialog();


//                            Toast.makeText(getApplicationContext(), loginReturnDto.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserLoginActivity.this, "请检查账号密码", Toast.LENGTH_SHORT).show();
                        }
// else if (result == 1) {
////                            SharedPreferences.Editor editor = getSharedPreferences("Token", MODE_PRIVATE).edit();
////                            editor.putString("value", loginReturnDto.getValue());
////                            Log.i("value", loginReturnDto.getValue());
////                            editor.apply();
////
////                            editor = getSharedPreferences("shop_user", MODE_PRIVATE).edit();
////                            editor.putString("shop_user", et_email.getText().toString());
////                            editor.apply();
////
////                            String[] names = loginReturnDto.getValue().split("\\,");
////                            editor = getSharedPreferences("token", MODE_PRIVATE).edit();
////                            token = names[0] + "," + names[1];
////                            Log.i("token", token);
////                            editor.putString("driverId", names[2]);
////                            editor.putString("token", names[0] + "," + names[1]);//+","+names[1]
////                            editor.apply();
//                            getData();
//                        }
                    }
                    break;
                case FINSH:
                    closeDialog();
                    SharedPreferences.Editor editor = getSharedPreferences("userMsg", MODE_PRIVATE).edit();
                    editor.putString("name", loginReturnDto.getName());
                    editor.putString("user", loginReturnDto.getUser());
                    editor.putString("uid", loginReturnDto.getUid());
                    editor.commit();
                    SharedPreferences.Editor edit = getSharedPreferences("login_saveInfo", MODE_PRIVATE).edit();
                    edit.putBoolean("login", true);
                    edit.commit();
                    Intent intent = new Intent(UserLoginActivity.this, Main_Login.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    //获取商品信息
    private void getGoods() {
        final RequestParams params = new RequestParams();//实例化后存入键值对
        new Server().getConnect(getApplicationContext(), new URL().SYNC_MENUS, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int code, Header[] headers, byte[] bytes) {
                        if (code == 200) {
                            Log.i("goodsDtosInfo", new String(bytes) + "");
                            try {
                                JSONObject object = new JSONObject(new String(bytes));
                                goodsMsg = new String(bytes);
//                                parseGoodsmsg(new String(bytes));
//                                LoginReturnDto loginReturnDto = new LoginReturnDto();
//                                loginReturnDto.setCode(object.getInt("Result"));
//                                loginReturnDto.setMsg(object.getString("Message"));
//                                Gson gson = new Gson();
//                                goodsDtosInfo = gson.fromJson(object.getString("Value"), new TypeToken<List<GoodsDto>>() {
//                                }.getType());

//                                goodsDao.addGoods(goodsDtosInfo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                    }
                }
        );
    }
    //解析商品信息
    private void parseGoodsmsg(String json) {

        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");
            final List<GoodsDto> dtos = new ArrayList<>();
            if (code == 200) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i=0 ;i<jsonArray.length() ;i++) {
                    GoodsDto gd = new GoodsDto();
                    gd.setGoods_id(jsonArray.getJSONObject(i).getString("id"));
                    gd.setGoods_name(jsonArray.getJSONObject(i).getString("name"));
                    gd.setGoods_num(jsonArray.getJSONObject(i).getString("num"));
                    gd.setGoods_spec(jsonArray.getJSONObject(i).getString("spec"));
                    gd.setGoods_lsj(jsonArray.getJSONObject(i).getString("lsj"));
                    gd.setGoods_jjfs(jsonArray.getJSONObject(i).getString("dzc"));
                    Log.i("goods", jsonArray.getJSONObject(i).getString("id") + "----"+jsonArray.getJSONObject(i).getString("num"));
                    gd.setCategory_sid(jsonArray.getJSONObject(i).getString("cat"));//
                    gd.setSpecification(jsonArray.getJSONObject(i).getString("unit"));//规格单位
                    Log.i("unit", jsonArray.getJSONObject(i).getString("unit") + "---->" + jsonArray.getJSONObject(i).getString("num"));
                    gd.setCostPrice(0);
                    dtos.add(gd);
                }
                showDialog("正在下载商品");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new GoodsDao(UserLoginActivity.this).addGoods(dtos);

                        Message message = Message.obtain();
                        message.what = FINSH;
                        handler.sendMessage(message);
                    }
                }).start();


            }
        } catch (JSONException e) {
            e.printStackTrace();
            Message message = Message.obtain();
            message.what = FINSH;
            handler.sendMessage(message);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlogin);
        initWidgets();//实例化所有的控件
        new Thread(new Runnable() {
            @Override
            public void run() {
                addMember();

            }
        }).start();
//        addCashierMsg();

//        UUID uuid = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");
//        client.read("7C:EC:79:3B:B5:6F",uuid, null, new BleReadResponse() {
//            @Override
//            public void onResponse(int code, byte[] data) {
//                Log.i("bluetooth", new String(data));
//            }
//        });


      /*  byte port = 0;
        boolean tag = true;
        while (tag) {
        try {
            PortManager.getInstance().portOpen(port, "9600,8,n,1");
            Log.e(TAG, "串口打开成功");

        } catch (PortException e) {
            e.printStackTrace();
            Log.e(TAG, "串口打开失败");
        }

        try {
            byte[] result = PortManager.getInstance().portRecvs(port, 1024, 10000);
            if (result.length != 0) {
                tag = false;
                Log.i(TAG, "scanerCode: " + byteToString(result));
                et_email.setText(byteToString(result));
            }
        } catch (PortException e) {
            e.printStackTrace();
            Log.e(TAG, "扫码失败");
        }
        }*/


        //通过回调来获得网络上返回来的json
        listener = new HttpCallBackListener() {
            @Override
            public void CallBack(String Response) {
                Message message = Message.obtain();
                message.what = RETURN;
                message.obj = Response;
                handler.sendMessage(message);
            }
        };
        Log.i("URL", new URL().LOGIN);

        //点击按钮之后会向服务器发送数据并且会接收到返回值，解析返回的json数据判断登录是否成功
        btn_login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        login();
                    }
                }
        );

        et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            /*判断是否是“GO”键*/
                if (actionId == EditorInfo.IME_ACTION_GO) {
                                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }
                    login();
                    return true;
                }
                return false;
            }
        });
    }

//    private void addCashierMsg() {
//        SharedPreferences.Editor editor = getSharedPreferences("cashierMsg" ,MODE_PRIVATE).edit();
//
//    }

    public void login() {

        if (et_email.getText().toString().trim().equals("vip") && et_password.getText().toString().isEmpty()) {
            //Toast.makeText(getApplicationContext(), "请输入密码！", Toast.LENGTH_SHORT).show();
            startActivity(UrlSettingActivity.class);

        } else if (et_email.getText().toString().isEmpty() || et_password.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "请补全用户信息！", Toast.LENGTH_SHORT).show();
        } else {
            if (new MainPospiActivity().isNetworkAvailable(getApplicationContext())) {
                showDialog("正在验证登录信息");
//                                Log.i("getPhoneMac()", "getPhoneMac(): " + getPhoneMac());

//                                HashMap<String, String> map = new HashMap<>();
//                                map.put("Email", et_email.getText().toString().trim());
//                                map.put("Password", et_password.getText().toString().trim());
//                                map.put("Imei", getPhoneMac());
//                                HttpClient.post(UserLoginActivity.class, new URL().LOGIN, map, new CallBack<String>() {
//                                    @Override
//                                    public void onSuccess(String result) {
//                                        Log.i("onSuccess", "onSuccess: " + result);
//                                    }
//                                });
                String marketid = getSharedPreferences("shopMsg", Context.MODE_PRIVATE).getString("shopId", "");
                httpConnection.SendDataToServer(et_email.getText().toString(), et_password.getText().toString(), getPhoneMac(), marketid,listener, getApplicationContext());
            } else {
//                showToast("网络不可用,请检查网络设置！");
                getNetFromSelf();
            }
        }
    }
    //从本地验证
    private void getNetFromSelf() {
      String name = getSharedPreferences("syy", MODE_PRIVATE).getString("countNum","");
      String pass = getSharedPreferences("syy", MODE_PRIVATE).getString("countPass","");
        Log.i("net", name+"---"+pass);

        if (name.equals(et_email.getText().toString().trim()) && pass.equals(et_password.getText().toString().trim())) {
            Intent intent = new Intent(UserLoginActivity.this, Main_Login.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "账号密码错误", Toast.LENGTH_SHORT).show();
        }
    }

    public void showDialog(String msg) {
        if (dialog == null) {
            if (dialog == null) {
                dialog = new LoadingDialog(this).setLoadingText("下载商品中...").setDrawColor(Color.GRAY).setSize(100).setTextSize(20);
                dialog.show();
            }
        }
    }

    public void closeDialog() {
        if (dialog != null) {
            dialog.close();
            dialog = null;
        }
    }

    //对所有的控件进行实例化
    public void initWidgets() {
        btn_login = (Button) findViewById(R.id.userlogin_btn_login);
        et_email = (EditText) findViewById(R.id.userlogin_uesr);
        et_password = (EditText) findViewById(R.id.userlogin_pwd);
        httpConnection = new HttpConnection();
        bar = (ProgressBar) findViewById(R.id.userlogin_pro);
        bar.setVisibility(View.INVISIBLE);
    }


    /**
     * 弹出选择店铺的dialog
     */

    private AlertDialog selectStoreDialog;

    public void selectStore(final List<StoreMsgDto> storename) {
        @SuppressLint("InflateParams")
        View store_lv = getLayoutInflater().inflate(R.layout.store_select_dialog, null);

        ListView lv = (ListView) store_lv.findViewById(R.id.store_select_dialog_lv);
        SelectStoreAdapter adapter = new SelectStoreAdapter(getApplicationContext(), storename);
        lv.setAdapter(adapter);
        selectStoreDialog = new AlertDialog.Builder(this)
                .setView(store_lv)
                .create();

        selectStoreDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(UserLoginActivity.this, MainPospiActivity.class);
                startActivity(intent);
                finish();

                SharedPreferences.Editor edit = getSharedPreferences("login_saveInfo", MODE_PRIVATE).edit();
                edit.putBoolean("login", true);
                edit.apply();

                SharedPreferences.Editor editor = getSharedPreferences("StoreMessage", MODE_PRIVATE).edit();
                editor.putString("Id", storename.get(position).getId());
                editor.putString("Name", storename.get(position).getName());
                editor.putString("Address", storename.get(position).getAddress());
                editor.putString("Contacts", storename.get(position).getContacts());
                editor.putString("Phone", storename.get(position).getPhone());
                editor.putFloat("Discount", storename.get(position).getDiscount());
                editor.apply();
            }
        });

    }

    //得到店铺的列表信息
//    public void getData() {
////        Log.i("getData", "getData");
//        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                List<StoreMsgDto> msgDtos = new ArrayList<>();
//                try {
//                    JSONObject object = new JSONObject(new String(bytes));
//                    Log.i("responseBodyStore", new String(bytes));
//                    if (i == 200) {
//                        loginReturnDto = new LoginReturnDto();
//                        loginReturnDto.setResult(object.getInt("Result"));
//                        loginReturnDto.setMessage(object.getString("Message"));
//                        if (object.getInt("Result") == 1) {
//                            JSONArray array = object.getJSONArray("Value");
//                            for (int j = 0; j < array.length(); j++) {
//                                JSONObject object1 = (JSONObject) array.opt(j);
//                                msgDto = new StoreMsgDto();
//                                msgDto.setId(object1.getString("Id"));
//                                msgDto.setName(object1.getString("Name"));
//                                msgDto.setAddress(object1.getString("Address"));
//                                msgDto.setPhone(object1.getString("Phone"));
//                                msgDto.setContacts(object1.getString("Contacts"));
//                                msgDto.setDiscount((float) object1.getDouble("Discount"));
//                                msgDtos.add(msgDto);
//                            }
//                        } else {
//                            showToast(object.getString("Message"));
//                        }
//                    } else {
//                        showToast(object.getString("Message"));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                selectStore(msgDtos);
//            }
//
//            @Override
//            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                try {
//                    Log.i("responseBodyStore", new String(bytes));
//                    showToast("获取店铺信息失败！");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        RequestParams params = new RequestParams();//实例化后存入键值对
//
//        new Server().getConnect(getApplicationContext(), new URL().SYNCSHOP, params, handler);
//    }

    //获取登录手机的Mac。但是该方法必须要连接上了wifi才可以
    public String getPhoneMac() {
        String macAddress = "000000000000";
        try {
            //首先得到系统的服务
            wifiManager = (WifiManager) UserLoginActivity.this.getSystemService(WIFI_SERVICE);
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


    public void addMember() {
        List<MemberDto> memberDtos = new ArrayList<>();
        MemberDto dto = new MemberDto();
        dto.setBornday(25);
        dto.setBornyear(1994);
        dto.setBornmonth(9);
        dto.setScore(10);
        dto.setTel("15107145495");
        dto.setGetcardtime("20160608");
        dto.setName("齐燕");
        dto.setAddress("湖北武汉武大科技园");
        dto.setNumber("6001102295082");
        memberDtos.add(dto);

        MemberDto dto1 = new MemberDto();
        dto1.setBornday(13);
        dto1.setBornyear(1994);
        dto1.setBornmonth(1);
        dto1.setScore(5);
        dto1.setTel("13016454381");
        dto1.setGetcardtime("20160608");
        dto1.setName("孙康");
        dto1.setAddress("湖北武汉金融港");
        dto1.setNumber("60011022954182");
        memberDtos.add(dto1);

        MemberDto dto2 = new MemberDto();
        dto2.setBornday(27);
        dto2.setBornyear(1994);
        dto2.setBornmonth(7);
        dto2.setScore(17);
        dto2.setTel("15807156017");
        dto2.setGetcardtime("20160608");
        dto2.setName("刘樊");
        dto2.setAddress("湖北武汉");
        dto2.setNumber("600110229515472");
        memberDtos.add(dto2);

        MemberDto dto3 = new MemberDto();
        dto3.setBornday(10);
        dto3.setBornyear(1994);
        dto3.setBornmonth(10);
        dto3.setScore(10);
        dto3.setTel("13477030397");
        dto3.setGetcardtime("20160608");
        dto3.setName("黄琦");
        dto3.setAddress("湖北武汉武大科技园");
        dto3.setNumber("6001102295015");
        memberDtos.add(dto3);


        MemberDto dto4 = new MemberDto();
        dto4.setBornday(27);
        dto4.setBornyear(1991);
        dto4.setBornmonth(5);
        dto4.setScore(10);
        dto4.setTel("18162662580");
        dto4.setGetcardtime("20160608");
        dto4.setName("易超");
        dto4.setAddress("湖北武汉武大科技园");
        dto4.setNumber("6001102295014");
        memberDtos.add(dto4);

        MemberDto dto5 = new MemberDto();
        dto5.setBornday(25);
        dto5.setBornyear(1994);
        dto5.setBornmonth(9);
        dto5.setScore(10);
        dto5.setTel("13151702237");
        dto5.setGetcardtime("20160608");
        dto5.setName("吴建勇");
        dto5.setAddress("湖北武汉武大科技园");
        dto5.setNumber("6001102295013");
        memberDtos.add(dto5);

        MemberDto dto6 = new MemberDto();
        dto6.setBornday(25);
        dto6.setBornyear(1994);
        dto6.setBornmonth(9);
        dto6.setScore(10);
        dto6.setTel("18621363598");
        dto6.setGetcardtime("20160608");
        dto6.setName("龚箭");
        dto6.setAddress("湖北武汉武大科技园");
        dto6.setNumber("6001102295012");
        memberDtos.add(dto6);

        MemberDto dto7 = new MemberDto();
        dto7.setBornday(25);
        dto7.setBornyear(1994);
        dto7.setBornmonth(9);
        dto7.setScore(10);
        dto7.setTel("18521737710");
        dto7.setGetcardtime("20160608");
        dto7.setName("Nora");
        dto7.setAddress("湖北武汉武大科技园");
        dto7.setNumber("6001102295011");
        memberDtos.add(dto7);

        new MemberDao(getApplicationContext()).addMember(memberDtos);


    }

    @Override
    protected void onStart() {
        super.onStart();
        getShop();
        getGoods();
    }

    @Override
    protected void onDestroy() {
        if (selectStoreDialog != null) {
            selectStoreDialog.dismiss();
            selectStoreDialog = null;
        }
        super.onDestroy();
    }

    //将读取到的byte[]数组转换成String
    public String byteToString(byte[] b) {
        Log.i("cardno", "byteToString: " + b);
        char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[((b[i] & 0xF0) >>> 4)]);
            sb.append(HEX_DIGITS[(b[i] & 0xF)]);
        }
        String str = sb.toString();
        String str2 = "";
        for (int i = 0; i < str.length(); i++) {
            if (i % 2 == 0) {
                if (!str.substring(i, i + 2).equals("00")) {
                    str2 = str2 + (char) Integer.parseInt(str.substring(i, i + 2), 16);
                } else {
                    break;
                }
            }
        }
        Log.i("cardno2", "byteToString: " + str2);
        return str2;
    }
    //获取店铺
    private void getShop() {
        final RequestParams params = new RequestParams();//实例化后存入键值对
        new Server().getConnect(getApplicationContext(), new URL().SYNCSHOP, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int code, Header[] headers, byte[] bytes) {
                        if (code == 200) {
                            parseShop(new String(bytes));

                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                    }
                }
        );
    }

    private void parseShop(String s) {
        try {
            JSONObject object = new JSONObject(s);
            JSONArray array = object.getJSONArray("data");
            List<ShopDto> list = new ArrayList<>();
            for (int i =0;i<array.length();i++) {
                JSONObject shop = array.getJSONObject(i);
                ShopDto dto = new ShopDto();
                dto.setShopId(shop.getString("id"));
                dto.setNum(shop.getString("num"));
                dto.setName(shop.getString("name"));
                dto.setAd(shop.getString("ad"));
                list.add(dto);
            }
            new ShopDao(this).insert(list);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}