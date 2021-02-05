package com.e.mtmtask.Adapters;

/**
 * Created by Hussein on 04/02/2021
 */

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.e.mtmtask.Models.DestinationLocationPojo.CandidatePojo;
import com.e.mtmtask.R;
import com.e.mtmtask.databinding.ItemDataBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DestinationLocationAdapter extends RecyclerView.Adapter<DestinationLocationAdapter.DestinationLocationViewHolder> {

    private List<CandidatePojo> mDestinationLocationPojoList;


    public void setResponseList(List<CandidatePojo> getModelList) {
        mDestinationLocationPojoList = getModelList;
    }

    @NonNull
    @Override
    public DestinationLocationViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        ItemDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_data, parent, false);
        return new DestinationLocationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationLocationViewHolder holder, int position) {
        holder.bind(mDestinationLocationPojoList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDestinationLocationPojoList != null ? mDestinationLocationPojoList.size() : 0;
    }

    class DestinationLocationViewHolder extends RecyclerView.ViewHolder {
        ItemDataBinding binding;

        public DestinationLocationViewHolder(@NonNull ItemDataBinding view) {
            super(view.getRoot());
            this.binding = view;
        }

        public void bind(final CandidatePojo pojo) {
            binding.tvDisplayName.setText(pojo.getName());
        }
    }
}
