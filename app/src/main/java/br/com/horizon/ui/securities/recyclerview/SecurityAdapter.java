package br.com.horizon.ui.securities.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.horizon.databinding.SecurityItemBinding;
import br.com.horizon.model.Security;
import br.com.horizon.ui.databinding.ObservableSecurity;

public class SecurityAdapter extends RecyclerView.Adapter<SecurityAdapter.ViewHolder> {
    private final OnItemClickListener onItemClickListener;
    private final Context context;
    private final List<Security> securities = new ArrayList<>();
    private final NumberFormat percentageFormatter;
    private SecurityItemBinding binder;
    private NumberFormat currencyFormatter;

    public SecurityAdapter(Context context, OnItemClickListener onItemClickListener) {
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
        return new ViewHolder(binder);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        binder.setLifecycleOwner(holder);
        Security security = securities.get(position);
        holder.vinculate(security);
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

    @Override
    public long getItemId(int position) {
        return securities.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return securities.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, LifecycleOwner {
        private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        private Security security;
        private final LifecycleRegistry registry = new LifecycleRegistry(this);

        ViewHolder(SecurityItemBinding binding) {
            super(binding.getRoot());
            registry.setCurrentState(Lifecycle.State.INITIALIZED);
            binder.setItemClick(this);
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

        void vinculate(Security security) {
            this.security = security;
            ObservableSecurity observableSecurity = new ObservableSecurity(this.security);
            binder.setSecurity(observableSecurity);
            binder.setDateFormat(dateFormat);
        }

        @NonNull
        @Override
        public Lifecycle getLifecycle() {
            return registry;
        }
    }

    public void addAll(List<Security> securities) {
//        notifyItemRangeRemoved(0, this.securities.size());
        this.securities.clear();
        this.securities.addAll(securities);
//        notifyItemRangeInserted(0, this.securities.size());
    }

    public interface OnItemClickListener {
        void onItemClick(Security security);
    }

}
