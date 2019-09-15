package com.apps.jlee.carcare.Dialog_Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;

import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.apps.jlee.carcare.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GasDialogFragment extends DialogFragment
{
    String milesValue,gallonsValue,costValue, DateFormat = "M/dd/yy";
    EditText miles,gallons,cost,date;
    TextInputLayout costtextInputLayout, milestextInputLayout, gallonstextInputLayout;
    TextView mpgTextView;
    Button OK,Cancel;
    private GasInterface listener;
    private AlertDialog dialog;
    final Calendar myCalendar = Calendar.getInstance();

    public interface GasInterface
    {
        public void onClick(String milesValue, String gallonsValue, String cost, Date date);
    }

    public GasDialogFragment()
    {
        this.listener = null;
    }

    public void onResume()
    {
        super.onResume();
        //Gets the window of the Dialog
        Window window = getDialog().getWindow();
        window.setLayout((int)(Resources.getSystem().getDisplayMetrics().widthPixels *.95), (int)(Resources.getSystem().getDisplayMetrics().heightPixels * .60));
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        getDialog().setCanceledOnTouchOutside(true);
    }

    public void setListener(GasInterface listener)
    {
        this.listener = listener;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeOverlay_AppCompat_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.gas_dialog, null);

        date = dialogView.findViewById(R.id.date);
        cost = dialogView.findViewById(R.id.costEditText);
        costtextInputLayout = dialogView.findViewById(R.id.cost_error);
        miles = dialogView.findViewById(R.id.milesEditText);
        milestextInputLayout = dialogView.findViewById(R.id.miles_error);
        gallons = dialogView.findViewById(R.id.gallonsEditText);
        gallonstextInputLayout = dialogView.findViewById(R.id.gallons_error);
        mpgTextView = dialogView.findViewById(R.id.mpgTextView);
        OK = dialogView.findViewById(R.id.OK);
        Cancel = dialogView.findViewById(R.id.Cancel);

        cost.requestFocus();

        //Sets the text for all edit text views when user wants to edit the gas entry
        if(getArguments() != null)
        {
            cost.setText((String)getArguments().get("Cost"));

            String [] str1 = ((String)getArguments().get("Miles")).split(" mi");
            miles.setText(str1[0]);

            String [] str2 = ((String)getArguments().get("Gallons")).split(" gal");
            gallons.setText(str2[0]);

            SimpleDateFormat sdf = new SimpleDateFormat(DateFormat, Locale.US);
            if(((String)getArguments().get("Date")).equals(""))
                date.setText(sdf.format(Calendar.getInstance().getTime()));
            else
            {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(Long.valueOf((String)getArguments().get("Date")));

                date.setText(sdf.format(c.getTime()));

                myCalendar.set(Calendar.YEAR,c.get(Calendar.YEAR));
                myCalendar.set(Calendar.MONTH,c.get(Calendar.MONTH));
                myCalendar.set(Calendar.DAY_OF_MONTH,c.get(Calendar.DAY_OF_MONTH));
                myCalendar.set(Calendar.HOUR_OF_DAY, 0);
                myCalendar.set(Calendar.MINUTE, 0);
            }

            milesValue = str1[0];
            gallonsValue = str2[0];
            costValue = (String)getArguments().get("Cost");
            calculateMPG();
        }

        builder.setView(dialogView);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        //Sets the listener for the DatePickerDialog that will be appear when the user clicks the Date edit text view
        final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                myCalendar.set(Calendar.HOUR_OF_DAY, 12);
                myCalendar.set(Calendar.MINUTE, 0);
                updateDateEditText();
            }
        };
        //listener for the Date edit text that will instantiate a DatePickerDialog with its listener defined above
        date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new DatePickerDialog(getContext(), datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        OK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(miles.getText().toString().matches(""))
                {
                    milestextInputLayout.setErrorEnabled(true);
                    milestextInputLayout.setError("Can't Leave Miles Blank");
                }
                else if(gallons.getText().toString().matches(""))
                {
                    gallonstextInputLayout.setErrorEnabled(true);
                    gallonstextInputLayout.setError("Can't Leave Gallons Blank");
                }
                else if(!cost.getText().toString().matches("^\\$?(([1-9]\\d{0,2}(,\\d{3})*)|0)?\\.\\d{1,2}$"))
                {
                    costtextInputLayout.setErrorEnabled(true);
                    costtextInputLayout.setError("Wrong format. e.g $10.42");
                }
                else
                {
                    listener.onClick(new BigDecimal(Double.parseDouble(milesValue)).setScale(2, RoundingMode.HALF_UP).toString(),
                                     new BigDecimal(Double.parseDouble(gallonsValue)).setScale(2, RoundingMode.HALF_UP).toString(),
                                     new BigDecimal(Double.parseDouble(costValue.split("\\$")[1])).setScale(2, RoundingMode.HALF_UP).toString(),
                                     myCalendar.getTime());
                    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    dialog.dismiss();
                }
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                cost.setText("");
                miles.setText("");
                gallons.setText("");
                mpgTextView.setText("");
                getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.dismiss();
            }
        });

        miles.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){}

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(milestextInputLayout.isErrorEnabled())
                    milestextInputLayout.setErrorEnabled(false);

                milesValue = editable.toString();
                calculateMPG();
            }
        });

        gallons.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){}

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(gallonstextInputLayout.isErrorEnabled())
                    gallonstextInputLayout.setErrorEnabled(false);

                gallonsValue = editable.toString();
                calculateMPG();
            }
        });

        cost.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(costtextInputLayout.isErrorEnabled())
                    costtextInputLayout.setErrorEnabled(false);

                if (!editable.toString().contains("$"))
                {
                    cost.setText("$" + editable.toString());
                    Selection.setSelection(cost.getText(), cost.getText().length());
                }
                costValue = editable.toString();
            }
        });

        return dialog;
    }

    public void calculateMPG()
    {
        if(milesValue != null && gallonsValue != null)
            if(milesValue.length() > 0 && gallonsValue.length() > 0)
                mpgTextView.setText(String.format("%.2f",(Double.parseDouble(milesValue) / Double.parseDouble(gallonsValue)))+" MPG");
    }

    private void updateDateEditText()
    {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat, Locale.US);
        date.setText(sdf.format(myCalendar.getTime()));
    }
}
