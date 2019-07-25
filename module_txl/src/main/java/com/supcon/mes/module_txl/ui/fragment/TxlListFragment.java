package com.supcon.mes.module_txl.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Presenter;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.base.fragment.BaseRefreshRecyclerFragment;
import com.supcon.common.view.ptr.PtrFrameLayout;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.mbap.view.CustomSearchView;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.TxlEntity;
import com.supcon.mes.middleware.model.bean.TxlListEntity;
import com.supcon.mes.middleware.util.EmptyAdapterHelper;
import com.supcon.mes.module_txl.IntentRouter;
import com.supcon.mes.module_txl.R;
import com.supcon.mes.module_txl.model.api.TxlListAPI;
import com.supcon.mes.module_txl.model.contract.TxlListContract;
import com.supcon.mes.module_txl.presenter.TxlListPresenter;
import com.supcon.mes.module_txl.ui.adapter.TxlListAdapter;

/**
 * @Author xushiyun
 * @Create-time 7/25/19
 * @Pageage com.supcon.mes.module_txl.ui.fragment
 * @Project eamtest
 * @Email ciruy.victory@gmail.com
 * @Related-classes
 * @Desc
 */
@Presenter(value = {TxlListPresenter.class})
public class TxlListFragment extends BaseRefreshRecyclerFragment<TxlEntity>
implements TxlListContract.View {
    @BindByTag("contentView")
    RecyclerView contentView;
    @BindByTag("searchView")
    CustomSearchView searchView;
    @BindByTag("departInfos")
    TextView departInfos;
    @BindByTag("refreshFrameLayout")
    PtrFrameLayout refreshFrameLayout;
    
    private TxlListAdapter mTxlListAdapter;
    
    @Override
    protected int getLayoutID() {
        return R.layout.frag_txl_list;
    }
    
    @Override
    protected void onInit() {
        super.onInit();
        refreshListController.setEmpterAdapter(EmptyAdapterHelper.getRecyclerEmptyAdapter(context, "未搜索到通讯录数据!"));
        refreshListController.setLoadMoreEnable(true);
        refreshListController.setPullDownRefreshEnabled(true);
        refreshListController.setOnRefreshPageListener(pageIndex -> doFilter(pageIndex));
        refreshListController.setAutoPullDownRefresh(true);
    }
    
    @Override
    protected void initView() {
        super.initView();
//        StatusBarUtils.setWindowStatusBarColor(getActivity(), R.color.themeColor);
        contentView.setLayoutManager(new LinearLayoutManager(context));
        final int spacingInPixels = 10;
        contentView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }
    
    @Override
    protected void initListener() {
        super.initListener();
        departInfos.setOnClickListener(v -> IntentRouter.go(context, Constant.Router.MULTI_DEPART_SELECT));
    }
    
    private void doFilter(int pageNum) {
        presenterRouter.create(TxlListAPI.class).getTxlList(pageNum);
    }
    
    @Override
    protected IListAdapter createAdapter() {
        mTxlListAdapter = new TxlListAdapter(context);
        return mTxlListAdapter;
    }
    
    
    @Override
    public void getTxlListSuccess(TxlListEntity entity) {
        refreshListController.refreshComplete(entity.result);
    }
    
    @Override
    public void getTxlListFailed(String errorMsg) {
        LogUtil.e(errorMsg);
        ToastUtils.show(context,errorMsg);
        refreshListController.refreshComplete();
    }
}
