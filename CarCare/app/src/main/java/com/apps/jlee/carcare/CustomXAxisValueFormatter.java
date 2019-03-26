package com.apps.jlee.carcare;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class CustomXAxisValueFormatter implements IAxisValueFormatter
{
    private String[] mValues;

    public CustomXAxisValueFormatter(String[] values)
    {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis)
    {
        // "value" represents the position of the label on the axis (x or y)
        if (value >= 0) {
            if (mValues.length > (int) value) {
                return mValues[(int) value];
            } else return "";
        } else {
            return "";
        }

    }

    /** this is only needed if numbers are returned, else return 0 */
    public int getDecimalDigits() { return 0; }
}
