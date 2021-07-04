package com.ciee.cau.recorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author 陈明涛 Email:cmt96@foxmail.com
 * @version V1.0
 * @Description: 用于选择日期的DialogFragment
 * @Date 2021/4/26 10:42
 */
public class DatePickerFragment extends DialogFragment {
    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "com.ciee.cau.recorder.date";

    private DatePicker mDatePicker;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args); // 通过Argument传入date
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        mDatePicker = v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null); // 设置初始值，为传入的date

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.data_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        year = mDatePicker.getYear(); // 获取修改后的值
                        month = mDatePicker.getMonth();
                        day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day, hour, minute).getTime();
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .create();
    }

    /**
     * 返回结果
     */
    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) { // 获取目标Fragment，调用该Fragment时有设置过
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        // 处理由同一activity托管的两个fragment间的数据返回时，可借用Fragment.onActivityResult(...)方法
        // 这里调用的时 RecordFragment.onActivityResult()
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
