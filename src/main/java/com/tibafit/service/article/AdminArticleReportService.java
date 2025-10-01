package com.tibafit.service.article;

import org.springframework.stereotype.Service;

import com.tibafit.dto.article.ArticleReportDTO;
import com.tibafit.dto.article.ReportStatusDTO;
import com.tibafit.model.article.ArticleReport;
import com.tibafit.model.article.ReportStatus;
import com.tibafit.repository.article.AdminArticleReportRepository;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminArticleReportService {

	private final AdminArticleReportRepository reportRepository;

	public AdminArticleReportService(AdminArticleReportRepository reportRepository) {
		this.reportRepository = reportRepository;

	}

	public List<ArticleReportDTO> getAllReports(String keyword) {
		return reportRepository.findAllReportDTO(keyword);
	}

	/** 取得所有檢舉狀態 */
	public List<ReportStatusDTO> getAllReportStatuses() {
		return reportRepository.findAllReportStatusesDTO();
	}
	

	/** 更新檢舉狀態 */
	@Transactional
	public void updateReportStatus(Integer reportId, Integer statusId) {
		reportRepository.updateReportStatus(reportId, statusId);
	}
}
