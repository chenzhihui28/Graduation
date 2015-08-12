package com.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Created by chenzhihui on 2015/5/23.
 */
public class Tools {
    /**
     * @param context
     * @param expression
     * @param type 1:Long 2:Short
     */
    public static void ShowToast(Context context,String expression,int type){
        if(type == 1)
            Toast.makeText(context, expression, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, expression, Toast.LENGTH_SHORT).show();
    }

    public static void MyLog(String tag , String msg){
        Log.v(tag,msg);
    }

    /**
     * 检验邮箱格式是否正确
     * @param target
     * @return
     */
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
    }


    /**
     * 隐藏系统键盘
     */
    public static void hideSystemKeyBoard(Activity acy) {
        InputMethodManager imm = (InputMethodManager) acy.getSystemService(acy.getApplicationContext().INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen && acy != null && acy.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(acy.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * 显示键盘
     */
    public static void showSystemKeyBoard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
