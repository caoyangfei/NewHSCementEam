package com.supcon.mes.module_sbda_online.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.app.annotation.BindByTag;
import com.app.annotation.Presenter;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.base.fragment.BaseRefreshRecyclerFragment;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.middleware.util.EmptyAdapterHelper;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.SnackbarHelper;
import com.supcon.mes.module_sbda_online.R;
import com.supcon.mes.module_sbda_online.model.api.SpareAPI;
import com.supcon.mes.module_sbda_online.model.bean.SparePartEntity;
import com.supcon.mes.module_sbda_online.model.bean.SparePartListEntity;
import com.supcon.mes.module_sbda_online.model.contract.SpareContract;
import com.supcon.mes.module_sbda_online.presenter.SparePresenter;
import com.supcon.mes.module_sbda_online.ui.adapter.SparePartAdapter;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/3/29
 * ------------- Description -------------
 * 备件
 */
@Presenter(SparePresenter.class)
public class SparePartFragment extends BaseRefreshRecyclerFragment<SparePartEntity> implements SpareContract.View {

    @BindByTag("contentView")
    RecyclerView contentView;

    private static Long eamId;
    private SparePartAdapter sparePartAdapter;

    public static SparePartFragment newInstance(Long id) {
        eamId = id;
        SparePartFragment fragment = new SparePartFragment();
        return fragment;
    }

    @Override
    protected void initView() {
        super.initView();
        refreshListController.setAutoPullDownRefresh(true);
        refreshListController.setPullDownRefreshEnabled(true);
        refreshListController.setEmpterAdapter(EmptyAdapterHelper.getRecyclerEmptyAdapter(context, null));
        contentView.setLayoutManager(new LinearLayoutManager(context));
        contentView.addItemDecoration(new SpaceItemDecoration(15));

    }

    @Override
    protected void initListener() {
        super.initListener();
        refreshListController.setOnRefreshListener(() -> {
            presenterRouter.create(SpareAPI.class).spareRecord(eamId);
        });
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_archives_list;
    }

    @Override
    protected IListAdapter createAdapter() {
        sparePartAdapter = new SparePartAdapter(getActivity());
        return sparePartAdapter;
    }

    @Override
    public void spareRecordSuccess(SparePartListEntity entity) {
        refreshListController.refreshComplete(entity.result);
    }

    @Override
    public void spareRecordFailed(String errorMsg) {
        SnackbarHelper.showError(rootView, ErrorMsgHelper.msgParse(errorMsg));
        refreshListController.refreshComplete(null);
    }
}
