package com.yiyi.providecodelib;

import java.util.Map;

/**
 *
 * @author yiyang
 */
public class CodeUtils {
    static {
        System.loadLibrary("code-lib");
    }
    public static native String getSign(Map<String, String> params);
}
