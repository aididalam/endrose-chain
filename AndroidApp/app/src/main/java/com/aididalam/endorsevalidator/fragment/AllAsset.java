package com.aididalam.endorsevalidator.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aididalam.endorsevalidator.R;
import com.aididalam.endorsevalidator.adapter.AssetListAdapter;
import com.aididalam.endorsevalidator.auth.LoginActivity;
import com.aididalam.endorsevalidator.util.APIClient;
import com.aididalam.endorsevalidator.util.APIInterface;
import com.aididalam.endorsevalidator.util.Method;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllAsset extends Fragment {
    Context context;
    RecyclerView assetList;
    ProgressBar progressbar;
    AssetListAdapter adapter;
    TextView warning;
    String state;
    public AllAsset(Context context, String state) {
        this.context=context;
        this.state=state;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Endorse Validator");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_all_asset, container, false);
        assetList=view.findViewById(R.id.assetList);
        warning=view.findViewById(R.id.warning);
        progressbar=view.findViewById(R.id.progressbar);
        progressbar.setVisibility(View.VISIBLE);
        Sprite threeBounce = new ThreeBounce();
        threeBounce.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        progressbar.setIndeterminateDrawable(threeBounce);
        assetList.setHasFixedSize(true);
        assetList.setLayoutManager(new LinearLayoutManager(context));

        APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
        Call<com.aididalam.endorsevalidator.pojo.AllAsset> call=api.my_asset(Method.getToken(context));

        call.enqueue(new Callback<com.aididalam.endorsevalidator.pojo.AllAsset>() {
            @Override
            public void onResponse(Call<com.aididalam.endorsevalidator.pojo.AllAsset> call, Response<com.aididalam.endorsevalidator.pojo.AllAsset> response) {
                if(response.code()==200){
                    com.aididalam.endorsevalidator.pojo.AllAsset allAsset=response.body();
                    try{
                        System.out.println("Aidid+ "+state);

                        if(state.equals("Current")){
                            adapter=new AssetListAdapter(allAsset.current,getActivity(),state);
                            assetList.setAdapter(adapter);

                            if(allAsset.current.size()==0){
                                warning.setVisibility(View.VISIBLE);
                                assetList.setVisibility(View.GONE);
                            }
                        }else{
                            adapter=new AssetListAdapter(allAsset.history,getActivity(),state);
                            assetList.setAdapter(adapter);

                            if(allAsset.history.size()==0){
                                warning.setVisibility(View.VISIBLE);
                                assetList.setVisibility(View.GONE);
                            }
                        }

                    }catch (Exception e){
                        System.out.println(e.getCause().toString());
                    }
                }else if (response.code()==401){
                    Method.dangerAlert(getActivity(),"You are logged out! Please Login again");
                    Intent intent=new Intent(context, LoginActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }

                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<com.aididalam.endorsevalidator.pojo.AllAsset> call, Throwable t) {
                progressbar.setVisibility(View.GONE);
                Method.dangerAlert(context,t.getMessage().toString());

            }
        });

        return view;
    }
}