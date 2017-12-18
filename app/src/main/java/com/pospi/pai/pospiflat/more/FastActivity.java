package com.pospi.pai.pospiflat.more;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.pospi.dao.FastDao;
import com.pospi.dao.GoodsDao;
import com.pospi.dto.FastGoods;
import com.pospi.dto.GoodsDto;
import com.pospi.pai.pospiflat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2017/9/12.
 */

public class FastActivity extends Activity implements View.OnClickListener {

    private ImageView back;
    private Button btnSave;
    private EditText no;
    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fasr);
        back = (ImageView) findViewById(R.id.fastBack);
        btnSave = (Button) findViewById(R.id.btnSave);
        no = (EditText) findViewById(R.id.fastNo);
        name = (EditText) findViewById(R.id.fastName);
        back.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (no.getText().toString().trim().length() > 5) {
                    judge(no.getText().toString().trim());
                }
            }
        });
    }

    private void judge(String s) {
        GoodsDto dto = new GoodsDao(this).selectGoods(s);
        if (dto == null) {
            return;
        }
        name.setText(dto.getName());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fastBack:
                finish();
                break;
            case R.id.btnSave:
                save();
                break;
        }
    }

    private void save() {
        String gNo = no.getText().toString().trim();
        String gName = name.getText().toString().trim();
        List<FastGoods> list = new ArrayList<>();
        if (gNo.length() == 0) {
            Toast.makeText(this, "请补全信息", Toast.LENGTH_SHORT).show();
            return;
        }
        GoodsDto dto = new GoodsDao(this).selectGoods(gNo);
        if (dto == null) {
            Toast.makeText(this, "商品不存在", Toast.LENGTH_SHORT).show();
        } else {

            FastGoods fg = new FastGoods();
            fg.setNo(gNo);
            fg.setName(gName);
            list.add(fg);
            new FastDao(this).insert(list);
            no.setText("");
            name.setText("");
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }

    }

}
