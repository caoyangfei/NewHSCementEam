package com.supcon.mes.module_main.presenter;

import com.supcon.mes.middleware.EamApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.model.bean.FastQueryCondEntity;
import com.supcon.mes.middleware.model.bean.JoinSubcondEntity;
import com.supcon.mes.middleware.util.BAPQueryParamsHelper;
import com.supcon.mes.module_main.model.bean.WaitDealtEntity;
import com.supcon.mes.module_main.model.contract.WaitDealtContract;
import com.supcon.mes.module_main.model.network.MainClient;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/7/24
 * ------------- Description -------------
 */
public class WaitDealtPresenter extends WaitDealtContract.Presenter {
    @Override
    public void getWaitDealt(int page,int pageSize) {
        FastQueryCondEntity fastQueryCond = BAPQueryParamsHelper.createJoinFastQueryCond(new HashMap<>());

        Map<String, Object> paramsName = new HashMap<>();
        paramsName.put(Constant.BAPQuery.NAME, EamApplication.getAccountInfo().staffName);
        JoinSubcondEntity joinSubcondEntity = BAPQueryParamsHelper.crateJoinSubcondEntity(paramsName, "base_staff,ID,BEAM2_PERSONWORKINFO,STAFFID");
        fastQueryCond.subconds.add(joinSubcondEntity);
        fastQueryCond.modelAlias = "personworkinfo";

        Map<String, Object> pageQueryParams = new HashMap<>();
        pageQueryParams.put("page.pageNo", page);
        pageQueryParams.put("page.pageSize", pageSize);
        pageQueryParams.put("page.maxPageSize", 500);

        mCompositeSubscription.add(MainClient.getWaitDealt(fastQueryCond, pageQueryParams)
                .onErrorReturn(new Function<Throwable, CommonBAPListEntity<WaitDealtEntity>>() {
                    @Override
                    public CommonBAPListEntity<WaitDealtEntity> apply(Throwable throwable) throws Exception {
                        CommonBAPListEntity<WaitDealtEntity> waitDealtEntity = new CommonBAPListEntity<>();
                        waitDealtEntity.errMsg = throwable.toString();
                        return waitDealtEntity;
                    }
                }).subscribe(new Consumer<CommonBAPListEntity<WaitDealtEntity>>() {
                    @Override
                    public void accept(CommonBAPListEntity<WaitDealtEntity> waitDealtEntity) throws Exception {
                        if (waitDealtEntity.result != null) {
                            getView().getWaitDealtSuccess(waitDealtEntity);
                        } else {
                            getView().getWaitDealtFailed(waitDealtEntity.errMsg);
                        }
                    }
                }));
    }
}
