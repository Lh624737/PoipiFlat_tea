package com.pospi.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pospi.database.DB;
import com.pospi.dto.PopDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2017/9/4.
 *
 */

public class PopDao {
    private DB popdb;
    private String tablename = "popTime";

    public PopDao(Context context) {
        popdb = new DB(context);
    }
    public void clearFeedTable(SQLiteDatabase db) {
        String sql = "DELETE FROM " + tablename + ";";
        db.execSQL(sql);
    }
    //添加促销单到数据库
    public void addPop(List<PopDto> dtos){
        SQLiteDatabase db = null;
        try {
            db = popdb.getWritableDatabase();
            clearFeedTable(db);
            for (PopDto dto : dtos) {
                ContentValues values = new ContentValues();
                values.put("sid", dto.getId());
                values.put("stime", dto.getStime());
                values.put("etime", dto.getEtime());
                values.put("detail", dto.getDetail());
                values.put("type",dto.getType());
                long result = db.insert(tablename, null, values);
                Log.i("goodsDtosInfo", "result=" + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }
    //获取数据库中促销单
    public List<PopDto> getPop(){
        List<PopDto> list = new ArrayList<>();
        SQLiteDatabase db = popdb.getReadableDatabase();
        Cursor cursor = db.query(tablename, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            PopDto dto = new PopDto();
            dto.setId(cursor.getString(cursor.getColumnIndex("sid")));
            dto.setStime(cursor.getString(cursor.getColumnIndex("stime")));
            dto.setEtime(cursor.getString(cursor.getColumnIndex("etime")));
            dto.setType(cursor.getString(cursor.getColumnIndex("type")));
            dto.setDetail(cursor.getString(cursor.getColumnIndex("detail")));
            list.add(dto);
        }
        cursor.close();
        db.close();
        return list;
    }
}
