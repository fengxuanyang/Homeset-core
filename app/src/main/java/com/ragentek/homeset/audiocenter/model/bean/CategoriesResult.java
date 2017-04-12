package com.ragentek.homeset.audiocenter.model.bean;


import com.ragentek.protocol.commons.audio.CategoryVO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xuanyang.feng on 2017/3/16.
 */

public class CategoriesResult implements Serializable {
    private List<CategoryVO> categories;
    private int category_count;

    public List<CategoryVO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryVO> categories) {
        this.categories = categories;
    }

    public int getCategory_count() {
        return category_count;
    }

    public void setCategory_count(int category_count) {
        this.category_count = category_count;
    }


}
