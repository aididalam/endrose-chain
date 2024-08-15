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
import com.aididalam.endorsevalidator.adapter.PendingTransferSignAdapter;
import com.aididalam.endorsevalidator.auth.LoginActivity;
import com.aididalam.endorsevalidator.pojo.PendingTransferSignPojo;
import com.aididalam.endorsevalidator.util.APIClient;
import com.aididalam.endorsevalidator.util.APIInterface;
import com.aididalam.endorsevalidator.util.Method;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PendingSignature extends Fragment {

    Context context;
    RecyclerView assetList;
    ProgressBar progressbar;
    PendingTransferSignAdapter adapter;
    TextView warning;
    public PendingSignature() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Sign pending Transactions");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_pending_signature, container, false);

        assetList=view.findViewById(R.id.assetList);
        warning=view.findViewById(R.id.warning);
        progressbar=view.findViewById(R.id.progressbar);
        progressbar.setVisibility(View.VISIBLE);
        Sprite threeBounce = new ThreeBounce();
        threeBounce.setColor(getActivity().getColor(R.color.blue));
        progressbar.setIndeterminateDrawable(threeBounce);
        assetList.setHasFixedSize(true);
        assetList.setLayoutManager(new LinearLayoutManager(context));
        progressbar.setVisibility(View.VISIBLE);
        APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
        Call<List<PendingTransferSignPojo>> call=api.pendingTransferSign(Method.getToken(getActivity()));
        call.enqueue(new Callback<List<PendingTransferSignPojo>>() {
            @Override
            public void onResponse(Call<List<PendingTransferSignPojo>> call, Response<List<PendingTransferSignPojo>> response) {
                if(response.code()==200){
                    try{
                        adapter=new PendingTransferSignAdapter(response.body(),getActivity(),progressbar);
                        assetList.setAdapter(adapter);
                        if(response.body().size()==0){
                            warning.setVisibility(View.VISIBLE);
                            assetList.setVisibility(View.GONE);
                        }
                    }catch (Exception e){
                        System.out.println(e.getCause().toString());
                    }
                }else if (response.code()==401){
                    Method.dangerAlert(getActivity(),"You are logged out! Please Login again");
                    Intent intent=new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }

                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<PendingTransferSignPojo>> call, Throwable t) {
                System.out.println("Hello error: "+t.getMessage());
                progressbar.setVisibility(View.GONE);

            }
        });

        return view;
    }
}