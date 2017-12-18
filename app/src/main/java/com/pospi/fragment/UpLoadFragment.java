package com.pospi.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pospi.dao.OrderDao;
import com.pospi.dao.PayWayDao;
import com.pospi.dto.OrderDto;
import com.pospi.dto.PayWayDto;
import com.pospi.http.UpLoadToServel;
import com.pospi.pai.pospiflat.R;
import com.pospi.util.App;
import com.pospi.util.UpdateOrder;
import com.pospi.util.constant.URL;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpLoadFragment extends Fragment {

    @Bind(R.id.btn_manual_upload)
    Button btnManualUpload;
//    @Bind(R.id.btn_manual_erp)
//    Button btnManualErp;
    @Bind(R.id.pb_upload)
    ProgressBar pbUpload;
    @Bind(R.id.layout_upload)
    RelativeLayout layoutUpload;
    @Bind(R.id.text_upload)
    TextView textUpload;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manual_upload, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.btn_manual_upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_manual_upload:
                PayWayDto payWayDto = null;
                List<OrderDto> orderDtos = new OrderDao(App.getContext()).findNOUpLoad();
                List<PayWayDto> payWayDtos = new PayWayDao(App.getContext()).findAllPayWay();
//        Log.i(TAG, "UpLoadService,大小为：" + orderDtos.size());
                layoutUpload.setVisibility(View.VISIBLE);
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
                            new UpLoadToServel(App.getContext()).uploadOrderToServer(orderDtos.get(j), payWayDto, "2", App.getContext(), UpdateOrder.findInfo);
                        } else {
                            new UpLoadToServel(App.getContext()).uploadOrderToServer(orderDtos.get(j), payWayDto, "1", App.getContext(), UpdateOrder.findInfo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                int num = size - new OrderDao(App.getContext()).findNOUpLoad().size();
//                String str = "上传完成！一共上传" + size + "笔订单，上传成功：" + num + "笔，上传失败：" + (size - num) + "笔";
//                show(str);
                Toast.makeText(getActivity(), "正在上传。。。", Toast.LENGTH_SHORT).show();
                textUpload.setText("上传完成！");
                layoutUpload.setVisibility(View.INVISIBLE);
                break;
//            case R.id.btn_manual_erp://        Log.i(TAG, "onStartCommand()");
//                List<OrderDto> erp_orderDtos = new OrderDao(App.getContext()).findOrderERPNOUpLoad();
//                List<PayWayDto> erp_payWayDtos = new PayWayDao(App.getContext()).findAllPayWay();
//                PayWayDto erp_payWayDto = new PayWayDto();
////        Log.i(TAG, "ERPService,大小为：" + orderDtos.size());
//
//                layoutUpload.setVisibility(View.VISIBLE);
//                int erp_size = erp_orderDtos.size();
//                for (int j = 0; j < erp_size; j++) {
//                    for (int i = 0; i < erp_payWayDtos.size(); i++) {
//                        if (erp_orderDtos.get(j).getStatus() == erp_payWayDtos.get(i).getPayType1()) {
//                            erp_payWayDto = erp_payWayDtos.get(i);
//                        }
//                    }
//                    try {
//                        if (erp_orderDtos.get(j).getOrderType() == URL.ORDERTYPE_REFUND) {
//                            new UpLoadToServel(App.getContext()).postWebServer(erp_orderDtos.get(j), erp_payWayDto.getName(), false);
//                        } else {
//                            new UpLoadToServel(App.getContext()).postWebServer(erp_orderDtos.get(j), erp_payWayDto.getName(), true);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
////                int num1 = erp_size - new OrderDao(App.getContext()).findERPNOUpLoad().size();
////                String str1 = "上传完成！一共上传" + erp_size + "笔订单，上传成功：" + num1 + "笔，上传失败：" + (erp_size - num1) + "笔.";
////                show(str1);
//                Toast.makeText(getActivity(), "上传完成！", Toast.LENGTH_SHORT).show();
//                textUpload.setText("上传完成！");
//                layoutUpload.setVisibility(View.INVISIBLE);
//                break;
        }
    }

    public void show(String srt) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(srt)
                .setTitle("提示")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

}
