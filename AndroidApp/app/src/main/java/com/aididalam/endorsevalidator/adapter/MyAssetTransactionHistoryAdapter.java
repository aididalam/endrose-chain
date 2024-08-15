package com.aididalam.endorsevalidator.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aididalam.endorsevalidator.R;
import com.aididalam.endorsevalidator.ViewFullTransactionActivity;
import com.aididalam.endorsevalidator.pojo.AssetTransactionPojo;
import com.aididalam.endorsevalidator.util.Constant;

import java.util.List;

public class MyAssetTransactionHistoryAdapter extends RecyclerView.Adapter<MyAssetTransactionHistoryAdapter.ViewHolder>{
   Context context;
    List<AssetTransactionPojo> transaction;
    String unit="";
    public MyAssetTransactionHistoryAdapter(Context context, List<AssetTransactionPojo> transaction) {
        this.context=context;
        this.transaction=transaction;
    }

    @NonNull
    @Override
    public MyAssetTransactionHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view= layoutInflater.inflate(R.layout.my_asset_history, parent, false);
        MyAssetTransactionHistoryAdapter.ViewHolder viewHolder = new MyAssetTransactionHistoryAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAssetTransactionHistoryAdapter.ViewHolder holder, int position) {
        AssetTransactionPojo trans=transaction.get(position);
        holder.time.setText(trans.data.time);
        holder.event.setText(trans.data.data.event);
        if(holder.event.getText().equals("mint")){
            holder.cardLayout.setBackgroundColor(Color.parseColor("#ccddff"));
        }

        if(holder.event.getText().equals("dead")){
            holder.cardLayout.setBackgroundColor(Color.parseColor("#ff6666"));
        }
        holder.sender.setText(trans.sender);
        holder.sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Check sender profile?")
                        .setMessage("Do you want to "+trans.sender+"'s profile?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse(Constant.api+"/frontend/address/?adr="+trans.data.data.from);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        holder.receiver.setText(trans.receiver);

        holder.receiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Check receiver profile?")
                        .setMessage("Do you want to "+trans.receiver+"'s profile?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse(Constant.api+"/frontend/address/?adr="+trans.data.data.to);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        if(unit.equals("")){
            unit=trans.data.data.transaction.unit;
        }
        holder.amount.setText(trans.data.data.transaction.amount+" "+unit);
        holder.description.setText(trans.data.data.transaction.description);
        holder.nonceInfo.setText(String.valueOf(trans.data.nonce));
        holder.checkTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Constant.api+"/frontend/transactions/"+trans.hash);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transaction.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

       TextView time,event,sender,receiver,amount,description,nonceInfo;
       CardView cardLayout;
       Button checkTransaction;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time=itemView.findViewById(R.id.time);
            event=itemView.findViewById(R.id.event);
            sender=itemView.findViewById(R.id.sender);
            receiver=itemView.findViewById(R.id.receiver);
            amount=itemView.findViewById(R.id.amount);
            description=itemView.findViewById(R.id.description);
            nonceInfo=itemView.findViewById(R.id.nonceInfo);
            checkTransaction=itemView.findViewById(R.id.checkTransaction);
            cardLayout=itemView.findViewById(R.id.cardLayout);

        }
    }
}
