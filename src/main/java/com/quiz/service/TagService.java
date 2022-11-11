package com.quiz.service;

import com.quiz.dao.TagDao;
import com.quiz.dto.TagDto;
import com.quiz.entities.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TagService {
    private final TagDao tagDao;

    public Tag findById(int id) {
        return tagDao.getTagById(id);
    }

    public Tag findTagByName(String name) {
        return tagDao.getTagByName(name);
    }

    public List<Tag> findTagsByName(String name) {
        return tagDao.getTagsByName(name);
    }

    public List<Tag> findTagsByQuiz(int quizId){
        return tagDao.getTagsByQuiz(quizId);
    }

    public TagDto insertTag(Tag tag){
        tagDao.insert(tag);
        return new TagDto(tag);
    }
}
