package com.example.dell.dkcwallet.http;

import java.io.Serializable;

/**
 *
 * @author yiyang
 */
public class HttpResult<T> implements Serializable{
    private static final long serialVersionUID = -541770978308896116L;
    /**
     * ret : string
     * data : {}
     * msg : string
     */

    private String ret;
    private T data;
    private String msg;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * error : false
     * results : []
     */

//    protected int msg_code;
//
//    public int getMsg_code() {
//        return msg_code;
//    }
//
//    public void setMsg_code(int msg_code) {
//        this.msg_code = msg_code;
//    }
//
//
//    protected String tocon;
//
//    public String getTocon() {
//        return tocon;
//    }
//
//    public void setTocon(String tocon) {
//        this.tocon = tocon;
//    }
//    //    private int ret;
////    private String message;
//
//    @Override
//    public String toString() {
//        return "HttpResult{" +
//                "msg_code=" + msg_code +
//                ", tocon='" + tocon + '\'' +
//                '}';
//    }
//    private T content;
//
//    public T getContent() {
//        return content;
//    }
//
//    public void setContent(T content) {
//        this.content = content;
//    }
//
//
//
//    public int getRet() {
//        return ret;
//    }
//
//    public void setRet(int ret) {
//        this.ret = ret;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }



}
