package com.example.m1j.CodeClass;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.m1j.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Functions {

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void Opendate_picker(Context context, final EditText editText){
        final SimpleDateFormat format= new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());


        String dateString=editText.getText().toString();
        if(dateString.equals("")){
            dateString="01/01/2000";
        }

        String[] parts = dateString.split("/");

        DatePickerDialog mdiDialog =new DatePickerDialog(context, R.style.datepicker_style,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(0);
                cal.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                Date chosenDate = cal.getTime();
                editText.setText(format.format(chosenDate));

            }
        }, Integer.parseInt(parts[2]),Integer.parseInt(parts[0])-1,Integer.parseInt(parts[1]));
        mdiDialog.show();
    }


    public static int convertDpToPx(Context context, int dp) {
        return (int) ((int) dp * context.getResources().getDisplayMetrics().density);
    }

}