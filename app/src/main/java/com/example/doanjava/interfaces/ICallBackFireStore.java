package com.example.doanjava.interfaces;

import java.util.List;

public interface ICallBackFireStore<T> {
    public void onCallBack(List<T> lstObject,Object value);
}
