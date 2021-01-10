package com.example.doanjava.interfaces;

import java.util.List;

public interface ICallBackFireStore<T> {
    /**
     * Vì tải dữ liệu từ firebase sử dụng func bất đồng bộ cho nên cần sử dụng interface này
     *     để callBack dữ liệu sau khi hàm bất đồng bộ xử lý xong.
     * @param lstObject có truyền vào một List Object bất kì
     * @param value có truyền vào một Object bất kì
     */
    void onCallBack(List<T> lstObject,Object value);
}
