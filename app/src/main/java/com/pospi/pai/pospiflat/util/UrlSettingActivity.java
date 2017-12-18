package com.pospi.pai.pospiflat.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pospi.dao.ShopDao;
import com.pospi.dto.ShopDto;
import com.pospi.pai.pospiflat.R;
import com.pospi.pai.pospiflat.base.BaseActivity;

public class UrlSettingActivity extends BaseActivity {

    private EditText et_basicURl;
    private EditText et_payURL;
    private EditText shopId;
    private EditText jiju;
    private EditText mac;
    private TextView shopName;
    private Button setting_save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
//        initWeidgets();
        shopId = (EditText) findViewById(R.id.login_shopId);
        jiju = (EditText) findViewById(R.id.login_jiju);
        mac = (EditText) findViewById(R.id.login_mac);
        shopName = (TextView) findViewById(R.id.login_shopName);
        setting_save = (Button) findViewById(R.id.setting_save);
        TextView login_back = (TextView) findViewById(R.id.login_back);
        shopId.setText(getParam("pay_shopId"));
        jiju.setText(getParam("pay_jiju"));
        mac.setText(getParam("mac"));
        String shopname = getSharedPreferences("shopMsg", Context.MODE_PRIVATE).getString("name", "");
        shopName.setText(shopname);

        shopId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (shopId.getText().toString().trim().length() > 1) {
                    judgeShop(shopId.getText().toString().trim());
                }

            }
        });



        setting_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("receipt_num", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                editor.putString("pay_shopId" , shopId.getText().toString().trim());
                editor.putString("pay_jiju" , jiju.getText().toString().trim());
                editor.putString("mac", mac.getText().toString().trim());
                editor.commit();

                Toast.makeText(UrlSettingActivity.this, "保存成功", Toast.LENGTH_SHORT).show();

            }
        });


        login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

    }
    private String getParam(String param) {
        SharedPreferences sp = getSharedPreferences("receipt_num", Context.MODE_PRIVATE);
        return sp.getString(param, "");
    }


    //判断店铺
    private void judgeShop(String num) {
        ShopDto shopDto = new ShopDao(this).select(num);
        if (shopDto == null) {
            shopId.setError("店铺号不正确");
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("shopMsg", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.clear();
        editor.putString("name", shopDto.getName());
        editor.putString("shopId", shopDto.getShopId());
        editor.putString("num", shopDto.getNum());
        editor.putString("ad", shopDto.getAd());
        editor.commit();
        shopName.setText(shopDto.getName());

    }


//
//    public void initWeidgets() {
//        et_basicURl = (EditText) findViewById(R.id.website_url_basic);
//        SharedPreferences sharedPreferences = this.getSharedPreferences("url", Context.MODE_PRIVATE);
//        String url = sharedPreferences.getString("url", "");
//        if(!url.equals("")){
//            et_basicURl.setText(url);
//        }
//
//        et_payURL = (EditText) findViewById(R.id.website_url_pay);
//        String pay_url = sharedPreferences.getString("pay_url", "");
//        if(!pay_url.equals("")){
//            et_payURL.setText(pay_url);
//        }
//
//    }
//
//    public void click(View view) {
//        switch (view.getId()) {
//            case R.id.website_cancle:
//                finish();
//                break;
//
//            case R.id.website_finish:
//                //当两个空都为空的时候
//                if (et_payURL.getText().toString().isEmpty() && et_basicURl.getText().toString().isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "设置完成\n服务器将采用默认网址", Toast.LENGTH_SHORT).show();
//                    SharedPreferences.Editor editor = getSharedPreferences("url", Context.MODE_PRIVATE).edit();
//                    editor.putString("url", "http://pos.pospi.com");
//                    editor.putString("pay_url", "");
//                    editor.apply();
//                    finish();
//                    // 当basicURl为空且payURL不为空的时候
//                } else if (et_basicURl.getText().toString().isEmpty() && !et_payURL.getText().toString().isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "基础服务器URL未填写，不能提交！", Toast.LENGTH_SHORT).show();
//                } else if (!et_basicURl.getText().toString().isEmpty() && et_payURL.getText().toString().isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "设置完成\n系统将采用当前网址", Toast.LENGTH_SHORT).show();
//                    SharedPreferences.Editor editor = getSharedPreferences("url", Context.MODE_PRIVATE).edit();
//                    editor.putString("url", et_basicURl.getText().toString());
//                    editor.putString("pay_url", "");
//                    editor.apply();
//                    finish();
//                } else if (!et_basicURl.getText().toString().isEmpty() && !et_payURL.getText().toString().isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "设置完成\n系统将采用当前网址", Toast.LENGTH_SHORT).show();
//                    SharedPreferences.Editor editor = getSharedPreferences("url", Context.MODE_PRIVATE).edit();
//                    editor.putString("url", et_basicURl.getText().toString());
//                    editor.putString("pay_url", et_payURL.getText().toString());
//                    editor.apply();
//                    finish();
//
//                }
//
//
//                break;
//        }
//    }
}
