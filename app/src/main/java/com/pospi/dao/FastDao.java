package com.pospi.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pospi.database.DB;
import com.pospi.dto.FastGoods;
import com.pospi.dto.GoodsDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2017/9/12.
 */

public class FastDao {
    private DB fastDB;
    private String tablename = "fastChoose";

    private FastGoods dto;
    private ContentValues values;

    public FastDao(Context context) {
        fastDB = new DB(context);//实例化数据库
    }

    //添加
    public void insert(List<FastGoods> list) {
        SQLiteDatabase db = null;
        try {
            db = fastDB.getWritableDatabase();
            for (int i = 0; i < list.size(); i++) {
                FastGoods dto = list.get(i);
                values = new ContentValues();
                values.put("no", dto.getNo());
                values.put("name", dto.getName());

                long result = db.insert(tablename, null, values);

                Log.i("goodsDtosInfo", "result=" + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }
   //查询
    public List<FastGoods> select(){
        SQLiteDatabase db = fastDB.getReadableDatabase();
        List<FastGoods> list = new ArrayList<>();
        Cursor cursor = db.query(tablename, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            FastGoods fg = new FastGoods();
            fg.setNo(cursor.getString(cursor.getColumnIndex("no")));
            fg.setName(cursor.getString(cursor.getColumnIndex("name")));
            list.add(fg);
        }
        cursor.close();
        db.close();
        return list;
    }

    //删除
    public boolean delete(String no) {
        SQLiteDatabase db = fastDB.getWritableDatabase();
        int num  = db.delete(tablename, "no = ?", new String[]{no});
        if (num == 0) {
            return false;
        }
        return true;
    }
}
