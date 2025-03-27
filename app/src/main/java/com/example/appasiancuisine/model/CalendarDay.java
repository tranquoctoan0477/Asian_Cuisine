package com.example.appasiancuisine.model;

import java.time.LocalDate;

public class CalendarDay {
    public LocalDate date;
    public boolean isCurrentMonth;

    public CalendarDay(LocalDate date, boolean isCurrentMonth) {
        this.date = date;
        this.isCurrentMonth = isCurrentMonth;
    }
}
