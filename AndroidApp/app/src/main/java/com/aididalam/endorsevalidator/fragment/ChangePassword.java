package com.aididalam.endorsevalidator.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.aididalam.endorsevalidator.R;
import com.aididalam.endorsevalidator.auth.LoginActivity;
import com.aididalam.endorsevalidator.pojo.AllAsset;
import com.aididalam.endorsevalidator.pojo.ResponsePojo;
import com.aididalam.endorsevalidator.util.APIClient;
import com.aididalam.endorsevalidator.util.APIInterface;
import com.aididalam.endorsevalidator.util.Method;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends Fragment {

    TextInputEditText old_password;
    TextInputEditText password;
    TextInputEditText confirm_password;
    Button change;
    ProgressBar progressbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_change_password, container, false);
        old_password=view.findViewById(R.id.old_password);
        password=view.findViewById(R.id.password);
        confirm_password=view.findViewById(R.id.confirm_password);
        change=view.findViewById(R.id.change);
        progressbar=view.findViewById(R.id.progressbar);
        Sprite threeBounce = new ThreeBounce();
        threeBounce.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        progressbar.setIndeterminateDrawable(threeBounce);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword=old_password.getEditableText().toString();
                String newPassword=password.getEditableText().toString();
                String confirmPassword=confirm_password.getEditableText().toString();

                if(oldPassword.length()==0){
                    Method.dangerAlert(getActivity(),"Current Password field is empty");
                    return;
                }

                if(newPassword.length()==0){
                    Method.dangerAlert(getActivity(),"New Password field is empty");
                    return;
                }

                if(confirmPassword.length()==0){
                    Method.dangerAlert(getActivity(),"Confirm Password field is empty");
                    return;
                }


                if(!confirmPassword.equals(newPassword)){
                    Method.dangerAlert(getActivity(),"New password and confirm password is not same");
                    return;
                }

                if(newPassword.length()<=6){
                    Method.dangerAlert(getActivity(),"Password must be 6 characters long");
                    return;
                }

                progressbar.setVisibility(View.VISIBLE);
                APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
                Call<ResponsePojo> call=api.changePassword(oldPassword,newPassword,Method.getToken(getActivity()));
                call.enqueue(new Callback<ResponsePojo>() {
                    @Override
                    public void onResponse(Call<ResponsePojo> call, Response<ResponsePojo> response) {
                        if(response.code()==401){
                            Method.dangerAlert(getActivity(),"You are logged out! Please Login again");
                            Intent intent=new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }

                        if(response.code()==400){
                            Method.dangerAlert(getActivity(),"Password did not updated");
                        }

                        if(response.code()==200){
                            Method.successAlert(getActivity(),response.body().success);
                        }

                        progressbar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onFailure(Call<ResponsePojo> call, Throwable t) {
                        progressbar.setVisibility(View.GONE);

                    }
                });
            }
        });
        return view;
    }
}