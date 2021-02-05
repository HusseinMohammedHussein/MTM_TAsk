package com.e.mtmtask.Adapters;
/**
 * Created by Hussein on 2/3/2021
 */

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.e.mtmtask.Models.SourceLocationPojo;
import com.e.mtmtask.R;
import com.e.mtmtask.databinding.ItemDataBinding;

import java.util.List;

public class SourceLocationAdapter extends RecyclerView.Adapter<SourceLocationAdapter.ViewHolder> {

    public List<SourceLocationPojo> mSourceLocationPojoPojoList;
    private OnItemClickListener onItemClickListener;

    public void setResponseList(List<SourceLocationPojo> getModelList) {
        mSourceLocationPojoPojoList = getModelList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_data, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mSourceLocationPojoPojoList.get(position));
    }

    @Override
    public int getItemCount() {
        return mSourceLocationPojoPojoList != null ? mSourceLocationPojoPojoList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemDataBinding binding;

        public ViewHolder(@NonNull ItemDataBinding view) {
            super(view.getRoot());
            this.binding = view;
        }

        public void bind(final SourceLocationPojo mSourceLocationPojo) {
            binding.tvDisplayName.setText(mSourceLocationPojo.getName());

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(mSourceLocationPojo));

        }
    }

    public interface OnItemClickListener {
        void onItemClick(SourceLocationPojo pojo);

    }
}
