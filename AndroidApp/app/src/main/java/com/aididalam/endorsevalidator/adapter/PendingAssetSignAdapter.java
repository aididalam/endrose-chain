package com.aididalam.endorsevalidator.adapter;

import android.content.Context;
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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aididalam.endorsevalidator.R;
import com.aididalam.endorsevalidator.pojo.PendingAssetPojo;
import com.aididalam.endorsevalidator.util.APIClient;
import com.aididalam.endorsevalidator.util.APIInterface;
import com.aididalam.endorsevalidator.util.Method;
import com.google.gson.JsonElement;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingAssetSignAdapter extends RecyclerView.Adapter<PendingAssetSignAdapter.ViewHolder>{
    List<PendingAssetPojo> pendingAssetList;
    Context context;
    public PendingAssetSignAdapter(List<PendingAssetPojo> pendingAssetList, Context context) {
        this.pendingAssetList=pendingAssetList;
        this.context=context;
    }

    @NonNull
    @Override
    public PendingAssetSignAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view= layoutInflater.inflate(R.layout.adapter_pending_transfer_sign, parent, false);
        PendingAssetSignAdapter.ViewHolder viewHolder = new PendingAssetSignAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PendingAssetSignAdapter.ViewHolder holder, int position) {
        PendingAssetPojo pojo=pendingAssetList.get(position);
        holder.name.setText(pojo.name);
        System.out.println(pojo.asset_id);
        holder.total_size.setText("Total: "+pojo.amount+" "+pojo.unit);
        holder.description.setText("Description: "+pojo.description);
        holder.request_on.setText("Request on: "+pojo.created_at);
        holder.request_by.setVisibility(View.GONE);

        holder.signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.signButton.setText("Singing");
                holder.signButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
                holder.signButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
                holder.signButton.setEnabled(false);

                APIInterface api = APIClient.getRetrofit().create(APIInterface.class);
                System.out.println("Clicked");

                Call<JsonElement> call=api.signAsset(pojo.asset_id, Method.getToken(context));
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        System.out.println(pojo.id);
                        if(response.code()==201){
                            pendingAssetList.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        System.out.println(t.getMessage().toString());
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return pendingAssetList.size();
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
