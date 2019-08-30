package com.supcon.mes.module_main.ui.adaper;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.util.Util;
import com.supcon.mes.module_main.IntentRouter;
import com.supcon.mes.module_main.R;
import com.supcon.mes.module_main.model.bean.ProcessedEntity;
import com.supcon.mes.module_main.model.bean.WaitDealtEntity;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/7/24
 * ------------- Description -------------
 * 待办adapter
 */
public class ProcessedAdapter extends BaseListDataRecyclerViewAdapter<ProcessedEntity> {
    public ProcessedAdapter(Context context) {
        super(context);
    }

    @Override
    protected BaseRecyclerViewHolder<ProcessedEntity> getViewHolder(int viewType) {
        return new ContentViewHolder(context);
    }


    class ContentViewHolder extends BaseRecyclerViewHolder<ProcessedEntity> {

        @BindByTag("processTableNo")
        TextView processTableNo;
        @BindByTag("processName")
        CustomTextView processName;
        @BindByTag("processState")
        TextView processState;
        @BindByTag("processTime")
        CustomTextView processTime;
        @BindByTag("processStaff")
        CustomTextView processStaff;


        public ContentViewHolder(Context context) {
            super(context);
        }


        @Override
        protected void initListener() {
            super.initListener();

        }

        @Override
        protected int layoutId() {
            return R.layout.hs_item_process;
        }

        @Override
        protected void update(ProcessedEntity data) {
            processTableNo.setText(Util.strFormat2(data.TABLE_NO));
            processName.setContent(Util.strFormat2(data.NAME));
            processState.setText(Util.strFormat2(data.STATUS));
            processTime.setContent(data.CREATE_TIME != null ? DateUtil.dateFormat(data.CREATE_TIME) : "");

            if (!TextUtils.isEmpty(data.STATUS)) {
                if (data.STATUS.equals("编辑") || data.STATUS.equals("派工")) {
                    processState.setTextColor(context.getResources().getColor(R.color.gray));
                } else if (data.STATUS.equals("执行") || data.STATUS.contains("接单")) {
                    processState.setTextColor(context.getResources().getColor(R.color.yellow));
                } else if (data.STATUS.equals("验收")) {
                    processState.setTextColor(context.getResources().getColor(R.color.blue));
                } else {
                    processState.setTextColor(context.getResources().getColor(R.color.gray));
                }
            } else {
                processState.setTextColor(context.getResources().getColor(R.color.gray));
            }
        }
    }
}
