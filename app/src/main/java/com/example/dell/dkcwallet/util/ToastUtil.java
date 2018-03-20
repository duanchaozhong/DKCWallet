package com.example.dell.dkcwallet.util;

import android.text.TextUtils;
import android.widget.Toast;

import com.example.dell.dkcwallet.base.App;

/**
 * Created by DELL on 2017/8/8.
 */

public class ToastUtil {
    private static Toast toast;

    public static void i(int id) {
        if (toast == null)
            toast = Toast.makeText(App.getInstance(), id,
                    Toast.LENGTH_SHORT);
        else
            toast.setText(id);
        toast.show();
    }

    public static void s(String str) {
        if (TextUtils.isEmpty(str))
            return;
        if (toast == null)
            toast = Toast.makeText(App.getInstance(), str,
                    Toast.LENGTH_SHORT);
        else
            toast.setText(str);
        toast.show();
    }
}
