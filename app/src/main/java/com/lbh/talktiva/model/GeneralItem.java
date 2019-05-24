package com.lbh.talktiva.model;

public class GeneralItem extends ListItem {
    private Event pojoOfJsonArray;

    public Event getPojoOfJsonArray() {
        return pojoOfJsonArray;
    }

    public void setPojoOfJsonArray(Event pojoOfJsonArray) {
        this.pojoOfJsonArray = pojoOfJsonArray;
    }

    @Override
    public int getType() {
        return TYPE_GENERAL;
    }
}
