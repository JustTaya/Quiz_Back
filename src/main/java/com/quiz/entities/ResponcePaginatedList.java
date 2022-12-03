package com.quiz.entities;


import lombok.Data;

import java.util.List;

@Data
public class ResponcePaginatedList<T> {
    private List<T> responceList;
    private int totalNumberOfElement;

    public ResponcePaginatedList(List<T> responceList, int totalNumberOfElement) {
        this.responceList = responceList;
        this.totalNumberOfElement = totalNumberOfElement;
    }
}
