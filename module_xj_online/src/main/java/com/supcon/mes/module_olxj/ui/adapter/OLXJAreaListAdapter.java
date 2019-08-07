package com.supcon.mes.module_olxj.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.supcon.common.com_http.util.RxSchedulers;
import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.mes.module_olxj.R;
import com.supcon.mes.module_olxj.model.bean.OLXJAreaEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Flowable;


/**
 * Created by wangshizhan on 2018/3/27.
 * Email:wangshizhan@supcon.com
 */

public class OLXJAreaListAdapter extends BaseListDataRecyclerViewAdapter<OLXJAreaEntity> {

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("MM-dd HH-mm");

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

        @BindByTag("itemAreaLineTop")
        View itemAreaLineTop;  //上边线
        @BindByTag("itemAreaDot")
        ImageView itemAreaDot;  //状态图标
        @BindByTag("itemAreaLineBottom")
        View itemAreaLineBottom;  //下边线

        @BindByTag("itemAreaName")
        TextView itemAreaName;  //区域名称
        @BindByTag("itemAreaTime")
        TextView itemAreaTime;  //巡检时间
        @BindByTag("itemAreaFault")
        ImageView itemAreaFault;  //是否隐患

        @BindByTag("itemAreaProgress")
        TextView itemAreaProgress; //区域任务进度


        public ViewHolder(Context context) {
            super(context, parent);
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
            //边线显示
            itemAreaLineTop.setVisibility(View.VISIBLE);
            itemAreaLineBottom.setVisibility(View.VISIBLE);
            if (getAdapterPosition() == 0) {
                itemAreaLineTop.setVisibility(View.INVISIBLE);
            }
            if (getAdapterPosition() == getListSize() - 1) {
                itemAreaLineBottom.setVisibility(View.INVISIBLE);
            }


            itemAreaName.setText((data.sort + 1) + ". " + data.name);
            itemAreaName.setTextColor(Color.GRAY);
            itemAreaProgress.setTextColor(Color.GRAY);


            itemView.setBackgroundResource(R.drawable.sl_area);

            AtomicInteger finishedNum = new AtomicInteger();
            Flowable.fromIterable(data.workItemEntities)
                    .compose(RxSchedulers.io_main())
                    .subscribe(xjWorkItemEntity -> {
                                if (xjWorkItemEntity.isFinished) {
                                    finishedNum.getAndIncrement();
                                }
                            },
                            throwable -> {
                            },
                            () -> {
                                itemAreaProgress.setText(String.format("%d/%d", finishedNum.get(), data.workItemEntities.size()));
                                if (finishedNum.get() == data.workItemEntities.size()) {
                                    itemAreaName.setTextColor(Color.GRAY);
                                    itemAreaProgress.setTextColor(Color.GRAY);
                                    data.finishType = "1";
                                } else {
                                    data.finishType = "0";
                                    itemAreaName.setTextColor(Color.parseColor("#366CBC"));
                                    itemAreaProgress.setTextColor(Color.parseColor("#366CBC"));
                                }
                            });
        }
    }
}
