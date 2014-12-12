package de.chrdw.mensa_siemens.parser.model;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import de.chrdw.mensa_siemens.parser.Parser;

public @ToString class Menu {
    @Getter
    @Expose
    private final List<Food> food;

    @Getter
    private final LocalDate date;

    @Expose
    @SerializedName("date")
    private final String sdate;

    public Menu(List<Food> food, LocalDate date) {
        super();
        this.food = food;
        this.date = date;
        this.sdate = date.format(Parser.formatter);
    }
}
