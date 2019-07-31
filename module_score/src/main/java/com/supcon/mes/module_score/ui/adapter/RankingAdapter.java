package com.supcon.mes.module_score.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.mes.middleware.util.Util;
import com.supcon.mes.module_score.R;
import com.supcon.mes.module_score.model.bean.ScoreStaffEntity;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/7/31
 * ------------- Description -------------
 */
public class RankingAdapter extends BaseListDataRecyclerViewAdapter<ScoreStaffEntity> {

    private int rank = -1;

    public RankingAdapter(Context context) {
        super(context);
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    protected BaseRecyclerViewHolder<ScoreStaffEntity> getViewHolder(int viewType) {
        return new ContentViewHolder(context);
    }

    class ContentViewHolder extends BaseRecyclerViewHolder<ScoreStaffEntity> {

        @BindByTag("ranking")
        TextView ranking;
        @BindByTag("name")
        TextView name;
        @BindByTag("depot")
        TextView depot;

        @BindByTag("score")
        TextView score;


        public ContentViewHolder(Context context) {
            super(context);
        }


        @Override
        protected void initListener() {
            super.initListener();

        }

        @Override
        protected int layoutId() {
            return R.layout.item_staff_ranking;
        }

        @Override
        protected void update(ScoreStaffEntity data) {
            ranking.setText(String.valueOf(getAdapterPosition() + 1));
            if (getAdapterPosition() == 0) {
            } else if (getAdapterPosition() == 1) {
            } else if (getAdapterPosition() == 2) {
            } else {
                ranking.setBackground(null);
            }

            name.setText(data.getPatrolWorker().name);
            depot.setText(Util.strFormat(data.getPatrolWorker().getMainPosition().getDepartment().name));
            score.setText(Util.big(data.score));
//            name.setText();
//            if (rank != -1 && rank == getAdapterPosition()) {
//
//            }
        }
    }
}
