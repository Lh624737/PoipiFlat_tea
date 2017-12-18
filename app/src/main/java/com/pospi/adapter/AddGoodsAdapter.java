package com.pospi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pospi.dto.GoodsDto;
import com.pospi.pai.pospiflat.R;
import com.pospi.util.DoubleSave;

import java.util.List;

/**
 * Created by Qiyan on 2016/5/11.
 */
public class AddGoodsAdapter extends BaseAdapter {
    private Context context;
    private List<GoodsDto> goodsDtos;

    private int number;

    public AddGoodsAdapter(Context context, List<GoodsDto> goodsDtos) {
        this.context = context;
        this.goodsDtos = goodsDtos;
    }

    //用户添加一条商品的信息
//    public void add(GoodsDto goodsdto) {
//        for (GoodsDto dto : goodsDtos) {
//            if (dto.getCode().equals(goodsdto.getCode())) {
//                number = number + 1;
//            } else {
//                goodsDtos.add(goodsdto);
//            }
//        }
//    }

    @Override
    public int getCount() {
        return goodsDtos.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsDtos.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.add_goods_lv_item, null);
            holder.num = (TextView) convertView.findViewById(R.id.num);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.momey = (TextView) convertView.findViewById(R.id.total);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.goods_num = (TextView) convertView.findViewById(R.id.goods_num);
            holder.discount = (TextView) convertView.findViewById(R.id.goods_discount);
            holder.nameNum = (TextView) convertView.findViewById(R.id.nameNum);
            holder.type = (TextView) convertView.findViewById(R.id.type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.num.setText(String.valueOf(position + 1));
        holder.type.setText(goodsDtos.get(position).getSaleType());
        holder.nameNum.setText(goodsDtos.get(position).getCode());
        holder.name.setText(goodsDtos.get(position).getName());
        Log.i("goods_num", String.valueOf(goodsDtos.get(position).getNum()));
        holder.goods_num.setText(String.valueOf(goodsDtos.get(position).getNum()));
//        if (goodsDtos.get(position).getCostPrice() != 0) {
//            if (goodsDtos.get(position).getCode().substring(0, 1).equals("1")) {
//                holder.price.setText("(会员价)" + String.valueOf(DoubleSave.doubleSaveTwo(goodsDtos.get(position).getPrice())) + "/500g");
//            } else {
//                holder.price.setText("(会员价)" + String.valueOf(DoubleSave.doubleSaveTwo(goodsDtos.get(position).getPrice())));
//            }
//        } else {
            //判断是否为称重商品
            if (goodsDtos.get(position).getUnit().equals("2")) {
                holder.price.setText(DoubleSave.formatDouble(goodsDtos.get(position).getOldPrice()) + "/500g");
            } else {
                if (!goodsDtos.get(position).getSpecification().equals("无")) {
                    holder.price.setText(DoubleSave.formatDouble(goodsDtos.get(position).getOldPrice())+"/"+goodsDtos.get(position).getSpecification());
                } else {
                    holder.price.setText(DoubleSave.formatDouble(goodsDtos.get(position).getOldPrice()));
                }

            }
//        }


        holder.discount.setText(DoubleSave.formatDouble(goodsDtos.get(position).getOldPrice() * goodsDtos.get(position).getNum() - goodsDtos.get(position).getPrice() * goodsDtos.get(position).getNum()+goodsDtos.get(position).getDiscount()));
        holder.momey.setText(DoubleSave.formatDouble(goodsDtos.get(position).getNum() * goodsDtos.get(position).getPrice() - goodsDtos.get(position).getDiscount()));

        return convertView;
    }

    class ViewHolder {
        TextView num;
        TextView nameNum;
        TextView name;
        TextView price;
        TextView momey;
        TextView discount;
        TextView goods_num;
        TextView type;
        TextView oldprice;


    }
}
