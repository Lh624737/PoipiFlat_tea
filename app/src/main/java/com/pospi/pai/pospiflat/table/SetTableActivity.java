package com.pospi.pai.pospiflat.table;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pospi.adapter.TableGridAdapter;
import com.pospi.dao.OrderDao;
import com.pospi.dao.TableDao;
import com.pospi.dto.OrderDto;
import com.pospi.dto.Tabledto;
import com.pospi.pai.pospiflat.R;
import com.pospi.pai.pospiflat.base.BaseActivity;
import com.pospi.util.constant.tableinfo.TableStatusConstance;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetTableActivity extends BaseActivity {
    @Bind(R.id.gridView)
    GridView gridView;
    @Bind(R.id.set_table_number)
    TextView setTableNumber;
    @Bind(R.id.iv_subtract)
    ImageView ivSubtract;
    @Bind(R.id.et_person_number)
    EditText etPersonNumber;
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    @Bind(R.id.table_more)
    Button tableMore;
    @Bind(R.id.table_sure)
    Button tableSure;
    @Bind(R.id.num_unused)
    TextView numUnused;
    @Bind(R.id.num_used)
    TextView numUsed;
    @Bind(R.id.num_reserved)
    TextView numReserved;
    @Bind(R.id.num_cleaning)
    TextView numCleaning;

    private TableDao tableDao;
    private OrderDao orderDao;
    private TableGridAdapter adapter;

    private List<Tabledto> tabledtos;
    private List<OrderDto> orderDtos;

    private int eatingNumber;

    public static final String TableName = "tableName";
    public static final String EatingNumber = "eatingNumber";
    public static final String TableID = "tableId";
    public static final String TableOrder = "hasorder";

    private int posion = -2;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    gridView.setAdapter(adapter);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_table);
        ButterKnife.bind(this);
        init();

        new Thread(new Runnable() {
            @Override
            public void run() {
                selectDataFromDb();
                Message msg = Message.obtain();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }).start();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setTableNumber.setText(tabledtos.get(position).getName());
                etPersonNumber.setText(String.valueOf(1));
                posion = position;
                eatingNumber = 1;
                if (tabledtos.get(position).getStatus() == TableStatusConstance.Status_Dining) {
                    OrderDto orderDto = orderDao.selectTableHasOrder(tabledtos.get(position).getSid());
                    if (orderDto != null) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(TableOrder, orderDto);
                        intent.putExtras(bundle);
                        intent.putExtra(TableName, tabledtos.get(position).getName());
                        Log.i("TableName", TableName);
                        Log.i("orderInfo", orderDto.getOrder_info());
                        SetTableActivity.this.setResult(2, intent);
                        finish();
                    }

                }
            }
        });

    }

    //没有餐桌名字的时候就会默认添加一条数据
    public void giveDefaultName() {
        for (int i = 0; i < tabledtos.size(); i++) {
            if (tabledtos.get(i).getName().trim().isEmpty()) {
                tabledtos.get(i).setName("餐桌" + i);
            }
        }

    }

    public void init() {
        tableDao = new TableDao(this);
        orderDao = new OrderDao(this);
        eatingNumber = 1;
    }

    //从数据库中查询数据
    public void selectDataFromDb() {
        tabledtos = tableDao.findTableInfo();
        orderDtos = orderDao.selectTableOrder();
        for (int i = 0; i < tabledtos.size(); i++) {
            Tabledto good1 = tabledtos.get(i);
            String good_name1 = good1.getSid();
            for (int j = tabledtos.size() - 1; j > i; j--) {
                Tabledto good2 = tabledtos.get(j);
                String good_name2 = good2.getSid();
                if (good_name1.equals(good_name2)) {
                    tabledtos.remove(good2);
                }
            }
        }

        for (int i = 0; i < tabledtos.size(); i++) {
            for (int j = 0; j < orderDtos.size(); j++) {
                if (tabledtos.get(i).getSid().equals(orderDtos.get(j).getTableNumber())) {
                    tabledtos.get(i).setStatus(TableStatusConstance.Status_Dining);
                }
            }
        }
        giveDefaultName();
        adapter = new TableGridAdapter(getApplicationContext(), orderDtos, tabledtos);
    }

    @OnClick({R.id.iv_subtract, R.id.iv_add, R.id.table_more, R.id.table_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_subtract:
                if (eatingNumber > 0) {
                    eatingNumber -= 1;
                }
                etPersonNumber.setText(String.valueOf(eatingNumber));
                break;
            case R.id.iv_add:
                eatingNumber += 1;
                etPersonNumber.setText(String.valueOf(eatingNumber));
                break;
            case R.id.table_more:
                break;
            case R.id.table_sure:
                if (eatingNumber > 0) {
                    if (posion >= 0) {
                        Intent intent = new Intent();
                        Log.i("TableName", tabledtos.get(posion).getName());
                        intent.putExtra(TableName, tabledtos.get(posion).getName());
                        intent.putExtra(EatingNumber, eatingNumber);
                        intent.putExtra(TableID, tabledtos.get(posion).getSid());
                        this.setResult(1, intent);
                        finish();
                    }
                }
                break;
        }
    }
}