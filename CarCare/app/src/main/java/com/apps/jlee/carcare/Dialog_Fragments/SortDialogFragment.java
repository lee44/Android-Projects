package com.apps.jlee.carcare.Dialog_Fragments;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.apps.jlee.carcare.Adapters.SpinnerCustomAdapter;
import com.apps.jlee.carcare.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SortDialogFragment extends DialogFragment
{
    private AlertDialog dialog;
    private EditText startingDate,endingDate;
    private Spinner dropdown;
    final Calendar startingCalendar = Calendar.getInstance(), endingCalendar = Calendar.getInstance();
    private String DateFormat = "M/dd/yy";
    private String[] filters = {"MPG","Cost","Miles","Gallons"};
    private Button OK,Reset;
    private FilterInterface listener;

    public interface FilterInterface {public void onClick(Date starting_date, Date ending_date, String sortBy);}

    public SortDialogFragment() {this.listener = null;}
    public void setListener (FilterInterface listener) {this.listener = listener;}

    public void onResume()
    {
        super.onResume();
        //Gets the window of the Dialog
        Window window = getDialog().getWindow();
        window.setLayout((int)(Resources.getSystem().getDisplayMetrics().widthPixels *.9), (int)(Resources.getSystem().getDisplayMetrics().heightPixels * .40));
        //window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        getDialog().setCanceledOnTouchOutside(true);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeOverlay_AppCompat_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sort_dialog, null);

        startingDate = dialogView.findViewById(R.id.startingdate);
        endingDate = dialogView.findViewById(R.id.endingdate);
        dropdown = dialogView.findViewById(R.id.spinner);
        OK = dialogView.findViewById(R.id.OK);
        Reset = dialogView.findViewById(R.id.Reset);

        SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(getContext(), filters);
        dropdown.setAdapter(adapter);

        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat);
        startingCalendar.set(Calendar.YEAR, startingCalendar.get(Calendar.YEAR));
        startingCalendar.set(Calendar.MONTH, startingCalendar.get(Calendar.MONTH));
        startingCalendar.set(Calendar.DAY_OF_MONTH, 1);
        startingCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startingCalendar.set(Calendar.MINUTE, 0);
        startingDate.setText(sdf.format(startingCalendar.getTime()));

        endingCalendar.set(Calendar.YEAR, endingCalendar.get(Calendar.YEAR));
        endingCalendar.set(Calendar.MONTH, endingCalendar.get(Calendar.MONTH));
        endingCalendar.set(Calendar.DAY_OF_MONTH, endingCalendar.getActualMaximum(Calendar.DATE));
        endingCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endingCalendar.set(Calendar.MINUTE, 59);
        endingDate.setText(sdf.format(endingCalendar.getTime()));

        //Sets the listener for the DatePickerDialog that will be appear when the user clicks the Date edit text view
        final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                startingCalendar.set(Calendar.YEAR, year);
                startingCalendar.set(Calendar.MONTH, monthOfYear);
                startingCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startingCalendar.set(Calendar.HOUR_OF_DAY, 0);
                startingCalendar.set(Calendar.MINUTE, 0);
                SimpleDateFormat sdf = new SimpleDateFormat(DateFormat);
                startingDate.setText(sdf.format(startingCalendar.getTime()));
            }
        };
        //listener for the Date edit text that will instantiate a DatePickerDialog with its listener defined above
        startingDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new DatePickerDialog(getContext(), datePicker, startingCalendar.get(Calendar.YEAR), startingCalendar.get(Calendar.MONTH), startingCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Sets the listener for the DatePickerDialog that will be appear when the user clicks the Date edit text view
        final DatePickerDialog.OnDateSetListener datePicker2 = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                endingCalendar.set(Calendar.YEAR, year);
                endingCalendar.set(Calendar.MONTH, monthOfYear);
                endingCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endingCalendar.set(Calendar.HOUR_OF_DAY, 23);
                endingCalendar.set(Calendar.MINUTE, 59);
                SimpleDateFormat sdf = new SimpleDateFormat(DateFormat);
                endingDate.setText(sdf.format(endingCalendar.getTime()));
            }
        };
        //listener for the Date edit text that will instantiate a DatePickerDialog with its listener defined above
        endingDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new DatePickerDialog(getContext(), datePicker2, endingCalendar.get(Calendar.YEAR), endingCalendar.get(Calendar.MONTH), endingCalendar.getActualMaximum(Calendar.DATE)).show();
            }
        });

        OK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Each spinner item is a textview so getSelectedView grabs the textview
                TextView textView = (TextView)dropdown.getSelectedView();
                String result = textView.getText().toString();
                listener.onClick(startingCalendar.getTime(),endingCalendar.getTime(),result);
                dialog.dismiss();
            }
        });

        Reset.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                listener.onClick(new Date(),new Date(),"Date");
                dialog.dismiss();
            }
        });

        builder.setView(dialogView);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        return dialog;
    }
}
