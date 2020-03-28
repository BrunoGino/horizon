package br.com.horizon.ui.securities;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import butterknife.BindView;
import butterknife.ButterKnife;

public class SecurityFragment extends Fragment {

    //View components
    @BindView(R.id.dialog_title_name)
    TextView dName;
    @BindView(R.id.dialog_title_publisher)
    TextView dPublisher;
    @BindView(R.id.dialog_title_emitter)
    TextView dEmitter;
    @BindView(R.id.dialog_title_interest)
    TextView dInterest;
    @BindView(R.id.dialog_title_ending_date)
    TextView dEndingDate;
    @BindView(R.id.dialog_title_fgc)
    TextView dFgc;
    @BindView(R.id.dialog_title_min_value)
    TextView dMinValue;
    @BindView(R.id.dialog_title_liquidity)
    TextView dLiquidity;
    @BindView(R.id.dialog_title_goto_url)
    MaterialButton dGoToUrl;

    private SecurityViewModel securityViewModel;
    private SecurityAdapter securityAdapter;
    private Dialog itemDialog;
    private SimpleDateFormat dateFormat;

    @SuppressLint("SimpleDateFormat")
    public SecurityFragment() {
        dateFormat = new SimpleDateFormat("dd/MM/yy");
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_securities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        securityViewModel = ViewModelProviders.of(this).get(SecurityViewModel.class);
        Dialog dialog = createDialog(view);
        ButterKnife.bind(this, dialog);
        setupRecyclerView(view, dialog);
        pullSecurities(view);
    }

    /**
     * Instantiates the pop-up dialog and sets its layout.
     *
     * @param view The host view.
     * @return The created dialog.
     */
    private Dialog createDialog(View view) {
        Dialog dialog = new Dialog(view.getContext());
        this.itemDialog = dialog;
        this.itemDialog.setContentView(R.layout.dialog_title);

        Objects.requireNonNull(this.itemDialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    /**
     * Fetches the securities list from ViewModel and add its content to the adapter.
     *
     * @param view A view instance to show a Snackbar containing the error message if the fetch goes
     *             wrong.
     */
    private void pullSecurities(View view) {
        securityViewModel.fetchAll().observe(this, listResource -> {
            if (listResource.getData() != null) {
                securityAdapter.addAll(listResource.getData());
            } else {
                Snackbar.make(view, Objects.requireNonNull(listResource.getError()), 3000).show();
            }
        });
    }

    /**
     * Sets up the securities recycler view.
     *
     * @param view       The view instance to host the recycler view component.
     * @param dialogView A dialog instance to get its content filled up by the
     *                   onRecyclerItemClickListener(Dialog dialogView) method.
     */
    private void setupRecyclerView(View view, Dialog dialogView) {
        RecyclerView recyclerView = view.findViewById(R.id.securities_recycler);
        securityAdapter = new SecurityAdapter(view.getContext(), onRecyclerItemClickListener(dialogView));
        recyclerView.setAdapter(securityAdapter);
    }

    /**
     * Fills up dialog components with the selected security object from the main list by sending
     * the selected object to fillUpDialog method.
     *
     * @param dialogView The dialog view to receive the current object.
     * @return An OnItemClickListener to be attached to the list.
     */
    private SecurityAdapter.OnItemClickListener onRecyclerItemClickListener(Dialog dialogView) {
        return (position, security)
                -> {
            fillUpDialog(security, dialogView);
            itemDialog.show();
        };
    }

    /**
     * Sets up the dialog's components with the content of the received object.
     *
     * @param security The security object to get its content fetched.
     * @param dialog   The dialog to get its components filled.
     */
    private void fillUpDialog(Security security, Dialog dialog) {
        dName.setText(security.getTitleName());
        dPublisher.setText(security.getPublisher());
        dEmitter.setText(security.getEmitter());
        dInterest.setText(String.valueOf(security.getInterest()));
        dEndingDate.setText(dateFormat.format(security.getEndingDate()));
        dFgc.setText(String.valueOf(security.getFgc()));
        dMinValue.setText(String.valueOf(security.getTitleValue()));
        dLiquidity.setText(String.valueOf(security.getLiquidity()));

        dGoToUrl.setOnClickListener(v -> {
            Uri webPage = Uri.parse(security.getUrl());
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);

            if (validateDialogUrl(webIntent, dialog)) {
                startActivity(webIntent);
            }
        });
    }

    /**
     * Validates Security's URL. If the URL is valid, the user can get redirected to the publisher
     * web page. (Method used inside of fillUpDialog(Security security, Dialog, dialog), when setting
     * up MaterialButton's listener).
     *
     * @param webIntent The Intent object to get the user redirected.
     * @param dialog    The Dialog view to get a context reference to instantiate a packageManager object.
     * @return A boolean that indicates if the URL can be opened by any of the user's mobile
     * applications.
     */
    private boolean validateDialogUrl(Intent webIntent, Dialog dialog) {
        PackageManager packageManager = dialog.getContext().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(webIntent, 0);
        return activities.size() > 0;
    }

}