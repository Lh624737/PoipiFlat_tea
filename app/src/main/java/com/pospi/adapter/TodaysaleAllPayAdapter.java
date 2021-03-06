package com.pospi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pospi.dto.OrderDto;
import com.pospi.dto.OrderPaytype;
import com.pospi.pai.pospiflat.R;
import com.pospi.util.constant.PayWay;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Qiyan on 2016/5/30.
 */
public class TodaysaleAllPayAdapter extends BaseAdapter {
    private Context context;
    private List<OrderPaytype> orderDtos;
    private double card_discount;

    public TodaysaleAllPayAdapter(Context context, List<OrderPaytype> orderDtos) {
        this.context = context;
        this.orderDtos = orderDtos;
        card_discount = context.getSharedPreferences("StoreMessage", Context.MODE_PRIVATE).getFloat("Discount", 1);
    }

    @Override
    public int getCount() {
        return orderDtos.size();
    }

    @Override
    public Object getItem(int position) {
        return orderDtos.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.todaysaleall_outlvitem, null);
            holder.way = (TextView) convertView.findViewById(R.id.today_sale_all_item_way);
            holder.ys = (TextView) convertView.findViewById(R.id.today_sale_all_item_ys);
            holder.ss = (TextView) convertView.findViewById(R.id.today_sale_all_item_ss);
            holder.zl = (TextView) convertView.findViewById(R.id.today_sale_all_item_zl);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.way.setText(orderDtos.get(position).getPayName());
        holder.ys.setText(orderDtos.get(position).getYs());
        holder.ss.setText(orderDtos.get(position).getSs());
        holder.zl.setText(orderDtos.get(position).getZl());
//        if (orderDtos.get(position).getPayway().equals(PayWay.CZK)) {
//            holder.ss.setText(String.valueOf(translateDouble(Double.parseDouble(orderDtos.get(position).getSs_money()) * card_discount)));
//            holder.ys.setText(String.valueOf(translateDouble(Double.parseDouble(orderDtos.get(position).getYs_money()) * card_discount)));
//        } else {
//            holder.ss.setText(String.valueOf(translateDouble(Double.parseDouble(orderDtos.get(position).getSs_money()))));
//            holder.ys.setText(String.valueOf(translateDouble(Double.parseDouble(orderDtos.get(position).getYs_money()))));
//        }
//        if (orderDtos.get(position).getZl_money().equals("")) {
//            holder.zl.setText(String.valueOf("0.0"));
//        } else {
//            holder.zl.setText(String.valueOf(translateDouble(Double.parseDouble(orderDtos.get(position).getZl_money()))));
//        }


        return convertView;
    }

    public double translateDouble(double b) {
        BigDecimal b2 = new BigDecimal(b);
        double f3 = b2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f3;
    }


    class ViewHolder {
        TextView way;
        TextView ys;
        TextView ss;
        TextView zl;
    }
}
