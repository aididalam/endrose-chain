package com.aididalam.endorsevalidator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.aididalam.endorsevalidator.adapter.MyAssetTransactionHistoryAdapter;
import com.aididalam.endorsevalidator.auth.LoginActivity;
import com.aididalam.endorsevalidator.pojo.AssetTransactionPojo;
import com.aididalam.endorsevalidator.util.APIClient;
import com.aididalam.endorsevalidator.util.APIInterface;
import com.aididalam.endorsevalidator.util.Method;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewFullTransactionActivity extends AppCompatActivity {
    Toolbar toolbar;
    String assetID="";
    String assetName="";
    RecyclerView assetHistory;
    ProgressBar progressbar;
    MyAssetTransactionHistoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_transaction);
        toolbar=findViewById(R.id.toolbar);
        assetHistory=findViewById(R.id.assetHistory);
        progressbar=findViewById(R.id.progressbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Asset Transaction");
        Intent intent = getIntent();
        assetID=intent.getStringExtra("asset_id");
        assetName=intent.getStringExtra("asset_name");
        getSupportActionBar().setTitle("Asset: "+assetName);
        getSupportActionBar().setSubtitle("Transaction History");
        Sprite threeBounce = new ThreeBounce();
        threeBounce.setColor(ContextCompat.getColor(ViewFullTransactionActivity.this, R.color.colorPrimary));
        progressbar.setIndeterminateDrawable(threeBounce);
        progressbar.setVisibility(View.VISIBLE);
        assetHistory.setHasFixedSize(true);
        assetHistory.setLayoutManager(new LinearLayoutManager(ViewFullTransactionActivity.this));
        APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
        Call<List<AssetTransactionPojo>> call=api.asset_transaction(assetID);

        call.enqueue(new Callback<java.util.List<AssetTransactionPojo>>() {
            @Override
            public void onResponse(Call<List<AssetTransactionPojo>> call, Response<List<AssetTransactionPojo>> response) {

                if(response.code()==200){
                    adapter=new MyAssetTransactionHistoryAdapter(ViewFullTransactionActivity.this, response.body());
                    assetHistory.setAdapter(adapter);
                    System.out.println(response.body().toString());

                }else if (response.code()==401){
                    Method.dangerAlert(ViewFullTransactionActivity.this,"You are logged out! Please Login again");
                    Intent intent=new Intent(ViewFullTransactionActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<AssetTransactionPojo>> call, Throwable t) {
                Method.dangerAlert(ViewFullTransactionActivity.this,t.getMessage().toString());
                progressbar.setVisibility(View.GONE);
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}