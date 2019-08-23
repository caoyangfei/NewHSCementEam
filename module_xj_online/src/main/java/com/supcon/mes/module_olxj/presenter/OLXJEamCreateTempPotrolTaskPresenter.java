package com.supcon.mes.module_olxj.presenter;

import com.supcon.mes.middleware.model.bean.CommonEntity;
import com.supcon.mes.module_olxj.model.bean.EamXJEntity;
import com.supcon.mes.module_olxj.model.contract.OLXJEamCreateTempPotrolTaskContract;
import com.supcon.mes.module_olxj.model.network.OLXJClient;

import java.util.Map;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class OLXJEamCreateTempPotrolTaskPresenter extends OLXJEamCreateTempPotrolTaskContract.Presenter {
    @Override
    public void createTempPotrolTaskByEam(Map<String, Object> paramMap) {
        mCompositeSubscription.add(OLXJClient.createTempPotrolTaskByEam(paramMap)
                .onErrorReturn(new Function<Throwable, CommonEntity<EamXJEntity>>() {
                    @Override
                    public CommonEntity<EamXJEntity> apply(Throwable throwable) throws Exception {
                        CommonEntity<EamXJEntity> waitDealtEntity = new CommonEntity<>();
                        waitDealtEntity.errMsg = throwable.toString();
                        return waitDealtEntity;
                    }
                }).subscribe(new Consumer<CommonEntity<EamXJEntity>>() {
                    @Override
                    public void accept(CommonEntity<EamXJEntity> result) throws Exception {
                        if (result.success) {
                            getView().createTempPotrolTaskByEamSuccess(result);
                        } else {
                            getView().createTempPotrolTaskByEamFailed(result.errMsg);
                        }
                    }
                }));
    }
}
