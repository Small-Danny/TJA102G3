package com.tibafit.service.article;

import java.util.List;

import org.springframework.stereotype.Service;
import com.tibafit.model.article.ForumType;
import com.tibafit.repository.article.ForumTypeRepository;


@Service
public class ForumTypeService {

    private final ForumTypeRepository forumTypeRepository;

    public ForumTypeService(ForumTypeRepository forumTypeRepository) {
        this.forumTypeRepository = forumTypeRepository;
    }

    public List<ForumType> getAllForumTypes() {
        return forumTypeRepository.findAll();
    }
}
