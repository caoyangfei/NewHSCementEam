package com.supcon.mes.middleware.model.bean;

import com.supcon.common.com_http.BaseEntity;
import com.supcon.mes.middleware.model.bean.Good;

public class SparePartId extends BaseEntity {
    public Good productID;
    public Long id;

    public Good getProductID() {
        if (productID == null) {
            productID = new Good();
        }
        return productID;
    }
}
