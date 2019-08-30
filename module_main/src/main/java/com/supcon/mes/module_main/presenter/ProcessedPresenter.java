package com.supcon.mes.module_main.presenter;

import android.text.TextUtils;

import com.supcon.mes.middleware.EamApplication;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.module_main.model.bean.EamEntity;
import com.supcon.mes.module_main.model.bean.ProcessedEntity;
import com.supcon.mes.module_main.model.contract.EamContract;
import com.supcon.mes.module_main.model.contract.ProcessedContract;
import com.supcon.mes.module_main.model.network.MainClient;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class ProcessedPresenter extends ProcessedContract.Presenter {

    @Override
    public void workflowHandleList(int page) {
        mCompositeSubscription.add(MainClient.workflowHandleList(page, 20)
                .onErrorReturn(new Function<Throwable, CommonBAPListEntity<ProcessedEntity>>() {
                    @Override
                    public CommonBAPListEntity<ProcessedEntity> apply(Throwable throwable) throws Exception {
                        CommonBAPListEntity<ProcessedEntity> processedEntitys = new CommonBAPListEntity<>();
                        processedEntitys.errMsg = throwable.toString();
                        return processedEntitys;
                    }
                }).subscribe(new Consumer<CommonBAPListEntity<ProcessedEntity>>() {
                    @Override
                    public void accept(CommonBAPListEntity<ProcessedEntity> processedEntitys) throws Exception {
                        if (TextUtils.isEmpty(processedEntitys.errMsg)) {
                            getView().workflowHandleListSuccess(processedEntitys);
                        } else {
                            getView().workflowHandleListFailed(processedEntitys.errMsg);
                        }
                    }
                }));
    }
}
