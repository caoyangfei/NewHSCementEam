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
 * 待办adapter
 */
public class WaitDealtAdapter extends BaseListDataRecyclerViewAdapter<WaitDealtEntity> {
    public WaitDealtAdapter(Context context) {
        super(context);
    }

    @Override
    protected BaseRecyclerViewHolder<WaitDealtEntity> getViewHolder(int viewType) {
        return new ContentViewHolder(context);
    }


    class ContentViewHolder extends BaseRecyclerViewHolder<WaitDealtEntity> implements View.OnClickListener {

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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WaitDealtEntity item = getItem(getAdapterPosition());
                    if (TextUtils.isEmpty(item.processkey)) {

                    }
                }
            });
        }

        @Override
        protected int layoutId() {
            return R.layout.hs_item_wait_dealt;
        }

        @Override
        protected void update(WaitDealtEntity data) {
            waitDealtEamName.setText(Util.strFormat(data.eamname));
            if (data.nextduration != null) {
                waitDealtTime.setText(Util.strFormat2(data.nextduration));
            } else {
                waitDealtTime.setText(data.excutetime != null ? DateUtil.dateFormat(data.excutetime, "yyyy-MM-dd HH:mm:ss") : "--");
            }
            waitDealtEamState.setText(Util.strFormat(data.soucretype));

            if (data.overdateflag.equals("1")) {
                waitDealtEamState.setTextColor(context.getResources().getColor(R.color.orange));
            } else {
                if (!TextUtils.isEmpty(data.state)) {
                    if (data.state.equals("编辑") || data.state.equals("派工")) {
                        waitDealtEamState.setTextColor(context.getResources().getColor(R.color.gray));
                    } else if (data.state.equals("执行") || data.state.contains("接单")) {
                        waitDealtEamState.setTextColor(context.getResources().getColor(R.color.yellow));
                    } else if (data.state.equals("验收")) {
                        waitDealtEamState.setTextColor(context.getResources().getColor(R.color.blue));
                    } else {
                        waitDealtEamState.setTextColor(context.getResources().getColor(R.color.gray));
                    }
                } else {
                    waitDealtEamState.setTextColor(context.getResources().getColor(R.color.gray));
                }
            }

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
