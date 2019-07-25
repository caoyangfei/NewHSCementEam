package com.supcon.mes.module_txl.ui;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.apt.Router;
import com.bumptech.glide.Glide;
import com.supcon.common.view.base.activity.BasePresenterActivity;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.mbap.view.CustomCircleTextImageView;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.util.RequestOptionUtil;
import com.supcon.mes.module_txl.R;
import com.supcon.mes.middleware.model.bean.TxlEntity;

import java.io.File;

/**
 * @Author xushiyun
 * @Create-time 7/8/19
 * @Pageage com.supcon.mes.module_txl.ui
 * @Project eamtest
 * @Email ciruy.victory@gmail.com
 * @Related-classes
 * @Desc
 */
@Router(Constant.Router.TXL_VIEW)
public class TxlViewActivity extends BasePresenterActivity {
    
    @BindByTag("ivBack")
    ImageView ivBack;
    @BindByTag("tvBack")
    TextView tvBack;
    @BindByTag("usrInfo")
    LinearLayout usrInfo;
    @BindByTag("tvTelephone")
    CustomTextView tvTelephone;
    @BindByTag("phone")
    CustomTextView phone;
    @BindByTag("otherPhone")
    CustomTextView otherPhone;
    @BindByTag("email")
    CustomTextView email;
    @BindByTag("master")
    CustomTextView master;
    @BindByTag("department")
    CustomTextView department;
    @BindByTag("flow")
    CustomTextView flow;
    @BindByTag("userName")
    TextView userName;
    @BindByTag("userWork")
    TextView userWork;
    private TxlEntity mData;
    @Override
    protected int getLayoutID() {
        return R.layout.activity_txl_view;
    }
    
    @Override
    protected void onInit() {
        super.onInit();
        mData = (TxlEntity) getIntent().getSerializableExtra(Constant.IntentKey.TXL_ENTITY);
    }
    
    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.txl_view_title);
    }
    
    @Override
    protected void initListener() {
        super.initListener();
        ivBack.setOnClickListener(v -> back());
        tvBack.setOnClickListener(v -> back());
    }
    
    @Override
    protected void initData() {
        super.initData();
//        tvTelephone.setContent(mData.getClass());
        userName.setText(mData.getStaffName());
        userWork.setText(mData.getStaffWork());
        tvTelephone.setContent((String) mData.getMOBILE());
        phone.setContent((String) mData.getMOBILE());
        otherPhone.setContent((String) mData.getMOBILE());
        email.setContent((String) mData.getEMAIL());
        master.setContent((String) mData.getRZSJ());
        department.setContent(mData.getDepartmentName());
        flow.setContent("0");
    
        File file = new File(Constant.IMAGE_SAVE_PATH +"TXL_"+ String.valueOf(mData.getStaffId())+".jpg");
        if(file.exists()) {
            CustomCircleTextImageView customCircleTextImageView = findViewById(R.id.userIcon);
            Glide.with(customCircleTextImageView.getContext()).load(file)
                    .apply(RequestOptionUtil.getEamRequestOptions(customCircleTextImageView.getContext()))
                    .into(customCircleTextImageView);
        }
    }
}
