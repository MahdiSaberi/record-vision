package com.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JalaliDateModel {
    private int year;
    private int month;
    private int day;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JalaliDateModel jalaliDateModel) {
            return this.year == jalaliDateModel.getYear()
                    && this.month == jalaliDateModel.getMonth()
                    && this.day == jalaliDateModel.getDay();
        } else {
            return false;
        }
    }

    public boolean isGreater(JalaliDateModel jalaliDateModel) {
        if (this.year > jalaliDateModel.getYear()) {
            return true;
        } else {
            if (this.year == jalaliDateModel.getYear() && (this.month > jalaliDateModel.getMonth())) {
                return true;
            } else {
                return (this.year == jalaliDateModel.getYear())
                        && (this.month == jalaliDateModel.getMonth())
                        && this.day > jalaliDateModel.getDay();
            }
        }
    }
}
