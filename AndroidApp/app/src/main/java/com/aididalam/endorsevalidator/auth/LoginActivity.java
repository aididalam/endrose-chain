package com.aididalam.endorsevalidator.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aididalam.endorsevalidator.ForgetPasswordActivity;
import com.aididalam.endorsevalidator.MainActivity;
import com.aididalam.endorsevalidator.R;
import com.aididalam.endorsevalidator.SplashActivity;
import com.aididalam.endorsevalidator.pojo.Login;
import com.aididalam.endorsevalidator.util.APIClient;
import com.aididalam.endorsevalidator.util.APIInterface;
import com.aididalam.endorsevalidator.util.DBHelper;
import com.aididalam.endorsevalidator.util.Method;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonElement;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity  {
    private TextInputEditText edit_email;
    private TextInputEditText edit_password;
    private Button login;
    private ProgressBar progressbar;
    private TextView create_account;
    private TextView forget_password;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login=findViewById(R.id.login);
        edit_email=findViewById(R.id.edit_email);
        edit_password=findViewById(R.id.edit_password);
        progressbar=findViewById(R.id.progressbar);
        Sprite threeBounce = new ThreeBounce();
        threeBounce.setColor(ContextCompat.getColor(LoginActivity.this, R.color.colorPrimary));
        progressbar.setIndeterminateDrawable(threeBounce);
        create_account=findViewById(R.id.create_account);
        forget_password=findViewById(R.id.forget_password);
        dbHelper=new DBHelper(LoginActivity.this);

        if(dbHelper.getData(1)!=null){
            finish();
        }
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=edit_email.getEditableText().toString();
                String password=edit_password.getEditableText().toString();
                if(email.length()<=3){
                    FancyToast.makeText(LoginActivity.this,"Email is too much short !",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                    return;
                }

                if(password.length()<=3){
                    FancyToast.makeText(LoginActivity.this,"Password is too much short !",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                    return;
                }

                loginStart(email,password);
            }
        });

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loginStart(String email, String password) {
        progressbar.setVisibility(View.VISIBLE);
        APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
        Call<Login> call=api.login(email,password);
        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                progressbar.setVisibility(View.GONE);
                if(response.code()==401){
                    Method.dangerAlert(LoginActivity.this,"Invalid email or password");
                    return;
                }
                try{
                    Login loginInfo= response.body();
                    dbHelper.insertUser(
                            loginInfo.user.id,
                            loginInfo.access_token,
                            loginInfo.token_type,
                            loginInfo.user.description,
                            loginInfo.user.email,
                            loginInfo.user.type,
                            loginInfo.user.publickey,
                            loginInfo.user.privatekey);
                    Method.successAlert(LoginActivity.this,"Logged in!");
                    Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }catch (Exception e){
                    System.out.println(e.toString());
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressbar.setVisibility(View.GONE);

            }
        });


    }
}