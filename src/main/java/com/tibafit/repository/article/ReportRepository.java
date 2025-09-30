package com.tibafit.repository.article;

import com.tibafit.model.article.ArticleReport;
import com.tibafit.model.article.ReportStatus;
import com.tibafit.model.article.ReportType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ReportRepository
 * 集中管理：
 *   - ArticleReport（文章檢舉紀錄）
 *   - ReportType（檢舉類型）
 *   - ReportStatus（檢舉狀態）
 *
 * 主要實體是 ArticleReport
 */
@Repository
public interface ReportRepository extends JpaRepository<ArticleReport, Integer> {

    /* =======================  ReportType  ======================= */

    /** 取得所有檢舉類型 */
    @Query("SELECT rt FROM ReportType rt")
    List<ReportType> findAllReportTypes();
//未使用
    /** 根據 ID 查詢檢舉類型 */
    @Query("SELECT rt FROM ReportType rt WHERE rt.reportTypeId = :id")
    ReportType findReportTypeById(@Param("id") Integer id);


    /* =======================  ReportStatus  ======================= */
//未使用
    /** 取得所有檢舉狀態 */
    @Query("SELECT rs FROM ReportStatus rs")
    List<ReportStatus> findAllReportStatuses();
//未使用
    /** 根據 ID 查詢檢舉狀態 */
    @Query("SELECT rs FROM ReportStatus rs WHERE rs.reportStatusId = :id")
    ReportStatus findReportStatusById(@Param("id") Integer id);


    /* =======================  ArticleReport  ======================= */
//未使用
    /** 依文章 ID 查詢該文章的檢舉紀錄 */
    List<ArticleReport> findByArticle_ArticleId(Integer articleId);
//未使用
    /** 依會員 ID 查詢該會員提交的檢舉紀錄 */
    List<ArticleReport> findByUser_UserId(Integer userId);

    /** 根據 ArticleReport ID 查詢 */
    ArticleReport findByReportId(Integer reportId);
    
    /** 判斷同一會員是否已經檢舉過該文章 */
    boolean existsByUser_UserIdAndArticle_ArticleId(Integer userId, Integer articleId);


}
