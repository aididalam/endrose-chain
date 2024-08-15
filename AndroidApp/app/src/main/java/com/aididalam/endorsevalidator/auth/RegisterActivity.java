package com.aididalam.endorsevalidator.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.aididalam.endorsevalidator.MainActivity;
import com.aididalam.endorsevalidator.R;
import com.aididalam.endorsevalidator.pojo.Login;
import com.aididalam.endorsevalidator.pojo.User;
import com.aididalam.endorsevalidator.util.APIClient;
import com.aididalam.endorsevalidator.util.APIInterface;
import com.aididalam.endorsevalidator.util.Method;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private Spinner type;
    TextInputEditText edit_name;
    TextInputEditText edit_description;
    TextInputEditText edit_email;
    TextInputEditText edit_password;
    TextInputEditText edit_password_confirm;
    Button register;
    ProgressBar progressbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        type=findViewById(R.id.type);
        edit_name=findViewById(R.id.edit_name);
        edit_description=findViewById(R.id.edit_description);
        edit_email=findViewById(R.id.edit_email);
        edit_password=findViewById(R.id.edit_password);
        edit_password_confirm=findViewById(R.id.edit_password_confirm);
        register=findViewById(R.id.register);
        progressbar=findViewById(R.id.progressbar);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Customer");
        arrayList.add("Retailer");
        arrayList.add("Supplier");
        arrayList.add("Producer");
        arrayList.add("Store");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
        type.setAdapter(arrayAdapter);
        progressbar=findViewById(R.id.progressbar);
        Sprite threeBounce = new ThreeBounce();
        threeBounce.setColor(ContextCompat.getColor(RegisterActivity.this, R.color.colorPrimary));
        progressbar.setIndeterminateDrawable(threeBounce);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=edit_name.getEditableText().toString();
                String email=edit_email.getEditableText().toString();
                String password=edit_password.getEditableText().toString();
                String confirmPassword=edit_password_confirm.getEditableText().toString();
                String description=edit_description.getEditableText().toString();
                int usertype=1;
                if(type.getSelectedItem().toString().equals("Customer")){
                    usertype=1;
                }else if(type.getSelectedItem().toString().equals("Supplier")){
                    usertype=2;
                }else if(type.getSelectedItem().toString().equals("Producer")){
                    usertype=3;
                }else if(type.getSelectedItem().toString().equals("Retailer")){
                    usertype=4;
                }else if(type.getSelectedItem().toString().equals("Retailer")){
                    usertype=5;
                }

                if(password.length()<6){
                    Method.dangerAlert(RegisterActivity.this,"Password must be 6 characters long");
                    return;
                }

                if(!password.equals(confirmPassword)){
                    Method.dangerAlert(RegisterActivity.this,"Confirm Password does not match");
                    return;
                }

                if(email.length()==0){
                    Method.dangerAlert(RegisterActivity.this,"Email cannot be blank");
                    return;
                }

                if(!isValidEmailAddress(email)){
                    Method.dangerAlert(RegisterActivity.this,"Please enter a valid email");
                    return;
                }

                progressbar.setVisibility(View.VISIBLE);
                APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
                Call<User> call=api.Register(
                        name,
                        password,
                        email,
                        description,
                        usertype
                );

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        progressbar.setVisibility(View.INVISIBLE);
                        if(response.code()==422){
                            Method.dangerAlert(RegisterActivity.this,"Please change your email");
                        }

                        if(response.code()==200){
                            Method.successAlert(RegisterActivity.this,"Registration Done\nLogin Please");
                            Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        progressbar.setVisibility(View.INVISIBLE);
                        Method.dangerAlert(RegisterActivity.this,t.getMessage());
                    }
                });
            }
        });


    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

}