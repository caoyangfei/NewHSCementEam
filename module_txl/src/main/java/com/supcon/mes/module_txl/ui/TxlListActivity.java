package com.supcon.mes.module_txl.ui;

import com.app.annotation.apt.Router;
import com.supcon.common.view.base.activity.BasePresenterActivity;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.module_txl.R;

/**
 * 通讯录列表功能开发
 */
@Router(value = Constant.Router.TXL_LIST)
//@Presenter(value = {TxlListPresenter.class})
public class TxlListActivity extends BasePresenterActivity
//        <TxlEntity>
//        implements TxlListContract.View
{
//    @BindByTag("contentView")
//    RecyclerView contentView;
//    @BindByTag("searchView")
//    CustomSearchView searchView;
//    @BindByTag("departInfos")
//    TextView departInfos;
//    @BindByTag("refreshFrameLayout")
//    PtrFrameLayout refreshFrameLayout;
//
//    private TxlListAdapter mTxlListAdapter;
//
    @Override
    protected int getLayoutID() {
        return R.layout.ac_txl_list;
    }
//
//    @Override
//    protected void onInit() {
//        super.onInit();
//        refreshListController.setEmpterAdapter(EmptyAdapterHelper.getRecyclerEmptyAdapter(context, "未搜索到通讯录数据!"));
//        refreshListController.setLoadMoreEnable(true);
//        refreshListController.setPullDownRefreshEnabled(true);
//        refreshListController.setOnRefreshPageListener(pageIndex -> doFilter(pageIndex));
//        refreshListController.setAutoPullDownRefresh(true);
//    }
//
//    @Override
//    protected void initView() {
//        super.initView();
//        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
//        contentView.setLayoutManager(new LinearLayoutManager(context));
//        final int spacingInPixels = 10;
//        contentView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
//    }
//
//    @Override
//    protected void initListener() {
//        super.initListener();
//        departInfos.setOnClickListener(v -> IntentRouter.go(context,Constant.Router.MULTI_DEPART_SELECT));
//    }
//
//    private void doFilter(int pageNum) {
//        presenterRouter.create(TxlListAPI.class).getTxlList(pageNum);
//    }
//
//    @Override
//    protected IListAdapter createAdapter() {
//        mTxlListAdapter = new TxlListAdapter(context);
//        return mTxlListAdapter;
//    }
//
//
//    @Override
//    public void getTxlListSuccess(TxlListEntity entity) {
//        refreshListController.refreshComplete(entity.result);
//    }
//
//    @Override
//    public void getTxlListFailed(String errorMsg) {
//        LogUtil.e(errorMsg);
//        ToastUtils.show(context,errorMsg);
//        refreshListController.refreshComplete();
//    }
}
