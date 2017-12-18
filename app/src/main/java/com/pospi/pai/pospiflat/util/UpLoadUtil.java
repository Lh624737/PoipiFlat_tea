package com.pospi.pai.pospiflat.util;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.pospi.dao.OrderDao;
import com.pospi.dao.PayWayDao;
import com.pospi.dto.OrderDto;
import com.pospi.dto.PayWayDto;
import com.pospi.http.UpLoadToServel;
import com.pospi.util.App;
import com.pospi.util.UpdateOrder;
import com.pospi.util.constant.URL;

import java.util.List;

/**
 * Created by acer on 2017/10/10.
 */

public class UpLoadUtil {
    public static void upLoad(Context context) {
        PayWayDto payWayDto = null;
        List<OrderDto> orderDtos = new OrderDao(context).findNOUpLoad();
        List<PayWayDto> payWayDtos = new PayWayDao(context).findAllPayWay();
//        Log.i(TAG, "UpLoadService,大小为：" + orderDtos.size());
        int size = orderDtos.size();
        for (int j = 0; j < size; j++) {
//            Log.i(TAG, "order_time：" + orderDto.getCheckoutTime());
            for (int i = 0; i < payWayDtos.size(); i++) {
                Log.i("payType1", "订单的付款方式" + orderDtos.get(j).getStatus());
                Log.i("payType1", "本地存储的付款方式" + payWayDtos.get(i).getPayType1());
                if (orderDtos.get(j).getStatus() == payWayDtos.get(i).getPayType1()) {
                    payWayDto = payWayDtos.get(i);
                }
            }
            try {
                if (orderDtos.get(j).getOrderType() == URL.ORDERTYPE_REFUND) {
                    new UpLoadToServel(context).uploadOrderToServer(orderDtos.get(j), payWayDto, "2", context, UpdateOrder.findInfo);
                } else {
                    new UpLoadToServel(context).uploadOrderToServer(orderDtos.get(j), payWayDto, "1", context, UpdateOrder.findInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
