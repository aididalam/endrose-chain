package com.aididalam.endorsevalidator.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aididalam.endorsevalidator.R;
import com.aididalam.endorsevalidator.UpdateProfileActivity;
import com.aididalam.endorsevalidator.adapter.AssetListAdapter;
import com.aididalam.endorsevalidator.auth.LoginActivity;
import com.aididalam.endorsevalidator.pojo.PendingTransferSignPojo;
import com.aididalam.endorsevalidator.pojo.UserFullDetails;
import com.aididalam.endorsevalidator.util.APIClient;
import com.aididalam.endorsevalidator.util.APIInterface;
import com.aididalam.endorsevalidator.util.Constant;
import com.aididalam.endorsevalidator.util.Method;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Profile extends Fragment {

    TextView name;
    TextView type;
    TextView email;
    TextView description;
    TextView publickey;
    TextView privatekey;
    TextView totalAsset;
    TextView totalTransaction;
    ImageView profileImg;

    Button editProfile;

    ProgressBar progressbar;

    public Profile() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Profile");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_profile, container, false);
        name=view.findViewById(R.id.name);
        type=view.findViewById(R.id.type);
        email=view.findViewById(R.id.email);
        editProfile=view.findViewById(R.id.editProfile);
        description=view.findViewById(R.id.description);
        publickey=view.findViewById(R.id.publickey);
        privatekey=view.findViewById(R.id.privatekey);
        profileImg=view.findViewById(R.id.profileImg);
        progressbar=view.findViewById(R.id.progressbar);
        totalAsset=view.findViewById(R.id.totalAsset);
        totalTransaction=view.findViewById(R.id.totalTransaction);
        Sprite threeBounce = new ThreeBounce();
        threeBounce.setColor(getActivity().getColor(R.color.blue));
        progressbar.setIndeterminateDrawable(threeBounce);
        progressbar.setVisibility(View.VISIBLE);

        loadProfileData();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), UpdateProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });




        return view;
    }

    private void loadProfileData() {
        APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
        Call<UserFullDetails> call=api.profileFullInfo(Method.getToken(getActivity()));
        call.enqueue(new Callback<UserFullDetails>() {
            @Override
            public void onResponse(Call<UserFullDetails> call, Response<UserFullDetails> response) {
                progressbar.setVisibility(View.INVISIBLE);
                if(response.code()==200){
                    name.setText(response.body().name);
                    type.setText(response.body().type);
                    email.setText(response.body().email);
                    description.setText(response.body().description);
                    publickey.setText(response.body().publickey);
                    privatekey.setText(response.body().privatekey);
                    totalTransaction.setText(response.body().total_transaction);
                    totalAsset.setText(response.body().total_asset);
                    Picasso.get().load(Constant.api+"/public/profileIMG/"+response.body().img)
                            .error(R.mipmap.ic_launcher_img)
                            .placeholder(R.mipmap.ic_launcher_img)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(profileImg);
                }else if (response.code()==401){
                    Method.dangerAlert(getActivity(),"You are logged out! Please Login again");
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<UserFullDetails> call, Throwable t) {
                progressbar.setVisibility(View.INVISIBLE);
                Method.dangerAlert(getActivity(),t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileData();
    }
}