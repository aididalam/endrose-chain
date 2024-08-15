package com.aididalam.endorsevalidator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aididalam.endorsevalidator.auth.LoginActivity;
import com.aididalam.endorsevalidator.pojo.Asset;
import com.aididalam.endorsevalidator.pojo.ResponsePojo;
import com.aididalam.endorsevalidator.pojo.Transfer;
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

public class TransferAssetActivity extends AppCompatActivity {
    Toolbar toolbar;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    TextView name,warning;
    TextView asset_id;
    EditText description;
    Asset asset;
    Button viewFullTransaction;
    String assetID=null;
    String assetName="";
    ImageView idIVQrcode;
    Button requestTransfer;
    ResponsePojo responsePojo;
    String owner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_asset);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Asset Info");
        progressBar=findViewById(R.id.progressbar);
        viewFullTransaction=findViewById(R.id.viewFullTransaction);
        warning=findViewById(R.id.warning);
        requestTransfer=findViewById(R.id.requestTransfer);
        linearLayout=findViewById(R.id.linearLayout);
        name=findViewById(R.id.name);
        asset_id=findViewById(R.id.asset_id);
        description=findViewById(R.id.description);
        idIVQrcode=findViewById(R.id.idIVQrcode);
        progressBar.setVisibility(View.VISIBLE);
        Sprite threeBounce = new ThreeBounce();
        threeBounce.setColor(ContextCompat.getColor(TransferAssetActivity.this, R.color.colorPrimary));
        progressBar.setIndeterminateDrawable(threeBounce);
        Intent intent = getIntent();
        assetID=intent.getStringExtra("asset_id");


        APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
        Call<Asset> call=api.asset_details(Method.getToken(TransferAssetActivity.this),assetID);
        call.enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                if(response.code()==200){
                    asset=response.body();
                    try{
                        linearLayout.setVisibility(View.VISIBLE);
                        asset_id.setText(response.body().asset_id);
                        name.setText(response.body().name);
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
                        owner=response.body().owner;
                        getPermission();


                    }catch (Exception e){
                        Method.dangerAlert(TransferAssetActivity.this,e.getMessage().toString());
                    }

                }else if(response.code()==401) {
                    Method.dangerAlert(TransferAssetActivity.this,"You are logged out! Please Login again");
                    Intent intent=new Intent(TransferAssetActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Asset> call, Throwable t) {
                Method.dangerAlert(TransferAssetActivity.this,t.getMessage().toString());
                progressBar.setVisibility(View.GONE);

            }
        });

        viewFullTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TransferAssetActivity.this, ViewFullTransactionActivity.class);
                intent.putExtra("asset_id",assetID);
                intent.putExtra("asset_name",assetName);
                startActivity(intent);
            }
        });

        requestTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String dsr=  description.getEditableText().toString();
                if(dsr.equals("")){
                   dsr= "Transferring asset from "+owner+" to"+Method.getPublicKey(TransferAssetActivity.this);
                }

                APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
                Call<Transfer> call1=api.requestAssetTransfer(assetID,String.valueOf(asset.balance),dsr,owner,Method.getToken(TransferAssetActivity.this));
                call1.enqueue(new Callback<Transfer>() {
                    @Override
                    public void onResponse(Call<Transfer> call, Response<Transfer> response) {
                        progressBar.setVisibility(View.GONE);
                        if(response.code()==201){
                            Method.successAlert(TransferAssetActivity.this, "Request sent successfully");
                        }
                        getPermission();
                    }

                    @Override
                    public void onFailure(Call<Transfer> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);

                    }
                });
            }
        });
    }

    private void getPermission(){
        progressBar.setVisibility(View.VISIBLE);
        APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
        Call<ResponsePojo> call= api.checkTransferPermission(assetID,Method.getPublicKey(TransferAssetActivity.this),Method.getToken(TransferAssetActivity.this));
        call.enqueue(new Callback<ResponsePojo>() {
            @Override
            public void onResponse(Call<ResponsePojo> call, Response<ResponsePojo> response) {
                progressBar.setVisibility(View.INVISIBLE);
                responsePojo=response.body();
                System.out.println(response.code());

                if(response.code()==200){
                    requestTransfer.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                    warning.setVisibility(View.GONE);
                }else if(response.code()==400) {
                    requestTransfer.setVisibility(View.GONE);
                    description.setVisibility(View.GONE);
                    warning.setVisibility(View.VISIBLE);
                }else if(response.code()==401) {
                    Method.dangerAlert(TransferAssetActivity.this,"You are logged out! Please Login again");
                    Intent intent=new Intent(TransferAssetActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }


                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponsePojo> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Method.dangerAlert(TransferAssetActivity.this,t.getMessage());
                requestTransfer.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
                warning.setVisibility(View.GONE);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}