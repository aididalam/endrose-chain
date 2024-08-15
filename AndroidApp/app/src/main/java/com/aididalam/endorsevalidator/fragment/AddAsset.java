package com.aididalam.endorsevalidator.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aididalam.endorsevalidator.MainActivity;
import com.aididalam.endorsevalidator.R;
import com.aididalam.endorsevalidator.auth.LoginActivity;
import com.aididalam.endorsevalidator.pojo.AllAsset;
import com.aididalam.endorsevalidator.pojo.CreateAssetPojo;
import com.aididalam.endorsevalidator.util.APIClient;
import com.aididalam.endorsevalidator.util.APIInterface;
import com.aididalam.endorsevalidator.util.Method;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAsset extends Fragment {
    Context context;
    TextInputEditText edit_name,edit_amount,edit_unit,edit_description;
    Button addAsset,cancelAsset,addAssetFinal;
    LinearLayout editLayout,addLayout;

    TextView finalName;
    TextView finalAmount;
    TextView finalUnit;
    TextView finalDescription;
    ProgressBar progressbar;
    public AddAsset() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Add Asset");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_add_asset, container, false);
        edit_name=view.findViewById(R.id.edit_name);
        edit_amount=view.findViewById(R.id.edit_amount);
        edit_unit=view.findViewById(R.id.edit_unit);
        edit_description=view.findViewById(R.id.edit_description);
        addAsset=view.findViewById(R.id.addAsset);
        editLayout=view.findViewById(R.id.editLayout);
        addLayout=view.findViewById(R.id.addLayout);
        cancelAsset=view.findViewById(R.id.cancelAsset);
        addAssetFinal=view.findViewById(R.id.addAssetFinal);
        finalName=view.findViewById(R.id.finalName);
        finalAmount=view.findViewById(R.id.finalAmount);
        finalUnit=view.findViewById(R.id.finalUnit);
        finalDescription=view.findViewById(R.id.finalDescription);
        progressbar=view.findViewById(R.id.progressbar);
        Sprite threeBounce = new ThreeBounce();
        threeBounce.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        progressbar.setIndeterminateDrawable(threeBounce);

        addLayout.setVisibility(View.GONE);
        editLayout.setVisibility(View.VISIBLE);

        addAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=edit_name.getEditableText().toString();
                String amount=edit_amount.getEditableText().toString();
                String unit=edit_unit.getEditableText().toString();
                String description=edit_description.getEditableText().toString();

                if(name.length()<3){
                    Method.dangerAlert(getActivity(),"Asset name must be 3 characters long");
                    return;
                }

                if(amount.equals("") || Double.valueOf(amount)==0){
                    Method.dangerAlert(getActivity(),"Amount must be greater than 0");
                    return;
                }

                if(unit.equals("")){
                    Method.dangerAlert(getActivity(),"Unit cannot be blank");
                    return;
                }

                if(description.equals("")){
                    Method.dangerAlert(getActivity(),"Description cannot be blank");
                    return;
                }

                finalName.setText(name);
                finalAmount.setText(amount);
                finalUnit.setText(unit);
                finalDescription.setText(description);

                addLayout.setVisibility(View.VISIBLE);
                editLayout.setVisibility(View.GONE);
            }
        });

        cancelAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout.setVisibility(View.GONE);
                editLayout.setVisibility(View.VISIBLE);
            }
        });

        addAssetFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbar.setVisibility(View.VISIBLE);
                APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
                Call<CreateAssetPojo> call=api.addAsset(
                        finalName.getText().toString(),
                        finalAmount.getText().toString(),
                        finalUnit.getText().toString(),
                        finalDescription.getText().toString(),
                        Method.getToken(getActivity()));

                call.enqueue(new Callback<CreateAssetPojo>() {
                    @Override
                    public void onResponse(Call<CreateAssetPojo> call, Response<CreateAssetPojo> response) {
                        progressbar.setVisibility(View.INVISIBLE);
                        if(response.code()==201){
                            Method.successAlert(getActivity(),"Asset Added.Now Please sign the asset");
                            Intent intent=new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("menu","pending_asset_sign");
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }else if(response.code()==401){
                            Method.dangerAlert(getActivity(),"You are logged out! Please Login again");
                            Intent intent=new Intent(context, LoginActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateAssetPojo> call, Throwable t) {
                        progressbar.setVisibility(View.INVISIBLE);

                        Method.dangerAlert(getActivity(),t.getMessage());

                    }
                });
            }
        });
        return view;
    }
}