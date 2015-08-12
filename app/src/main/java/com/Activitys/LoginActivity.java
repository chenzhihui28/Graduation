package com.Activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.Model.User;
import com.Utils.Constants;
import com.Utils.SharePreferenceUtil;
import com.Utils.SingleDataUtils;
import com.Utils.Tools;
import cn.bmob.v3.listener.SaveListener;
import huti.material.R;

public class LoginActivity extends Activity implements View.OnClickListener{

    private Button login_btn;
    private ProgressBar login_progress;
    private ScrollView login_form;
    private EditText username_et,password_et;
    private TextView register_tv,forgotpassword_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        login_btn = (Button) findViewById(R.id.login_btn);
        login_progress = (ProgressBar) findViewById(R.id.login_progress);
        login_form = (ScrollView) findViewById(R.id.login_form);
        username_et = (EditText) findViewById(R.id.username_et);
        password_et = (EditText) findViewById(R.id.password_et);
        register_tv = (TextView) findViewById(R.id.register_tv);
        forgotpassword_tv = (TextView) findViewById(R.id.forgotpassword_tv);
        login_btn.setOnClickListener(this);
        register_tv.setOnClickListener(this);
        forgotpassword_tv.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn :
                String username = username_et.getText().toString();
                String password = password_et.getText().toString();
                if(!username.equals("") && !password.equals("")){
                    doLogin(username,password);
                }else {
                    Tools.ShowToast(LoginActivity.this , "用户名密码不能为空!" , 2);
                }
                break;
            case R.id.register_tv :
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, Constants.AR_Login2Register_RequestCode);
                break;
            case R.id.forgotpassword_tv :
                break;

        }
    }

    public void doLogin(final String username , String password){
        Tools.hideSystemKeyBoard(LoginActivity.this);
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        login_progress.setVisibility(View.VISIBLE);
        login_progress.setContentDescription("登录中");
        user.login(LoginActivity.this,new SaveListener() {
            @Override
            public void onSuccess() {
                SingleDataUtils.getInstance().setUsername(username);
                SharePreferenceUtil.setUserName(LoginActivity.this, username);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                login_progress.setVisibility(View.GONE);
                Tools.ShowToast(getApplicationContext(),"登录失败，请稍后再试!",2);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.AR_Login2Register_RequestCode){
            if(resultCode == Constants.AR_Login2Register_ResultCode_OK){//注册成功
                String username = data.getStringExtra("username")+"";
                String password = data.getStringExtra("password")+"";
                doLogin(username,password);
            }
        }
    }
}
