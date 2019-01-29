package com.apps.jlee.carcare;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GasDialogFragment extends DialogFragment
{
    String milesValue,gallonsValue,costValue,displayDateFormat = "MM/dd/yy";
    EditText miles,gallons,cost,date;
    TextInputLayout milestextInputLayout, gallonstextInputLayout;
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

    @Override
    public void onStart()
    {
        super.onStart();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
            miles.setText((String)getArguments().get("Miles"));
            gallons.setText((String)getArguments().get("Gallons"));

            SimpleDateFormat sdf = new SimpleDateFormat(displayDateFormat, Locale.US);
            if(((String)getArguments().get("Date")).equals(""))
                date.setText(sdf.format(Calendar.getInstance().getTime()));
            else
            {
                date.setText((String)getArguments().get("Date"));

                String [] str = ((String)getArguments().get("Date")).split("/");
                myCalendar.set(Calendar.MONTH,Integer.parseInt(str[0])-1);
                myCalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(str[1]));
                myCalendar.set(Calendar.YEAR,Integer.parseInt(str[2]));
            }

            milesValue = (String)getArguments().get("Miles");
            gallonsValue = (String)getArguments().get("Gallons");
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
                else
                {
                    if(costValue.equals(""))
                        costValue = "0.00";

                    listener.onClick(milesValue,gallonsValue,costValue,myCalendar.getTime());
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

                if(gallonstextInputLayout.isErrorEnabled())
                    gallonstextInputLayout.setErrorEnabled(false);

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
                gallonsValue = editable.toString();
                calculateMPG();
            }
        });

        cost.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){}

            @Override
            public void afterTextChanged(Editable editable)
            {
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
        SimpleDateFormat sdf = new SimpleDateFormat(displayDateFormat, Locale.US);
        date.setText(sdf.format(myCalendar.getTime()));
    }
}
