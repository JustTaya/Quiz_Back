package com.quiz.entities;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public  class ResponsePaginatedList<T> {
    private List<T> responseList;
    private int totalNumberOfElement;
}
