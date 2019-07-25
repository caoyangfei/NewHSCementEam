package com.supcon.mes.viber_mogu.presenter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.Time;

import com.supcon.common.view.contract.IBaseView;
import com.supcon.common.view.util.LogUtil;
import com.supcon.mes.viber_mogu.model.contract.MGViberContract;
import com.supcon.mes.viber_mogu.model.model.BleSearchEntity;
import com.sytest.app.blemulti.BleDevice;
import com.sytest.app.blemulti.Rat;
import com.sytest.app.blemulti.data.B1_SampleData;
import com.sytest.app.blemulti.easy.Recipe;
import com.sytest.app.blemulti.exception.BleException;
import com.sytest.app.blemulti.interfaces.Scan_CB;
import com.sytest.app.blemulti.interfaces.SucFail;
import com.sytest.app.blemulti.interfaces.Value_CB;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by wangshizhan on 2019/1/23
 * Email:wangshizhan@supcom.com
 */
public class MGViberPresenter extends MGViberContract.Presenter {


    private Disposable searchTimer = null;
    private Disposable viberTimer = null;

    @SuppressLint("CheckResult")
    @Override
    public void search() {

        Flowable.timer(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        startSearch();
                    }
                });

        searchTimer = Flowable.timer(30, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        stopSearch(true);
                    }
                });
    }

    private void startSearch(){
//        stopSearch(false);
        Rat.getInstance().startScan(new Scan_CB() {
            @Override
            public void onLeScan(BluetoothDevice bluetoothDevice) {
                LogUtil.d("BluetoothDevice name:"+bluetoothDevice.getName()+" address:"+bluetoothDevice.getAddress());
                LogUtil.d(""+bluetoothDevice.getBluetoothClass().toString());
                LogUtil.d(""+bluetoothDevice.getBluetoothClass().getMajorDeviceClass());


                if(TextUtils.isEmpty(bluetoothDevice.getName())){
                    return;
                }

                if(!bluetoothDevice.getName().contains("SU-100")){
                    return;
                }

                BleSearchEntity bleEntity = new BleSearchEntity();
                bleEntity.bluetoothDevice = bluetoothDevice;
                bleEntity.name = bluetoothDevice.getName();

                bleEntity.address = bluetoothDevice.getAddress();
                if(bleEntity.name!=null){
                    bleEntity.name+="  "+bleEntity.address;
                }
                if(getView()!=null)
                    getView().searchSuccess(bleEntity);
                stopSearch(false);

            }
        });
    }

    private void stopSearch(boolean fored){
        Rat.getInstance().stopScan();
        if(searchTimer!=null) {
            searchTimer.dispose();
            if(fored && getView()!=null)
                getView().searchFailed("搜索超时");
            searchTimer = null;
        }

    }


    @Override
    public void connect(String address) {
        Rat.getInstance().connectDevice_Normal(address, new SucFail() {
            @Override
            public void onSucceed_UI(@Nullable String s) {
                LogUtil.d(""+s);
                if(getView()!=null)
                getView().connectSuccess(true);
            }

            @Override
            public void onFailed_UI(@Nullable String s) {
                LogUtil.e(""+s);
                if(getView()!=null)
                getView().connectFailed(s);
            }
        });
    }

    @Override
    public void startViberTest(byte mode, BleDevice bleDevice) {
        viberTimer = Flowable.interval(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        getViberData(mode, bleDevice);
                    }
                });
    }

    @Override
    public void stopViberTest() {
        if(viberTimer!=null){
            viberTimer.dispose();
            viberTimer = null;
        }
    }


    /**
     * public static final byte ACCELERATION = 0;加速度
     * public static final byte VELOCITY = 1;   速度
     * public static final byte DISPLACEMENT = 2;位移
     * public static final byte TEMPERATURE = -60;
     * public static final byte ENVELOPE = 3;
     *
     * */
    private void getViberData(byte mode, BleDevice bleDevice) {

//        byte mode = -1;
//        if("位移模式".equals(currentMode)){
//            mode = B1_SampleData.SignalType.DISPLACEMENT;
//        }
//        else if("速度模式".equals(currentMode)){
//            mode = B1_SampleData.SignalType.VELOCITY;
//        }
//        else if("加速度模式".equals(currentMode)){
//            mode = B1_SampleData.SignalType.ACCELERATION;
//        }
//        else if("温度模式".equals(currentMode)){
//            mode = B1_SampleData.SignalType.TEMPERATURE;
//        }

        if(mode!=-1)
        Recipe.newInstance(bleDevice).getValue_Z(mode , false, new Value_CB() {
            @Override
            public void onValue_UI(float v, float v1, float v2) {
//                LogUtil.d("onValue_UI v:"+v+" v1:"+v1+" v2:"+v2);
                if(getView()!=null)
                    getView().startViberTestSuccess(""+v2);
            }

            @Override
            public void onFail_UI(@NonNull BleException e) {
//                LogUtil.e("onFail_UI");
                if(getView()!=null)
                    getView().startViberTestFailed(""+e.getMessage());
            }
        });

    }
}
