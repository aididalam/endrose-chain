package com.aididalam.endorsevalidator.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aididalam.endorsevalidator.AssetDetailsActivity;
import com.aididalam.endorsevalidator.R;
import com.aididalam.endorsevalidator.pojo.Asset;

import java.util.List;

public class AssetListAdapter extends RecyclerView.Adapter<AssetListAdapter.ViewHolder> {
    List<Asset> assetList;
    Context context;
    String state;
    public AssetListAdapter(List<Asset> assetList, Context context, String state) {
        this.assetList=assetList;
        this.context=context;
        this.state=state;
    }

    @NonNull
    @Override
    public AssetListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view= layoutInflater.inflate(R.layout.all_asset_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AssetListAdapter.ViewHolder holder, int position) {
        Asset asset=assetList.get(position);
        holder.name.setText(asset.name);
        holder.total_size.setText("Total Size : "+asset.balance+" "+asset.unit);
        holder.description.setText(asset.description);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, AssetDetailsActivity.class);
                intent.putExtra("asset_id",asset.asset_id);
                if(state.equals("Current")){
                    intent.putExtra("lost","lost");
                }
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return assetList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView total_size;
        TextView description;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            total_size=itemView.findViewById(R.id.total_size);
            description=itemView.findViewById(R.id.description);
            linearLayout=itemView.findViewById(R.id.linearLayout);

        }
    }
}
