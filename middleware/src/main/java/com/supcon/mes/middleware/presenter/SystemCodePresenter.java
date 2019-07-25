package com.supcon.mes.middleware.presenter;

import com.supcon.mes.middleware.model.bean.SystemCodeListEntity;
import com.supcon.mes.middleware.model.contract.SystemCodeContract;
import com.supcon.mes.middleware.model.network.MiddlewareHttpClient;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by wangshizhan on 2018/7/18
 * Email:wangshizhan@supcom.com
 */
public class SystemCodePresenter extends SystemCodeContract.Presenter{

    @Override
    public void getSystemCodeList(String entityCode) {
        mCompositeSubscription.add(
                MiddlewareHttpClient.querySystemCode(entityCode)
                        .onErrorReturn(new Function<Throwable, SystemCodeListEntity>() {
                            @Override
                            public SystemCodeListEntity apply(Throwable throwable) throws Exception {
                                SystemCodeListEntity systemCodeListEntity = new SystemCodeListEntity();
                                systemCodeListEntity.success = false;
                                systemCodeListEntity.errMsg = throwable.toString();

                                return systemCodeListEntity;
                            }
                        })
                        .subscribe(new Consumer<SystemCodeListEntity>() {
                            @Override
                            public void accept(SystemCodeListEntity systemCodeListEntity) throws Exception {
                                if(systemCodeListEntity.result!=null){
                                    if(systemCodeListEntity.result.size()==0){
                                        getView().getSystemCodeListFailed("获取列表为空");
                                    }
                                    else{
                                        getView().getSystemCodeListSuccess(systemCodeListEntity);
                                    }
                                }

                                else{
                                    getView().getSystemCodeListFailed(systemCodeListEntity.errMsg);
                                }
                            }
                        })
        );
    }

}
