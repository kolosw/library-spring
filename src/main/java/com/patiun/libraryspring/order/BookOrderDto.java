package com.patiun.libraryspring.order;

import com.patiun.libraryspring.validation.AcceptableOrderDaysByType;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@AcceptableOrderDaysByType(type = RentalType.OUT_OF_LIBRARY, value = {7, 14, 21}, message = "Days number must be 7, 14 or 21")
@AcceptableOrderDaysByType(type = RentalType.TO_READING_HALL, value = {0}, message = "Days number must be 0")
public class BookOrderDto {

    @NotNull(message = "Rental type must not be null")
    private RentalType rentalType;

    @NotNull(message = "Days must not be null")
    private Integer days = 0;

    public BookOrderDto() {
    }

    public BookOrderDto(RentalType rentalType, Integer days) {
        this.rentalType = rentalType;
        this.days = days;
    }

    public RentalType getRentalType() {
        return rentalType;
    }

    public void setRentalType(RentalType rentalType) {
        this.rentalType = rentalType;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BookOrderDto that = (BookOrderDto) o;

        if (rentalType != that.rentalType) {
            return false;
        }
        return Objects.equals(days, that.days);
    }

    @Override
    public int hashCode() {
        int result = rentalType != null ? rentalType.hashCode() : 0;
        result = 31 * result + (days != null ? days.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BookOrderDto{" +
                "rentalType=" + rentalType +
                ", days=" + days +
                '}';
    }
}
