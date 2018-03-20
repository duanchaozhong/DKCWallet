package com.example.dell.dkcwallet.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.example.dell.dkcwallet.R;

/**
 *
 * @author yiyang
 */
public class CommonDialog {
    public static class Builder {
        private Activity activity;
        private String title;
        private String message;
        private boolean cancelable;
        private String positiveButton;
        private String negativeButton;
        private DialogInterface.OnClickListener pBtnListener;
        private DialogInterface.OnClickListener nBtnListener;

        public Builder(Activity activity) {
            this.activity = activity;
        }


        public Builder setTitle(@StringRes int title) {
            this.title = activity.getString(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(@StringRes int message) {
            this.message = activity.getString(message);
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setPositiveButton(@StringRes int positiveButton, DialogInterface.OnClickListener listener) {
            this.positiveButton = activity.getString(positiveButton);
            this.pBtnListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButton, DialogInterface.OnClickListener listener) {
            this.positiveButton = positiveButton;
            this.pBtnListener = listener;
            return this;
        }

        public Builder enablePositiveButton(DialogInterface.OnClickListener listener) {
            this.positiveButton = activity.getString(R.string.confirm);
            this.pBtnListener = listener;
            return this;
        }

        public Builder setNegativeButton(@StringRes int negativeButton, DialogInterface.OnClickListener listener) {
            this.negativeButton = activity.getString(negativeButton);
            this.nBtnListener = listener;
            return this;
        }

        public Builder enableNegativeButton() {
            this.negativeButton = activity.getString(R.string.cancel);
            this.nBtnListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
            return this;
        }

        public Builder enableNegativeButton(DialogInterface.OnClickListener listener) {
            this.negativeButton = activity.getString(R.string.cancel);
            this.nBtnListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButton, DialogInterface.OnClickListener listener) {
            this.negativeButton = negativeButton;
            this.nBtnListener = listener;
            return this;
        }

        public Dialog build() {
            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            if (!TextUtils.isEmpty(title)) {
                alertDialog.setTitle(title);
            }
            if (!TextUtils.isEmpty(message)) {
                alertDialog.setMessage(message);
            }
            alertDialog.setCancelable(cancelable);
            if (!TextUtils.isEmpty(positiveButton) && pBtnListener != null)
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveButton, pBtnListener);
            if (!TextUtils.isEmpty(negativeButton) && nBtnListener != null)
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeButton, nBtnListener);
            return alertDialog;
        }

        public Dialog show() {
            Dialog dialog = build();
            dialog.show();
            return dialog;
        }

    }
}
