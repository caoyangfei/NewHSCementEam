package com.supcon.mes.module_main.ui.adaper;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.middleware.util.Util;
import com.supcon.mes.module_main.R;
import com.supcon.mes.module_main.model.bean.WaitDealtEntity;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/7/24
 * ------------- Description -------------
 * 异常记录adapter
 */
public class AnomalyAdapter extends BaseListDataRecyclerViewAdapter<WaitDealtEntity> {
    public AnomalyAdapter(Context context) {
        super(context);
    }

    @Override
    protected BaseRecyclerViewHolder<WaitDealtEntity> getViewHolder(int viewType) {
        return new ContentViewHolder(context);
    }


    class ContentViewHolder extends BaseRecyclerViewHolder<WaitDealtEntity> implements View.OnClickListener{

        @BindByTag("waitDealtEamName")
        TextView waitDealtEamName;
        @BindByTag("waitDealtTime")
        TextView waitDealtTime;
        @BindByTag("waitDealtEamState")
        TextView waitDealtEamState;

        @BindByTag("waitDealtEntrust")
        ImageView waitDealtEntrust;


        public ContentViewHolder(Context context) {
            super(context);
        }


        @Override
        protected void initListener() {
            super.initListener();
            waitDealtEntrust.setOnClickListener(this::onClick);
        }

        @Override
        protected int layoutId() {
            return R.layout.hs_item_wait_dealt;
        }

        @Override
        protected void update(WaitDealtEntity data) {
            waitDealtEamName.setText(Util.strFormat(data.eamname));
            waitDealtTime.setText(data.excutetime != null ? DateUtil.dateFormat(data.excutetime, "yyyy-MM-dd HH:mm:ss") : "");
            waitDealtEamState.setText(Util.strFormat(data.getStaffid().name));

            if (TextUtils.isEmpty(data.processkey)) {
                waitDealtEntrust.setVisibility(View.GONE);
            } else {
                waitDealtEntrust.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View view) {
            onItemChildViewClick(view, 0, getItem(getAdapterPosition()));
        }
    }

}
