package com.tibafit.repository.article;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.tibafit.model.article.ForumType;


@Repository
public interface ForumTypeRepository extends JpaRepository<ForumType, Integer> {

}
