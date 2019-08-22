package com.supcon.mes.module_main.presenter;

import com.supcon.mes.middleware.model.bean.CommonEntity;
import com.supcon.mes.module_main.model.bean.EamXJEntity;
import com.supcon.mes.module_main.model.contract.EamDetailContract;
import com.supcon.mes.module_main.model.network.MainClient;

import java.util.Map;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/7/29
 * ------------- Description -------------
 */
public class EamDetailPresenter extends EamDetailContract.Presenter {
    @Override
    public void getEamScore(long deviceID) {
        mCompositeSubscription.add(MainClient.getEamScore(deviceID)
                .onErrorReturn(new Function<Throwable, CommonEntity<String>>() {
                    @Override
                    public CommonEntity<String> apply(Throwable throwable) throws Exception {
                        CommonEntity<String> waitDealtEntity = new CommonEntity<>();
                        waitDealtEntity.errMsg = throwable.toString();
                        return waitDealtEntity;
                    }
                }).subscribe(new Consumer<CommonEntity<String>>() {
                    @Override
                    public void accept(CommonEntity<String> result) throws Exception {
                        if (result.success) {
                            getView().getEamScoreSuccess(result);
                        } else {
                            getView().getEamScoreFailed(result.errMsg);
                        }
                    }
                }));
    }

    @Override
    public void createTempPotrolTaskByEam(Map<String, Object> paramMap) {
        mCompositeSubscription.add(MainClient.createTempPotrolTaskByEam(paramMap)
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
