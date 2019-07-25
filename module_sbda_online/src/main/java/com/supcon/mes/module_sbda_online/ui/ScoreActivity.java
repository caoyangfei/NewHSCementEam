package com.supcon.mes.module_sbda_online.ui;

import com.app.annotation.apt.Router;
import com.supcon.common.view.base.activity.BaseWebViewActivity;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.module_sbda_online.R;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/4/2
 * ------------- Description -------------
 */
@Router(Constant.Router.SCORE)
public class ScoreActivity extends BaseWebViewActivity {

    @Override
    protected int getLayoutID() {
        return R.layout.ac_score;
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
        findViewById(R.id.titleBarLayout).setBackgroundResource(R.color.themeColor);
    }
}
