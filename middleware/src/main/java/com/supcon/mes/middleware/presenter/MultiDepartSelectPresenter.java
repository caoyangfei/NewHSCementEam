package com.supcon.mes.middleware.presenter;

import android.annotation.SuppressLint;

import com.supcon.common.com_http.util.RxSchedulers;
import com.supcon.mes.middleware.EamApplication;
import com.supcon.mes.middleware.model.bean.AreaMultiStageEntity;
import com.supcon.mes.middleware.model.bean.DepartmentInfo;
import com.supcon.mes.middleware.model.bean.DepartmentInfoDao;
import com.supcon.mes.middleware.model.bean.TxlEntityDao;
import com.supcon.mes.middleware.model.contract.MultiDepartSelectContract;

import java.util.ArrayList;

import io.reactivex.Flowable;

/**
 * @Author xushiyun
 * @Create-time 7/22/19
 * @Pageage com.supcon.mes.middleware.presenter
 * @Project eamtest
 * @Email ciruy.victory@gmail.com
 * @Related-classes
 * @Desc
 */
public class MultiDepartSelectPresenter extends MultiDepartSelectContract.Presenter {
    @Override
    public void getDepartmentInfoList(String id) {
        mCompositeSubscription.add(Flowable.just(EamApplication.dao().getDepartmentInfoDao().queryBuilder()
//                .where(DepartmentInfoDao.Properties.LayRec.like("?" + id + "?"))
                .orderAsc(DepartmentInfoDao.Properties.LayRec).list())
                .map(areas -> {
                    AreaMultiStageEntity rootMultiStageEntity = new AreaMultiStageEntity();
                    DepartmentInfo rootArea = new DepartmentInfo();
                    rootArea.layRec = "";
                    rootArea.name = "集团";
                    AreaMultiStageEntity currentNode = rootMultiStageEntity;
                    rootMultiStageEntity.setCurrentEntity(rootArea);
                    for (int i = 0; i < areas.size(); i++) {
                        DepartmentInfo area = areas.get(i);
                        currentNode = insertNode(currentNode, area);
                        //Todo:这行代码的效率极其低下,找时间还需要改改
                        insertDirectBelongsNode(currentNode);
                    }
                    return rootMultiStageEntity;
                })
                .compose(RxSchedulers.io_main())
                .subscribe(areas -> getView().getDepartmentInfoListSuccess(areas)));
    }
    
    /**
     * 创建直属部门信息,bap和e-message的信息组织结构存在着一些不同,e-message的配置中所有人员都处于末节点中
     *
     * @param currentNode
     */
    @SuppressLint("CheckResult")
    private void insertDirectBelongsNode(AreaMultiStageEntity currentNode) {
        Flowable.fromIterable(
                EamApplication.dao()
                        .getTxlEntityDao().queryBuilder()
                        .where(TxlEntityDao.Properties.FULLPATHNAME.eq(currentNode.currentEntity.fullPathName))
                        .orderAsc(TxlEntityDao.Properties.NAME)
                        .list())
                .subscribe(txlEntity -> {
                    if (currentNode.getActualChildNodeList() == null)
                        currentNode.setChildNodeList(new ArrayList<>());
                    if (currentNode.getActualChildNodeList().size() == 0 || !currentNode.getActualChildNodeList().get(0).getInfo().endsWith("-direct")) {
                        AreaMultiStageEntity cloneCurrent = currentNode.clone();
                        DepartmentInfo cloneDepartInfo = cloneCurrent.currentEntity.clone();
                        cloneDepartInfo.name = "直属部门";
                        cloneCurrent.setCurrentEntity(cloneDepartInfo);
                        cloneCurrent.setFatherNode(currentNode);
                        cloneCurrent.setInfo(currentNode.getInfo() + "-direct");
                        currentNode.childNodeList.add(0, cloneCurrent);
                    }
                    AreaMultiStageEntity directNode = (AreaMultiStageEntity) currentNode.getActualChildNodeList().get(0);
                    AreaMultiStageEntity directNodeClone = directNode.clone();
                    directNodeClone.setFatherNode(directNode);
                    DepartmentInfo cloneDepartInfo = directNodeClone.currentEntity.clone();
                    cloneDepartInfo.userInfo = txlEntity;
                    directNodeClone.setCurrentEntity(cloneDepartInfo);
                    directNode.getActualChildNodeList().add(directNodeClone);
//                    AreaMultiStageEntity cloneSSCurrent = currentNode.getActualChildNodeList().get(0).clone();
//                    DepartmentInfo cloneDepartInfo = cloneCurrent.currentEntity.clone();
//                    cloneCurrent.setCurrentEntity(cloneDepartInfo);
//                    cloneCurrent.childNodeList.get(0)..add(0, cloneCurrent);
                });
    }

    private AreaMultiStageEntity insertNode(AreaMultiStageEntity currentNode, DepartmentInfo area) {
        DepartmentInfo currentEntity = currentNode.currentEntity;
        AreaMultiStageEntity insertedreaMultiStageEntity = new AreaMultiStageEntity()
                .setFatherNode(currentNode)
                .setCurrentEntity(area);
        if (area.layRec.contains(currentEntity.layRec)) {
            currentNode.getActualChildNodeList().add(insertedreaMultiStageEntity);
//            .get().subscribe(new Consumer<List<CustomMultiStageView.CustomMultiStageEntity<DepartmentInfo>>>() {
//                @Override
//                public void accept(List<CustomMultiStageView.CustomMultiStageEntity<DepartmentInfo>> list) throws Exception {
//                    list.add(insertedreaMultiStageEntity);
        } else {
            AreaMultiStageEntity iterateEntity = currentNode;
            while (iterateEntity.fatherNode() != null) {
                if (area.layRec.contains(iterateEntity.fatherNode().getCurrentEntity().layRec)) {
//                    iterateEntity.fatherNode().getChildNodeList().subscribe(list -> list.add(insertedreaMultiStageEntity));
                    iterateEntity.fatherNode.getActualChildNodeList().add(insertedreaMultiStageEntity);
                    break;
                }
                iterateEntity = (AreaMultiStageEntity) iterateEntity.fatherNode();
            }
        }
        return insertedreaMultiStageEntity;
    }
}
