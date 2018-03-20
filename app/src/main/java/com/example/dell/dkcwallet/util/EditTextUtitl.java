package com.example.dell.dkcwallet.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

/**
 * Created by DELL on 2017/8/1.
 */

public class EditTextUtitl {
    /**
     * 禁止EditText输入空格
     * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(" "))
                    return "";
                else
                    return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }
}
