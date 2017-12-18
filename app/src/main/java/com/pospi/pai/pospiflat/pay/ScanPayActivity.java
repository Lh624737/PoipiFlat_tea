package com.pospi.pai.pospiflat.pay;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pax.api.scanner.ScannerManager;
import com.pospi.pai.pospiflat.R;
import com.pospi.pai.pospiflat.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanPayActivity extends BaseActivity {

    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.scan_iv)
    ImageView scanIv;
    @Bind(R.id.btn_sure)
    Button btnSure;
    private ScannerManager sm;
    private TextView tvTime;
    private boolean isTimeRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_pay);
        ButterKnife.bind(this);
        tvTime = (TextView) findViewById(R.id.tv_time);



    }

    @Override
    protected void onStart() {
        super.onStart();
        isTimeRun = true;
        new TimeThread().start(); //启动新的线程
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isTimeRun = false;
    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 100;  //消息(一个整型值)
                    mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (isTimeRun);
        }
    }
    //在主线程里面处理消息并更新UI界面
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd HH:mm:ss", sysTime);
                    Log.i("time", sysTimeStr+"");
                    tvTime.setText(sysTimeStr); //更新时间
                    break;
             default:
                break;

            }
        }
    };


    @OnClick({R.id.scan_iv, R.id.btn_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_iv:
                break;
            case R.id.btn_sure:
                break;
        }
    }
}
