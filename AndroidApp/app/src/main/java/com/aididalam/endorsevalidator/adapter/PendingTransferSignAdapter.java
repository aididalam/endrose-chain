package com.aididalam.endorsevalidator.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aididalam.endorsevalidator.AssetDetailsActivity;
import com.aididalam.endorsevalidator.R;
import com.aididalam.endorsevalidator.pojo.PendingTransferSignPojo;
import com.aididalam.endorsevalidator.pojo.Transfer;
import com.aididalam.endorsevalidator.util.APIClient;
import com.aididalam.endorsevalidator.util.APIInterface;
import com.aididalam.endorsevalidator.util.Method;
import com.google.gson.JsonElement;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingTransferSignAdapter extends RecyclerView.Adapter<PendingTransferSignAdapter.ViewHolder>{
    List<PendingTransferSignPojo> pendingTransfer;
    Context context;
    ProgressBar progressBar;
    public PendingTransferSignAdapter(List<PendingTransferSignPojo> pendingTransfer, Context context, ProgressBar progressbar) {
        this.pendingTransfer=pendingTransfer;
        this.context=context;
        this.progressBar=progressBar;
    }

    @NonNull
    @Override
    public PendingTransferSignAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view= layoutInflater.inflate(R.layout.adapter_pending_transfer_sign, parent, false);
        PendingTransferSignAdapter.ViewHolder viewHolder = new PendingTransferSignAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PendingTransferSignAdapter.ViewHolder holder, int position) {
        PendingTransferSignPojo pojo=pendingTransfer.get(position);
        holder.name.setText(pojo.name);
        holder.total_size.setText("Total: "+pojo.amount+" "+pojo.unit);
        holder.description.setText("Description: "+pojo.description);
        holder.request_by.setText("Request by: "+pojo.request_by);
        holder.request_on.setText("Request on: "+pojo.created_at);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, AssetDetailsActivity.class);
                intent.putExtra("asset_id",pojo.asset_id);
                context.startActivity(intent);
            }
        });

        holder.signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.signButton.setText("Singing");
                holder.signButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
                holder.signButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
                holder.signButton.setEnabled(false);

                APIInterface api = APIClient.getRetrofit().create(APIInterface.class);

                Call<JsonElement> call=api.signTransfer(pojo.pending_id,Method.getToken(context));
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if(response.code()==201){
                            pendingTransfer.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                        }

                        System.out.println(response.code());
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        System.out.println("Error: "+t.getMessage());
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return pendingTransfer.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView total_size;
        TextView description;
        TextView request_by;
        TextView request_on;
        LinearLayout linearLayout;
        Button signButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            total_size=itemView.findViewById(R.id.total_size);
            description=itemView.findViewById(R.id.description);
            request_by=itemView.findViewById(R.id.request_by);
            request_on=itemView.findViewById(R.id.request_on);
            linearLayout=itemView.findViewById(R.id.linearLayout);
            signButton=itemView.findViewById(R.id.signButton);
        }
    }
}
