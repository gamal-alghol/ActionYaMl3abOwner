package com.malaab.ya.action.actionyamalaab.owner.data.model;

import java.util.Objects;


public class Day {

    public long id;
    public String name;


    public Day(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Day(Day day) {
        this.id = day.id;
        this.name = day.name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Day day = (Day) o;
        return id == day.id &&
                Objects.equals(name, day.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }
}
