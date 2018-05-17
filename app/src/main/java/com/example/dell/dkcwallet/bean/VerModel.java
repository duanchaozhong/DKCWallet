package com.example.dell.dkcwallet.bean;

/**
 *
 * @author yiyang
 */
public class VerModel {
    /**
     * content : 测试
     * version : 1.0
     * size : 100MB
     * url : www.baidu.com
     * time : 1503306535000
     */

    private String content;
    private String version;
    private String size;
    private String url;
    private long time;
    private Integer type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
