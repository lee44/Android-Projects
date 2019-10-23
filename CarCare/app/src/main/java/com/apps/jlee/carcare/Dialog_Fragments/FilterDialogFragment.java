package com.apps.jlee.carcare.Dialog_Fragments;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.apps.jlee.carcare.R;

public class FilterDialogFragment extends DialogFragment
{
    private AlertDialog dialog;
    private RadioGroup rg;
    private RadioButton rb;
    private RadioGroupInput listener;
    private Button OK,Cancel;

    public interface RadioGroupInput
    {
        public void onClick(int i);
    }

    public FilterDialogFragment() {this.listener = null;}

    public void onResume()
    {
        super.onResume();
        //Gets the window of the Dialog
        Window window = getDialog().getWindow();
        window.setLayout((int)(Resources.getSystem().getDisplayMetrics().widthPixels *.7), (int)(Resources.getSystem().getDisplayMetrics().heightPixels * .40));
        //window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        getDialog().setCanceledOnTouchOutside(true);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeOverlay_AppCompat_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_dialog, null);

        rg = dialogView.findViewById(R.id.radio_button_group);
        rb = dialogView.findViewById(R.id.Cost_radioButton);
        OK = dialogView.findViewById(R.id.OK);
        Cancel = dialogView.findViewById(R.id.Cancel);

        rb.setChecked(true);
        builder.setView(dialogView);
        dialog = builder.create(); //Build the AlertDialog Object
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        OK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int graphType = 1;
                for(int i = 0; i < rg.getChildCount(); i++)
                {
                    if(((RadioButton)rg.getChildAt(i)).isChecked())
                        graphType = i+1;
                }
                listener.onClick(graphType);
                dialog.dismiss();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public void setListener(RadioGroupInput listener)
    {
        this.listener = listener;
    }
}
