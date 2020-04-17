package de.whatsLeft.ui.stores;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import de.whatsLeft.R;

/**
 * Dialog class to ask the user for zip code input
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.0
 */
public class ZipCodeDialogFragment extends DialogFragment {

    private static final String TAG = "ZipCodeDialog";
    private ZipCodeDialogListener listener;

    /**
     * Interface to access data from non Dialog class
     *
     * @since 1.0.0
     */
    public interface ZipCodeDialogListener {
        void onDialogPositiveClick(DialogFragment dialogFragment);
        void onDialogNegativeClick(DialogFragment dialogFragment);
    }

    /**
     * Constructor
     *
     * @param listener class that implements the ZipCodeDialogListener interface
     * @since 1.0.0
     */
    public ZipCodeDialogFragment(ZipCodeDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.dialog_zip_code);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDialogPositiveClick(ZipCodeDialogFragment.this);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDialogNegativeClick(ZipCodeDialogFragment.this);
            }
        });

        Log.i(TAG, "onCreateDialog: dialog created");

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ZipCodeDialogListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
