package com.aididalam.endorsevalidator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aididalam.endorsevalidator.auth.LoginActivity;
import com.aididalam.endorsevalidator.pojo.ResponsePojo;
import com.aididalam.endorsevalidator.pojo.User;
import com.aididalam.endorsevalidator.util.APIClient;
import com.aididalam.endorsevalidator.util.APIInterface;
import com.aididalam.endorsevalidator.util.Method;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

    TextInputEditText edit_email;
    TextInputEditText otp;
    TextView create_account;
    TextView login;

    ProgressBar progressbar;
    Button otpsend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        edit_email=findViewById(R.id.edit_email);
        otp=findViewById(R.id.otp);
        create_account=findViewById(R.id.create_account);
        login=findViewById(R.id.login);
        otpsend=findViewById(R.id.otpsend);
        progressbar=findViewById(R.id.progressbar);
        Sprite threeBounce = new ThreeBounce();
        threeBounce.setColor(ContextCompat.getColor(ForgetPasswordActivity.this, R.color.colorPrimary));
        progressbar.setIndeterminateDrawable(threeBounce);
        otpsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otp.getVisibility()==View.VISIBLE){
                    APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
                    Call<ResponsePojo> call=api.checkOTP(edit_email.getEditableText().toString(),otp.getEditableText().toString());
                    progressbar.setVisibility(View.VISIBLE);
                    call.enqueue(new Callback<ResponsePojo>() {
                        @Override
                        public void onResponse(Call<ResponsePojo> call, Response<ResponsePojo> response) {
                            progressbar.setVisibility(View.INVISIBLE);
                            if(response.code()==204){
                                Method.dangerAlert(ForgetPasswordActivity.this,"invalid OTP");
                            }

                            if(response.code()==200){
                                Method.successAlert(ForgetPasswordActivity.this,"Password is changed!\nPlease check your email for new Password");
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponsePojo> call, Throwable t) {
                            progressbar.setVisibility(View.INVISIBLE);
                            Method.dangerAlert(ForgetPasswordActivity.this, t.getMessage());
                        }
                    });
                }else{
                    APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
                    Call<ResponsePojo> call=api.sendOTP(edit_email.getEditableText().toString());
                    progressbar.setVisibility(View.VISIBLE);
                    call.enqueue(new Callback<ResponsePojo>() {
                        @Override
                        public void onResponse(Call<ResponsePojo> call, Response<ResponsePojo> response) {
                            progressbar.setVisibility(View.INVISIBLE);
                            if(response.code()==204){
                                Method.dangerAlert(ForgetPasswordActivity.this,"There is no account with that email");
                            }

                            if(response.code()==200){
                                Method.successAlert(ForgetPasswordActivity.this,"OTP sent\nPlease check your email");
                                otp.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponsePojo> call, Throwable t) {
                            progressbar.setVisibility(View.INVISIBLE);
                            Method.dangerAlert(ForgetPasswordActivity.this, t.getMessage());
                        }
                    });
                }
            }
        });
    }
}