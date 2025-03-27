package com.example.appasiancuisine.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasiancuisine.R;
import com.example.appasiancuisine.model.CalendarDay;

import java.time.LocalDate;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayViewHolder> {

    private List<CalendarDay> dayList;
    private LocalDate selectedDate;
    private OnDateSelectedListener listener;

    public interface OnDateSelectedListener {
        void onDateSelected(LocalDate date);
    }

    public CalendarAdapter(List<CalendarDay> dayList, LocalDate selectedDate, OnDateSelectedListener listener) {
        this.dayList = dayList;
        this.selectedDate = selectedDate;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        CalendarDay calendarDay = dayList.get(position);
        LocalDate date = calendarDay.date;

        holder.textDay.setText(String.valueOf(date.getDayOfMonth()));

        // Ngày không thuộc tháng hiện tại
        if (!calendarDay.isCurrentMonth) {
            holder.textDay.setTextColor(Color.GRAY);
        } else {
            holder.textDay.setTextColor(Color.WHITE);
        }

        // Ngày được chọn
        if (date.equals(selectedDate)) {
            holder.dayBackground.setBackgroundResource(R.drawable.bg_day_selected);
        } else {
            holder.dayBackground.setBackgroundResource(R.drawable.bg_day_unselected);
        }

        // Sự kiện click
        holder.itemView.setOnClickListener(v -> {
            selectedDate = date;
            listener.onDateSelected(date);
            notifyDataSetChanged(); // Cập nhật lại để hiển thị chọn
        });
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView textDay;
        View dayBackground;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            textDay = itemView.findViewById(R.id.text_day);
            dayBackground = itemView.findViewById(R.id.view_day_background);
        }
    }
    public void updateData(List<CalendarDay> newDays, LocalDate selectedDate) {
        this.dayList = newDays;
        this.selectedDate = selectedDate;
        notifyDataSetChanged();
    }


}

