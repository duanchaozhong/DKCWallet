package com.example.dell.dkcwallet.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.view.View;
import android.widget.TextView;

import com.example.dell.dkcwallet.R;


/**
 * Created by enjoytouch-ad02 on 2015/8/5.
 */
public class MiddleDialog<E> extends Dialog {
    private onButtonCLickListener listener;
    private onButtonCLickListener2 listener2;
    private onOKListeners okListeners;
    private onUpdateListeners listeners3;
    private E bean;
    private int position;
    private View view;
    private String content;

    private FingerprintManagerCompat fingerprintManager = null;
    private CancellationSignal cancellationSignal = null;
    public static boolean isForeground = true;
    private Handler handler = null;
    public static final int MSG_AUTH_SUCCESS = 100;
    public static final int MSG_AUTH_FAILED = 101;
    public static final int MSG_AUTH_ERROR = 102;
    public static final int MSG_AUTH_HELP = 103;
    private TextView cntent;
    private Context acontext;
    /**
     *     确认与取消
     *
     * */
/*    public MiddleDialog(Context context, String tv_ok, String content, final Boolean type, final onButtonCLickListener2<E> listener, int theme) {
        super(context, theme);
        view = View.inflate(context, R.layout.dialog_middle2, null);
        setContentView(view);
        setCancelable(false);        //设置点击对话框以外的区域时，是否结束对话框
        ((TextView) view.findViewById(R.id.title)).setText(tv_ok);       //设置对话框的标题内容
        ((TextView) view.findViewById(R.id.content)).setText(content);
        if(tv_ok!=null){
            ((TextView) view.findViewById(R.id.tv_ok)).setText(tv_ok);
        }else {
            ((TextView) view.findViewById(R.id.title)).setVisibility(View.GONE);
        }
        this.listener2 = listener;
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {//取消
            @Override
            public void onClick(View v) {
                if(type==true){
                    dismiss();
                }
                listener2.onActivieButtonClick("1", position);
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {//取消
            @Override
            public void onClick(View v) {
                dismiss();
                listener2.onActivieButtonClick(null, position);
            }
        });
    }

    public MiddleDialog(Context context, String tv_ok, String content, String button, final Boolean type, final onButtonCLickListener2<E> listener, int theme) {
        super(context, theme);
        view = View.inflate(context, R.layout.dialog_middle3, null);
        setContentView(view);
        setCancelable(false);        //设置点击对话框以外的区域时，是否结束对话框
        ((TextView) view.findViewById(R.id.title)).setText(tv_ok);       //设置对话框的标题内容
        ((TextView) view.findViewById(R.id.content)).setText(content);
        ((TextView) view.findViewById(R.id.tv_ok)).setText(button);
        this.listener2 = listener;
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {//取消
            @Override
            public void onClick(View v) {
                dismiss();
                listener2.onActivieButtonClick("1", position);
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {//取消
            @Override
            public void onClick(View v) {
                dismiss();
                listener2.onActivieButtonClick(null, position);
            }
        });
    }*/
    /**
     *      提示
     *
     * */
    public MiddleDialog(Context context, String content,int theme) {
        super(context, theme);
        view = View.inflate(context, R.layout.dialog, null);
        setContentView(view);
        setCancelable(false);        //设置点击对话框以外的区域时，是否结束对话框
        ((TextView) view.findViewById(R.id.content)).setText(content);
        view.findViewById(R.id.execute).setOnClickListener(new View.OnClickListener() {      //确定
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     *      提示（点击确定返回上级页面）
     *
     * */
   /* public MiddleDialog(Context context, String content, final onButtonCLickListener listener, int theme) {
        super(context, theme);
        view = View.inflate(context, R.layout.dialog, null);
        setContentView(view);
        setCancelable(false);        //设置点击对话框以外的区域时，是否结束对话框
        ((TextView) view.findViewById(R.id.content)).setText(content);
        view.findViewById(R.id.execute).setOnClickListener(new View.OnClickListener() {      //确定
            @Override
            public void onClick(View v) {
                dismiss();
                listener.onButtonCancel("a");
            }
        });
    }*/

    /**
     *      下线通知
     *
     * */
    /*public MiddleDialog(Context context, String title, String content, String tishi, final onButtonCLickListener2<E> listener, int theme) {
        super(context, theme);
        view = View.inflate(context, R.layout.dialog_middle, null);
        setContentView(view);
        setCancelable(false);        //设置点击对话框以外的区域时，是否结束对话框
        ((TextView) view.findViewById(R.id.title)).setText(title);       //设置对话框的标题内容
        this.listener2 = listener;
        ((TextView) view.findViewById(R.id.content)).setText(content);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {//取消
            @Override
            public void onClick(View v) {
                dismiss();
                listener2.onActivieButtonClick("2", position);
            }
        });
    }*/

    public interface onOKListeners{
        void onOkButton();
    }

    public interface onUpdateListeners{
        void onButton(String password);
    }

    public interface onButtonCLickListener{
       public void onButtonCancel(String string);
    }
    public interface onButtonCLickListener2<E>{
        public void onActivieButtonClick(E bean, int position);
    }
    public void resetData(E bean,int position) {
        this.bean=bean;
        this.position = position;
    }
}
