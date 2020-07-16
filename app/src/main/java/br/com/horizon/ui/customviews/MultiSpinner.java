package br.com.horizon.ui.customviews;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.horizon.R;
import lombok.Getter;
import lombok.Setter;

public class MultiSpinner extends androidx.appcompat.widget.AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener {

    private List<String> items = null;
    private boolean[] selection = null;
    @Getter
    @Setter
    private boolean applied = false;
    private ArrayAdapter adapter;
    @Getter
    @Setter
    private OnApplyListener onApplyListener;
    @Getter
    @Setter
    private OnSelectionListener onSelectionListener;


    public MultiSpinner(Context context) {
        super(context);

        adapter = new ArrayAdapter(context,
                R.layout.spinner_item);
        super.setAdapter(adapter);
    }

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        adapter = new ArrayAdapter(context,
                R.layout.spinner_item);
        super.setAdapter(adapter);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (selection != null && which < selection.length) {
            selection[which] = isChecked;
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
//        super.performClick();
        final AlertDialog.Builder builder = new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_App_MaterialAlertDialog);

        String[] itemNames = new String[items.size()];

        for (int i = 0; i < items.size(); i++) {
            itemNames[i] = items.get(i);
        }

        builder.setMultiChoiceItems(itemNames, selection, this);
        builder.setPositiveButton(getContext().getString(R.string.apply), (arg0, arg1) -> {
            if (getSelectedItems().size() > 0) {
                applied = true;
                setBackgroundColor(getContext().getColor(R.color.graphInterestLiq));
                onApplyListener.onApply(getSelectedItems());
            } else {
                setBackgroundColor(getContext().getColor(R.color.colorPrimaryLight));
            }
        });
        builder.setNegativeButton(getContext().getString(R.string.cancel), (dialog, which) -> {

        });
        builder.setOnCancelListener(dialog -> {
            if (applied && getSelectedItems().size() > 0) {
                setBackgroundColor(getContext().getColor(R.color.graphInterestLiq));

            } else {
                setBackgroundColor(getContext().getColor(R.color.colorPrimaryLight));
            }
            applied = false;
        });
        builder.show();

        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(List<String> items) {
        this.items = items;
        selection = new boolean[this.items.size()];
        Arrays.fill(selection, false);
    }

    public void setSelection(ArrayList<String> selection) {
        for (int i = 0; i < this.selection.length; i++) {
            this.selection[i] = false;
        }

        for (String sel : selection) {
            for (int j = 0; j < items.size(); ++j) {
                if (items.get(j).equals(sel)) {
                    this.selection[j] = true;
                }
            }
        }

        adapter.clear();
        adapter.add(buildSelectedItemString());
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < items.size(); ++i) {
            if (selection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }

                foundOne = true;

                sb.append(items.get(i));
            }
        }

        return sb.toString();
    }

    public ArrayList<String> getSelectedItems() {
        ArrayList<String> selectedItems = new ArrayList<>();

        for (int i = 0; i < items.size(); ++i) {
            if (selection[i]) {
                selectedItems.add(items.get(i));
            }
        }

        return selectedItems;
    }

    public void setSpinnerText(String text) {
        adapter.clear();
        adapter.add(text);
    }

    public interface OnApplyListener {
        void onApply(List<String> items);
    }

    public interface OnSelectionListener {
        void onSelection(boolean isSelected);
    }
}