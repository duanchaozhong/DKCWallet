package com.example.dell.dkcwallet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.dell.dkcwallet.R;
import com.example.dell.dkcwallet.decoration.DividerGridItemDecoration;
import com.example.dell.dkcwallet.helper.OnResultListener;
import com.example.dell.dkcwallet.util.DensityUtils;
import com.example.dell.dkcwallet.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 2017/8/14.
 */

public class PayPwdDialog {
    private Context mContext;
    private Dialog payDialog;

    public PayPwdDialog(Context context){
        mContext = context;
    }

    public void showDialog(OnResultListener<String> listener){
        if (payDialog != null && payDialog.isShowing())
            ;
        else
            initDialog(listener);

    }

    private List<CharSequence> keys;
    public void initDialog(final OnResultListener<String> listener){

        payDialog = new Dialog(mContext, R.style.shapeDialogTheme);
        View root = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_input_pwd, null);
        payDialog.setContentView(root);

        RecyclerView keyRv = (RecyclerView) root.findViewById(R.id.recycler_view);
        keyRv.setLayoutManager(new GridLayoutManager(mContext, 3));
        keyRv.addItemDecoration(new DividerGridItemDecoration((int) (DensityUtils.dp2px(mContext,1f)+.5f)));
        keys = new ArrayList<>(12);
        for (int i = 1; i < 10; i++) {
            keys.add(""+i);
        }
        keys.add(".");
        keys.add("0");
        Drawable keyBack = ContextCompat.getDrawable(mContext, R.drawable.key_back);
        keyBack.setBounds(0,0, DensityUtils.dp2px(mContext,35),DensityUtils.dp2px(mContext, 19.33f));
        SpannableString backString = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(keyBack);
        backString.setSpan(imageSpan, 0,1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        keys.add(backString);
        BaseQuickAdapter<CharSequence, BaseViewHolder> adapter = new BaseQuickAdapter<CharSequence, BaseViewHolder>(R
                .layout.item_pwd_keyborad, keys) {
            @Override
            protected void convert(BaseViewHolder helper, CharSequence item) {
                int position = helper.getAdapterPosition();
                if (position == 9 || position == 11)
                    helper.setBackgroundColor(R.id.item_root, Color.parseColor("#DDDDDD"));
                helper.setText(R.id.key_tv, item);
            }
        };
        final StringBuilder pwdSb = new StringBuilder();

        keyRv.setAdapter(adapter);

        ImageButton close = (ImageButton) root.findViewById(R.id.close_button);
        Button sure = (Button) root.findViewById(R.id.sure_pwd);
        final TextView pwdEdit = (TextView) root.findViewById(R.id.pwd_input);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position) {
                    case 11:
                        if(pwdSb.length()>0){
                            pwdSb.deleteCharAt(pwdSb.length()-1);
                            refreshPwdEt(pwdSb, pwdEdit);
                        }
                        break;
                    case 10:
                        pwdSb.append("0");
                        refreshPwdEt(pwdSb, pwdEdit);
                        break;
                    case 9:

                        break;


                    default:
                        pwdSb.append(++position);
                        refreshPwdEt(pwdSb, pwdEdit);
                        break;
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payDialog.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(pwdEdit.getText().toString())){
                    ToastUtil.s(String.format(mContext.getString(R.string.s_can_not_be_empty), mContext.getString(R.string.pay_pwd)));
                    return;
                }
                payDialog.dismiss();
                if(listener != null)
                    listener.onResult(pwdSb.toString());
            }
        });
        Window dialogWindow = payDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = (int)mContext.getResources().getDisplayMetrics().widthPixels; // 宽度
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
//      lp.alpha = 9f; // 透明度
        root.measure(0, 0);
        //       lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        payDialog.show();
    }
    String dot = "●";
    private void refreshPwdEt(StringBuilder builder, TextView tv){
//        StringBuilder stringBuilder = new StringBuilder(builder.length());
//        for (int i = 0; i < builder.length(); i++) {
//            stringBuilder.append(dot);
//        }
//        tv.setText(stringBuilder);

        tv.setText(builder);
    }
}
