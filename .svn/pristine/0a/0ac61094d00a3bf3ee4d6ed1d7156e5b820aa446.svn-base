package com.supcon.mes.module_olxj.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.supcon.common.com_http.util.RxSchedulers;
import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.mes.module_olxj.R;
import com.supcon.mes.module_olxj.model.bean.OLXJAreaEntity;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Flowable;


/**
 * Created by wangshizhan on 2018/3/27.
 * Email:wangshizhan@supcon.com
 */

public class OLXJAreaListAdapter extends BaseListDataRecyclerViewAdapter<OLXJAreaEntity> {

    public OLXJAreaListAdapter(Context context) {
        super(context);
    }

    public OLXJAreaListAdapter(Context context, List<OLXJAreaEntity> list) {
        super(context, list);
    }

    @Override
    protected BaseRecyclerViewHolder<OLXJAreaEntity> getViewHolder(int viewType) {
        return new ViewHolder(context);
    }


    class ViewHolder extends BaseRecyclerViewHolder<OLXJAreaEntity> {

        @BindByTag("itemAreaName")
        TextView itemAreaName;  //区域名称

        @BindByTag("itemAreaProgress")
        TextView itemAreaProgress; //区域任务进度

        @BindByTag("itemAreaDot")
        ImageView itemAreaDot;


        public ViewHolder(Context context) {
            super(context);
        }

        @Override
        protected void initBind() {
            super.initBind();
        }

        @Override
        protected void initView() {
            super.initView();
        }

        @Override
        protected void initListener() {
            super.initListener();
            itemView.setOnClickListener(v -> {
                OLXJAreaEntity xjAreaEntity = getItem(getAdapterPosition());
                        onItemChildViewClick(itemView, 0, xjAreaEntity);
                    }
            );
        }


        @Override
        protected int layoutId() {
            return R.layout.item_xj_area;
        }

        @SuppressLint("CheckResult")
        @Override
        protected void update(OLXJAreaEntity data) {

            itemAreaName.setText((data.sort + 1) + ". " + data.name);


            itemAreaName.setTextColor(Color.GRAY);
            itemAreaProgress.setTextColor(Color.GRAY);

//            if(getAdapterPosition() %2 == 1){
//                itemView.setBackgroundResource(R.drawable.sl_area_dark);
//            }
//            else{
//                itemView.setBackgroundResource(R.drawable.sl_area);
//            }

            itemView.setBackgroundResource(R.drawable.sl_area);

            AtomicInteger finishedNum = new AtomicInteger();
            Flowable.fromIterable(data.workItemEntities)
                    .compose(RxSchedulers.io_main())
                    .subscribe(xjWorkItemEntity ->{
                        if(xjWorkItemEntity.isFinished) {
                            finishedNum.getAndIncrement();
                        } },
                        throwable -> {
                        },
                        () -> {
                            itemAreaProgress.setText(String.format("%d/%d", finishedNum.get(), data.workItemEntities.size()));
                            if(finishedNum.get() == data.workItemEntities.size()) {
                                itemAreaName.setTextColor(Color.GRAY);
                                itemAreaProgress.setTextColor(Color.GRAY);
                                data.finishType = "1";
                            }else {
                                data.finishType = "0";
                                itemAreaName.setTextColor(Color.parseColor("#366CBC"));
                                itemAreaProgress.setTextColor(Color.parseColor("#366CBC"));
                            }
                        });
        }
    }
}
