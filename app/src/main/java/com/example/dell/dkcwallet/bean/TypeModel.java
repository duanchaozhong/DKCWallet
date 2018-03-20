package com.example.dell.dkcwallet.bean;

import java.util.List;

/**
 *
 * @author yiyang
 */
public class TypeModel {

    /**
     * adds : 12345678901234567894
     * map : [{"type":"1","value":"互助转账"},{"type":"2","value":"出金到原力"}]
     */

    private String adds;
    private List<MapBean> map;

    public String getAdds() {
        return adds;
    }

    public void setAdds(String adds) {
        this.adds = adds;
    }

    public List<MapBean> getMap() {
        return map;
    }

    public void setMap(List<MapBean> map) {
        this.map = map;
    }

}
