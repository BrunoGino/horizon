package br.com.horizon.ui.about;

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
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import br.com.horizon.MainActivity;
import br.com.horizon.R;
import br.com.horizon.databinding.FragmentAboutTheAppBinding;
import br.com.horizon.ui.BaseFragment;

public class AboutFragment extends BaseFragment {
    private FragmentAboutTheAppBinding viewBinder;
    private View.OnClickListener rateOnClick;
    private MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinder = FragmentAboutTheAppBinding.inflate(inflater, container, false);
        return viewBinder.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity.setActionBarTitle(getString(R.string.about_title));
        rateOnClick = v -> redirectsToBrowserIfUrlIsValid();
        viewBinder.setRateButtonOnClick(rateOnClick);
    }

    private void redirectsToBrowserIfUrlIsValid() {
        Uri uri = Uri.parse(getString(R.string.google_forms_url));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, uri);
        if (validateUrl(webIntent)) {
            final AlertDialog.Builder builder = new MaterialAlertDialogBuilder(requireContext(),
                    R.style.ThemeOverlay_App_MaterialAlertDialog);

            builder.setMessage(getString(R.string.would_you_like_to_be_redirected));
            builder.setPositiveButton(getString(R.string.yes), (arg0, arg1) ->
                    startActivity(webIntent));
            builder.setNegativeButton(getString(R.string.no), (dialog, which) -> {

            });
            builder.show();

        }
    }

    private boolean validateUrl(Intent webIntent) {
        Context context = viewBinder.getRoot().getContext();
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(webIntent, 0);
        return activities.size() > 0;
    }

}
