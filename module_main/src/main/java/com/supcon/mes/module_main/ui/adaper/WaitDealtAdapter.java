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
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.util.Util;
import com.supcon.mes.module_main.IntentRouter;
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
                        if (item.dataid == null || TextUtils.isEmpty(item.soucretype)) {
                            ToastUtils.show(context, "未查询到当前单据状态!");
                            return;
                        }
                        if (!TextUtils.isEmpty(item.istemp) && item.soucretype.equals("巡检提醒")) {
                            if (item.istemp.equals("1")) {
                                IntentRouter.go(context, Constant.Router.LSXJ_LIST);
                            } else {
                                IntentRouter.go(context, Constant.Router.JHXJ_LIST);
                            }
                        } else {
                            if (TextUtils.isEmpty(item.peroidtype)) {
                                ToastUtils.show(context, "未查询到当前单据周期类型!");
                                return;
                            }
                            Bundle bundle = new Bundle();
                            bundle.putLong(Constant.IntentKey.WARN_ID, item.dataid);
                            bundle.putString(Constant.IntentKey.PROPERTY, item.peroidtype);
                            if (item.soucretype.equals("润滑提醒")) {
                                IntentRouter.go(context, Constant.Router.LUBRICATION_EARLY_WARN, bundle);
                            } else if (item.soucretype.equals("零部件提醒")) {
                                IntentRouter.go(context, Constant.Router.SPARE_EARLY_WARN, bundle);
                            } else if (item.soucretype.equals("维保提醒")) {
                                IntentRouter.go(context, Constant.Router.MAINTENANCE_EARLY_WARN, bundle);
                            }
                        }

                    } else {
                        if (!TextUtils.isEmpty(item.tableno)) {
                            Bundle bundle = new Bundle();
                            bundle.putString(Constant.IntentKey.TABLENO, item.tableno);
                            if (item.processkey.equals("work")) {
                                if (!TextUtils.isEmpty(item.openurl)) {
                                    switch (item.openurl) {
                                        case Constant.WxgdView.RECEIVE_OPEN_URL:
                                            IntentRouter.go(context, Constant.Router.WXGD_RECEIVE, bundle);
                                            break;
                                        case Constant.WxgdView.DISPATCH_OPEN_URL:
                                            IntentRouter.go(context, Constant.Router.WXGD_DISPATCHER, bundle);
                                            break;
                                        case Constant.WxgdView.EXECUTE_OPEN_URL:
                                            IntentRouter.go(context, Constant.Router.WXGD_EXECUTE, bundle);
                                            break;
                                        case Constant.WxgdView.ACCEPTANCE_OPEN_URL:
                                            IntentRouter.go(context, Constant.Router.WXGD_ACCEPTANCE, bundle);
                                            break;
                                    }
                                } else {
                                    ToastUtils.show(context, "未查询到工单状态状态!");
                                }
                            } else if (item.processkey.equals("faultInfoFW")) {
                                IntentRouter.go(context, Constant.Router.YH_EDIT, bundle);
                            }
                        }
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
