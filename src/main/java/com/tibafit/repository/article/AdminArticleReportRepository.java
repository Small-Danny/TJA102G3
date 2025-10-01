package com.tibafit.repository.article;

import com.tibafit.dto.article.ArticleReportDTO;
import com.tibafit.dto.article.ReportStatusDTO;
import com.tibafit.model.article.ArticleReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminArticleReportRepository extends JpaRepository<ArticleReport, Integer> {
	/**取得所有檢舉列表 DTO */
    @Query("SELECT new com.tibafit.dto.article.ArticleReportDTO(" +
           "r.reportId, u.nickName, a.title, rt.reportTypeName, r.reason, r.reportTime, rs.statusName) " +
           "FROM ArticleReport r " +
           "JOIN r.user u " +
           "JOIN r.article a " +
           "JOIN r.reportType rt " +
           "JOIN r.reportStatus rs " +
           "WHERE (:keyword IS NULL OR :keyword = ''"+ 
           "OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))"+
           "OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')))"+
           "ORDER BY r.reportTime DESC"
           )
    List<ArticleReportDTO> findAllReportDTO(@Param("keyword") String keyword);
    
    /**取得所有檢舉狀態 DTO */
    @Query("SELECT new com.tibafit.dto.article.ReportStatusDTO(rs.reportStatusId, rs.statusName) FROM ReportStatus rs")
    List<ReportStatusDTO> findAllReportStatusesDTO();
    
    @Modifying
    @Query("UPDATE ArticleReport r SET r.reportStatus.reportStatusId = :statusId WHERE r.reportId = :reportId")
    int updateReportStatus(Integer reportId, Integer statusId);

}

