package com.pospi.pai.pospiflat.cash;

import android.app.ActivityManager;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.pax.api.scanner.ScanResult;
import com.pax.api.scanner.ScannerListener;
import com.pax.api.scanner.ScannerManager;
import com.pospi.dao.GoodsDao;
import com.pospi.dao.MemberDao;
import com.pospi.dao.ModifiedGroupDao;
import com.pospi.dao.OrderDao;
import com.pospi.dao.PopDao;
import com.pospi.dao.TableDao;
import com.pospi.dto.GoodsDto;
import com.pospi.dto.GoodsPop;
import com.pospi.dto.MemberDto;
import com.pospi.dto.MenuDto;
import com.pospi.dto.ModifiedDto;
import com.pospi.dto.OrderDto;
import com.pospi.dto.PopDto;
import com.pospi.dto.Tabledto;
import com.pospi.fragment.BlankFragment;
import com.pospi.http.MaxNO;
import com.pospi.pai.pospiflat.R;
import com.pospi.pai.pospiflat.more.LockActivity;
import com.pospi.pai.pospiflat.more.RestOrderActivity;
import com.pospi.pai.pospiflat.more.StatisticsActivity;
import com.pospi.pai.pospiflat.pay.PayActivity;
import com.pospi.pai.pospiflat.table.SetTableActivity;
import com.pospi.pai.pospiflat.util.BluetoothHelper;
import com.pospi.pai.pospiflat.util.DiscountDialogActivity;
import com.pospi.pai.pospiflat.util.ModifiedDialogActivity;
import com.pospi.util.App;
import com.pospi.util.CashierLogin_pareseJson;
import com.pospi.util.DoubleSave;
import com.pospi.util.GetData;
import com.pospi.util.Sava_list_To_Json;
import com.pospi.util.SaveMenuInfo;
import com.pospi.util.TurnSize;
import com.pospi.util.ViewFindUtils;
import com.pospi.util.constant.URL;
import com.pospi.view.swipemenulistview.SwipeMenu;
import com.pospi.view.swipemenulistview.SwipeMenuCreator;
import com.pospi.view.swipemenulistview.SwipeMenuItem;
import com.pospi.view.swipemenulistview.SwipeMenuListView;

import org.apache.commons.lang.ArrayUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * 点选收款
 */

public class PointActivity extends FragmentActivity implements BlankFragment.OnGridViewClick {

    @Bind(R.id.cashier_name)
    TextView cashierName;
    @Bind(R.id.vip_icon)
    ImageView vipIcon;
    @Bind(R.id.vip_name)
    TextView vipName;
    @Bind(R.id.menu)
    ImageView menu;
    @Bind(R.id.goods_lv)
    SwipeMenuListView goodsLv;
    @Bind(R.id.more_payway)
    Button morePayway;
    @Bind(R.id.pay)
    Button pay;
    @Bind(R.id.tv_num)
    TextView tvNum;
    @Bind(R.id.tv_discount)
    TextView tvDiscount;
    @Bind(R.id.tv_money)
    TextView tvMoney;
//    @Bind(R.id.table_name)
//    TextView tableName;
//    @Bind(R.id.ll_table)
//    LinearLayout llTable;
    @Bind(R.id.parent)
    LinearLayout parent;
    @Bind(R.id.text_upload_hint)
    TextView textUploadHint;
    @Bind(R.id.ll_good_title)
    LinearLayout llGoodTitle;

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private List<String> titles = null;
    private List<String> code = null;
    private List<MenuDto> menuDtos = null;
    private List<GoodsDto> lv_goodsBeens = new ArrayList<>();

    private PopupWindow mPopWindow, morePopWindow, mPricePopupWindow;
    private AlertDialog dialog, hydialog;
    private GoodsDao dao;
    private OrderDto orderDto;
    private ScannerManager sm;
    private MyLvAdapter adapter;
    private MyClickListener listener;

    private EditText etHy;

    private double total_money;
    private boolean more_popup_state = false;
    private boolean popup_state, repeat;
    private boolean sale_state = true;
    private String maxNo;
    private int posi;
    public static final int REQUESTCODE = 101;
    public static final int TableRequest = 22;
    private int eatingNumber = 0;
    private String tableNumber = "";
    private String tableNamel;
    private int good_num = 1;
    private GoodsDto goodsdto;
    private ModifiedGroupDao modifiedGroupDao;
    private EditText good_weight;
    private EditText goodsNum;
    private String mac;
    private TextView tv_singlePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.point_select);
            ButterKnife.bind(this);
            SharedPreferences sp = getSharedPreferences("receipt_num", Context.MODE_PRIVATE);
            mac = sp.getString("mac", "");
            getData();
            sale_state = true;
            listener = new MyClickListener();
            orderDto = (OrderDto) getIntent().getSerializableExtra(RestOrderActivity.REST_ORDER);

            goodsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    posi = position;
                    Intent intent = new Intent(PointActivity.this, DiscountDialogActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(DiscountDialogActivity.DISCOUNT, lv_goodsBeens.get(position));
                    intent.putExtras(bundle);
                    startActivityForResult(intent, DiscountDialogActivity.DISCOUNTDIALOGACTIVITY_REQUEST);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (App.isHasNoUpLoad()) {
            textUploadHint.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        reStartUI();
    }

    public void reStartUI() {
        if (orderDto != null) {
            maxNo = orderDto.getMaxNo();
//            Log.i("跳转之后", "NO" + orderDto.getMaxNo() + "");
            lv_goodsBeens = Sava_list_To_Json.changeToList(orderDto.getOrder_info());
            setScreenData();
            orderDto = null;
        } else {
            maxNo = MaxNO.getMaxNo(getApplicationContext());
            lv_goodsBeens = new ArrayList<>();
        }
        adapter = new MyLvAdapter(this, lv_goodsBeens);
        goodsLv.setAdapter(adapter);
    }

    @OnClick({R.id.menu, R.id.more_payway, R.id.pay, R.id.vip_icon,  R.id.text_upload_hint})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_upload_hint:
                Intent intent2 = new Intent(PointActivity.this, StatisticsActivity.class);
                intent2.putExtra(StatisticsActivity.REFUNDS_NAME, StatisticsActivity.REFUNDS_IN);
                startActivity(intent2);
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
            scan.setOnClickListener(listener);
            search.setOnClickListener(listener);
                break;
            case R.id.menu:
                if (!popup_state) {
                    showPopupWindow();
                } else {
                    dismissPopupWindow();
                }
                break;
            case R.id.more_payway:
//                Log.i("more_popup_state", "onClick: " + more_popup_state);
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
                    if (!lv_goodsBeens.isEmpty()) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!tableNumber.trim().isEmpty()) {
                                    try {
                                        new TableDao(getApplicationContext()).deleteTable(orderDto.getTableNumber());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();

                        Intent intent = new Intent(PointActivity.this, PayActivity.class);
                        intent.putExtra("money", tvMoney.getText().toString());
                        intent.putExtra("orderType", URL.ORDERTYPE_SALE);
                        intent.putExtra("maxNo", maxNo);
                        startActivity(intent);

                        Sava_list_To_Json.changeToJaon(getApplicationContext(), lv_goodsBeens);
                        textviewClear();
//                        maxNo = ""+(Integer.parseInt(maxNo) + 1);
                    } else {
                        Toast.makeText(PointActivity.this, "未点选任何商品！", Toast.LENGTH_SHORT).show();
                    }
                } else {//退款模式
                    //当点击了退款之后，会把金额给传送到下一个界面
                    if (!(lv_goodsBeens.isEmpty())) {
                        Intent intent = new Intent(PointActivity.this, PayActivity.class);
                        intent.putExtra("money", tvMoney.getText().toString());
                        intent.putExtra("orderType", URL.ORDERTYPE_REFUND);
                        intent.putExtra("maxNo", MaxNO.getMaxNo(getApplicationContext()));
                        startActivity(intent);

                        Sava_list_To_Json.changeToJaon(getApplicationContext(), lv_goodsBeens);
                        textviewClear();
//                        maxNo = ""+(Integer.parseInt(maxNo) + 1);

                        sale_state = true;
                        pay.setText("付款");
//                        Log.i("pay.setText", "onClick: ");
                    } else {
                        Toast.makeText(PointActivity.this, "未点选任何商品！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
//            case R.id.ll_table:
//
////                Intent intent = new Intent(PointActivity.this, SetTableActivity.class);
////                startActivityForResult(intent, TableRequest);
//                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                Bundle b = data.getExtras(); //data为B中回传的Intent
//                String str = b.getString("str1");//str即为回传的值
                break;
            case ModifiedDialogActivity.REQUESTCODE:
                switch (resultCode) {
                    case ModifiedDialogActivity.RESULTCODE_CANCLE:
                        break;
                    case ModifiedDialogActivity.RESULTCODE_SURE:
                        Bundle bundle = data.getExtras(); //data为B中回传的Intent
                        List<ModifiedDto> modifiedDtos = (List<ModifiedDto>) bundle.getSerializable("modifieds");
                        goodsdto.setModified(new Gson().toJson(modifiedDtos));
                        if (goodsdto.getPrice() > 0) {
                            if (sale_state) {
                                for (int i = 0; i < lv_goodsBeens.size(); i++) {
                                    if (lv_goodsBeens.get(i).getName().equals(goodsdto.getName())) {
                                        lv_goodsBeens.get(i).setNum(lv_goodsBeens.get(i).getNum() + 1);
                                        repeat = false;
                                    }
                                }
                                if (repeat) {
                                    goodsdto.setNum(1);
                                    lv_goodsBeens.add(goodsdto);
                                }
                            } else {
                                for (int i = 0; i < lv_goodsBeens.size(); i++) {
                                    if (lv_goodsBeens.get(i).getName().equals(goodsdto.getName())) {
                                        lv_goodsBeens.get(i).setNum(lv_goodsBeens.get(i).getNum() - 1);
                                        repeat = false;
                                    }
                                }
                                if (repeat) {
                                    goodsdto.setNum(-1);
                                    lv_goodsBeens.add(goodsdto);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            setScreenData();
                            break;
                        }
                        break;

                }
            case DiscountDialogActivity.DISCOUNTDIALOGACTIVITY_REQUEST://点击进行折扣的返回值
                Log.i("onActivityResult", "onActivityResult: " + "收到返回");
                switch (resultCode) {
                    case DiscountDialogActivity.DISCOUNTDIALOGACTIVITY_RESULT_OK:
                        double num = data.getDoubleExtra("num", 1);
                        double discount = data.getDoubleExtra("discount", 0.0);
                        Log.i("result", "数量: " + num + "折让：" + discount);
                        lv_goodsBeens.get(posi).setNum(num);
                        lv_goodsBeens.get(posi).setDiscount(DoubleSave.doubleSaveTwo(discount));
                        setScreenData();
                        adapter.notifyDataSetChanged();
                        break;

                    case DiscountDialogActivity.DISCOUNTDIALOGACTIVITY_RESULT_CANCEL:

                        lv_goodsBeens.remove(posi);
                        adapter.notifyDataSetChanged();
                        setScreenData();
                        break;
                }
                break;
            case TableRequest:
                switch (resultCode) {
                    case 1:
                        eatingNumber = data.getIntExtra(SetTableActivity.EatingNumber, 1);
                        tableNumber = data.getStringExtra(SetTableActivity.TableID);
                        tableNamel = data.getStringExtra(SetTableActivity.TableName);
//                        Log.i("tableNamel", tableNamel);
//                        tableName.setText(eatingNumber + "人" + tableNamel);
                        break;
                    case 2:
                        orderDto = (OrderDto) data.getSerializableExtra(SetTableActivity.TableOrder);
                        tableNumber = orderDto.getTableNumber();
                        tableNamel = data.getStringExtra(SetTableActivity.TableName);
//                        Log.i("tableNamel", tableNamel);
//                        tableName.setText(eatingNumber + "人" + tableNamel);
                        reStartUI();
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
                            for (int i = 0; i < lv_goodsBeens.size(); i++) {
                                GoodsDto goodsDto = lv_goodsBeens.get(i);
                                dou = dou + lv_goodsBeens.get(i).getNum() * lv_goodsBeens.get(i).getPrice();
                                if (i == lv_goodsBeens.size() - 1) {
//                                    Log.i("discounall", "onActivityResult: " + new BigDecimal(dou * discount_num).setScale(2, RoundingMode.DOWN).doubleValue());
                                    goodsDto.setDiscount(new BigDecimal(dou * discount_num).setScale(2, RoundingMode.DOWN).doubleValue() - discount);
                                } else {
                                    discount += new BigDecimal(goodsDto.getPrice() * goodsDto.getNum() * discount_num).setScale(2, RoundingMode.DOWN).doubleValue();
//                                    Log.i("discoun+", "onActivityResult: " + discount);
                                    goodsDto.setDiscount(new BigDecimal(goodsDto.getPrice() * goodsDto.getNum() * discount_num).setScale(2, RoundingMode.DOWN).doubleValue());
                                }
                            }
                        } else {
                            for (int i = 0; i < lv_goodsBeens.size(); i++) {
                                dou = dou + lv_goodsBeens.get(i).getNum() * lv_goodsBeens.get(i).getPrice();
                            }
                            double discount_money = data.getDoubleExtra("discount_num", 0);
                            double discount_num = new BigDecimal(discount_money / dou).setScale(2, RoundingMode.DOWN).doubleValue();
                            for (int i = 0; i < lv_goodsBeens.size(); i++) {
                                GoodsDto goodsDto = lv_goodsBeens.get(i);
                                if (i == lv_goodsBeens.size() - 1) {
//                                    Log.i("discounall", "onActivityResult: " + new BigDecimal(dou * discount_num).setScale(2, RoundingMode.DOWN).doubleValue());
                                    goodsDto.setDiscount(DoubleSave.doubleSaveTwo(discount_money - discount));
                                } else {
                                    discount += new BigDecimal(goodsDto.getPrice() * goodsDto.getNum() * discount_num).setScale(2, RoundingMode.DOWN).doubleValue();
//                                    Log.i("discoun+", "onActivityResult: " + discount);
                                    goodsDto.setDiscount(new BigDecimal(goodsDto.getPrice() * goodsDto.getNum() * discount_num).setScale(2, RoundingMode.DOWN).doubleValue());
                                }
                            }
                        }
                        setScreenData();
                        adapter.notifyDataSetChanged();
                        break;
                }
                break;
            default:
                break;
        }
    }

    /**
     * 隐藏更多PopupWindow
     */
    private void dismissMorePopupWindow() {
        if (morePopWindow != null) {
            morePopWindow.dismiss();
        }
        more_popup_state = false;
    }

    /**
     * 显示更多PopupWindow
     */
    private void showMorePopupWindow() {
        more_popup_state = true;
        View contentView = LayoutInflater.from(PointActivity.this).inflate(R.layout.menu_more, null);
        morePopWindow = new PopupWindow(contentView);
        morePopWindow = new PopupWindow(contentView, morePayway.getWidth(), TurnSize.dip2px(getApplicationContext(), 200));

        TextView cancel_order = (TextView) contentView.findViewById(R.id.cancel_order);
        TextView guaDan = (TextView) contentView.findViewById(R.id.guaDan);
        TextView luoDan = (TextView) contentView.findViewById(R.id.luodan);
//        TextView discount = (TextView) contentView.findViewById(R.id.discount);

        cancel_order.setOnClickListener(listener);
        guaDan.setOnClickListener(listener);
        luoDan.setOnClickListener(listener);
//        discount.setOnClickListener(listener);

        //指定PopupWindow显示在你指定的view下
        int[] location = new int[2];
        morePayway.getLocationOnScreen(location);
        morePopWindow.showAtLocation(morePayway, Gravity.NO_GRAVITY, location[0], location[1] - morePopWindow.getHeight());
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

    public void getData() {
        if (App.isHasNoUpLoad()) {
            textUploadHint.setVisibility(View.VISIBLE);
        }

        String menuInfo = getSharedPreferences("MenuDto_json", MODE_PRIVATE).getString("json", "");
        menuDtos = SaveMenuInfo.changeToList(menuInfo);
        titles = new ArrayList<>();
        code = new ArrayList<>();
        for (int i = 0; i < menuDtos.size(); i++) {
            titles.add(menuDtos.get(i).getName());
            code.add(menuDtos.get(i).getSid());
        }
//        for (int i = 0; i < 1; i++) {
//            titles.add("茶叶");
//            code.add("24wswddew31ewe213ewew");
//        }
        init();
    }

    public boolean isTimePop(String start, String end) {
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = sf.parse(start);
            Date d2 = sf.parse(end);
            if (date.before(d2)&&date.after(d1)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 绑定Tab和ViewPager，并将获取到的商品数据传进去
     */
    private void init() {
        try {
            modifiedGroupDao = new ModifiedGroupDao(this);
            dao = new GoodsDao(this);
            /**
             * GridView的数据
             */
            String[] mTitles = new String[titles.size()];
            List<GoodsDto> goodsBeens = new ArrayList<>();

            //获取所有促销单
            List<PopDto> popDtos = new PopDao(this).getPop();

            goodsBeens.clear();
            for (int i = 0; i < titles.size(); i++) {
                mTitles[i] = titles.get(i);
                goodsBeens = dao.findSelectPointGoods(code.get(i));

                //设置促销,遍历所有促销单，根据有效促销单设置促销价格
//                for (PopDto popDto : popDtos) {
//                    if (isTimePop(popDto.getStime(), popDto.getEtime())) {
//                        List<GoodsPop> gp = SaveMenuInfo.changePop(popDto.getDetail());
//                        for (GoodsPop pop : gp) {
//                            for (GoodsDto dto : goodsBeens) {
//                                if (dto.getSid().equals(pop.getGoodsid())) {
//                                    dto.setPopPrice(pop.getPopsj());
//                                }
//                            }
//                        }
//                    }
//                }


//                goodsBeens = dao.findSelectPointGoods();
                mFragments.add(new BlankFragment(this, goodsBeens));
            }
            View decorView = getWindow().getDecorView();
            ViewPager vp = ViewFindUtils.find(decorView, R.id.vp);

            MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager());
            vp.setAdapter(mAdapter);

            SlidingTabLayout tabLayout_8 = ViewFindUtils.find(decorView, R.id.tl_8);
//            tabLayout_8.setse
            if (mTitles.length < 6) {
                tabLayout_8.setTabSpaceEqual(true);
            }
            try {
                tabLayout_8.setViewPager(vp, mTitles, this, mFragments);
            } catch (Exception e) {
                finish();
                Toast.makeText(PointActivity.this, "没有商品信息", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            setSwipeListView();

//            int whichOne = getSharedPreferences("islogin", MODE_PRIVATE).getInt("which", 0);
//            String cashier_name = new CashierLogin_pareseJson().parese(
//                    getSharedPreferences("cashierMsgDtos", MODE_PRIVATE)
//                            .getString("cashierMsgDtos", ""))
//                    .get(whichOne).getName();

//            SharedPreferences  sp = getSharedPreferences("cashierMsg" ,MODE_PRIVATE);
//            String cn = sp.getString("cashierName", "1000");
            cashierName.setText(getSharedPreferences("syy", MODE_PRIVATE).getString("countNum","100"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置可左滑的ListView的参数属性
     */
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
        goodsLv.setMenuCreator(creator);

        // step 2. listener item click event
        goodsLv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
//                GoodsDto item = lv_goodsBeens.get(position);
//					delete(item);
                lv_goodsBeens.remove(position);
                adapter.notifyDataSetChanged();
                setScreenData();
            }
        });
    }

    /**
     * dp转换
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    /**
     * 弹出菜单
     */
    private void showPopupWindow() {
        popup_state = true;
        View contentView = LayoutInflater.from(PointActivity.this).inflate(R.layout.menu_fragment, null);
        mPopWindow = new PopupWindow(contentView, 300, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow.setFocusable(true);

        LinearLayout l1 = (LinearLayout) contentView.findViewById(R.id.return_good);
        LinearLayout l3 = (LinearLayout) contentView.findViewById(R.id.money_box);
        LinearLayout l5 = (LinearLayout) contentView.findViewById(R.id.screen_lock);
        LinearLayout l7 = (LinearLayout) contentView.findViewById(R.id.back_menu);

        l1.setOnClickListener(listener);
        l3.setOnClickListener(listener);
        l5.setOnClickListener(listener);
        l7.setOnClickListener(listener);

        mPopWindow.showAsDropDown(menu);
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
                    try {
                        EscCommand esc = new EscCommand();
                        esc.addGeneratePluseAtRealtime(LabelCommand.FOOT.F2, (byte) 2);//开钱箱
                        Vector<Byte> datas = esc.getCommand(); // 发送数据
                        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
                        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
                        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);// 最终转换好的数据
                        App.getmGpService().sendEscCommand(0, sss);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dismissPopupWindow();
                    break;
                case R.id.screen_lock:
                    dismissPopupWindow();
                    startActivity(new Intent(PointActivity.this, LockActivity.class));
                    break;
                case R.id.back_menu:
                    dismissPopupWindow();
                    finish();
                    break;
                case R.id.have_note:
                    dialog.dismiss();
                    Intent intent = new Intent(PointActivity.this, StatisticsActivity.class);
                    intent.putExtra(StatisticsActivity.REFUNDS_NAME, StatisticsActivity.REFUNDS_IN);
                    startActivity(intent);
                    break;
//                case R.id.not_note:
//                    if (lv_goodsBeens.size() > 0) {
//                        new AlertDialog.Builder(PointActivity.this)
//                                .setMessage("此操作会销毁正在销售的订单！是否要销毁该订单？")
//                                .setTitle("提示")
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        textviewClear();
//                                    }
//                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        }).create().show();
//                    }
//                    changeState();
//                    break;
                case R.id.cancel_order:
                    try {
                        more_popup_state = false;
                        morePopWindow.dismiss();
                        delete();
                        if (!orderDto.getTableNumber().trim().isEmpty()) {
                            new TableDao(getApplicationContext()).deleteTable(orderDto.getTableNumber());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.guaDan:
                    restOrder();
                    more_popup_state = false;
                    morePopWindow.dismiss();
                    break;
//                case R.id.discount:
//                    if (lv_goodsBeens.size() > 0) {
//                        Intent intent1 = new Intent(PointActivity.this, OrderDiscountActivity.class);
//                        intent1.putExtra("money", Double.parseDouble(tvMoney.getText().toString()));
//                        startActivityForResult(intent1, 123);
//                    }
//                    more_popup_state = false;
//                    morePopWindow.dismiss();
//                    break;
//                case R.id.luodan:
////                    LuoDanOrder();
//                    if (Build.MODEL.equals(URL.MODEL_D800)) {
//                        startActivity(new Intent(PointActivity.this ,QueryYeActivity.class));
//                    }
//
//                    more_popup_state = false;
//                    morePopWindow.dismiss();
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
                                        Toast.makeText(getApplicationContext(), "未添加该会员", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "暂不支持此功能", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.hy_btn_search:
                    if (!(etHy.getText().toString().isEmpty())) {
                        MemberDto memberDto;
                        if (etHy.getText().toString().trim().length() == 11) {
                            memberDto = new MemberDao(getApplicationContext()).findMemberByTel(etHy.getText().toString().trim());
                        } else {
                            memberDto = new MemberDao(getApplicationContext()).findMemberByNumber(etHy.getText().toString().trim());
                        }
                        if (memberDto == null) {
                            Toast.makeText(getApplicationContext(), "未添加该会员", Toast.LENGTH_SHORT).show();
                            vipName.setText("");
                            hydialog.dismiss();
                        } else {
                            vipName.setText(memberDto.getName());
                            hydialog.dismiss();
                        }
                    }
                    break;
                case R.id.clear:
                    goodsNum.setText("");
                    numberAdd = "";
                    break;
//                case R.id.jia:
////                    good_num += 1;
////                    text_num.setText(String.valueOf(good_num));
////                    break;
                case R.id.scan_sure2:
                    popup_state = false;

                    App.mClient.disconnect(mac);
                    if (mPricePopupWindow != null) {
                        mPricePopupWindow.dismiss();
                    }
                    String weight = good_weight.getText().toString().trim();
                    if (weight.equals("")) {
                        return;
                    }
                    double gd_price =0;
                    if ( Pattern.matches("^\\d+(\\.\\d+)?",weight)) {
                        gd_price = DoubleSave.doubleSaveTwo(Double.parseDouble(weight)*goodsdto.getPrice());
                    }  else {
                        Toast.makeText(PointActivity.this, "不合法重量", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(PointActivity.this, "称重总价为："+DoubleSave.doubleSaveTwo(gd_price)+"元", Toast.LENGTH_SHORT).show();
                    goodsdto.setNum(Double.parseDouble(weight));
                    lv_goodsBeens.add(goodsdto);
                    adapter.notifyDataSetChanged();
                    setScreenData();
                    break;
                case R.id.sure1:
//                    App.mClient.disconnect(App.mac);
//                    Log.i("test",good_price.getText().toString());
//                    numberAdd = good_price.getText().toString().trim();
//                    int len = numberAdd.length();
//                    if (len > 7) {
//                        numberAdd = numberAdd.substring(0, len-2);
//                    }else {
//                        numberAdd = numberAdd.substring(0, len - 1);
//                    }
                    popup_state = false;
                    try {
                        if (mPricePopupWindow != null) {
                            mPricePopupWindow.dismiss();
                        }
                        if (!numberAdd.equals("")) {
//                            GoodsDto gd = new GoodsDto();
//                            gd.setName(goodsdto.getName());
//                            gd.setCategory_sid(goodsdto.getCategory_sid());
                            goodsdto.setNum(Integer.parseInt(numberAdd));
//                            gd.setSid(goodsdto.getSid());
//                            gd.setPrice(goodsdto.getPrice());
                            lv_goodsBeens.add(goodsdto);
                            adapter.notifyDataSetChanged();
                            setScreenData();


//
//                            GoodsDto goodsDto = new GoodsDto();
//                            goodsDto.setName(goodsdto.getName());
//                            goodsDto.setCategory_sid(goodsdto.getCategory_sid());
//                            goodsDto.setSid(goodsdto.getSid());
//                            //设置订单详情的sid
//                            goodsDto.setGoodsId(GetData.getStringRandom(32));
//                            if (sale_state) {
//                                for (int i = 0; i < lv_goodsBeens.size(); i++) {
//                                    if (lv_goodsBeens.get(i).getSid().equals(goodsDto.getSid())) {
//                                        lv_goodsBeens.get(i).setNum(lv_goodsBeens.get(i).getNum() + 1);
//                                        goodsDto.setPrice(Double.parseDouble(numberAdd));
//                                        repeat = false;
//                                    }
//                                }
//                                if (repeat) {
//                                    goodsDto.setNum(good_num);
//                                    goodsDto.setPrice(Double.parseDouble(numberAdd));
//                                    lv_goodsBeens.add(goodsDto);
//                                }
//                            } else {
//                                for (int i = 0; i < lv_goodsBeens.size(); i++) {
//                                    if (lv_goodsBeens.get(i).getSid().equals(goodsDto.getSid())) {
//                                        lv_goodsBeens.get(i).setNum(lv_goodsBeens.get(i).getNum() + 1);
//                                        repeat = false;
//                                    }
//                                }
//                                if (repeat) {
//                                    goodsDto.setNum(-good_num);
//                                    goodsDto.setPrice(-Double.parseDouble(numberAdd));
//                                    lv_goodsBeens.add(goodsDto);
//                                }
//                            }
//                            adapter.notifyDataSetChanged();
//                            setScreenData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

    /**
     * 挂单
     */
    private void restOrder() {
        if (lv_goodsBeens.size() > 0) {
            final AlertDialog deleteDialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("确定挂单？")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addRestOrderinfo();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();
            deleteDialog.show();
        } else {
            Toast.makeText(PointActivity.this, " 没有可挂单的订单！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 落单
     */
    private void LuoDanOrder() {
        if (lv_goodsBeens.size() > 0) {
            String shopId = getSharedPreferences("StoreMessage", MODE_PRIVATE).getString("Id", "");
            OrderDto orderDto = new OrderDto();
            orderDto.setOrder_info(Sava_list_To_Json.changeGoodDtoToJaon(lv_goodsBeens));//存储的该订单的商品的信息
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
                    textviewClear();
                    Toast.makeText(PointActivity.this, " 落单成功！", Toast.LENGTH_SHORT).show();
                    //maxNo = "" + (Integer.parseInt(maxNo) + 1);
                    maxNo = "" + (Long.parseLong(maxNo) + 1);
                }
            } else {
                orderDto.setTableNumber(tableNumber);
                //给数据库里面添加一条记录
                new OrderDao(getApplicationContext()).addOrder(orderDto);
                textviewClear();
                Toast.makeText(PointActivity.this, " 落单成功！", Toast.LENGTH_SHORT).show();
               // maxNo = "" + (Integer.parseInt(maxNo) + 1);
                maxNo = "" + (Long.parseLong(maxNo) + 1);
            }
        } else {
            Toast.makeText(PointActivity.this, " 没有可落单的订单！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 添加一条挂单
     */
    public void addRestOrderinfo() {
        String shopId = getSharedPreferences("userMsg", MODE_PRIVATE).getString("uid", "");
        OrderDto orderDto = new OrderDto();
        orderDto.setOrder_info(Sava_list_To_Json.changeGoodDtoToJaon(lv_goodsBeens));//存储的该订单的商品的信息
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
        orderDto.setIfFinishOrder(URL.BAOLIU);
        //给数据库里面添加一条记录
        new OrderDao(getApplicationContext()).addOrder(orderDto);
        textviewClear();
        maxNo = MaxNO.getMaxNo(this);
        Toast.makeText(PointActivity.this, " 挂单成功！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 改变状态为退款模式
     */
    public void changeState() {
        llGoodTitle.setBackgroundColor(getResources().getColor(R.color.pay_bg));
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

    /**
     * 显示退款方式选择Dialog
     */
    public void showReturnDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.return_dialog, null);
        dialog = new AlertDialog.Builder(PointActivity.this).setView(dialogView).create();

        Button have_note = (Button) dialogView.findViewById(R.id.have_note);
//        Button not_note = (Button) dialogView.findViewById(R.id.not_note);
        have_note.setOnClickListener(listener);
//        not_note.setOnClickListener(listener);

        int height = URL.getScreemHeight();
        int width = URL.getScreemWidth();
        //设置对话框的高度和宽度
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = width / 5;
        lp.height = height / 5;
        dialog.show();
    }

    /**
     * 清除订单数据
     */
    private void textviewClear() {
        adapter.delete();
        total_money = 0.00;
        tvNum.setText("0");
        tvDiscount.setText("0.00");
        tvMoney.setText(String.valueOf(total_money));
        eatingNumber = 0;
        tableNumber = "";
//        tableName.setText(eatingNumber + "人");
        lv_goodsBeens.clear();
    }

    /**
     * 删除该订单时候弹出的对话框
     */
    public void delete() {
        if (lv_goodsBeens.size() > 0) {
            new AlertDialog.Builder(this)
                    .setMessage("是否要销毁该订单？")
                    .setTitle("提示")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            textviewClear();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).create().show();
        } else {
            Toast.makeText(PointActivity.this, "没有可取消的订单！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fragment里的回掉
     */
    @Override
    public void ongridviewclick(GoodsDto value) {
        value.setDiscount(0);
        repeat = true;
        //设置选择的商品的sid
        value.setGoodsId(GetData.getStringRandom(32));
        String unit = value.getCode();
//        if (value.getPopPrice() != 0) {
//            value.setPrice(value.getPopPrice());
//        }


        goodsdto = value;

        goodsdto.setOldPrice(goodsdto.getPrice());//原价
        goodsdto.setSaleType("正常");
        goodsdto.setIszp("0");


        String groupsid = value.getUnit();
                Log.i("groupsid", "groupsid: "+groupsid);
//        try {
//            if (!"".equals(groupsid)&&value.getPrice()!=0) {
//                String[] groupsids = groupsid.split(",");
//                Intent intent = new Intent(PointActivity.this, ModifiedDialogActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putStringArray("groupsids", groupsids);
//                bundle.putString("good_name", value.getName());
//                intent.putExtras(bundle);
//                startActivityForResult(intent, ModifiedDialogActivity.REQUESTCODE);
//                return;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if (unit.substring(0 ,1).equals("1")) {
            good_num = 1;
            numberAdd = "";
            showSetWeightPopupWindow();
            return;
        }else {
            good_num = 1;
            numberAdd = "";
            showSetPricePopupWindow();
//            good_num = 1;
//            numberAdd = "";
////            showSetPricePopupWindow();
//            for (int i = 0; i < lv_goodsBeens.size(); i++) {
//                    if (lv_goodsBeens.get(i).getSid().equals(value.getSid())) {
//                        lv_goodsBeens.get(i).setNum(lv_goodsBeens.get(i).getNum() +1);
//                        repeat = false;
//                    }
//                }
//                if (repeat) {
//                    value.setNum(1);
//                    lv_goodsBeens.add(value);
//                }
//            }
//            adapter.notifyDataSetChanged();
//            setScreenData();
//            return;
        }

//        if (value.getPrice() > 0.1) {
//            if (sale_state) {
//                for (int i = 0; i < lv_goodsBeens.size(); i++) {
//                    if (lv_goodsBeens.get(i).getSid().equals(value.getSid())) {
//                        lv_goodsBeens.get(i).setNum(lv_goodsBeens.get(i).getNum() + 1);
//                        repeat = false;
//                    }
//                }
//                if (repeat) {
//                    value.setNum(1);
//                    lv_goodsBeens.add(value);
//                }
//            } else {
//                for (int i = 0; i < lv_goodsBeens.size(); i++) {
//                    if (lv_goodsBeens.get(i).getSid().equals(value.getSid())) {
//                        lv_goodsBeens.get(i).setNum(lv_goodsBeens.get(i).getNum() - 1);
//                        repeat = false;
//                    }
//                }
//                if (repeat) {
//                    value.setNum(-1);
//                    lv_goodsBeens.add(value);
//                }
//            }
//            adapter.notifyDataSetChanged();
//            setScreenData();
//        } else if (value.getPrice()==0){

//        }

    }
    //设置称重窗口
    private void showSetWeightPopupWindow() {
        BluetoothHelper.connect(this);

        popup_state = true;
        View contentView = LayoutInflater.from(PointActivity.this).inflate(R.layout.weight_layout, null);
        mPricePopupWindow = new PopupWindow(contentView, 500, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPricePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPricePopupWindow.setFocusable(true);

//        ImageView jian = (ImageView) contentView.findViewById(R.id.jian);
//        ImageView jia = (ImageView) contentView.findViewById(R.id.jia);
//        ImageView clear = (ImageView) contentView.findViewById(R.id.clear);
        TextView sure = (TextView) contentView.findViewById(R.id.scan_sure2);
//        text_num = (TextView) contentView.findViewById(R.id.text_num);
        good_weight = (EditText) contentView.findViewById(R.id.scan_tv_weight);
        tv_singlePrice = (TextView) contentView.findViewById(R.id.scan_tv_weight_price);
        TextView tvName = (TextView) contentView.findViewById(R.id.scan_tv_name);
        TextView tvPrice = (TextView) contentView.findViewById(R.id.scan_tv_price);
        tvName.setText(goodsdto.getName());
        tvPrice.setText(goodsdto.getPrice()+"/斤");
//        tv_singlePrice.setText(goodsdto.getPrice()+"元/500g");
//        jian.setOnClickListener(listener);
//        jia.setOnClickListener(listener);
//        clear.setOnClickListener(listener);
        sure.setOnClickListener(listener);

        mPricePopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        mPricePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                App.mClient.disconnect(mac);
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
                        zl = DoubleSave.doubleSaveTwo(Double.parseDouble(weight) / 500);
                        price = DoubleSave.doubleSaveTwo(Double.parseDouble(weight) / 500 * goodsdto.getPrice());
                        Log.i("weight", weight + "-----------" + price);
                    } else {
                        String s = data.substring(0, len - 4);//注意kg为单位时，后有两个空格字符
                        Log.i("weight", s+"-----------"+len);
                        price = DoubleSave.doubleSaveTwo(Double.parseDouble(s)*2*goodsdto.getPrice());
                        zl = DoubleSave.doubleSaveTwo(Double.parseDouble(s)*2);
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

    /**
     * 弹出设置价格窗口
     */
    private EditText good_price;
    private TextView text_num;

    private void showSetPricePopupWindow() {

        popup_state = true;
        View contentView = LayoutInflater.from(PointActivity.this).inflate(R.layout.set_price_popup, null);
        mPricePopupWindow = new PopupWindow(contentView, 500, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPricePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPricePopupWindow.setFocusable(true);

        ImageView clear = (ImageView) contentView.findViewById(R.id.clear);
        TextView sure = (TextView) contentView.findViewById(R.id.sure1);
//        text_num = (TextView) contentView.findViewById(R.id.text_num);
        goodsNum = (EditText) contentView.findViewById(R.id.good_price);

        clear.setOnClickListener(listener);
        sure.setOnClickListener(listener);

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

    public void setScreenData() {
        good_num = 0;
        numberAdd = "";
        double dou = 0;
        double totalNum = 0;
        double discount = 0.0;
        for (int i = 0; i < lv_goodsBeens.size(); i++) {
            dou = dou + lv_goodsBeens.get(i).getNum() * lv_goodsBeens.get(i).getPrice();
            if (sale_state) {
                totalNum = totalNum + lv_goodsBeens.get(i).getNum();
            } else {
                totalNum = totalNum - lv_goodsBeens.get(i).getNum();
            }
            discount += lv_goodsBeens.get(i).getDiscount();
        }
        tvDiscount.setText(DoubleSave.formatDouble(discount));
        tvNum.setText(String.valueOf(totalNum));
        tvMoney.setText(DoubleSave.formatDouble(dou - discount));
    }

    class MyLvAdapter extends BaseAdapter {
        private Context context;
        private List<GoodsDto> goodsBeens;

        public MyLvAdapter(Context context, List<GoodsDto> goodsBeens) {
            this.context = context;
            this.goodsBeens = goodsBeens;
        }

        //清除数据
        public void delete() {
            goodsBeens.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return goodsBeens.size();
        }

        @Override
        public Object getItem(int position) {
            return goodsBeens.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.lvsale_item, null);
                holder.name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.momey = (TextView) convertView.findViewById(R.id.tv_money);
                holder.price = (TextView) convertView.findViewById(R.id.tv_num_price);
                holder.num = (TextView) convertView.findViewById(R.id.tv_num);
                holder.discount = (TextView) convertView.findViewById(R.id.tv_discount);
                holder.modified = (TextView) convertView.findViewById(R.id.text_modified);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            GoodsDto goodsBean = goodsBeens.get(position);
            holder.name.setText(goodsBean.getName().trim());
            holder.price.setText(String.valueOf(DoubleSave.doubleSaveTwo(goodsBean.getPrice())));
            holder.momey.setText(String.valueOf(DoubleSave.doubleSaveTwo(goodsBean.getNum() * goodsBean.getPrice() - goodsBean.getDiscount())));
            holder.num.setText(String.valueOf(DoubleSave.doubleSaveTwo(goodsBean.getNum())));
            holder.discount.setText(String.valueOf(DoubleSave.doubleSaveTwo(goodsBean.getDiscount())));
            Log.i("getModified", "getModified: " + goodsBean.getModified());
            if (goodsBean.getModified() != null) {
                holder.modified.setVisibility(View.VISIBLE);
                List<ModifiedDto> modifiedDtos = new Gson().fromJson(goodsBean.getModified(), new TypeToken<List<ModifiedDto>>() {
                }.getType());
                Log.i("getModified", "modifiedDtos: " + modifiedDtos.size());
                String modified = "";
                for (int i = 0; i < modifiedDtos.size(); i++) {
                    if (i > 0) {
                        modified += "\n";
                    }
                    modified += modifiedDtos.get(i).getName();
                }
                holder.modified.setText(modified);
            }
            return convertView;
        }

        class ViewHolder {
            TextView name;
            TextView price;
            TextView momey;
            TextView num;
            TextView discount;
            TextView modified;
        }
    }

//    //屏蔽Home键
//    @Override
//    public void onAttachedToWindow() {
//        System.out.println("Page01 -->onAttachedToWindow");
//        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
//        super.onAttachedToWindow();
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_MENU) {//MENU键
//            //监控/拦截菜单键
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    //屏蔽菜单键
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        ActivityManager activityManager = (ActivityManager) getApplicationContext()
//                .getSystemService(Context.ACTIVITY_SERVICE);
//
//        activityManager.moveTaskToFront(getTaskId(), 0);
//    }

    @Override
    protected void onDestroy() {
        if (mPopWindow != null) {
            mPopWindow = null;
        }
        if (morePopWindow != null) {
            morePopWindow = null;
        }
        if (mPricePopupWindow != null) {
            mPricePopupWindow = null;
        }
        if (dialog != null) {
            dialog = null;
        }
        super.onDestroy();
    }
}
