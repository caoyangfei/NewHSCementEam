package com.supcon.mes.viber_mogu.config;

import com.sytest.app.blemulti.data.B1_SampleData;

/**
 * Created by wangshizhan on 2019/1/24
 * Email:wangshizhan@supcom.com
 */
public enum ViberMode {

    DISPLACEMENT(B1_SampleData.SignalType.DISPLACEMENT, "位移模式"),
    VELOCITY(B1_SampleData.SignalType.VELOCITY, "速度模式"),
    ACCELERATION(B1_SampleData.SignalType.ACCELERATION, "加速度模式"),
    TEMPERATURE(B1_SampleData.SignalType.TEMPERATURE, "测温模式"),
    ENVELOPE(B1_SampleData.SignalType.ENVELOPE, "包络模式");

    private byte mode;
    private String modeName;
    ViberMode(byte mode, String modeName){
        this.mode = mode;
        this.modeName = modeName;
    }

    public String modeName(){
        return modeName;
    }

    public byte mode(){
        return mode;
    }

    public static ViberMode getMode(String modeName){
        if(DISPLACEMENT.modeName.equals(modeName) || DISPLACEMENT.name().equals(modeName)){
            return DISPLACEMENT;
        }
        else if(VELOCITY.modeName.equals(modeName) || VELOCITY.name().equals(modeName)){
            return VELOCITY;
        }
        else if(ACCELERATION.modeName.equals(modeName) || ACCELERATION.name().equals(modeName)){
            return ACCELERATION;
        }
        else if(TEMPERATURE.modeName.equals(modeName) || TEMPERATURE.name().equals(modeName)){
            return TEMPERATURE;
        }
        else {

            return ENVELOPE;
        }
    }
}
