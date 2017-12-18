package com.pospi.pai.pospiflat.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.pospi.dao.OrderPaytypeDao;
import com.pospi.dao.PayWayDao;
import com.pospi.dto.GoodsDto;
import com.pospi.dto.OrderPaytype;
import com.pospi.dto.ValueCardDto;
import com.pospi.http.MaxNO;
import com.pospi.util.App;
import com.pospi.util.CashierLogin_pareseJson;
import com.pospi.util.DoubleSave;
import com.pospi.util.GetData;
import com.pospi.util.MyPrinter;
import com.pospi.util.Sava_list_To_Json;
import com.pospi.util.constant.PayWay;
import com.pospi.util.constant.URL;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 *  * 在此写用途
 *  * @FileName:com.pospi.pai.pospiflat.util.SuMiPrint.java
 *  * @author: myName
 *  * @date: 2017-06-06 13:44
 *  * @version V1.0 <描述当前版本功能>
 *  
 */
public class SuMiPrint implements MyPrinter{
    private static final String TAG = "SuMiPrint";
    private static Context context;
    private String shop_name;
    private String shop_address;
    private String shop_phone;
    private String device_no;
    private String shop_id;//得到店铺的id 也就是门店号
    private String cashier_name;
    private int whichOne;
    private String maxNo;
    private String payWay;


    int nums = 0;
    double prices = 0.00;
    int goodsNum = 0;
    double moneys = 0.00;
    double discounts = 0.0;
    double goodsDiscount = 0.0;
    private String orderId;

    public SuMiPrint(Context context, String MaxNo, String payWay ,String orderId) {
        this.orderId = orderId;

        SharedPreferences ssp = context.getSharedPreferences("shopMsg", MODE_PRIVATE);
        shop_name = ssp.getString("name", "");//得到店铺
        SharedPreferences preferences = context.getSharedPreferences("userMsg", MODE_PRIVATE);//StoreMessage  存储的店铺的信息
//        shop_name = preferences.getString("name", "");//得到存储的店铺的名字
        shop_address = preferences.getString("address", "测试店铺");//得到店铺的地址
        shop_phone = preferences.getString("phone", "1000000");//得到店铺的联系电话
//
//        SharedPreferences preferences1 = context.getSharedPreferences("Token", MODE_PRIVATE);//得到第一个登陆之后存储的Token  里面存储的有机器的设备号 店铺的id
//        String value = preferences1.getString("value", "");
//        String[] names = value.split("\\,");
        shop_id = preferences.getString("uid" ,"");//得到店铺的id 也就是门店号

//        whichOne = context.getSharedPreferences("islogin", MODE_PRIVATE).getInt("which", 0);
//        cashier_name = new CashierLogin_pareseJson().parese(
//                context.getSharedPreferences("cashierMsgDtos", MODE_PRIVATE)
//                        .getString("cashierMsgDtos", ""))
//                .get(whichOne).getName();
//        SharedPreferences  sp = context.getSharedPreferences("cashierMsg" ,MODE_PRIVATE);
        cashier_name = context.getSharedPreferences("syy", MODE_PRIVATE).getString("countNum","100");

        //得到最大的小票号
        this.maxNo = MaxNo; //订单的小票号
        this.payWay = payWay;
        this.context = context;
    }
    private String printModle = "1";//1销售2退货
    public void beginPrint(String goods ,String payMoney ,String modle){
        printModle = modle;
//        SharedPreferences sharedPreferences = context.getSharedPreferences("receipt_num", Context.MODE_PRIVATE);
//        int receipt_num = sharedPreferences.getInt("receipt_num", 1);

        // 1: Get BluetoothAdapter
        BluetoothAdapter btAdapter = BluetoothUtil.getBTAdapter();
        if (btAdapter == null) {
            Log.i("test", "蓝牙未打开");
//			Toast.makeText(context, "Please Open Bluetooth!", Toast.LENGTH_LONG).show();
            return;
        }
        // 2: Get Sunmi's InnerPrinter BluetoothDevice
        BluetoothDevice device = BluetoothUtil.getDevice(btAdapter);
        if (device == null) {
            Log.i("test", "未找到打印设备");
//			Toast.makeText(context, "Please Make Sure Bluetooth have InnterPrinter!",
//					Toast.LENGTH_LONG).show();
            return;
        }
        // 3: Generate a order data
//				byte[] data = ESCUtil.generateMockData();
        byte[] data = printTop(maxNo);
        byte[] data1 = printBody(goods);
        byte[] data2 = printFoot(payMoney);
        byte[] data3 = ESCUtil.byteMerger(data, data1);
        byte[] result = ESCUtil.byteMerger(data3, data2);
        // 4: Using InnerPrinter print data
        BluetoothSocket socket = null;
        try {
            socket = BluetoothUtil.getSocket(device);

            BluetoothUtil.sendData(result, socket);
        } catch (IOException e) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    public byte[] printTop(String maxNo ) {

        try {
            byte[] underoff = ESCUtil.underlineOff();
            byte[] next2Line = ESCUtil.nextLine(2);
//			byte[] title = "The menu（launch）**wanda plaza".getBytes("gb2312");
            byte[] nextLine = ESCUtil.nextLine(1);
            byte[] boldOn = ESCUtil.boldOn();
            byte[] fontSize2Big = ESCUtil.fontSizeSetBig(2);
            byte[] center = ESCUtil.alignCenter();
            byte[] Focus;
            if (printModle.equals("2")) {
                Focus = (shop_name+"(退货)").getBytes("gb2312");
            } else {
                Focus = shop_name.getBytes("gb2312");
            }

            byte[] boldOff = ESCUtil.boldOff();
            byte[] fontSizeNormal = ESCUtil.fontSizeSetBig(1);
            byte[] line = "                                             ".getBytes("gb2312");
            byte[] left = ESCUtil.alignLeft();
            byte[] orderNumber = ("单据："+maxNo).getBytes("gb2312");
            byte[] cashier = ("收银员："+cashier_name).getBytes("gb2312");
            byte[] orderTime = ("单据时间："+ GetData.getDataTime()).getBytes("gb2312");
//            byte[] orderTime = ("单据时间："+ "2017-10-25 14:34:29").getBytes("gb2312");
            byte[] underline = ESCUtil.underlineWithOneDotWidthOn();
            byte[] menu = "品名  单价   数量    金额        优惠".getBytes("gb2312");
            byte[] fontSize1Small = ESCUtil.fontSizeSetSmall(2);

            int nums = 0;
            double price = 0.00;
            int goodsNum = 0;
            double moneys = 0.00;
            double discounts = 0.0;
            double goodsDiscount = 0.0;
//            for (GoodsDto gd:goodsList) {
//
//
//            }

            byte[] detail = "豆浆    2.00     1    2.00     0.0".getBytes("gb2312");


            byte[] number = "数量：       1".getBytes("gb2312");
            byte[] payWay = "付款：       现金".getBytes("gb2312");
            byte[] yinshou = "应收：       2.00".getBytes("gb2312");
            byte[] shihsou = "实收：       2.00".getBytes("gb2312");
            byte[] discount = "折让：       0.0".getBytes("gb2312");
            byte[] zhaolin = "找零：       0.00".getBytes("gb2312");
            byte[] address = "地址：中山路8号".getBytes("gb2312");
            byte[] phone = "电话：1242343535".getBytes("gb2312");
//			boldOn = ESCUtil.boldOn();
//			byte[] fontSize1Big = ESCUtil.fontSizeSetBig(2);
//			byte[] FocusOrderContent = "Big hamburger(single)".getBytes("gb2312");
//			boldOff = ESCUtil.boldOff();
////			byte[] fontSize1Small = ESCUtil.fontSizeSetSmall(2);
//
//			next2Line = ESCUtil.nextLine(2);
//
//			byte[] priceInfo = "Receivable:$22  Discount：$2.5 ".getBytes("gb2312");
//			byte[] nextLine = ESCUtil.nextLine(1);
//
//			byte[] priceShouldPay = "Actual collection:$19.5".getBytes("gb2312");
//			nextLine = ESCUtil.nextLine(1);
//
//			byte[] takeTime = "Pickup time:2015-02-13 12:51:59".getBytes("gb2312");
//			nextLine = ESCUtil.nextLine(1);
//			byte[] setOrderTime = "Order time：2015-02-13 12:35:15".getBytes("gb2312");
//
//			byte[] tips_1 = "Follow twitter\"**\"order for $1 discount".getBytes("gb2312");
//			nextLine = ESCUtil.nextLine(1);
//			byte[] tips_2 = "Commentary reward 50 cents".getBytes("gb2312");
            byte[] next4Line = ESCUtil.nextLine(4);

            byte[] breakPartial = ESCUtil.feedPaperCutPartial();

            byte[][] cmdBytes = { underoff ,center, boldOn, fontSize2Big, Focus, boldOff, nextLine,
                    fontSizeNormal, left,nextLine, orderNumber, nextLine, cashier,nextLine,orderTime,nextLine,underline, line ,underoff ,nextLine,menu,nextLine
            };


            return ESCUtil.byteMerger(cmdBytes);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    public  byte[] printBody(String goods) {

        List<GoodsDto> goodsList = Sava_list_To_Json.changeToList(goods);

        try {
            byte[][] b = new byte[goodsList.size()*4][];
            for (int i = 0,j =0 ;i<goodsList.size()*4;i+=4) {
                String name = goodsList.get(j).getName();
                String no = goodsList.get(j).getCode();
                Double price = goodsList.get(j).getOldPrice();
                double num = goodsList.get(j).getNum();
                Double discount = goodsList.get(j).getOldPrice()*goodsList.get(j).getNum() - goodsList.get(j).getPrice()*goodsList.get(j).getNum()+goodsList.get(j).getDiscount();
                Double money = num *price -discount;


                nums += num;
                discounts += discount;
                 moneys += money;

                b[i] = (no+"    "+name ).getBytes("gb2312");
                b[i+1] = ESCUtil.nextLine(1);
                      // "品名  单价   数量          金额         优惠".getBytes("gb2312");
                b[i+2] = ("     "+DoubleSave.formatDouble(price) + "  "+num +"    "+ DoubleSave.formatDouble(money) + "       "+DoubleSave.formatDouble(discount)).getBytes("gb2312");
                b[i+3] = ESCUtil.nextLine(1);
                j++;
            }
            return ESCUtil.byteMerger(b);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
//    public  byte[] printBody(String goods) {
//
//        List<GoodsDto> goodsList = Sava_list_To_Json.changeToList(goods);
//
//        try {
//            byte[][] b = new byte[goodsList.size()*4][];
//            for (int i = 0,j =0 ;i<goodsList.size()*4;i+=4) {
//                String name = goodsList.get(j).getName();
//                String no = goodsList.get(j).getCode();
//                Double price = goodsList.get(j).getOldPrice();
//                double num = goodsList.get(j).getNum();
//                Double discount = goodsList.get(j).getOldPrice()*goodsList.get(j).getNum() - goodsList.get(j).getPrice()*goodsList.get(j).getNum()+goodsList.get(j).getDiscount();
//                Double money = num *price -discount;
//
//
//                nums += num;
//                discounts += discount;
//                moneys += money;
//
//                b[i] = ("套餐").getBytes("gb2312");
//                b[i+1] = ESCUtil.nextLine(1);
//                // "品名   单价   数量       金额         优惠".getBytes("gb2312");
//                b[i+2] = ("     "+DoubleSave.formatDouble(price) + "  "+num +"   "+ DoubleSave.formatDouble(money) + "  "+DoubleSave.formatDouble(discount)).getBytes("gb2312");
//                b[i+3] = ESCUtil.nextLine(1);
//                j++;
//            }
//            return ESCUtil.byteMerger(b);
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public byte[] printFoot(String payMoney) {
        List<OrderPaytype> orderPaytypes = new OrderPaytypeDao(App.getContext()).query(orderId);
        StringBuilder builder = new StringBuilder();
        for (OrderPaytype paytype : orderPaytypes) {
            builder.append(paytype.getPayName() + "(" + paytype.getSs() + ")"+"    ");
        }

        String way = new PayWayDao(context).findPayName(payWay);
        if ("".equals(way)) {
            way = payWay;
        }
        try {
            byte[] underoff = ESCUtil.underlineOff();
            byte[] nextLine = ESCUtil.nextLine(1);
            byte[] refundYJ = ("退款合计：    "+DoubleSave.formatDouble(-(moneys+discounts))).getBytes("gb2312");
            byte[] refundYT = ("应退：       "+DoubleSave.formatDouble(-moneys)).getBytes("gb2312");
            byte[] refundST = ("实退：       "+DoubleSave.formatDouble(-Double.parseDouble(payMoney))).getBytes("gb2312");
            byte[] number = ("数量：       "+nums).getBytes("gb2312");
            byte[] payway = ("付款方式：  "+builder.toString()).getBytes("gb2312");
            byte[] yunjia = ("原价合计：  "+DoubleSave.formatDouble(moneys+discounts)).getBytes("gb2312");
            byte[] yinshou = ("应收金额：  "+DoubleSave.formatDouble(moneys)).getBytes("gb2312");
            byte[] shihsou = ("实收金额：  "+payMoney).getBytes("gb2312");
            byte[] discount = ("优 惠：     "+DoubleSave.formatDouble(discounts)).getBytes("gb2312");
            byte[] zhaolin ="找 零：     0.00".getBytes("gb2312");
            if (payWay.equals(String.valueOf(PayWay.CASH)) || payWay.equals(String.valueOf(PayWay.OTHER))) {
                zhaolin = ("找 零:      " + DoubleSave.formatDouble(Double.parseDouble(payMoney) - moneys) ).getBytes("gb2312");
            }
            String ad = context.getSharedPreferences("shopMsg", Context.MODE_PRIVATE).getString("ad", "");
//            String ad = "电话：010-624737";
            byte[] address = ad.getBytes("gb2312");
            byte[] phone = "------------谢谢惠顾，欢迎下次光临！------------".getBytes("gb2312");
            byte[] next4Line = ESCUtil.nextLine(4);
            byte[] line = "                                             ".getBytes("gb2312");
            byte[] underline = ESCUtil.underlineWithOneDotWidthOn();
            byte[] breakPartial = ESCUtil.feedPaperCutPartial();

            if (moneys < 0) {

            }
            byte[][] cmdBytes = {underline ,line ,underoff ,nextLine ,payway, nextLine,yunjia,nextLine,  yinshou, nextLine, shihsou, nextLine, discount, nextLine, zhaolin, nextLine, underline, line, underoff, nextLine, address, nextLine,nextLine,nextLine, phone, underline, next4Line, breakPartial};
            return ESCUtil.byteMerger(cmdBytes);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void starPrint(String goods, String payMoney, Bitmap qrCode, boolean isSale, ValueCardDto valueCardDto ,String sid ,int orderType) {

        if (orderType == URL.ORDERTYPE_REFUND) {
            beginPrint(goods, String.valueOf(-Double.parseDouble(payMoney)), "2");
        } else {
            beginPrint(goods ,payMoney ,"1");
        }

    }



    public byte[] printDayMsgTop( String d) {

        try {
            byte[] underoff = ESCUtil.underlineOff();
            byte[] next2Line = ESCUtil.nextLine(2);
//			byte[] title = "The menu（launch）**wanda plaza".getBytes("gb2312");
            byte[] nextLine = ESCUtil.nextLine(1);
            byte[] boldOn = ESCUtil.boldOn();
            byte[] fontSize2Big = ESCUtil.fontSizeSetBig(2);
            byte[] center = ESCUtil.alignCenter();
            byte[] Focus = "日结".getBytes("gb2312");
            byte[] date = ("日结日期："+d).getBytes("gb2312");
            byte[] dateTime = ("单据时间："+GetData.getDataTime()).getBytes("gb2312");
            byte[] totalSale = "销售合计".getBytes("gb2312");

            byte[] boldOff = ESCUtil.boldOff();
            byte[] fontSizeNormal = ESCUtil.fontSizeSetBig(1);
            byte[] line = "-----------------------------------------".getBytes("gb2312");
            byte[] left = ESCUtil.alignLeft();
            byte[] totalSaleMoney = ("总销售额："+maxNo).getBytes("gb2312");
            byte[] discountSale = ("打折销售："+cashier_name).getBytes("gb2312");
            byte[] sales = ("净销售额："+ GetData.getDataTime()).getBytes("gb2312");
            byte[] refunds = ("退货："+ GetData.getDataTime()).getBytes("gb2312");
            byte[] underline = ESCUtil.underlineWithOneDotWidthOn();
            byte[] ordeNum = "订单数量".getBytes("gb2312");
            byte[] refundsNum = ("退货订单数量："+ GetData.getDataTime()).getBytes("gb2312");
            byte[] fontSize1Small = ESCUtil.fontSizeSetSmall(2);
            byte[] next4Line = ESCUtil.nextLine(4);

            byte[] breakPartial = ESCUtil.feedPaperCutPartial();

            byte[][] cmdBytes = { underoff ,center, boldOn, fontSize2Big, Focus, boldOff, nextLine,
                    fontSizeNormal, left,nextLine, date, nextLine, dateTime,nextLine,line ,nextLine ,totalSale,
                    nextLine,line, nextLine ,totalSaleMoney ,nextLine,discountSale,nextLine ,sales ,nextLine ,refunds ,nextLine ,ordeNum ,nextLine,
                    refundsNum ,nextLine ,line,next4Line
            };


            return ESCUtil.byteMerger(cmdBytes);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
