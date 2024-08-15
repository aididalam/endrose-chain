package com.aididalam.endorsevalidator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aididalam.endorsevalidator.auth.LoginActivity;
import com.aididalam.endorsevalidator.pojo.Asset;
import com.aididalam.endorsevalidator.util.APIClient;
import com.aididalam.endorsevalidator.util.APIInterface;
import com.aididalam.endorsevalidator.util.Constant;
import com.aididalam.endorsevalidator.util.Method;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    ProgressBar progressBar;
    LinearLayout linearLayout,fakeData;
    TextView amount;
    TextView name;
    TextView asset_id;
    TextView minted_on;
    TextView description;
    Button viewFullTransaction,getAsset,shareQr,lost;
    String assetID=null;
    String assetName="";
    ImageView idIVQrcode;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_details);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Asset Info");
        progressBar=findViewById(R.id.progressbar);
        viewFullTransaction=findViewById(R.id.viewFullTransaction);
        getAsset=findViewById(R.id.getAsset);
        lost=findViewById(R.id.lost);
        linearLayout=findViewById(R.id.linearLayout);
        fakeData=findViewById(R.id.fakeData);
        amount=findViewById(R.id.amount);
        shareQr=findViewById(R.id.shareQr);
        name=findViewById(R.id.name);
        asset_id=findViewById(R.id.asset_id);
        minted_on=findViewById(R.id.minted_on);
        description=findViewById(R.id.description);
        idIVQrcode=findViewById(R.id.idIVQrcode);
        progressBar.setVisibility(View.VISIBLE);
        Sprite threeBounce = new ThreeBounce();
        threeBounce.setColor(ContextCompat.getColor(AssetDetailsActivity.this, R.color.colorPrimary));
        progressBar.setIndeterminateDrawable(threeBounce);
        Intent intent = getIntent();
        assetID=intent.getStringExtra("asset_id");
        if(intent.getStringExtra("tr")!=null){
            getAsset.setVisibility(View.VISIBLE);
        }

        if(intent.getStringExtra("lost")!=null){
            lost.setVisibility(View.VISIBLE);
        }

        APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
        Call<Asset> call=api.asset_details(Method.getToken(AssetDetailsActivity.this),assetID);
        call.enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                if(response.code()==200){

                    try{
                        linearLayout.setVisibility(View.VISIBLE);
                        name.setText(response.body().name);
                        amount.setText(response.body().balance+" "+response.body().unit);
                        asset_id.setText(response.body().asset_id);
                        name.setText(response.body().name);
                        minted_on.setText(response.body().created_at);
                        description.setText(response.body().description);
                        getSupportActionBar().setTitle("Asset: "+response.body().name);
                        asset_id.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.api+"/frontend/assetsearch?hash="+response.body().asset_id));
                                startActivity(browserIntent);
                            }
                        });

                        generateQR(response.body().asset_id);


                        assetName=response.body().name;

                    }catch (Exception e){
                        Method.dangerAlert(AssetDetailsActivity.this,e.getMessage().toString());
                    }

                }else if(response.code()==401) {
                    Method.dangerAlert(AssetDetailsActivity.this,"You are logged out! Please Login again");
                    Intent intent=new Intent(AssetDetailsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    linearLayout.setVisibility(View.INVISIBLE);
                    fakeData.setVisibility(View.VISIBLE);
                }



                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Asset> call, Throwable t) {
                Method.dangerAlert(AssetDetailsActivity.this,t.getMessage().toString());
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.INVISIBLE);

            }
        });

        viewFullTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AssetDetailsActivity.this, ViewFullTransactionActivity.class);
                intent.putExtra("asset_id",assetID);
                intent.putExtra("asset_name",assetName);
                startActivity(intent);
            }
        });

        getAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AssetDetailsActivity.this, TransferAssetActivity.class);
                intent.putExtra("asset_id",assetID);
                intent.putExtra("asset_name",assetName);
                startActivity(intent);
            }
        });

        lost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AssetDetailsActivity.this, LostActivity.class);
                intent.putExtra("asset_id",assetID);
                intent.putExtra("asset_name",assetName);
                startActivity(intent);
            }
        });

    }

    private void generateQR(String asset_id) {
        Bitmap bitmap;
        QRGEncoder qrgEncoder;
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;
        qrgEncoder = new QRGEncoder(asset_id, null, QRGContents.Type.TEXT, dimen);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            idIVQrcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.e("Tag", e.toString());
        }
        shareQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable mDrawable = idIVQrcode.getDrawable();
                Bitmap mBitmap = ((BitmapDrawable)mDrawable).getBitmap();
                String path = MediaStore.Images.Media.insertImage(AssetDetailsActivity.this.getContentResolver(),
                        mBitmap, assetName, null);
                Uri uri = Uri.parse(path);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.putExtra(Intent.EXTRA_TEXT, "Asset Name: "+assetName+"\nAsset ID: "+asset_id+"\nAsset Explorer: "+Constant.api+"/frontend/asset/"+asset_id);
                startActivity(Intent.createChooser(share, "Share "+assetName+"'s QR"));
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