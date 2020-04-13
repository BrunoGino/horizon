package br.com.horizon.ui.securities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

import br.com.horizon.MainActivity;
import br.com.horizon.databinding.SecurityDetailsBinding;
import br.com.horizon.ui.VisualComponents;

public class SecurityDetailsFragment extends Fragment {
    private SimpleDateFormat dateFormat;
    private SecurityViewModel securityViewModel;
    private SecurityDetailsBinding dataBinder;
    private String securityId;

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.securityViewModel = new SecurityViewModel();
        this.dateFormat = new SimpleDateFormat("dd/MM/yy");
        Bundle bundle = Objects.requireNonNull(getArguments());
        this.securityId = SecurityDetailsFragmentArgs.fromBundle(bundle).getSecurityId();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.getAppStateViewModel()
                .setComponents(new VisualComponents(false, false));
        dataBinder = SecurityDetailsBinding.inflate(inflater, container, false);
        setupSecurity();
        return dataBinder.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUrlClickListener();
    }

    private void setupUrlClickListener() {
        this.dataBinder.setUrlClick(v -> redirectsToBrowserIfUrlIsValid());
    }

    private void redirectsToBrowserIfUrlIsValid() {
        Uri uri = Uri.parse(dataBinder.getSecurity().getUrl());
        Intent webIntent = new Intent(Intent.ACTION_VIEW, uri);
        if (validateUrl(webIntent)) {
            startActivity(webIntent);
        }
    }

    private void setupSecurity() {
        securityViewModel.fetchById(securityId)
                .observe(this, securityResource ->
                        dataBinder.setSecurity(securityResource.getData()));
    }

    private boolean validateUrl(Intent webIntent) {
        Context context = dataBinder.getRoot().getContext();
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(webIntent, 0);
        return activities.size() > 0;
    }


}
