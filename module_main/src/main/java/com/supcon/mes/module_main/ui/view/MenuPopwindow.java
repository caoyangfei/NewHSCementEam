package com.supcon.mes.module_main.ui.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.mes.module_main.R;

import java.util.List;

/**
 * @author yangfei.cao
 * @ClassName depot
 * @date 2018/8/13
 * ------------- Description -------------
 */
public class MenuPopwindow extends PopupWindow implements PopupWindow.OnDismissListener {
    private View conentView;
    private RecyclerView lvContent;
    private final MyAdapter myAdapter;
    private List<MenuPopwindowBean> beans;

    public static final int left = 0;
    public static final int right = 1;
    private PopwinBackView popwinBackView;
    private Activity context;

    public MenuPopwindow(Activity context, List<MenuPopwindowBean> list) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        conentView = inflater.inflate(R.layout.menu_popup_window, null);
        lvContent = conentView.findViewById(R.id.lv_toptitle_menu);
        popwinBackView = conentView.findViewById(R.id.pv_triangle);
        lvContent.setLayoutManager(new GridLayoutManager(context, 2));
        myAdapter = new MyAdapter(context);
        myAdapter.setList(list);
        lvContent.setAdapter(myAdapter);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w * 2 / 3);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);
        beans = list;
        setOnDismissListener(this::onDismiss);
    }


    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        super.setOnDismissListener(onDismissListener);
    }

    @Override
    public void onDismiss() {
        changeWindowAlfa(1f);//pop消失，透明度恢复
    }

    class MyAdapter extends BaseListDataRecyclerViewAdapter<MenuPopwindowBean> {


        public MyAdapter(Context context) {
            super(context);
        }

        @Override
        protected BaseRecyclerViewHolder<MenuPopwindowBean> getViewHolder(int viewType) {
            return new ViewHolder(context);
        }

        class ViewHolder extends BaseRecyclerViewHolder<MenuPopwindowBean> {

            @BindByTag("menuTip")
            TextView menuTip;
            @BindByTag("menuName")
            TextView menuName;
            @BindByTag("menuNum")
            TextView menuNum;


            public ViewHolder(Context context) {
                super(context, parent);
            }

            @Override
            protected int layoutId() {
                return R.layout.menu_popup_window_item;
            }

            @Override
            protected void initView() {
                super.initView();
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemChildViewClick(itemView, 0, getItem(getLayoutPosition()));
                    }
                });
            }

            @Override
            protected void initListener() {
                super.initListener();
                itemView.setOnClickListener(v -> onItemChildViewClick(itemView, 0, getItem(getLayoutPosition())));
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void update(MenuPopwindowBean data) {
                menuName.setText(data.getName());
                if (data.getNum() > 0) {
                    menuNum.setVisibility(View.VISIBLE);
                    menuNum.setText(String.valueOf(data.getNum()));
                    menuTip.setSelected(true);
                } else {
                    menuNum.setVisibility(View.INVISIBLE);
                    menuTip.setSelected(false);
                }
            }
        }
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent, int gravity, int mark) {
        if (!isShowing()) {
            changeWindowAlfa(0.7f);//改变窗口透明度
            parent.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int[] location = new int[2];
            parent.getLocationOnScreen(location);
            //优先进行计算
            this.getContentView().measure(0, 0);
            //之后通过此方法回去就可以了
            int measuredHeight = this.getContentView().getMeasuredHeight();
            int measuredWidth = this.getContentView().getMeasuredWidth();
            int width = context.getWindowManager().getDefaultDisplay().getWidth();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) popwinBackView.getLayoutParams();

            int margin;
            int x;
            if (gravity == left) {

                if (mark == 1) {
                    margin = measuredWidth / 10;
                    x = parent.getWidth() + location[0] - measuredWidth * 9 / 10;
                } else {
                    margin = measuredWidth / 6;
                    x = parent.getWidth() + location[0] - measuredWidth * 5 / 6;
                }
                this.showAtLocation(parent, Gravity.NO_GRAVITY, x, location[1] - measuredHeight);
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutParams.rightMargin = margin;
            } else if (gravity == right) {
                if (mark == 1) {
                    margin = measuredWidth / 10;
                    x = location[0] + parent.getWidth() / 2 - measuredWidth / 10;
                } else {
                    margin = measuredWidth / 6;
                    x = location[0] + parent.getWidth() / 2 - measuredWidth / 6;
                }
                this.showAtLocation(parent, Gravity.NO_GRAVITY, x, location[1] - measuredHeight);
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.leftMargin = margin;
            }
            popwinBackView.setLayoutParams(layoutParams);
        } else {
            this.dismiss();
        }
    }

    public void refreshList(List<MenuPopwindowBean> list) {
        beans = list;
        myAdapter.setList(list);
        myAdapter.notifyDataSetChanged();
    }

    public List<MenuPopwindowBean> getBeans() {
        return beans;
    }

    /*
       更改屏幕窗口透明度
    */
    void changeWindowAlfa(float alfa) {
        WindowManager.LayoutParams params = context.getWindow().getAttributes();
        params.alpha = alfa;
        context.getWindow().setAttributes(params);
    }
}

