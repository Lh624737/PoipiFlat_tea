package com.pospi.pai.pospiflat.cash;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

//import com.baoyz.swipemenulistview.SwipeMenu;
//import com.baoyz.swipemenulistview.SwipeMenuCreator;
//import com.baoyz.swipemenulistview.SwipeMenuItem;
//import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pax.api.scanner.ScanResult;
import com.pax.api.scanner.ScannerListener;
import com.pax.api.scanner.ScannerManager;
import com.pospi.adapter.AddGoodsAdapter;
import com.pospi.dao.FastDao;
import com.pospi.dao.GoodsDao;
import com.pospi.dao.MemberDao;
import com.pospi.dao.OrderDao;
import com.pospi.dao.PopDao;
import com.pospi.dao.TableDao;
import com.pospi.dto.FastGoods;
import com.pospi.dto.GoodsDto;
import com.pospi.dto.GoodsPop;
import com.pospi.dto.MemberDto;
import com.pospi.dto.ModifiedDto;
import com.pospi.dto.OrderDto;
import com.pospi.dto.PopDto;
import com.pospi.dto.Tabledto;
import com.pospi.http.MaxNO;
import com.pospi.http.Server;
import com.pospi.pai.pospiflat.R;
import com.pospi.pai.pospiflat.base.BaseActivity;
import com.pospi.pai.pospiflat.base.MyGvAdapter;
import com.pospi.pai.pospiflat.more.FastActivity;
import com.pospi.pai.pospiflat.more.LockActivity;
import com.pospi.pai.pospiflat.more.RestOrderActivity;
import com.pospi.pai.pospiflat.more.StatisticsActivity;
import com.pospi.pai.pospiflat.pay.PayActivity;
import com.pospi.pai.pospiflat.pay.ScanPayActivity;
import com.pospi.pai.pospiflat.table.SetTableActivity;
import com.pospi.pai.pospiflat.util.BluetoothHelper;
import com.pospi.pai.pospiflat.util.DiscountDialogActivity;
import com.pospi.util.AidlUtil;
import com.pospi.util.App;
import com.pospi.util.CashierLogin_pareseJson;
import com.pospi.util.CustomDialog;
import com.pospi.util.DoubleSave;
import com.pospi.util.GetData;
import com.pospi.util.Sava_list_To_Json;
import com.pospi.util.SaveMenuInfo;
import com.pospi.util.TurnSize;
import com.pospi.util.constant.URL;
import com.pospi.util.constant.VipMsg;
import com.pospi.view.swipemenulistview.PinnedHeaderListView;
import com.pospi.view.swipemenulistview.SwipeMenu;
import com.pospi.view.swipemenulistview.SwipeMenuCreator;
import com.pospi.view.swipemenulistview.SwipeMenuItem;
import com.pospi.view.swipemenulistview.SwipeMenuListView;

import org.apache.commons.lang.ArrayUtils;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanActivity extends BaseActivity {

    @Bind(R.id.menu)
    ImageView menu;
    @Bind(R.id.lv_scan)
    SwipeMenuListView lv_scan;
    @Bind(R.id.cashier_name)
    TextView cashierName;
    @Bind(R.id.vip_icon)
    LinearLayout vipIcon;
    @Bind(R.id.vip_name)
    TextView vipName;
    @Bind(R.id.tv_num)
    TextView tvNum;
    @Bind(R.id.tv_discount)
    TextView tvDiscount;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.et_led)
    EditText etLed;
    @Bind(R.id.scan_iv)
    ImageView scanIv;
    @Bind(R.id.scan_qc)
    TextView scanQc;
    @Bind(R.id.scan_sure)
    TextView scanSure;
    @Bind(R.id.more_payway)
    Button morePayway;
    @Bind(R.id.pay)
    Button pay;
    @Bind(R.id.scan_title)
    TextView scan_title;
//    @Bind(R.id.table_name)
//    TextView tableName;
//    @Bind(R.id.ll_table)
//    LinearLayout llTable;

    private String CodeNumberShow = "";

    private boolean more_popup_state;
    private GoodsDao goodsDao;

    private boolean sale_state;
    private List<GoodsDto> goodsDtos = new ArrayList<>();//new ArrayList<>();
    private AddGoodsAdapter adapter;

    private AlertDialog dialog;
    private PopupWindow mPopWindow;
    private boolean popup_state;
    private CustomDialog.Builder ibuilder;
    private OrderDto orderDto;
    private ScannerManager sm;

    private int posi;

    private String maxNo;
    private int eatingNumber = 0;
    private String tableNumber = "";
    private String tableNamel = "";
    private PopupWindow mPricePopupWindow;
    private LinearLayout parent;
    private String mac;
    private GoodsDto goodsDto;
    private TextView tvPrice;
    private TextView tvName;
    private TextView tvTime;
    private boolean isTimeRun = true;
    private VipMsg vipMsg =null;
    private List<PopDto> popsOk;
    List<PopDto> zhpops = new ArrayList<>();//组合促销单
    List<PopDto> zpops = new ArrayList<>();//赠品促销单
    List<GoodsDto> zhgoods = new ArrayList<>();//组合促销单
    List<PopDto> tjpops = new ArrayList<>();//特价促销单
    private EditText goodsNum;
    private TextView tv_shop_device;
    private Button bt_qx;
    private Button bt_qr;
    private EditText price_et_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        parent = (LinearLayout) findViewById(R.id.scan_parent);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tv_shop_device = (TextView) findViewById(R.id.tv_shop_device);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        SharedPreferences sp = getSharedPreferences("receipt_num", Context.MODE_PRIVATE);
        mac = sp.getString("mac", "");
        tv_shop_device.setText(getSharedPreferences("shopMsg", Context.MODE_PRIVATE).getString("name", "")+sp.getString("pay_jiju","01"));
        orderDto = (OrderDto) getIntent().getSerializableExtra(RestOrderActivity.REST_ORDER);
//        ReStartUI();
        cashierName.setText(getSharedPreferences("syy", MODE_PRIVATE).getString("countNum","100"));
        init();
        lv_scan.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (!sale_state) {//退款模式
                    Toast.makeText(ScanActivity.this, "开始更改价格", Toast.LENGTH_SHORT).show();

                    View scPy = getLayoutInflater().inflate(R.layout.price_dialog, null);
                    pydialog = new AlertDialog.Builder(ScanActivity.this)
                            .setView(scPy)
                            .create();
                    pydialog.show();
                    bt_qx = (Button) scPy.findViewById(R.id.py_btn_qx);
                    bt_qr = (Button) scPy.findViewById(R.id.py_btn_qr);
                    price_et_scan = (EditText) scPy.findViewById(R.id.price_et_scan);
                    bt_qr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String m = price_et_scan.getText().toString().trim();

                            if (Pattern.matches("^\\d+(\\.\\d+)?", m)&&!m.equals("")) {
                                goodsDtos.get(position).setPrice(Double.parseDouble(m));
                                goodsDtos.get(position).setOldPrice(Double.parseDouble(m));
                                adapter.notifyDataSetChanged();
                                setScreenData();
                                ETRequestFocus();
                                lv_scan.setFocusable(false);
                                pydialog.dismiss();
                            } else {
                                Toast.makeText(ScanActivity.this, "金额不合法", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                    bt_qx.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            pydialog.dismiss();
                        }
                    });

                }
                return true;
            }
        });

        lv_scan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posi = position;
                Intent intent = new Intent(ScanActivity.this, DiscountDialogActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(DiscountDialogActivity.DISCOUNT, goodsDtos.get(position));
                intent.putExtras(bundle);
                intent.putExtra("saleType", sale_state);
                startActivityForResult(intent, 100);
            }
        });
        ETRequestFocus();

        new TimeThread().start(); //启动新的线程

        //取出促销单
        List<PopDto> popDtos = new PopDao(this).getPop();
        Log.i("pop", popDtos.size() + ">>>>>>>>>");
        //获取有效促销单
        popsOk = new ArrayList<>();
        for (PopDto dto : popDtos) {
            Log.i("pop", isTimePop(dto.getStime(), dto.getEtime())+">>>>>>>>>>>");
            if (isTimePop(dto.getStime(), dto.getEtime())) {
                Log.i("pop", dto.getType() + "->>>>>>>-" + dto.getEtime());
                popsOk.add(dto);
            }
        }
        //组合促销
        for (PopDto dto : popsOk) {
            if (dto.getType().equals("611")) {//取出组合促销单
                zhpops.add(dto);
            }
        }
        //赠品促销
        for (PopDto dto : popsOk) {
            if (dto.getType().equals("607")) {//取出组合促销单
                zpops.add(dto);
            }
        }
        //特价促销
        for (PopDto dto : popsOk) {
            if (dto.getType().equals("601")) {//取出特价促销单
                tjpops.add(dto);
            }
        }
        etLed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("test", s.toString()+"---beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("test", s.toString()+">>>>onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("test", s.toString());
                if (s.length() > 5) {
                    if (etLed.getText().length() > 5) {
                        judgeCode(etLed.getText().toString().trim());
                    }


                }
//                judgeCode(etLed.getText().toString().trim());
            }
        });

    }

    //判断是否在促销时间
    public boolean isTimePop(String start, String end) {
        Date today = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Log.i("time", sf.format(today)+"当前日期");
        try {
            Date endDate = sf.parse(end);
            Calendar c= Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.DAY_OF_MONTH,1);//+1天

            Date tomorrow=c.getTime();
            Date startDate = sf.parse(start);
            Calendar c1= Calendar.getInstance();
            c1.setTime(startDate);
            c1.add(Calendar.DAY_OF_MONTH,-1);//-1天

            Date sDate=c1.getTime();

            Log.i("time", sf.format(tomorrow)+"截至日期-------起始日期"+sf.format(sDate));
            if (today.before(tomorrow)&&today.after(sDate)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onResume() {
        ETRequestFocus();
        super.onResume();
    }

    /**
     * 设置EditText获取焦点
     */
    public void ETRequestFocus() {
        etLed.setFocusable(true);
        etLed.setFocusableInTouchMode(true);
        etLed.requestFocus();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ETRequestFocus();
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == 1111) {
                    double num = data.getDoubleExtra("num", 1);
                    double discount = data.getDoubleExtra("discount", 0.0);
                    boolean isZp = data.getBooleanExtra("isZp", false);
                    if (isZp) {//如果手动设置成赠品
                        goodsDtos.get(posi).setIszp("1");
                        goodsDtos.get(posi).setPrice(0);
                        goodsDtos.get(posi).setOldPrice(0);
                        goodsDtos.get(posi).setIszp("1");
                        goodsDtos.get(posi).setPopid("");
                        goodsDtos.get(posi).setPoptype("607");
                        goodsDtos.get(posi).setIspop("1");
                        goodsDtos.get(posi).setSaleType("赠品");
                    }
//                    double discount =0;
                    if (!isZp&&!goodsDtos.get(posi).getSaleType().equals("正常")||!sale_state) {
                        discount = 0;
                        Toast.makeText(this, "商品无法折扣", Toast.LENGTH_SHORT).show();
                    }
                    if (sale_state) {
                        goodsDtos.get(posi).setNum(num);
                    } else {
                        goodsDtos.get(posi).setNum(-num);
                    }

                    Log.i("num", "" + num);
                    Log.i("discount", "" + discount);

                    goodsDtos.get(posi).setDiscount(discount);
                    setScreenData();
                    adapter.notifyDataSetChanged();
                    if (sale_state) {
                        setZppop(goodsDtos.get(posi));
                        setZhpops();
                    }

                } else if (resultCode == 2222) {
                    goodsDtos.remove(posi);
                    adapter.notifyDataSetChanged();
                    setScreenData();
                }
                ETRequestFocus();
                break;
            case PointActivity.TableRequest:
                switch (resultCode) {
                    case 1:
                        eatingNumber = data.getIntExtra(SetTableActivity.EatingNumber, 1);
                        tableNumber = data.getStringExtra(SetTableActivity.TableID);
                        tableNamel = data.getStringExtra(SetTableActivity.TableName);
                        Log.i("tableNamel", tableNamel);
//                        tableName.setText(eatingNumber + "人" + tableNamel);
                        break;
                    case 2:
                        orderDto = (OrderDto) data.getSerializableExtra(SetTableActivity.TableOrder);
                        tableNumber = orderDto.getTableNumber();
                        tableNamel = data.getStringExtra(SetTableActivity.TableName);
                        Log.i("tableNamel", tableNamel);
//                        tableName.setText(eatingNumber + "人" + tableNamel);
                        ReStartUI();
                        break;
                }
            case 123:
                switch (resultCode) {
                    case 0:
                        break;
                    case 1:
                        double discount = 0;
                        double dou = 0;//订单总额
                        String discount_type = data.getStringExtra("discount_type");
                        if (discount_type.equals("折扣")) {
                            double discount_num = data.getDoubleExtra("discount_num", 0) / 100;
                            for (int i = 0; i < goodsDtos.size(); i++) {
                                GoodsDto goodsDto = goodsDtos.get(i);
                                dou = dou + goodsDtos.get(i).getNum() * goodsDtos.get(i).getPrice();
                                if (i == goodsDtos.size() - 1) {
                                    Log.i("discounall", "onActivityResult: " + new BigDecimal(dou * discount_num).setScale(2, RoundingMode.DOWN).doubleValue());
                                    goodsDto.setDiscount(new BigDecimal(dou * discount_num).setScale(2, RoundingMode.DOWN).doubleValue() - discount);
                                } else {
                                    discount += new BigDecimal(goodsDto.getPrice() * goodsDto.getNum() * discount_num).setScale(2, RoundingMode.DOWN).doubleValue();
                                    Log.i("discoun+", "onActivityResult: " + discount);
                                    goodsDto.setDiscount(new BigDecimal(goodsDto.getPrice() * goodsDto.getNum() * discount_num).setScale(2, RoundingMode.DOWN).doubleValue());
                                }
                            }
                        } else {
                            for (int i = 0; i < goodsDtos.size(); i++) {
                                GoodsDto goodsDto = goodsDtos.get(i);
                                dou = dou + goodsDtos.get(i).getNum() * goodsDtos.get(i).getPrice();
                            }
                            double discount_money = data.getDoubleExtra("discount_num", 0);
                            double discount_num = new BigDecimal(discount / dou).setScale(2, RoundingMode.DOWN).doubleValue();
                            for (int i = 0; i < goodsDtos.size(); i++) {
                                GoodsDto goodsDto = goodsDtos.get(i);
                                if (i == goodsDtos.size() - 1) {
                                    Log.i("discounall", "onActivityResult: " + new BigDecimal(dou * discount_num).setScale(2, RoundingMode.DOWN).doubleValue());
                                    goodsDto.setDiscount(DoubleSave.doubleSaveTwo(discount_money - discount));
                                } else {
                                    discount += new BigDecimal(goodsDto.getPrice() * goodsDto.getNum() * discount_num).setScale(2, RoundingMode.DOWN).doubleValue();
                                    Log.i("discoun+", "onActivityResult: " + discount);
                                    goodsDto.setDiscount(new BigDecimal(goodsDto.getPrice() * goodsDto.getNum() * discount_num).setScale(2, RoundingMode.DOWN).doubleValue());
                                }
                            }
                        }
                        setScreenData();
                        adapter.notifyDataSetChanged();
                        break;
                }
                break;
        }

    }

    public void ReStartUI() {
        if (orderDto != null) {
            Log.i("跳转之后", "NO" + orderDto.getMaxNo() + "");
            goodsDtos = Sava_list_To_Json.changeToList(orderDto.getOrder_info());
            setScreenData();
            maxNo = orderDto.getMaxNo();
            orderDto = null;
        } else {
            String goodsMsg = getSharedPreferences("goodsdto_json1", MODE_PRIVATE).getString("goodsMsg", "");
            if (goodsMsg.equals("")) {
                goodsDtos = new ArrayList<>();
            } else {
                goodsDtos =  Sava_list_To_Json.changeToList(goodsMsg);
            }

            maxNo = MaxNO.getMaxNo(getApplicationContext());
        }
        adapter = new AddGoodsAdapter(ScanActivity.this, goodsDtos);
        setScreenData();
        lv_scan.setAdapter(adapter);
        vipName.setText(maxNo);
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
                    tvTime.setText(sysTimeStr); //更新时间
                    break;
                default:
                    break;

            }
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
//        isTimeRun = true;

        ReStartUI();
        initGridview();

    }

    private void initGridview() {
        final GridView gridView = (GridView) findViewById(R.id.fast_gv);
        final List<FastGoods> list = new FastDao(this).select();
        final MyGvAdapter gvAdapter = new MyGvAdapter(this, list);
        gridView.setAdapter(gvAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                judgeCode(list.get(position).getNo());
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(ScanActivity.this).setTitle("提示").setMessage("是否删除该快捷键")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FastGoods fg = list.get(position);
                                boolean isDelet =  new FastDao(ScanActivity.this).delete(fg.getNo());
                                if (isDelet) {
                                    list.remove(position);
                                    gvAdapter.notifyDataSetChanged();
                                }
                            }
                        }).setNegativeButton("取消", null).show();

                return true;
            }
        });
    }


    public void init() {
        goodsDao = new GoodsDao(getApplicationContext());
        sale_state = true;
        etLed.setInputType(InputType.TYPE_NULL);

        int whichOne = getSharedPreferences("islogin", MODE_PRIVATE).getInt("which", 0);
//        String cashier_name = new CashierLogin_pareseJson().parese(
//                getSharedPreferences("cashierMsgDtos", MODE_PRIVATE)
//                        .getString("cashierMsgDtos", ""))
//                .get(whichOne).getName();
//        cashierName.setText(cashier_name);


        setSwipeListView();
    }

    private void setSwipeListView() {
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem();
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xD9, 0x47,
                        0x47)));
                // set item width
                openItem.setWidth(dp2px(100));
                // set item title
                openItem.setTitle("删除");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
            }
        };
        // set creator
        lv_scan.setMenuCreator(creator);

        // step 2. listener item click event
        lv_scan.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                GoodsDto item = goodsDtos.get(position);
//					delete(item);
//                setZppop(position);
                goodsDtos.remove(position);
                setZhpops();
                adapter.notifyDataSetChanged();
                setScreenData();

            }
//            @Override
//            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                GoodsDto item = goodsDtos.get(position);
////					delete(item);
//                setZppop(position);
//                goodsDtos.remove(position);
//                setZhpops();
//                adapter.notifyDataSetChanged();
//                setScreenData();
//                return false;
//
//            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public void numberClick(View view) {
        TextView tv = (TextView) view;
        CodeNumberShow += tv.getText().toString();
        etLed.setText(CodeNumberShow);
    }

    AlertDialog hydialog;
    AlertDialog pydialog;
    EditText etHy;

    @OnClick({R.id.menu, R.id.vip_icon, R.id.scan_iv, R.id.scan_qc, R.id.scan_sure, R.id.more_payway, R.id.pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu:
                if (!popup_state) {
                    showPopupWindow();
                } else {
                    dismissPopupWindow();
                }
                break;
            case R.id.vip_icon:
                View scHy = getLayoutInflater().inflate(R.layout.huiyuan_dialog, null);
                hydialog = new AlertDialog.Builder(this)
                        .setView(scHy)
                        .create();
                hydialog.show();
                etHy = (EditText) scHy.findViewById(R.id.hy_et);
                Button scan = (Button) scHy.findViewById(R.id.hy_btn_scan);
                Button search = (Button) scHy.findViewById(R.id.hy_btn_search);
                MyClickListener listener = new MyClickListener();
                scan.setOnClickListener(listener);
                search.setOnClickListener(listener);
                break;
            case R.id.scan_iv:
                if (Build.MODEL.equalsIgnoreCase(URL.MODEL_D800)) {
                    sm = ScannerManager.getInstance(getApplication());
                    sm.scanOpen();
                    sm.scanStart(new ScannerListener() {
                        @Override
                        public void scanOnComplete() {
                            sm.scanClose();
                        }

                        @Override
                        public void scanOnCancel() {

                        }

                        @Override
                        public void scanOnRead(ScanResult arg0) {
                            if (arg0 != null) {
                                etLed.setText(arg0.getContent());
                                sm.scanClose();
                            }
                        }
                    });
                } else {
                    showToast("暂不支持此功能");
                }
                break;
            case R.id.scan_qc:
                CodeNumberShow = "";
                etLed.setText(CodeNumberShow);
                break;
            case R.id.scan_sure:
                if (CodeNumberShow.length() > 0) {
                    CodeNumberShow = CodeNumberShow.substring(0, CodeNumberShow.length() - 1);
                    etLed.setText(CodeNumberShow);
                }
//                if (!etLed.getText().toString().isEmpty()) {
//                    GoodsDto goodsDto = goodsDao.selectGoods(etLed.getText().toString());
//                    if (goodsDto == null) {
//                        Toast.makeText(getApplicationContext(), "商品不存在", Toast.LENGTH_SHORT).show();
//                    } else {
//                        judgeCode(etLed.getText().toString().trim());
//                    }
//                }
                break;
            case R.id.more_payway:
                if (!more_popup_state) {
                    showMorePopupWindow();
                } else {
                    dismissMorePopupWindow();
                }
                break;
            case R.id.pay:
                if (sale_state) {//销售模式
                    /**
                     * 当点击了收款之后，会把金额给传送到下一个界面
                     */
                    if (!goodsDtos.isEmpty()) {
                        Intent intent = new Intent(ScanActivity.this, PayActivity.class);
                        intent.putExtra("money", tvMoney.getText().toString());
                        Log.i("money", tvMoney.getText().toString());
                        intent.putExtra("orderType", URL.ORDERTYPE_SALE);
                        intent.putExtra("maxNo", maxNo);
                        startActivity(intent);

                        Sava_list_To_Json.changeToJaon(getApplicationContext(), goodsDtos);

//                        goodsDtos.clear();
//                        setScreenData();
//                        maxNo = MaxNO.getPhoneMac()+(Integer.parseInt(maxNo.substring(12)) + 1);
//                        maxNo = MaxNO.getPhoneMac()+(Integer.parseInt(maxNo.substring(maxNo.length()-4,maxNo.length())) + 1);
                    } else {
                        showToast("商品为空");
                    }
                } else {//退款模式
                    //当点击了退款之后，会把金额给传送到下一个界面
                    if (!(goodsDtos.size() == 0)) {
                        Intent intent = new Intent(ScanActivity.this, PayActivity.class);
                        intent.putExtra("money", tvMoney.getText().toString());
                        intent.putExtra("orderType", URL.ORDERTYPE_REFUND);
                        intent.putExtra("maxNo", maxNo);

                        sale_state = true;
                        scan_title.setText("扫码收款");
                        pay.setText("付款");
                        startActivity(intent);
                        Sava_list_To_Json.changeToJaon(getApplicationContext(), goodsDtos);
                        goodsDtos.clear();
                        adapter.notifyDataSetChanged();
                        Sava_list_To_Json.clearGoodsMsg(ScanActivity.this);
                        setScreenData();
//                        maxNo = MaxNO.getPhoneMac()+(Integer.parseInt(maxNo.substring(12)) + 1);
//                        maxNo = MaxNO.getPhoneMac()+(Integer.parseInt(maxNo.substring(maxNo.length()-4,maxNo.length())) + 1);
                    } else {
                        showToast("未选取任何商品！");
                    }
                }
                break;
//            case R.id.ll_table:
//                Intent intent = new Intent(ScanActivity.this, SetTableActivity.class);
//                startActivityForResult(intent, PointActivity.TableRequest);
//                break;

        }
    }

    private void dismissMorePopupWindow() {
        if (morePopWindow != null) {
            morePopWindow.dismiss();
        }
        more_popup_state = false;
    }

    /**
     * 显示更多PopupWindow
     */
    private PopupWindow morePopWindow;

    private void showMorePopupWindow() {
        more_popup_state = true;
        View contentView = LayoutInflater.from(ScanActivity.this).inflate(R.layout.menu_more, null);
        morePopWindow = new PopupWindow(contentView);
        morePopWindow.setWidth(morePayway.getWidth());
        morePopWindow.setHeight(TurnSize.dip2px(getApplicationContext(), 200));

        TextView cancel_order = (TextView) contentView.findViewById(R.id.cancel_order);
        TextView guaDan = (TextView) contentView.findViewById(R.id.guaDan);
        TextView luoDan = (TextView) contentView.findViewById(R.id.luodan);
//        TextView discount = (TextView) contentView.findViewById(R.id.discount);

        MyClickListener listener = new MyClickListener();
        cancel_order.setOnClickListener(listener);
        guaDan.setOnClickListener(listener);
        luoDan.setOnClickListener(listener);
//        discount.setOnClickListener(listener);

        //指定PopupWindow显示在你指定的view下
        int[] location = new int[2];
        morePayway.getLocationOnScreen(location);
        morePopWindow.showAtLocation(morePayway, Gravity.NO_GRAVITY, location[0], location[1] - morePopWindow.getHeight());
    }
    //判断商品
    public void judgeCode(String code) {
        goodsDto = new GoodsDto();

        goodsDto = goodsDao.selectGoods(code);
        if (goodsDto == null) {
//            Toast.makeText(this, "无商品信息", Toast.LENGTH_SHORT).show();
            return;
        }
        goodsDto.setOldPrice(goodsDto.getPrice());//原价


        goodsDto.setSaleType("正常");


        goodsDto.setIszp("0");

        if (sale_state) {//销售操作
            ok:
            for (PopDto pd : tjpops) {
                List<GoodsPop> pop = SaveMenuInfo.changePop(pd.getDetail());
                for (GoodsPop gp : pop) {
                    Log.i("pop", goodsDto.getSid() + "------" + gp.getGoodsid());
                    if (goodsDto.getSid().equals(gp.getGoodsid())) {
                        goodsDto.setPrice(gp.getPopsj());//将商品价格改为促销价
                        goodsDto.setSaleType("促销");
                        goodsDto.setPopid(pd.getId());
                        goodsDto.setIspop("1");
                        goodsDto.setPoptype("601");
                        break ok;
                    }
                }
            }

            if (vipMsg!=null) {
                if (goodsDto.getCostPrice() != 0) {
                    goodsDto.setPrice(goodsDto.getCostPrice());
                    goodsDto.setSaleType("会员");
                }
            }
        }
        Log.i("goods", goodsDto.getUnit());

        if (goodsDto.getUnit().equals("1")) {//计数
//            if (goodsDtos.size() != 0) {
//                for (int i =0 ;i<goodsDtos.size();i++) {
//                    if (goodsDtos.get(i).getCode().equals(code)) {
//                        goodsDtos.get(i).setNum(goodsDtos.get(i).getNum()+1);
//                        break;
//                    }
//                    if (i == goodsDtos.size()-1) {
//                        Log.i("gds", goodsDto.getName()+"------"+ goodsDto.getNum() );
//                        goodsDtos.add(goodsDto);
//                        break;
//                    }
//                }
//
//            } else {
//                if (sale_state) {
//                    goodsDto.setNum(1);
//                } else {
//                    goodsDto.setNum(-1);
//                }
//                goodsDtos.add(goodsDto);
//            }
            showSetPricePopupWindow();
        } else if (goodsDto.getUnit().equals("2")){
//            goodsDto.setNum(1);
            showSetWeightPopupWindow();

        }




    }



    GoodsDto zpGoods=null;//赠品
    //设置赠品促销
    public void setZppop() {
        for (PopDto pd : zpops) {
            List<GoodsPop> gp = SaveMenuInfo.changePop(pd.getDetail());
            if (goodsDto.getSid().equals(gp.get(0).getGoodsid())) {
                if (goodsDto.getNum() < gp.get(0).getPopsl()) {

                    Toast.makeText(this, "未达到满赠要求,该商品数量必须达到" + gp.get(0).getPopsl(), Toast.LENGTH_SHORT).show();
                    return;
                }
                int num = (int) (goodsDto.getNum() / gp.get(0).getPopsl());
                goodsDto.setIspop("1");
                goodsDto.setPopid(pd.getId());
                goodsDto.setPoptype("607");
                zpGoods = new GoodsDao(this).selectGoodsById(gp.get(1).getGoodsid());
                zpGoods.setPrice(0);
                zpGoods.setOldPrice(0);
                zpGoods.setIszp("1");
                zpGoods.setPopid(pd.getId());
                zpGoods.setNum(DoubleSave.doubleSaveTwo(num * gp.get(1).getPopsl()));
                zpGoods.setPoptype("607");
                zpGoods.setIspop("1");
                zpGoods.setSaleType("赠品");
                goodsDtos.add(zpGoods);

                adapter.notifyDataSetChanged();
                setScreenData();
            }

        }
    }
    //设置赠品促销
    public void setZppop(GoodsDto goods) {
        try {
            for (PopDto pd : zpops) {
                List<GoodsPop> gp = SaveMenuInfo.changePop(pd.getDetail());
                if (gp.size() == 0) {
                    Toast.makeText(this, "促销单信息为空,请检查促销单", Toast.LENGTH_SHORT).show();
                    return;
                }
                GoodsPop ys= null;//原始
                GoodsPop zp = null;//赠品
                for (GoodsPop pop : gp) {
                    if (pop.getIszp().equals("1")) {
                        zp = pop;
                    } else {
                        ys = pop;
                    }

                }
                if (ys == zp) {
                    Toast.makeText(this, "促销商品未指定，请检查促销单", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (goods.getSid().equals(ys.getGoodsid())) {
                    if (ys.getPopsl() == 0) {
                        ys.setPopsl(1);
                    }
                    if (goods.getNum() < ys.getPopsl()) {
//                    GoodsDto dto = checkZp(gp.get(1).getGoodsid());
//                    if (dto != null) {
//                        goodsDtos.remove(dto);
//                    }
//                    adapter.notifyDataSetChanged();
//                    setScreenData();
                        Toast.makeText(this, "未达到满赠要求,该商品数量必须达到" + gp.get(0).getPopsl(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int num = (int) (goods.getNum() / ys.getPopsl());
                    Log.i("pop", goods.getNum() + "--" + ys.getPopsl() + "---"+num);
                    goods.setIspop("1");
                    goods.setPopid(pd.getId());
                    goods.setPoptype("607");
                    zpGoods = new GoodsDao(this).selectGoodsById(zp.getGoodsid());
                    zpGoods.setPrice(0);
                    zpGoods.setOldPrice(0);
                    zpGoods.setIszp("1");
                    zpGoods.setPopid(pd.getId());
                    zpGoods.setNum(DoubleSave.doubleSaveTwo(num * zp.getPopsl()));
                    zpGoods.setPoptype("607");
                    zpGoods.setIspop("1");
                    zpGoods.setSaleType("赠品");
//                GoodsDto dtoZp = checkZp(gp.get(1).getGoodsid());

                    if (!checkAndSetZpNum(zpGoods)) {
                        goodsDtos.add(zpGoods);
                    }


                    adapter.notifyDataSetChanged();
                    setScreenData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //设置赠品促销
    public void setZppop(int index) {
        for (PopDto pd : zpops) {
            List<GoodsPop> gp = SaveMenuInfo.changePop(pd.getDetail());
            if (goodsDtos.get(index).getSid().equals(gp.get(0).getGoodsid())) {
               GoodsDto dto = checkZp(gp.get(1).getGoodsid());
                if (dto != null) {
                    goodsDtos.remove(dto);
                }
            }

        }
    }
    //检查是否存在指定赠品
    public boolean checkZp(GoodsDto dto){
        for (GoodsDto gd : goodsDtos) {
            if (dto.getSid().equals(gd.getSid())) {
                gd.setNum(dto.getNum());
                return true;
            }
        }
        return false;
    }
    //检查是否存在指定赠品
    public GoodsDto checkZp(String sid){
        for (GoodsDto gd : goodsDtos) {
            if (sid.equals(gd.getSid())&&"1".equals(gd.getIszp())) {
                return gd;
            }
        }
        return null;
    }

    public void setZhpops(){
        //判断组合促销
        Log.i("pop", "---------------------------------------------------------");
        Log.i("pop", zhpops.size() + "组合单数量");
        for (PopDto pd : zhpops) {
            List<GoodsPop> gp = SaveMenuInfo.changePop(pd.getDetail());
            int temp = 0;
            for (GoodsPop pop : gp) {
                Log.i("pop", pop.getGoodsid()+"----"+pop.getPopsl() + "促销商品");
                for (GoodsDto gd : goodsDtos) {
                    if (pop.getGoodsid().equals(gd.getSid())) {
                       temp =  temp + 1;
                    }
                }
            }
            Log.i("pop", temp + "相同数量的个数");
            if (temp == gp.size()) {

                List<Integer> num = new ArrayList<>();

                for (GoodsPop pop : gp) {
                    if (pop.getPopsl() == 0) {
                        Toast.makeText(this, "商品的促销规格数量未设置，请检查促销单", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (GoodsDto gd : goodsDtos) {
                        if (pop.getGoodsid().equals(gd.getSid())) {
                            if (gd.getNum() < pop.getPopsl()) {
                                Toast.makeText(this, "未满足组合促销,该商品数量必须达到"+pop.getPopsl(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            num.add((int) (gd.getNum() / pop.getPopsl()));
                        }
                    }
                }

                int min = num.get(0);
                for (int i = 0;i<num.size();i++) {
                    if (min>num.get(i)) {
                        min = num.get(i);
                    }
                }


                for (GoodsPop pop : gp) {
                    for (GoodsDto gd : goodsDtos) {
                        if (pop.getGoodsid().equals(gd.getSid())) {
                            double gnum = DoubleSave.doubleSaveTwo(gd.getNum() - min * pop.getPopsl());
                            if (gnum > 0) {
                                GoodsDto dto = new GoodsDto();
                                dto.setPrice(gd.getPrice());
                                dto.setSid(gd.getSid());
                                dto.setOldPrice(gd.getOldPrice());
                                dto.setNum(gnum);
                                dto.setSpecification(gd.getSpecification());
                                dto.setUnit(gd.getUnit());
                                dto.setName(gd.getName());
                                dto.setSaleType("正常");
                                dto.setIszp("0");
                                dto.setIspop("1");
                                dto.setCode(gd.getCode());
                                zhgoods.add(dto);
                            }

                            gd.setNum(DoubleSave.doubleSaveTwo(min * pop.getPopsl()));
                            gd.setPoptype(pd.getType());
                            gd.setPopid(pd.getId());
                            gd.setIspop("1");
                            gd.setPrice(pop.getPopsj());
                            gd.setSaleType("促销");

                        }
                    }
                }
                for (GoodsDto dto : zhgoods) {
                    goodsDtos.add(dto);
                }

                adapter.notifyDataSetChanged();
                setScreenData();
            }
        }
    }


    //设置称重窗口
        EditText good_weight;
        TextView tv_singlePrice;
    private void showSetWeightPopupWindow() {
        BluetoothHelper.connect(this);
        View contentView = LayoutInflater.from(ScanActivity.this).inflate(R.layout.weight_layout, null);
        mPricePopupWindow = new PopupWindow(contentView, 800, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPricePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPricePopupWindow.setFocusable(true);

//        ImageView jian = (ImageView) contentView.findViewById(R.id.jian);
//        ImageView jia = (ImageView) contentView.findViewById(R.id.jia);
//        ImageView clear = (ImageView) contentView.findViewById(R.id.clear);
        TextView sure = (TextView) contentView.findViewById(R.id.scan_sure2);
//        text_num = (TextView) contentView.findViewById(R.id.text_num);
        good_weight = (EditText) contentView.findViewById(R.id.scan_tv_weight);
        tv_singlePrice = (TextView) contentView.findViewById(R.id.scan_tv_weight_price);
        tvName = (TextView) contentView.findViewById(R.id.scan_tv_name);
        tvPrice = (TextView) contentView.findViewById(R.id.scan_tv_price);
        TextView clear_weight = (TextView) contentView.findViewById(R.id.clear_weight);
        tvName.setText(goodsDto.getName());
        tvPrice.setText(goodsDto.getPrice()+"/斤");
//        tv_singlePrice.setText(goodsdto.getPrice()+"元/500g");
//        jian.setOnClickListener(listener);
//        jia.setOnClickListener(listener);
//        clear.setOnClickListener(listener);
        clear_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberAdd = "";
                good_weight.setText("");
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                App.mClient.disconnect(mac);
                App.mClient.unnotify(mac, App.serviceUuid, App.characterUuid, new BleUnnotifyResponse() {
                    @Override
                    public void onResponse(int code) {

                    }
                });
                if (mPricePopupWindow != null) {
                    mPricePopupWindow.dismiss();
                }
//                mPricePopupWindow.dismiss();
                String weight = good_weight.getText().toString().trim();
                if (weight.equals("")) {
                    numberAdd = "";
                    good_weight.setText("");
                    return;
                }
                double gd_price =0;
                if ( Pattern.matches("^\\d+(\\.\\d+)?",weight)) {
                    gd_price = DoubleSave.doubleSaveTwo(Double.parseDouble(weight)*goodsDto.getPrice());
                }  else {
                    Toast.makeText(ScanActivity.this, "不合法重量", Toast.LENGTH_SHORT).show();
                    numberAdd = "";
                    good_weight.setText("");
                    return;
                }

//                Toast.makeText(ScanActivity.this, "称重总价为："+DoubleSave.doubleSaveTwo(gd_price)+"元", Toast.LENGTH_SHORT).show();
                if (sale_state) {
                    goodsDto.setNum(Double.parseDouble(weight));
                } else {
                    goodsDto.setNum(-Double.parseDouble(weight));
                }

                if (!checkGoods(goodsDto)) {
                    goodsDtos.add(goodsDto);
                }



                adapter.notifyDataSetChanged();
                lv_scan.smoothScrollToPosition(goodsDtos.size());
                setScreenData();
                lv_scan.setFocusable(false);
                etLed.setText("");
                CodeNumberShow = "";
                ETRequestFocus();
                numberAdd = "";
                good_weight.setText("");
                if (sale_state) {
//                    GoodsDto goodsBean = checkZp(goodsDto.getSid());
                    setZppop(goodsDto);
                    setZhpops();
                }

            }
        });

        mPricePopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        mPricePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                numberAdd = "";
                etLed.setText("");
                CodeNumberShow = "";
                App.mClient.unnotify(mac, App.serviceUuid, App.characterUuid, new BleUnnotifyResponse() {
                    @Override
                    public void onResponse(int code) {

                    }
                });
            }
        });
        App.mClient.notify(mac, App.serviceUuid, App.characterUuid, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                try {
                    Log.i("onNotify", new String(value));
                    String[] ss = new String(value).split(" ");
                    String data = "";
                    if (ss.length == 4) {
                        data = Integer.parseInt(ss[2]) + "g";
                    } else if (ss.length == 3) {
                        data = ss[2];
                    }
                    Log.i("onNotify", data);
                    String weight = data;
                    int len = data.length();
                    double zl = 0;
                    double price =0;
                    if (Pattern.matches("^\\d+(\\.\\d+)?g", data)) {
                        weight = data.substring(0, len - 1);
                        zl = DoubleSave.doubleSaveThree(Double.parseDouble(weight) / 500);
                        price = DoubleSave.doubleSaveThree(Double.parseDouble(weight) / 500 * goodsDto.getPrice());
                        Log.i("weight", weight + "-----------" + price);
                    } else {
                        String s = data.substring(0, len - 4);//注意kg为单位时，后有两个空格字符
                        Log.i("weight", s+"-----------"+len);
                        price = DoubleSave.doubleSaveThree(Double.parseDouble(s)*2*goodsDto.getPrice());
                        zl = DoubleSave.doubleSaveThree(Double.parseDouble(s)*2);
                    }
                    good_weight.setText(String.valueOf(zl));
                    tv_singlePrice.setText(price+"元");
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onResponse(int code) {
                Log.i("blue", code+"----------");
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isTimeRun = false;
    }
    public void setScreenData() {
        double dou = 0;
        double totalNum = 0;
        double discount = 0.0;
        double oldprice = 0;
        for (int i = 0; i < goodsDtos.size(); i++) {
            dou = dou + goodsDtos.get(i).getNum() * goodsDtos.get(i).getPrice();
            totalNum = totalNum + goodsDtos.get(i).getNum();
            discount += goodsDtos.get(i).getDiscount();
//            Log.i("tvNum1", String.valueOf(totalNum));
            oldprice+=goodsDtos.get(i).getOldPrice()*goodsDtos.get(i).getNum();
        }
        Log.i("tvNum2", String.valueOf(totalNum));
        tvDiscount.setText(DoubleSave.formatDouble(oldprice-dou+discount));
        tvNum.setText(String.valueOf(DoubleSave.doubleSaveThree(totalNum)));
        tvMoney.setText(DoubleSave.formatDouble(dou-discount));
//        tableName.setText(eatingNumber + "人" + tableNamel);
    }

    public void delete() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("退货操作将会清空所选商品，是否要销毁该订单？")
                .setTitle("提示")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goodsDtos.clear();
                        adapter.notifyDataSetChanged();
                        Sava_list_To_Json.clearGoodsMsg(ScanActivity.this);
                        setScreenData();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        dialog.show();
    }

    private void showPopupWindow() {
        popup_state = true;
        View contentView = LayoutInflater.from(ScanActivity.this).inflate(R.layout.menu_fragment, null);
        mPopWindow = new PopupWindow(contentView);
        mPopWindow.setWidth(240);
        mPopWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout l1 = (LinearLayout) contentView.findViewById(R.id.return_good);
        LinearLayout l3 = (LinearLayout) contentView.findViewById(R.id.money_box);
        LinearLayout l5 = (LinearLayout) contentView.findViewById(R.id.screen_lock);
        LinearLayout l7 = (LinearLayout) contentView.findViewById(R.id.back_menu);

        MyClickListener listener = new MyClickListener();
        l1.setOnClickListener(listener);
        l3.setOnClickListener(listener);
        l5.setOnClickListener(listener);
        l7.setOnClickListener(listener);

        mPopWindow.showAsDropDown(menu);
    }

    public void changeState() {
        scan_title.setText("扫码退货");
        pay.setText("退款");
        sale_state = false;
        dialog.dismiss();
    }

    public void dismissPopupWindow() {
        if (mPopWindow != null) {
            mPopWindow.dismiss();
        }
        popup_state = false;
    }

    public void showReturnDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.return_dialog, null);
        dialog = new AlertDialog.Builder(ScanActivity.this).setView(dialogView).create();

        Button have_note = (Button) dialogView.findViewById(R.id.have_note);
        Button not_note = (Button) dialogView.findViewById(R.id.not_note);
        MyClickListener listener = new MyClickListener();
        have_note.setOnClickListener(listener);
        not_note.setOnClickListener(listener);

        int height = URL.getScreemHeight();
        int width = URL.getScreemWidth();
        //设置对话框的高度和宽度
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = width / 5;
        lp.height = height / 5;
        dialog.show();
    }

    private class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.return_good:
                    pay.setText("付款");
                    dismissPopupWindow();
                    showReturnDialog();
                    break;
                case R.id.money_box:
//                    try {
//                        EscCommand esc = new EscCommand();
//                        esc.addGeneratePluseAtRealtime(LabelCommand.FOOT.F2, (byte) 2);//开钱箱
//                        Vector<Byte> datas = esc.getCommand(); // 发送数据
//                        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
//                        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
//                        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);// 最终转换好的数据
//                        App.getmGpService().sendEscCommand(0, sss);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    App.isAidl = true;
                    AidlUtil.getInstance().openCash();
                    dismissPopupWindow();
                    break;
                case R.id.screen_lock:
                    dismissPopupWindow();
                    startActivity(new Intent(ScanActivity.this, LockActivity.class));
                    break;
                case R.id.back_menu:
                    dismissPopupWindow();
                    finish();
                    break;
                case R.id.have_note:
                    dialog.dismiss();
                    Intent intent = new Intent(ScanActivity.this, StatisticsActivity.class);
                    intent.putExtra(StatisticsActivity.REFUNDS_NAME, StatisticsActivity.REFUNDS_IN);
                    startActivity(intent);
                    break;
                case R.id.not_note:
                    if (goodsDtos.size() > 0) {
                        delete();
                        changeState();
                    } else {
                        changeState();
                    }
                    break;
                case R.id.cancel_order:
                    delete();
                    dismissMorePopupWindow();
                    break;
                case R.id.guaDan:
                    restOrder();
                    dismissMorePopupWindow();
                    break;
//                case R.id.discount:
//                    if (goodsDtos.size() > 0) {
//                        Intent intent1 = new Intent(ScanActivity.this, OrderDiscountActivity.class);
//                        intent1.putExtra("money", Double.parseDouble(tvMoney.getText().toString()));
//                        startActivityForResult(intent1, 123);
//                    }
//                    morePopWindow.dismiss();
//                    more_popup_state = false;
//                    break;
                case R.id.hy_btn_scan:
                    if (Build.MODEL.equalsIgnoreCase(URL.MODEL_D800)) {
                        sm = ScannerManager.getInstance(getApplication());
                        sm.scanOpen();
                        sm.scanStart(new ScannerListener() {
                            @Override
                            public void scanOnComplete() {
                                sm.scanClose();
                            }

                            @Override
                            public void scanOnCancel() {

                            }

                            @Override
                            public void scanOnRead(ScanResult arg0) {
                                if (arg0 != null) {
                                    MemberDto memberDto;
                                    if (arg0.getContent().trim().length() == 11) {
                                        memberDto = new MemberDao(getApplicationContext()).findMemberByTel(arg0.getContent().trim());
                                    } else {
                                        memberDto = new MemberDao(getApplicationContext()).findMemberByNumber(arg0.getContent().trim());
                                    }
                                    if (memberDto == null) {
                                        showToast("未添加该会员");
                                        hydialog.dismiss();
                                        vipName.setText("");
                                    } else {
                                        vipName.setText(memberDto.getName());
                                        hydialog.dismiss();
                                    }
                                    sm.scanClose();
                                }
                            }
                        });
                    } else {
                        showToast("暂不支持此功能");
                    }
                    break;
                case R.id.hy_btn_search:
                    if (!(etHy.getText().toString().isEmpty())) {
                        findVip();
//                        MemberDto memberDto;
//                        if (etHy.getText().toString().trim().length() == 11) {
//                            memberDto = new MemberDao(getApplicationContext()).findMemberByTel(etHy.getText().toString().trim());
//                        } else {
//                            memberDto = new MemberDao(getApplicationContext()).findMemberByNumber(etHy.getText().toString().trim());
//                        }
//                        if (memberDto == null) {
//                            showToast("未添加该会员");
//                            vipName.setText("");
//                            hydialog.dismiss();
//                        } else {
//
//                            vipName.setText(memberDto.getName());
//                            hydialog.dismiss();
//                        }
                    }
                    break;
                case R.id.luodan:
//                    LuoDanOrder();
                    startActivity(FastActivity.class);
                    dismissMorePopupWindow();
                    break;
            }
        }
    }
    //vip
    private void findVip() {
        final RequestParams params = new RequestParams();//实例化后存入键值对
        new Server().getConnect(getApplicationContext(), new URL().SYNC_MENUS, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int code, Header[] headers, byte[] bytes) {
                        if (code == 200) {
                            Log.i("goodsDtosInfo", new String(bytes) + "");
                            try {
                                JSONObject object = new JSONObject(new String(bytes));
                                String co = object.getString("code");
                                if (co.equals(200)) {
                                    JSONObject data = object.getJSONObject("data");
                                    vipMsg = new VipMsg();
                                    vipMsg.setVipNum(data.getString("num"));
                                    vipMsg.setVipName(data.getString("name"));
                                    vipMsg.setVipJf(data.getString("jf"));
                                    String vipNum =  vipMsg.getVipNum();
                                    SharedPreferences.Editor sp = getSharedPreferences("vipMsg", MODE_PRIVATE).edit();
                                    sp.putString("vipId", vipNum);
                                    sp.commit();
                                }
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
        if (vipMsg != null) {
            vipName.setText(vipMsg.getVipJf()+"分");
        }

    }

    private void LuoDanOrder() {
        if (goodsDtos.size() > 0) {
            String shopId = getSharedPreferences("StoreMessage", MODE_PRIVATE).getString("Id", "");
            OrderDto orderDto = new OrderDto();
            orderDto.setOrder_info(Sava_list_To_Json.changeGoodDtoToJaon(goodsDtos));//存储的该订单的商品的信息
            orderDto.setPayway(null);
            orderDto.setTime(GetData.getYYMMDDTime());
            orderDto.setSs_money("0.00");
            orderDto.setYs_money(tvMoney.getText().toString());
            orderDto.setZl_money("0.00");
            orderDto.setShop_id(shopId);
            orderDto.setOrderType(URL.ORDERTYPE_SALE);
            orderDto.setMaxNo(maxNo);
            orderDto.setCheckoutTime(GetData.getDataTime());
            orderDto.setDetailTime(GetData.getHHmmssTimet());
            orderDto.setHasReturnGoods(URL.hasReturnGoods_No);
            orderDto.setOut_trade_no(null);
            //当挂单的时候该数据用于保存是哪个界面进行的挂单操作
            orderDto.setPayReturn(RestOrderActivity.REST_Point);
            orderDto.setIfFinishOrder(URL.ZHUOTai);

            if (tableNumber.trim().isEmpty()) {

                List<Tabledto> FreeTable = new TableDao(getApplicationContext()).findFreeTableInfo();
                if (FreeTable.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "没有空闲餐桌", Toast.LENGTH_SHORT).show();
                } else {
                    orderDto.setTableNumber(FreeTable.get(0).getSid());
                    //给数据库里面添加一条记录
                    new OrderDao(getApplicationContext()).addOrder(orderDto);
                    goodsDtos.clear();
                    adapter.notifyDataSetChanged();
                    setScreenData();
                    Toast.makeText(ScanActivity.this, " 落单成功！", Toast.LENGTH_SHORT).show();
//                    maxNo = MaxNO.getPhoneMac()+(Integer.parseInt(maxNo.substring(12)) + 1);
                    maxNo = MaxNO.getMaxNo(this);
                }
            } else {
                orderDto.setTableNumber(tableNumber);
                //给数据库里面添加一条记录
                new OrderDao(getApplicationContext()).addOrder(orderDto);
                goodsDtos.clear();
                eatingNumber = 0;
                tableNamel = "";
                adapter.notifyDataSetChanged();
                setScreenData();
                Toast.makeText(ScanActivity.this, " 落单成功！", Toast.LENGTH_SHORT).show();
//                maxNo = MaxNO.getPhoneMac()+(Integer.parseInt(maxNo.substring(12)) + 1);
                maxNo = MaxNO.getMaxNo(this);
            }
        } else {
            Toast.makeText(ScanActivity.this, " 没有可落单的订单！", Toast.LENGTH_SHORT).show();
        }
    }

    private void restOrder() {
        if (goodsDtos.size() > 0) {
            final AlertDialog deleteDialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("确定挂单？")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addRestOrderinfo2();
//                            maxNo = MaxNO.getPhoneMac()+(Integer.parseInt(maxNo.substring(12)) + 1);
                            maxNo = MaxNO.getMaxNo(ScanActivity.this);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
            deleteDialog.show();
        } else {
            Toast.makeText(ScanActivity.this, " 没有可挂单的订单！", Toast.LENGTH_SHORT).show();
        }
    }
    public void addRestOrderinfo2() {
        String shopId = getSharedPreferences("userMsg", MODE_PRIVATE).getString("uid", "");
        OrderDto orderDto = new OrderDto();
        orderDto.setOrder_info(Sava_list_To_Json.changeGoodDtoToJaon(goodsDtos));//存储的该订单的商品的信息
        orderDto.setPayway(null);
        orderDto.setTime(GetData.getYYMMDDTime());
        orderDto.setSs_money("0.00");
        orderDto.setYs_money(tvMoney.getText().toString());
        orderDto.setZl_money("0.00");
        orderDto.setShop_id(shopId);
        orderDto.setOrderType(URL.ORDERTYPE_SALE);
        orderDto.setMaxNo(maxNo);
        orderDto.setCheckoutTime(GetData.getDataTime());
        orderDto.setDetailTime(GetData.getHHmmssTimet());
        orderDto.setHasReturnGoods(URL.hasReturnGoods_No);
        orderDto.setOut_trade_no(null);
        //当挂单的时候该数据用于保存是哪个界面进行的挂单操作
        orderDto.setPayReturn(RestOrderActivity.REST_SCAN);
        orderDto.setIfFinishOrder(URL.BAOLIU);
        //给数据库里面添加一条记录
        new OrderDao(getApplicationContext()).addOrder(orderDto);
        goodsDtos.clear();
        adapter.notifyDataSetChanged();
        setScreenData();
        Toast.makeText(ScanActivity.this, " 挂单成功！", Toast.LENGTH_SHORT).show();
        maxNo = MaxNO.getMaxNo(this);
        vipName.setText(maxNo);
        Sava_list_To_Json.clearGoodsMsg(this);

    }

    public void addRestOrderinfo() {
        String shopId = getSharedPreferences("StoreMessage", MODE_PRIVATE).getString("Id", "");
        OrderDto orderDto = new OrderDto();
        orderDto.setOrder_info(Sava_list_To_Json.changeGoodDtoToJaon(goodsDtos));//存储的该订单的商品的信息
        orderDto.setPayway(null);
        orderDto.setTime(GetData.getYYMMDDTime());
        orderDto.setSs_money("0.00");
        orderDto.setYs_money(tvMoney.getText().toString());
        orderDto.setZl_money("0.00");
        orderDto.setShop_id(shopId);
        orderDto.setOrderType(URL.ORDERTYPE_SALE);
        orderDto.setMaxNo(maxNo);
        orderDto.setCheckoutTime(GetData.getDataTime());
        orderDto.setDetailTime(GetData.getHHmmssTimet());
        orderDto.setHasReturnGoods(URL.hasReturnGoods_No);
        orderDto.setOut_trade_no(null);
        //当挂单的时候该数据用于保存是哪个界面进行的挂单操作
        orderDto.setPayReturn(RestOrderActivity.REST_SCAN);
        orderDto.setIfFinishOrder(URL.BAOLIU);
        /**
         * 给数据库里面添加一条记录
         */
        new OrderDao(getApplicationContext()).addOrder(orderDto);
        goodsDtos.clear();
        adapter.notifyDataSetChanged();
        setScreenData();
        Toast.makeText(ScanActivity.this, " 挂单成功！", Toast.LENGTH_SHORT).show();
    }


    private void showSetPricePopupWindow() {

        popup_state = true;
        View contentView = LayoutInflater.from(ScanActivity.this).inflate(R.layout.set_price_popup, null);
        mPricePopupWindow = new PopupWindow(contentView, 500, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPricePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPricePopupWindow.setFocusable(true);

        ImageView clear = (ImageView) contentView.findViewById(R.id.clear);
        TextView sure = (TextView) contentView.findViewById(R.id.sure1);
        TextView goodsName = (TextView) contentView.findViewById(R.id.tv_goodsName);
        goodsName.setText(goodsDto.getName()+"  单位"+"("+goodsDto.getSpecification()+")");
//        text_num = (TextView) contentView.findViewById(R.id.text_num);
        goodsNum = (EditText) contentView.findViewById(R.id.good_price);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberAdd = "";
                goodsNum.setText("");

            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (mPricePopupWindow != null) {
                        mPricePopupWindow.dismiss();
                    }
                    if (!numberAdd.equals("")) {
                        if (!sale_state) {//退货状态
                            goodsDto.setNum(-Double.parseDouble(numberAdd));
                        } else {
                            goodsDto.setNum(Double.parseDouble(numberAdd));
                        }
                        if (!checkGoods(goodsDto)) {
                            goodsDtos.add(goodsDto);
                        }


                        if (sale_state) {
//                            GoodsDto goodsBean = checkZp(goodsDto.getSid());
                            setZppop(goodsDto);
                            setZhpops();
                        }

                        adapter.notifyDataSetChanged();
                        lv_scan.smoothScrollToPosition(goodsDtos.size());
                        setScreenData();
                        ETRequestFocus();
                        lv_scan.setFocusable(false);
                        etLed.setText("");
                        CodeNumberShow = "";

                        numberAdd = "";
                    }
            }
        });

        mPricePopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);

    }

    /**
     * 设置价格窗口的数字键点击事件
     */
    private String numberAdd = "";

    public void popupWindowNumberClick(View view) {
        TextView tv = (TextView) view;
        if (numberAdd.isEmpty()) {
            if (view.getId() != R.id.dian) {
                numberAdd += tv.getText().toString();
            }
        } else {
            if (numberAdd.equals("0") && view.getId() == R.id.dian) {
                numberAdd = tv.getText().toString();
            } else {
                numberAdd += tv.getText().toString();
            }
        }
        goodsNum.setText(numberAdd);

    }
    public void popupWindowNumberClick2(View view) {
        TextView tv = (TextView) view;
        if (numberAdd.isEmpty()) {
            if (view.getId() != R.id.dian) {
                numberAdd += tv.getText().toString();
            }
        } else {
            if (numberAdd.equals("0") && view.getId() == R.id.dian) {
                numberAdd = tv.getText().toString();
            } else {
                numberAdd += tv.getText().toString();
            }
        }
        good_weight.setText(numberAdd);
    }
    //检测当前购物车是否存在该商品
    public boolean checkGoods(GoodsDto gd){
        for (GoodsDto dto : goodsDtos) {
            if (gd.getCode().equals(dto.getCode())) {
                dto.setNum(DoubleSave.doubleSaveThree(gd.getNum() + dto.getNum()));
                return true;
            }
        }
        return false;
    }
    //检测当前购物车是否存在该赠品
    public boolean checkAndSetZpNum(GoodsDto gd){
        for (GoodsDto dto : goodsDtos) {
            if (gd.getCode().equals(dto.getCode())&&"1".equals(dto.getIszp())) {
                dto.setNum(DoubleSave.doubleSaveThree(gd.getNum()));
                return true;
            }
        }
        return false;
    }


}
