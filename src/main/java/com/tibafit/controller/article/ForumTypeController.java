package com.tibafit.controller.article;
import org.springframework.web.bind.annotation.GetMapping;


import org.springframework.web.bind.annotation.RestController;
import com.tibafit.model.article.ForumType;
import com.tibafit.service.article.ForumTypeService;

import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
public class ForumTypeController {

    private final ForumTypeService forumTypeService;

    // 建構子注入 ForumTypeService
    public ForumTypeController(ForumTypeService forumTypeService) {
        this.forumTypeService = forumTypeService;
    }

    @GetMapping("/api/categories")
    public ResponseEntity<Map<String, Object>> getCategories() {
        Map<String, Object> response = new HashMap<>();

        // 取得所有分類
        List<ForumType> forumTypes = forumTypeService.getAllForumTypes();

        // 封裝成前端需要的格式
        List<Map<String, Object>> categories = new ArrayList<>();
        for (ForumType type : forumTypes) {
            categories.add(Map.of(
                "id", type.getForumTypeId(),
                "name", type.getForumTypeName()
            ));
        }

        response.put("categories", categories);
        return ResponseEntity.ok(response);
    }
}
