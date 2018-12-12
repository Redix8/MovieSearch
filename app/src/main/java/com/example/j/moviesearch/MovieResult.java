package com.example.j.moviesearch;

import java.util.ArrayList;

public class MovieResult {
    String lastBuildDate;
    int total;
    int start;
    int display;
    ArrayList<MovieResultItem> items = new ArrayList<>();

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getDisplay() {
        return display;
    }

    public void setDisplay(int display) {
        this.display = display;
    }

    public ArrayList<MovieResultItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<MovieResultItem> items) {
        this.items = items;
    }
}
