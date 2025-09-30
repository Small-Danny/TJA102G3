package com.tibafit.model.article;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import com.tibafit.model.user.User;






@Entity
@Table(name = "article_report")
public class ArticleReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 檢舉人

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article; // 被檢舉文章

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_type_id", nullable = false)
    private ReportType reportType; // 檢舉類型

    @Column(length = 500)
    private String reason; // 檢舉原因補充

//    @Column(name = "report_time", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
//    private LocalDateTime reportTime;
    @Column(name = "report_time", updatable = false, insertable = false)
    private LocalDateTime reportTime;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_status", nullable = false)
    private ReportStatus reportStatus; // 處理狀態

    public ArticleReport() {
    }

    // getter / setter
    public Integer getReportId() { return reportId; }
    public void setReportId(Integer reportId) { this.reportId = reportId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }

    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getReportTime() { return reportTime; }
    public void setReportTime(LocalDateTime reportTime) { this.reportTime = reportTime; }

    public ReportStatus getReportStatus() { return reportStatus; }
    public void setReportStatus(ReportStatus reportStatus) { this.reportStatus = reportStatus; }

    @Override
    public String toString() {
        return "ArticleReport [reportId=" + reportId + ", user=" + user + ", article=" + article
                + ", reportType=" + reportType + ", reason=" + reason
                + ", reportTime=" + reportTime + ", reportStatus=" + reportStatus + "]";
    }
}
