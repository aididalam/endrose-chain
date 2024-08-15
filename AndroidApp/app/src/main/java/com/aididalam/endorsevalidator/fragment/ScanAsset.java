package com.aididalam.endorsevalidator.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.aididalam.endorsevalidator.AssetDetailsActivity;
import com.aididalam.endorsevalidator.MainActivity;
import com.aididalam.endorsevalidator.R;
import com.aididalam.endorsevalidator.util.Method;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;


public class ScanAsset extends Fragment {
    private CodeScanner mCodeScanner;
    FrameLayout scannerContainer;
    LinearLayout introContainer;
    CodeScannerView scannerView;
    public ScanAsset(MainActivity mainActivity) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_scan_asset, container, false);
        scannerView = view.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(getActivity(), scannerView);
        scannerView.setVisibility(View.VISIBLE);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                System.out.println(result.getText());
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Method.successAlert(getActivity(),"Scanned Text: "+result.getText());
                    }
                });

                Intent intent=new Intent(getActivity(), AssetDetailsActivity.class);
                intent.putExtra("asset_id",result.getText());
                intent.putExtra("tr",result.getText());
                getActivity().startActivity(intent);
            }


        });


        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCodeScanner.startPreview();
            }
        });

        return view;
    }
}