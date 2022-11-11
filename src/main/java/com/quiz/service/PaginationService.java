package com.quiz.service;

import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaginationService {
    public <T> List<T> paginate(List<T> listForPagination, int pageSize, int pageNumber){
        PagedListHolder<T> pageToReturn = new PagedListHolder<T>(listForPagination);
        pageToReturn.setPage(pageNumber);
        pageToReturn.setPageSize(pageSize);

        return pageToReturn.getPageList();
    }
}
