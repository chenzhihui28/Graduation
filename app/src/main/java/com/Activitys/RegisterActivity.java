package com.Activitys;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.Model.User;
import com.Utils.Constants;
import com.Utils.Tools;

import cn.bmob.v3.listener.SaveListener;
import huti.material.R;


public class RegisterActivity extends Activity  {
    private ProgressBar registerProgress;
    private ScrollView registerForm;
    private EditText emailEt,usernameEt,passwordEt,passwordconfirmEt;
    private Button registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        registerProgress = (ProgressBar)findViewById( R.id.register_progress );
        registerForm = (ScrollView)findViewById( R.id.register_form );
        emailEt = (EditText)findViewById( R.id.email_et );
        usernameEt = (EditText)findViewById( R.id.username_et );
        passwordEt = (EditText)findViewById( R.id.password_et );
        passwordconfirmEt = (EditText)findViewById( R.id.passwordconfirm_et );
        registerBtn = (Button)findViewById( R.id.register_btn );

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(emailEt.getText()) || TextUtils.isEmpty(usernameEt.getText()) ||
                TextUtils.isEmpty(passwordconfirmEt.getText()) || TextUtils.isEmpty(passwordEt.getText())){
                    Tools.ShowToast(RegisterActivity.this , "每一项都不能为空!" , 2);
                }else {
                    String usernameString = usernameEt.getText().toString();
                    String emailString = emailEt.getText().toString();
                    String pwdString = passwordEt.getText().toString();
                    String pwdConfirmString = passwordconfirmEt.getText().toString();
                    if(Tools.isValidEmail(emailString)){
                        if(pwdString.equals(pwdConfirmString)){
                            if(pwdString.length() > 5){
                                Tools.hideSystemKeyBoard(RegisterActivity.this);
                                registerProgress.setVisibility(View.VISIBLE);
                                registerProgress.setContentDescription("注册中...");
                                User user = new User();
                                user.setUsername(usernameString);
                                user.setEmail(emailString);
                                user.setPassword(pwdString);
                                user.signUp(RegisterActivity.this, new SaveListener() {
                                    @Override
                                    public void onSuccess() {
                                        registerProgress.setVisibility(View.GONE);
                                        Tools.ShowToast(getApplicationContext(),"注册成功!",2);
                                        Intent intent = new Intent();
                                        intent.putExtra("username",usernameEt.getText().toString());
                                        intent.putExtra("password",passwordEt.getText().toString());
                                        setResult(Constants.AR_Login2Register_ResultCode_OK,intent);
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        registerProgress.setVisibility(View.GONE);
                                        Tools.ShowToast(getApplicationContext(),"注册失败! "+s,2);

                                    }
                                });
                            }else {
                                Tools.ShowToast(RegisterActivity.this , "密码长度必须大于5!" , 2);
                            }

                        }else {
                            Tools.ShowToast(RegisterActivity.this , "两次密码不一致!" , 2);
                        }
                    }else {
                        Tools.ShowToast(RegisterActivity.this , "email格式不正确!" , 2);
                    }
                }

            }
        });
    }





}




