package com.supcon.mes.module_olxj.presenter;

import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.module_olxj.model.bean.OLXJAreaEntity;
import com.supcon.mes.module_olxj.model.contract.OLXJAreaContract;
import com.supcon.mes.module_olxj.model.network.OLXJClient;

import java.util.Objects;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by wangshizhan on 2019/4/2
 * Email:wangshizhan@supcom.com
 */
public class OLXJAreaListPresenter extends OLXJAreaContract.Presenter{
    @Override
    public void getOJXJAreaList(long groupId, int pageNo) {
        mCompositeSubscription.add(
                OLXJClient.queryOLXJArea(groupId, pageNo)
                        .onErrorReturn(new Function<Throwable, CommonBAPListEntity<OLXJAreaEntity>>() {
                            @Override
                            public CommonBAPListEntity<OLXJAreaEntity> apply(Throwable throwable) throws Exception {
                                CommonBAPListEntity<OLXJAreaEntity> commonBAPListEntity = new CommonBAPListEntity<>();
                                commonBAPListEntity.success = false;
                                commonBAPListEntity.errMsg = throwable.toString();
                                return commonBAPListEntity;
                            }
                        })
                        .subscribe(new Consumer<CommonBAPListEntity<OLXJAreaEntity>>() {
                            @Override
                            public void accept(CommonBAPListEntity<OLXJAreaEntity> olxjAreaEntityCommonBAPListEntity) throws Exception {
                                if(olxjAreaEntityCommonBAPListEntity.result!=null){
                                    Objects.requireNonNull(getView()).getOJXJAreaListSuccess(olxjAreaEntityCommonBAPListEntity);
                                }
                                else{
                                    Objects.requireNonNull(getView()).getOJXJAreaListFailed(olxjAreaEntityCommonBAPListEntity.errMsg);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Objects.requireNonNull(getView()).getOJXJAreaListFailed(throwable.toString());
                            }
                        }));
    }
}
