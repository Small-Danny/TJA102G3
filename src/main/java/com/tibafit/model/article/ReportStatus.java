package com.tibafit.model.article;

import jakarta.persistence.*;

import java.util.List;
import com.tibafit.model.article.ArticleReport;

@Entity
@Table(name = "report_status",
       uniqueConstraints = {@UniqueConstraint(columnNames = "status_name")})
public class ReportStatus {

    @Id
    @Column(name = "report_status")
    private Integer reportStatusId; // 對應報表的 report_status FK

    @Column(name = "status_name", nullable = false, length = 50)
    private String statusName;

    // 反向關聯 ArticleReport（可選擇雙向）
    @OneToMany(mappedBy = "reportStatus", fetch = FetchType.LAZY)
    private List<ArticleReport> articleReports;

    public ReportStatus() {
    }

    public ReportStatus(Integer reportStatusId, String statusName) {
        this.reportStatusId = reportStatusId;
        this.statusName = statusName;
    }

    // getter / setter
    public Integer getReportStatusId() { return reportStatusId; }
    public void setReportStatusId(Integer reportStatusId) { this.reportStatusId = reportStatusId; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public List<ArticleReport> getArticleReports() { return articleReports; }
    public void setArticleReports(List<ArticleReport> articleReports) { this.articleReports = articleReports; }

    @Override
    public String toString() {
        return "ReportStatus [reportStatusId=" + reportStatusId + ", statusName=" + statusName + "]";
    }
}
