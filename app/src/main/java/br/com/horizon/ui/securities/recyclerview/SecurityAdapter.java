package br.com.horizon.ui.securities.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.horizon.R;
import br.com.horizon.model.Security;

public class SecurityAdapter extends RecyclerView.Adapter<SecurityAdapter.ViewHolder> {

    private final OnItemClickListener onItemClickListener;
    private final Context context;

    private final List<Security> securities = new ArrayList<>();

    public SecurityAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View createdView = LayoutInflater.from(context).inflate(R.layout.security_item, parent, false);
        return new ViewHolder(createdView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Security security = securities.get(position);
        holder.vinculateWithModel(security);
    }

    @Override
    public int getItemCount() {
        return securities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleName;
        private final TextView interest;
        Security security;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleName = itemView.findViewById(R.id.title_name);
            interest = itemView.findViewById(R.id.net_income_value);
            configuratesOnItemClick(itemView);
        }

        public void vinculateWithModel(Security security) {
            this.security = security;
            this.titleName.setText(String.valueOf(security.getTitleName()));
            this.interest.setText(String.valueOf(security.getInterest()));
        }

        //On item click and stuff here
        private void configuratesOnItemClick(@NonNull View itemView) {
            itemView.setOnClickListener(v -> onItemClickListener
                    .onItemClick(getAdapterPosition(), security));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Integer position, Security security);
    }

    public void addAll(List<Security> securities) {
        notifyItemRangeRemoved(0, this.securities.size());
        this.securities.clear();
        this.securities.addAll(securities);
        this.notifyItemRangeInserted(0, this.securities.size());
    }

}
