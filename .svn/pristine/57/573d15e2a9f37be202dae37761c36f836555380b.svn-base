package com.supcon.mes.module_login.presenter;


import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.supcon.common.view.util.LogUtil;
import com.supcon.mes.module_login.model.bean.WorkEntity;
import com.supcon.mes.module_login.model.bean.WorkNumEntity;
import com.supcon.mes.module_login.model.contract.WorkContract;
import com.supcon.mes.module_login.model.network.LoginHttpClient;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by wangshizhan on 2017/8/15.
 */
public class WorkPresenter extends WorkContract.Presenter {

    @Override
    public void getAllPendings(String staffName) {
//        getView().getPendingsFailed("not work");

        mCompositeSubscription.add(
                LoginHttpClient.getAllPendings()
                        .onErrorReturn(new Function<Throwable, String>() {
                            @Override
                            public String apply(Throwable throwable) throws Exception {
                                return "http error";
                            }
                        })
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                LogUtil.d("s:"+s);

                            }
                        })
        );



        getView().getAllPendingsSuccess(new WorkEntity());
    }

    @SuppressLint("CheckResult")
    @Override
    public void getPendingsByModule(List<String> pendingParams) {

        for (String url : pendingParams){


            mCompositeSubscription.add(
                    LoginHttpClient.getPendingsByModule(url)
                            .onErrorReturn(new Function<Throwable, WorkNumEntity>() {
                                @Override
                                public WorkNumEntity apply(Throwable throwable) throws Exception {
                                    WorkNumEntity entity = new WorkNumEntity();
                                    entity.success = false;
                                    entity.errMsg = throwable.toString();
                                    return entity;
                                }
                            })
                            .subscribe(new Consumer<WorkNumEntity>() {
                                @Override
                                public void accept(WorkNumEntity workNumEntity) throws Exception {

                                    if(!TextUtils.isEmpty(workNumEntity.errMsg)){
                                        getView().getPendingsByModuleFailed(workNumEntity.errMsg);
                                    }
                                    else{
                                        workNumEntity.url = url;
                                        getView().getPendingsByModuleSuccess(workNumEntity);
                                    }
                                }
                            }));

        }

    }

}
