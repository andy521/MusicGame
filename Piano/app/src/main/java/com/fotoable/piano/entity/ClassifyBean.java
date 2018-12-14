package com.fotoable.piano.entity;

/**
 * Created by fotoable on 2017/6/30.
 */

public class ClassifyBean{
    //分类名称
    private String classifyName;
    //分类图标
    private int classifyImg;
    //分类ID
    private int classifyId;
    //是否单选
    private boolean selectItem;

    public String getClassifyName() {
        return classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

    public int getClassifyImg() {
        return classifyImg;
    }

    public void setClassifyImg(int classifyImg) {
        this.classifyImg = classifyImg;
    }

    public int getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(int classifyId) {
        this.classifyId = classifyId;
    }

    public boolean isSelectItem() {
        return selectItem;
    }

    public void setSelectItem(boolean selectItem) {
        this.selectItem = selectItem;
    }

    @Override
    public String toString() {
        return "ClassifyBean{" +
                "classifyName='" + classifyName + '\'' +
                ", classifyImg=" + classifyImg +
                ", classifyId=" + classifyId +
                ", selectItem=" + selectItem +
                '}';
    }
}
