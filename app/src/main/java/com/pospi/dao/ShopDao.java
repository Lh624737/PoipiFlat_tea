package com.pospi.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pospi.database.DB;
import com.pospi.dto.FastGoods;
import com.pospi.dto.ShopDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2017/9/13.
 */

public class ShopDao {
    private DB shopDB;
    private String tablename = "shopMsg";

    private ShopDto dto;
    private ContentValues values;

    public ShopDao(Context context) {
        shopDB = new DB(context);//实例化数据库
    }

    //添加
    public void insert(List<ShopDto> list) {
        SQLiteDatabase db = null;
        try {
            db = shopDB.getWritableDatabase();
            clearFeedTable(db);
            for (int i = 0; i < list.size(); i++) {
                ShopDto dto = list.get(i);
                values = new ContentValues();
                values.put("shopId", dto.getShopId());
                values.put("num", dto.getNum());
                values.put("name", dto.getName());
                values.put("ad", dto.getAd());

                long result = db.insert(tablename, null, values);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }
    public void clearFeedTable(SQLiteDatabase db) {
        String sql = "DELETE FROM " + tablename + ";";
        db.execSQL(sql);
    }
    //查询
    public ShopDto select(String num){
        SQLiteDatabase db = shopDB.getReadableDatabase();
        ShopDto dto =null ;
        Cursor cursor = db.query(tablename, null, "num =?", new String[]{num}, null, null, null);
        if (cursor.moveToFirst()) {
            dto = new ShopDto();
            dto.setShopId(cursor.getString(cursor.getColumnIndex("shopId")));
            dto.setNum(cursor.getString(cursor.getColumnIndex("num")));
            dto.setName(cursor.getString(cursor.getColumnIndex("name")));
            dto.setAd(cursor.getString(cursor.getColumnIndex("ad")));
        }
        cursor.close();
        db.close();
        return dto;
    }
}
