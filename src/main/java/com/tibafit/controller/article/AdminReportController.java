package com.tibafit.controller.article;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tibafit.dto.article.ArticleReportDTO;
import com.tibafit.dto.article.ReportStatusDTO;
import com.tibafit.service.article.AdminArticleReportService;
import com.tibafit.service.article.ArticleReportService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/community/reports")
public class AdminReportController {

	private final AdminArticleReportService reportService;

	public AdminReportController(AdminArticleReportService reportService) {
		this.reportService = reportService;
	}

	@GetMapping
	public String listReports(@RequestParam(required = false) String keyword,Model model) {
		List<ArticleReportDTO> reports = reportService.getAllReports(keyword);
		model.addAttribute("reports", reports);
		model.addAttribute("keyword", keyword);
		// 傳下拉選單資料給前端
		model.addAttribute("statusList", reportService.getAllReportStatuses());

		return "admin/article/reportList";
	}

	// 更新檢舉狀態
	@PostMapping("/updateStatus")
	public String updateStatus(@RequestParam Integer reportId, @RequestParam Integer statusId) {
		reportService.updateReportStatus(reportId, statusId);
		return "redirect:/admin/community/reports"; // 重定向回列表頁
	}
}
