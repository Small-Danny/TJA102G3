package com.tibafit.model.article;

import jakarta.persistence.*;

import java.util.List;



@Entity
@Table(name = "report_type", uniqueConstraints = { @UniqueConstraint(columnNames = "report_type_name") })
public class ReportType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_type_id")
	private Integer reportTypeId;

	@Column(name = "report_type_name", nullable = false, length = 50)
	private String reportTypeName;

	// 反向關聯 ArticleReport（可選雙向）
	@OneToMany(mappedBy = "reportType", fetch = FetchType.LAZY)
	private List<ArticleReport> articleReports;

	public ReportType() {
	}

	public ReportType(Integer reportTypeId, String reportTypeName) {
		this.reportTypeId = reportTypeId;
		this.reportTypeName = reportTypeName;
	}

	// getter / setter
	public Integer getReportTypeId() {
		return reportTypeId;
	}

	public void setReportTypeId(Integer reportTypeId) {
		this.reportTypeId = reportTypeId;
	}

	public String getReportTypeName() {
		return reportTypeName;
	}

	public void setReportTypeName(String reportTypeName) {
		this.reportTypeName = reportTypeName;
	}

	public List<ArticleReport> getArticleReports() {
		return articleReports;
	}

	public void setArticleReports(List<ArticleReport> articleReports) {
		this.articleReports = articleReports;
	}

	@Override
	public String toString() {
		return "ReportType [reportTypeId=" + reportTypeId + ", reportTypeName=" + reportTypeName + "]";
	}
}
