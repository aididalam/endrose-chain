package com.aididalam.endorsevalidator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.aididalam.endorsevalidator.auth.LoginActivity;
import com.aididalam.endorsevalidator.pojo.UserFullDetails;
import com.aididalam.endorsevalidator.util.APIClient;
import com.aididalam.endorsevalidator.util.APIInterface;
import com.aididalam.endorsevalidator.util.Constant;
import com.aididalam.endorsevalidator.util.Method;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    ProgressBar progressBar;
    Button selectIMG;
    ImageView profileImg;
    File file=null;
    TextInputEditText name;
    TextInputEditText description;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Profile");
        progressBar=findViewById(R.id.progressbar);
        selectIMG=findViewById(R.id.selectIMG);
        profileImg=findViewById(R.id.profileImg);
        name=findViewById(R.id.name);
        description=findViewById(R.id.description);
        save=findViewById(R.id.save);


        progressBar.setVisibility(View.VISIBLE);
        APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
        Call<UserFullDetails> call=api.profileFullInfo(Method.getToken(UpdateProfileActivity.this));
        call.enqueue(new Callback<UserFullDetails>() {
            @Override
            public void onResponse(Call<UserFullDetails> call, Response<UserFullDetails> response) {
                if(response.code()==200){
                    name.setText(response.body().name.replace("\"",""));
                    description.setText(response.body().description.replace("\"",""));
                    Picasso.get().load(Constant.api+"/public/profileIMG/"+response.body().img)
                            .error(R.mipmap.ic_launcher_img)
                            .placeholder(R.mipmap.ic_launcher_img)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(profileImg);
                }else if (response.code()==401){
                    Method.dangerAlert(UpdateProfileActivity.this,"You are logged out! Please Login again");
                    Intent intent=new Intent(UpdateProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<UserFullDetails> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Method.dangerAlert(UpdateProfileActivity.this,t.getMessage());
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                RequestBody requestFile=null;
                MultipartBody.Part body=null;
                if(file!=null){
                     requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                     body = MultipartBody.Part.createFormData("profileIMG", file.getName(), requestFile);
                }
                APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
                Call<UserFullDetails> call=api.updateProfileFullInfo(
                        RequestBody.create(MediaType.parse("text/plain"), name.getEditableText().toString()),
                        RequestBody.create(MediaType.parse("text/plain"), description.getEditableText().toString()),
                        body,
                        Method.getToken(UpdateProfileActivity.this));

                call.enqueue(new Callback<UserFullDetails>() {
                    @Override
                    public void onResponse(Call<UserFullDetails> call, Response<UserFullDetails> response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Method.successAlert(UpdateProfileActivity.this,"Profile Updated");
                        finish();
                    }

                    @Override
                    public void onFailure(Call<UserFullDetails> call, Throwable t) {
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });
            }
        });


        selectIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(UpdateProfileActivity.this).crop().start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK){
            Uri uri =data.getData();
            profileImg.setImageURI(uri);
            System.out.println(uri);
            file=new File(uri.getPath());
            System.out.println(file);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}