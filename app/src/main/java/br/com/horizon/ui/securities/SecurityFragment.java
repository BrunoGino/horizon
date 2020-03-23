package br.com.horizon.ui.securities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

import br.com.horizon.R;
import br.com.horizon.model.Security;
import br.com.horizon.ui.securities.recyclerview.SecurityAdapter;

public class SecurityFragment extends Fragment {

    private SecurityViewModel securityViewModel;
    private SecurityAdapter securityAdapter;
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
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        securityViewModel = ViewModelProviders.of(this).get(SecurityViewModel.class);

        root = inflater.inflate(R.layout.fragment_securities, container, false);
        setupRecyclerView(root);

        createDialog();

        pullSecurities();

        return root;
    }

    private void createDialog() {
        itemDialog = new Dialog(root.getContext());
        itemDialog.setContentView(R.layout.dialog_title);

        Objects.requireNonNull(itemDialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void pullSecurities() {
        securityViewModel.fetchAll().observe(this, listResource -> {
            if (listResource.getData() != null) {
                securityAdapter.addAll(listResource.getData());
            } else {
                Snackbar.make(root, listResource.getError(), 3000).show();
            }
        });
    }

    private void setupRecyclerView(View root) {
        RecyclerView recyclerView = root.findViewById(R.id.securities_recycler);
        securityAdapter = new SecurityAdapter(root.getContext(), (position, security)
                -> {
            fillUpDialog(security);
            itemDialog.show();
        });
        recyclerView.setAdapter(securityAdapter);
    }

    private void fillUpDialog(Security security) {
        initializeDialogViews();
        dName.setText(security.getTitleName());
        dPublisher.setText(security.getPublisher());
        dEmitter.setText(security.getEmitter());
        dInterest.setText(String.valueOf(security.getInterest()));
        dEndingDate.setText(dateFormat.format(security.getEndingDate()));
        dFgc.setText(String.valueOf(security.getFgc()));
        dMinValue.setText(String.valueOf(security.getTitleValue()));
        dLiquidity.setText(String.valueOf(security.getLiquidity()));

        Toast.makeText(root.getContext(), security.getUrl(), Toast.LENGTH_LONG).show();

        dGoToUrl.setOnClickListener(v -> {
            Uri webpage = Uri.parse(security.getUrl());
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);

            if (validateUrl(webIntent)) {
                startActivity(webIntent);
            }
        });
    }

    private boolean validateUrl(Intent webIntent) {
        PackageManager packageManager = root.getContext().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(webIntent, 0);
        return activities.size() > 0;
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

}