package com.pospi.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pospi.dao.ModifiedDao;
import com.pospi.dto.ModifiedDto;
import com.pospi.pai.pospiflat.R;
import com.pospi.pai.pospiflat.util.ModifiedDialogActivity;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class ModifiedFragment extends Fragment {
    public interface OnFragmentClick {
        void onFragmentClick(ModifiedDto modifiedDto);//value为传入的值
    }

    private String modifiedgroup_sid;
    private Context context;
    private List<ModifiedDto> dtos;
    private ModifiedDao modifiedDao;
    private List<String> modifie_names;
    private OnFragmentClick listener;
    public static Activity activity;
    private RecyclerAdapter mRecyclerAdapter;

    public ModifiedFragment(Context context, String modifiedgroup_sid) {
        this.context = context;
        this.modifiedgroup_sid = modifiedgroup_sid;
        modifiedDao = new ModifiedDao(context);
        modifie_names = new ArrayList<>();
        Log.i("modifiedgroup_sid", "GroupSid=" + modifiedgroup_sid);
        dtos = modifiedDao.findModifiedByGroupSid(modifiedgroup_sid);
        Log.i("Modified_namedtos: ", dtos.size() + "");
        for (int i = 0; i < dtos.size(); i++) {
            String modifie_name = dtos.get(i).getName();
            Log.i("Modified_name: ", modifie_name);
            modifie_names.add(modifie_name);
        }
    }

    @Override
    public void onAttach(Context context) {
        listener = (OnFragmentClick) context;
        activity = (Activity) context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modified, container, false);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //1.配置RecyclerView,
        // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        //创建线性布局
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        //垂直方向
        mLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        //给RecyclerView设置布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager);
        //创建适配器，并且设置
        mRecyclerAdapter = new RecyclerAdapter(context, modifie_names, modifiedgroup_sid);
        mRecyclerAdapter.setOnItemClickLitener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String name, String modifiedgroup_sid) {
//                vip_name = name;
                ModifiedDto modifiedDto = new ModifiedDto();
                modifiedDto.setName(name);
                modifiedDto.setGroupSid(modifiedgroup_sid);
                if (listener != null) {
                    listener.onFragmentClick(modifiedDto);
                    mRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });
        mRecyclerView.setAdapter(mRecyclerAdapter);//设置适配器
        return view;
    }

    public static List<ModifiedDto> modifiedDtos;

    static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        public interface OnItemClickListener {
            void onItemClick(View view, String name, String modifiedgroup_sid);
        }

        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickLitener(OnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        private String modifiedgroup_sid;
        private Context context;
        private List<String> modifieds;

        public RecyclerAdapter(Context context, List<String> modifieds, String modifiedgroup_sid) {
            this.context = context;
            this.modifieds = modifieds;
            this.modifiedgroup_sid = modifiedgroup_sid;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_modified, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.tv_modified.setText(modifieds.get(position));//*2\n￥6.00
            if (modifiedDtos != null) {
                for (ModifiedDto modifiedDto1 : modifiedDtos) {
                    if (modifiedDto1.getName().equals(modifieds.get(position))) {
                        holder.image_check.setVisibility(View.VISIBLE);
                    }else{
                        holder.image_check.setVisibility(View.INVISIBLE);
                    }
                }
            }
            if (mOnItemClickListener != null) {
                holder.tv_modified.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(holder.tv_modified, modifieds.get(position), modifiedgroup_sid);
                        modifiedDtos = ((ModifiedDialogActivity) activity).getModifiedDtos();
                        notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return modifieds.size();
        }

        //自定义的ViewHolder，持有每个Item的的所有界面元素
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_modified;
            ImageView image_check;

            public ViewHolder(View view) {
                super(view);
                tv_modified = (TextView) view.findViewById(R.id.tv_modified);
                image_check = (ImageView) view.findViewById(R.id.image_check);
            }
        }
    }
}
