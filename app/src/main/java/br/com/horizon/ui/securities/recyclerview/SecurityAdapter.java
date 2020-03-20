package br.com.horizon.ui.securities.recyclerview;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.horizon.R;
import br.com.horizon.model.Security;

public class SecurityAdapter extends RecyclerView.Adapter<SecurityAdapter.ViewHolder> {

    private final OnItemClickListener onItemClickListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private final Context context;
    private final List<Security> securities = new ArrayList<>();
    private Dialog itemDialog;
    private TextView dName;
    private TextView dPublisher;
    private TextView dEmitter;
    private TextView dInterest;
    private TextView dEndingDate;
    private TextView dFgc;
    private TextView dMinValue;
    private TextView dLiquidity;
    private MaterialButton dGoToUrl;

    public SecurityAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View createdView = LayoutInflater.from(context).inflate(R.layout.security_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(createdView);

        this.itemDialog = new Dialog(context);
        itemDialog.setContentView(R.layout.dialog_title);
        initializeDialogViews();

        viewHolder.itemView.setOnClickListener(v -> {
            setupDialogViews(viewHolder);
            itemDialog.show();
        });


        return viewHolder;
    }

    private void setupDialogViews(ViewHolder viewHolder) {
        dName.setText(securities.get(viewHolder.getAdapterPosition()).getTitleName());
        dPublisher.setText(securities.get(viewHolder.getAdapterPosition()).getPublisher());
        dEmitter.setText(securities.get(viewHolder.getAdapterPosition()).getEmitter());
        dInterest.setText(String.valueOf(securities.get(viewHolder.getAdapterPosition()).getInterest()));
        dEndingDate.setText(dateFormat.format(securities.get(viewHolder.getAdapterPosition()).getEndingDate()));
        dFgc.setText(String.valueOf(securities.get(viewHolder.getAdapterPosition()).getFgc()));
        dMinValue.setText(String.valueOf(securities.get(viewHolder.getAdapterPosition()).getTitleValue()));
        dLiquidity.setText(String.valueOf(securities.get(viewHolder.getAdapterPosition()).getLiquidity()));
    }

    private void initializeDialogViews() {
        dName = itemDialog.findViewById(R.id.dialog_title_name);
        dPublisher = itemDialog.findViewById(R.id.dialog_title_publisher);
        dEmitter = itemDialog.findViewById(R.id.dialog_title_emitter);
        dInterest = itemDialog.findViewById(R.id.dialog_title_interest);
        dEndingDate = itemDialog.findViewById(R.id.dialog_title_ending_date);
        dFgc = itemDialog.findViewById(R.id.dialog_title_fgc);
        dMinValue = itemDialog.findViewById(R.id.dialog_title_min_value);
        dLiquidity = itemDialog.findViewById(R.id.dialog_title_liquidity);
        dGoToUrl = itemDialog.findViewById(R.id.dialog_title_goto_url);
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
