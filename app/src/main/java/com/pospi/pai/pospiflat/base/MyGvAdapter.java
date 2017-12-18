package com.pospi.pai.pospiflat.base;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pospi.dto.FastGoods;
import com.pospi.dto.GoodsDto;
import com.pospi.dto.ModifiedDto;
import com.pospi.pai.pospiflat.R;
import com.pospi.pai.pospiflat.cash.PointActivity;
import com.pospi.util.DoubleSave;

import java.util.List;

/**
 * Created by acer on 2017/9/12.
 */

public class MyGvAdapter extends BaseAdapter {
        private Context context;
        private List<FastGoods> fastGoodses;

        public MyGvAdapter(Context context, List<FastGoods> fastGoodses) {
            this.context = context;
            this.fastGoodses = fastGoodses;
        }

//        //清除数据
//        public void delete() {
//            goodsBeens.clear();
//            notifyDataSetChanged();
//        }

        @Override
        public int getCount() {
            return fastGoodses.size();
        }

        @Override
        public Object getItem(int position) {
            return fastGoodses.get(position);
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
                convertView = LayoutInflater.from(context).inflate(R.layout.item_fast, null);
                holder.no = (TextView) convertView.findViewById(R.id.item_no);
                holder.name = (TextView) convertView.findViewById(R.id.item_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
//            GoodsDto goodsBean = goodsBeens.get(position);
//            holder.name.setText(goodsBean.getName().trim());
            FastGoods fg = fastGoodses.get(position);
            Log.i("fast", fg.getNo() + "------" + fg.getName());
            holder.no.setText(fg.getNo());
            holder.name.setText(fg.getName());
            return convertView;
        }

        class ViewHolder {
            TextView no;
            TextView name;

        }

}
