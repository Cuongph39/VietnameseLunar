package com.khang.vietnameselunar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

import static com.khang.vietnameselunar.VietnameseLunar.TZ;
import static com.khang.vietnameselunar.VietnameseLunar.convertLunar2Solar;
import static com.khang.vietnameselunar.VietnameseLunar.convertSolar2Lunar;
import static com.khang.vietnameselunar.VietnameseLunar.jdFromDate;
import static com.khang.vietnameselunar.VietnameseLunar.jdToDate;

public class DialogConvertCalendar extends Dialog {

    private Context mContext;
    private DatePicker lunarPicker, solarPicker;

    public DialogConvertCalendar(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.convert_calendar_dialog);
        lunarPicker = (DatePicker)findViewById(R.id.datePickerLunar);
        solarPicker = (DatePicker)findViewById(R.id.datePickerSolar);
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        int jd = jdFromDate(mDay, mMonth+1, mYear);
        int[] s = jdToDate(jd);
        int[] l = convertSolar2Lunar(s[0], s[1], s[2], TZ);
        int[] s2 = convertLunar2Solar(l[0], l[1], l[2], l[3], TZ);

        if (s[0] == s2[0] && s[1] == s2[1] && s[2] == s2[2]) {

            solarPicker.init(mYear, mMonth, mDay, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    int jd = jdFromDate(dayOfMonth, month + 1, year);
                    int[] s = jdToDate(jd);
                    int[] l = convertSolar2Lunar(s[0], s[1], s[2], TZ);
                    int[] s2 = convertLunar2Solar(l[0], l[1], l[2], l[3], TZ);

                    if (s[0] == s2[0] && s[1] == s2[1] && s[2] == s2[2]) {
                        lunarPicker.updateDate(l[2], l[1]-1, l[0]);
                    } else {
                        Toast.makeText(mContext, "Lỗi chuyển ngày âm dương", Toast.LENGTH_LONG).show();
                    }
                }
            });

            lunarPicker.init(l[2], l[1]-1, l[0], new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    /*int jd = jdFromDate(dayOfMonth, month, year);
                    int[] l = jdToDate(jd);
                    int[] s = convertLunar2Solar(l[0], l[1], l[2], l[3], TZ);
                    int[] l2 = convertSolar2Lunar(s[0], s[1], s[2], TZ);

                    if (l[0] == l2[0] && l[1] == l2[1] && l[2] == l2[2]) {
                        lunarPicker.updateDate(s[2], s[1]-1, s[0]);
                    } else {
                        Toast.makeText(mContext, "Lỗi chuyển ngày âm dương", Toast.LENGTH_LONG).show();
                    }*/
                }
            });
        } else {
            Toast.makeText(mContext, "Lỗi chuyển ngày âm dương", Toast.LENGTH_LONG).show();
        }
    }
}
