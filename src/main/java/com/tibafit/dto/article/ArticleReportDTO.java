package com.tibafit.dto.article;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ArticleReportDTO {
	private Integer reportId;
	private String name; // 檢舉者
	private String title; // 文章標題
	private String reportTypeName; // 檢舉類型
	private String reason; // 原因
	private LocalDateTime reportTime; // 檢舉時間
	private String reportStatusName; // 處理狀態

	public ArticleReportDTO(Integer reportId, String name, String title, String reportTypeName, String reason,
			LocalDateTime reportTime, String reportStatusName) {
		this.reportId = reportId;
		this.name = name;
		this.title = title;
		this.reportTypeName = reportTypeName;
		this.reason = reason;
		this.reportTime = reportTime;
		this.reportStatusName = reportStatusName;
	}

	// getter / setter
	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReportTypeName() {
		return reportTypeName;
	}

	public void setReportTypeName(String reportTypeName) {
		this.reportTypeName = reportTypeName;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDateTime getReportTime() {
		return reportTime;
	}

	public void setReportTime(LocalDateTime reportTime) {
		this.reportTime = reportTime;
	}

	public String getReportStatusName() {
		return reportStatusName;
	}

	public void setReportStatusName(String reportStatusName) {
		this.reportStatusName = reportStatusName;
	}
	public String getFormattedReportTime() {
	    if (reportTime == null) return "";
	    return reportTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
}
