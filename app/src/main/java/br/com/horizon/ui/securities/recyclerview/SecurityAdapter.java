package br.com.horizon.ui.securities.recyclerview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.horizon.databinding.SecurityItemBinding;
import br.com.horizon.model.Security;
import br.com.horizon.ui.databinding.ObservableSecurity;
import lombok.Getter;

public class SecurityAdapter extends ListAdapter<Security, SecurityAdapter.ViewHolder> {
    private final OnItemClickListener onItemClickListener;
    private final Context context;
    private final NumberFormat percentageFormatter;
    private SecurityItemBinding binder;
    private NumberFormat currencyFormatter;

    public SecurityAdapter(Context context, OnItemClickListener onItemClickListener) {
        super(new DiffCallback());
        this.onItemClickListener = onItemClickListener;
        this.context = context;
        this.currencyFormatter = NumberFormat.getCurrencyInstance();
        this.percentageFormatter = NumberFormat.getPercentInstance();
        this.percentageFormatter.setMaximumFractionDigits(2);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binder = SecurityItemBinding.inflate(LayoutInflater.from(context), parent, false);
        binder.setCurrencyFormatter(currencyFormatter);
        binder.setPercentageFormatter(percentageFormatter);
        ViewHolder viewHolder = new ViewHolder(binder);
        binder.setLifecycleOwner(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("ADAPTERPOSITION", "onBindViewHolder: " + position);
        holder.link(getItem(position));
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.markAsActive();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.markAsDestroyed();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, LifecycleOwner {
        private Security security;
        private final LifecycleRegistry registry = new LifecycleRegistry(this);

        ViewHolder(SecurityItemBinding binding) {
            super(binding.getRoot());
            registry.setCurrentState(Lifecycle.State.INITIALIZED);
            binder.setItemClick(this);
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
            binder.setDateFormat(dateFormat);
        }

        private void markAsActive() {
            registry.setCurrentState(Lifecycle.State.STARTED);
        }

        private void markAsDestroyed() {
            registry.setCurrentState(Lifecycle.State.DESTROYED);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(security);
        }

        void link(Security security) {
            this.security = security;
            ObservableSecurity observableSecurity = new ObservableSecurity(security);
            binder.setSecurity(observableSecurity);
        }

        @NonNull
        @Override
        public Lifecycle getLifecycle() {
            return registry;
        }
    }

    public static class DiffCallback extends DiffUtil.ItemCallback<Security> {
        @Override
        public boolean areItemsTheSame(@NonNull Security oldItem, @NonNull Security newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Security oldItem, @NonNull Security newItem) {
            return oldItem.equals(newItem);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Security security);
    }

}
