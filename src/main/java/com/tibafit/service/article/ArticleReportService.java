package com.tibafit.service.article;

import com.tibafit.dto.article.ReportTypeDTO;

import com.tibafit.model.article.Article;
import com.tibafit.model.article.ArticleReport;
import com.tibafit.model.article.ReportStatus;
import com.tibafit.model.article.ReportType;
import com.tibafit.model.user.User;
import com.tibafit.repository.article.ReportRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ArticleReportService {

    private final ReportRepository reportRepository;

    public ArticleReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /** 取得所有檢舉類型，回傳 DTO */
    public List<ReportTypeDTO> getAllReportTypes() {
        List<ReportType> reportTypes = reportRepository.findAllReportTypes();
        List<ReportTypeDTO> dtos = new ArrayList<>();
        for (ReportType rt : reportTypes) {
            dtos.add(new ReportTypeDTO(rt.getReportTypeId(), rt.getReportTypeName()));
        }
        return dtos;
    }

    /** 依 ID 取得檢舉類型 */
    public ReportType getReportTypeById(Integer id) {
        return reportRepository.findReportTypeById(id);
    }

    /** 建立檢舉文章紀錄（createTime 由資料庫自動處理） */
    public ArticleReport createArticleReport(Article article, Integer reportTypeId, String reason, User user) {
        if (article == null) {
            throw new IllegalArgumentException("文章不存在");
        }

        // 檢查是否已經檢舉過
        boolean alreadyReported = reportRepository.existsByUser_UserIdAndArticle_ArticleId(user.getUserId(), article.getArticleId());
        if (alreadyReported) {
            throw new IllegalArgumentException("你已經檢舉過這篇文章了");
        }

        ReportType reportType = getReportTypeById(reportTypeId);
        if (reportType == null) {
            throw new IllegalArgumentException("檢舉類型不存在");
        }

        ReportStatus defaultStatus = reportRepository.findReportStatusById(0);
        ArticleReport report = new ArticleReport();
        report.setArticle(article);
        report.setUser(user);
        report.setReportType(reportType);
        report.setReason(reason);
        report.setReportStatus(defaultStatus); //設定預設狀態為0

        return reportRepository.save(report);
    }

}
