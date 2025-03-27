package com.example.appasiancuisine.utils;

import com.example.appasiancuisine.model.CalendarDay;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class CalendarUtils {

    public static List<CalendarDay> generateCalendarDays(YearMonth yearMonth) {
        List<CalendarDay> dayList = new ArrayList<>();

        // Ngày đầu tiên trong tháng
        LocalDate firstOfMonth = yearMonth.atDay(1);

        // Thứ trong tuần (MON = 1, SUN = 7)
        int dayOfWeekIndex = firstOfMonth.getDayOfWeek().getValue();

        // Lịch bắt đầu từ Chủ nhật → điều chỉnh index
        int daysFromPrevMonth = dayOfWeekIndex % 7;

        // Ngày đệm đầu tháng
        for (int i = daysFromPrevMonth - 1; i >= 0; i--) {
            LocalDate date = firstOfMonth.minusDays(i + 1);
            dayList.add(new CalendarDay(date, false));
        }

        // Ngày trong tháng hiện tại
        int lengthOfMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            dayList.add(new CalendarDay(date, true));
        }

        // Ngày đệm sau
        int remaining = 42 - dayList.size();
        for (int i = 1; i <= remaining; i++) {
            LocalDate date = yearMonth.atEndOfMonth().plusDays(i);
            dayList.add(new CalendarDay(date, false));
        }

        return dayList;
    }
}
