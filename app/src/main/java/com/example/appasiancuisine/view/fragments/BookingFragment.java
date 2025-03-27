package com.example.appasiancuisine.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.adapter.CalendarAdapter;
import com.example.appasiancuisine.model.CalendarDay;
import com.example.appasiancuisine.utils.CalendarUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;

public class BookingFragment extends Fragment {

    private Button buttonSelectDate;
    private NumberPicker pickerHour, pickerMinute;
    private Spinner spinnerAmPm;

    private LocalDate selectedDate = LocalDate.now();
    private YearMonth currentMonth = YearMonth.now();
    private PopupWindow popupWindow;

    public BookingFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ View
        buttonSelectDate = view.findViewById(R.id.button_select_date);
        pickerHour = view.findViewById(R.id.picker_hour);
        pickerMinute = view.findViewById(R.id.picker_minute);
        spinnerAmPm = view.findViewById(R.id.spinner_ampm);

        setupCurrentTimePickers();

        buttonSelectDate.setOnClickListener(v -> {
            showCalendarPopup(v); // dùng Popup thay vì include
        });
    }

    private void setupCurrentTimePickers() {
        pickerHour.setMinValue(1);
        pickerHour.setMaxValue(12);

        pickerMinute.setMinValue(0);
        pickerMinute.setMaxValue(59);
        pickerMinute.setFormatter(i -> String.format("%02d", i));

        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();

        boolean isAm = hour < 12;
        int displayHour = hour % 12;
        if (displayHour == 0) displayHour = 12;

        pickerHour.setValue(displayHour);
        pickerMinute.setValue(minute);
        spinnerAmPm.setSelection(isAm ? 0 : 1);
    }

    private void showCalendarPopup(View anchor) {
        View popupView = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_booking_calendar, null);

        RecyclerView recyclerCalendar = popupView.findViewById(R.id.recycler_calendar);
        ImageButton buttonPrevMonth = popupView.findViewById(R.id.button_prev_month);
        ImageButton buttonNextMonth = popupView.findViewById(R.id.button_next_month);
        TextView textMonthYear = popupView.findViewById(R.id.text_month_year);

        textMonthYear.setText(formatMonthYear(currentMonth));

        List<CalendarDay> dayList = CalendarUtils.generateCalendarDays(currentMonth);
        CalendarAdapter calendarAdapter = new CalendarAdapter(dayList, selectedDate, date -> {
            selectedDate = date;
            buttonSelectDate.setText("Ngày: " + selectedDate.toString());
            popupWindow.dismiss();
        });

        recyclerCalendar.setLayoutManager(new GridLayoutManager(getContext(), 7));
        recyclerCalendar.setAdapter(calendarAdapter);

        buttonPrevMonth.setOnClickListener(v -> {
            currentMonth = currentMonth.minusMonths(1);
            textMonthYear.setText(formatMonthYear(currentMonth));
            updateCalendar(calendarAdapter, currentMonth);
        });

        buttonNextMonth.setOnClickListener(v -> {
            currentMonth = currentMonth.plusMonths(1);
            textMonthYear.setText(formatMonthYear(currentMonth));
            updateCalendar(calendarAdapter, currentMonth);
        });

        popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupWindow.setElevation(10f);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(anchor);
    }

    private void updateCalendar(CalendarAdapter adapter, YearMonth month) {
        List<CalendarDay> newDays = CalendarUtils.generateCalendarDays(month);
        adapter.updateData(newDays, selectedDate); // Đảm bảo CalendarAdapter có hàm updateData()
    }

    private String formatMonthYear(YearMonth yearMonth) {
        String month = yearMonth.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new Locale("vi"));
        return capitalizeFirst(month) + " " + yearMonth.getYear();
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
