package br.com.horizon.ui.securities.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.horizon.databinding.SecurityItemBinding;
import br.com.horizon.model.Security;

public class SecurityAdapter extends RecyclerView.Adapter<SecurityAdapter.ViewHolder> {

    private final OnItemClickListener onItemClickListener;
    private final Context context;
    private final List<Security> securities = new ArrayList<>();
    private SecurityItemBinding binder;

    public SecurityAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.binder = SecurityItemBinding.inflate(LayoutInflater.from(context), parent, false);

        return new ViewHolder(binder);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Security security = securities.get(position);
        holder.vinculate(security);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        //Set holder as Active
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        //Set holder as inactive
    }


    @Override
    public int getItemCount() {
        return securities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        private Security security;

        ViewHolder(SecurityItemBinding binding) {
            super(binding.getRoot());
            binder.setItemClick(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(security);
        }

        void vinculate(Security security) {
            this.security = security;
            binder.setSecurity(security);//Needs to be SecurityData
            binder.setDateFormat(dateFormat);
        }

    }

    public void addAll(List<Security> securities) {
        notifyItemRangeRemoved(0, this.securities.size());
        this.securities.clear();
        this.securities.addAll(securities);
        this.notifyItemRangeInserted(0, this.securities.size());
    }


    public interface OnItemClickListener {
        void onItemClick(Security security);
    }

}
