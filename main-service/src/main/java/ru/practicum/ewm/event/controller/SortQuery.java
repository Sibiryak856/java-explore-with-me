package ru.practicum.ewm.event.controller;

public enum SortQuery {

    EVENT_DATE,

    VIEWS;


    public static SortQuery from(String sort) {
        for (SortQuery value : SortQuery.values()) {
            if (value.name().equals(sort)) {
                return value;
            }
        }
        return null;
    }
}
