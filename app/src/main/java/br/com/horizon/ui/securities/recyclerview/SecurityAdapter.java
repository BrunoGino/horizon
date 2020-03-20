package br.com.horizon.ui.securities.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
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
        holder.bindWithModel(security);
    }

    @Override
    public int getItemCount() {
        return securities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleName;
        private TextView interest;
        private TextView endingDate;
        private TextView description;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");


        private Security security;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            bindViewObjects(itemView);
            configuratesOnItemClick(itemView);
        }

        private void bindViewObjects(View view) {
            this.description = view.findViewById(R.id.security_description_value);
            this.titleName = view.findViewById(R.id.security_name_value);
            this.interest = view.findViewById(R.id.net_income_value);
            this.endingDate = view.findViewById(R.id.ending_date_value);

        }

        public void bindWithModel(Security security) {
            this.security = security;
            this.titleName.setText(security.getTitleName());
            this.endingDate.setText(dateFormat.format(security.getEndingDate()));
            this.interest.setText(String.valueOf(security.getInterest()));
            this.description.setText(security.getEmitter());
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
