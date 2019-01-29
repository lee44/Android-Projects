package com.apps.jlee.carcare;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OilDialogFragment extends DialogFragment
{
    private EditText oil_name_editText,oil_amount_editText,mileage_editText,date;
    private Button OK,Cancel;
    private TextInputLayout oil_amount_textInputLayout, mileage_textInputLayout;
    private AlertDialog dialog;
    private OilInterface listener;
    final Calendar myCalendar = Calendar.getInstance();
    private String displayDateFormat = "MM/dd/yy";

    public interface OilInterface
    {
        public void onClick(String oil_name, double oil_amount, double mileage, Date date);
    }

    public OilDialogFragment(){this.listener = null;}

    @Override
    public void onStart()
    {
        super.onStart();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void setListener(OilInterface listener){this.listener = listener;}

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeOverlay_AppCompat_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.oil_dialog, null);

        date = dialogView.findViewById(R.id.date);
        oil_name_editText = dialogView.findViewById(R.id.oilEditText);
        oil_amount_editText = dialogView.findViewById(R.id.oilAmountEditText);
        mileage_editText = dialogView.findViewById(R.id.mileageEditText);
        OK = dialogView.findViewById(R.id.OK);
        Cancel = dialogView.findViewById(R.id.Cancel);

        oil_amount_textInputLayout = dialogView.findViewById(R.id.oilAmount_error);
        mileage_textInputLayout = dialogView.findViewById(R.id.miles_error);

        if(getArguments() != null)
        {
            oil_name_editText.setText((String) getArguments().get("oil_name"));
            oil_amount_editText.setText((String) getArguments().get("oil_amount"));
            mileage_editText.setText((String) getArguments().get("Mileage"));
            SimpleDateFormat sdf = new SimpleDateFormat(displayDateFormat, Locale.US);
            if (((String) getArguments().get("Date")).equals(""))
                date.setText(sdf.format(Calendar.getInstance().getTime()));
            else
            {
                date.setText((String) getArguments().get("Date"));

                String[] str = ((String) getArguments().get("Date")).split("/");
                myCalendar.set(Calendar.MONTH, Integer.parseInt(str[0]) - 1);
                myCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(str[1]));
                myCalendar.set(Calendar.YEAR, Integer.parseInt(str[2]));
            }
        }

        builder.setView(dialogView);
        dialog = builder.create();
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

        //Rect displayRectangle = new Rect();
        //Window window = getActivity().getWindow();
        //window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        //dialog.getWindow().setLayout((int)(displayRectangle.width()), (int)(displayRectangle.height() * 0.4f));

        OK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(oil_name_editText.getText().equals(""))
                {
                    oil_amount_textInputLayout.setErrorEnabled(true);
                    oil_amount_textInputLayout.setError("Can't Leave Oil Amount Blank");
                }
                else if(mileage_editText.getText().toString().matches(""))
                {
                    mileage_textInputLayout.setErrorEnabled(true);
                    mileage_textInputLayout.setError("Can't Leave Mileage Blank");
                }
                else
                {
                    if(oil_name_editText.getText().toString().equals(""))
                        listener.onClick("",Double.valueOf(oil_amount_editText.getText().toString()),Double.valueOf(mileage_editText.getText().toString()),myCalendar.getTime());
                    else
                        listener.onClick(oil_name_editText.getText().toString(),Double.valueOf(oil_amount_editText.getText().toString()),Double.valueOf(mileage_editText.getText().toString()),myCalendar.getTime());

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
                oil_name_editText.setText("");
                oil_amount_editText.setText("");
                mileage_editText.setText("");
                getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.dismiss();
            }
        });

        return dialog;
    }
    private void updateDateEditText()
    {
        SimpleDateFormat sdf = new SimpleDateFormat(displayDateFormat, Locale.US);
        date.setText(sdf.format(myCalendar.getTime()));
    }
}
