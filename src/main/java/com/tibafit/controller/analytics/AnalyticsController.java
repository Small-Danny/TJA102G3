package com.tibafit.controller.analytics;

import com.tibafit.dto.analytics.SeriesResponse;
import com.tibafit.service.analytics.AnalyticsService;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

	private final AnalyticsService analyticsService;

	public AnalyticsController(AnalyticsService analyticsService) {
		this.analyticsService = analyticsService;
	}

	// /api/analytics 與 /api/analytics/series 都可用
	@GetMapping(value = { "", "/series" }, produces = MediaType.APPLICATION_JSON_VALUE)
	public SeriesResponse getSeries(@RequestParam String metric, @RequestParam String range,
			@RequestParam(required = false) Long userId, @RequestParam(required = false) Long planId) {
		long uid = (userId != null) ? userId : 1L;
		return analyticsService.buildSeries(metric, range, uid, planId);
	}

	@GetMapping("/export")
	public void exportCsv(@RequestParam String metric, @RequestParam String range,
			@RequestParam(required = false) Long userId, @RequestParam(required = false) Long planId,
			HttpServletResponse response) throws IOException {

		long uid = (userId != null) ? userId : 1L;

		SeriesResponse series = analyticsService.buildSeries(metric, range, uid, planId);

		String metricZh = toMetricZh(metric);
		String filename = java.net.URLEncoder.encode(
				"報表_" + metricZh + (planId != null ? ("_plan_" + planId) : "") + ".csv",
				java.nio.charset.StandardCharsets.UTF_8);

		response.setContentType("text/csv; charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename);

		try (var os = response.getOutputStream();
				var osw = new java.io.OutputStreamWriter(os, java.nio.charset.StandardCharsets.UTF_8);
				var out = new java.io.PrintWriter(osw)) {

			out.write('\uFEFF'); // UTF-8 BOM
			out.println("日期,數值,單位,指標");

			var labels = series.getLabels();
			var data = series.getData();
			var unit = series.getUnit() != null ? series.getUnit() : "";

			for (int i = 0; i < labels.size(); i++) {
				String date = String.valueOf(labels.get(i));
				String value = String.valueOf(data.get(i));
				out.printf("%s,%s,%s,%s%n", csv(date), csv(value), csv(unit), csv(metricZh));
			}
			out.flush();
		}
	}

	private static String toMetricZh(String metric) {
		if (metric == null)
			return "";
		return switch (metric) {
		case "workout-time" -> "運動時長";
		case "calories" -> "消耗熱量";
		case "tasks" -> "任務數";
		default -> metric;
		};
	}

	private static String csv(String v) {
		if (v == null)
			return "";
		boolean needQuote = v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r");
		String s = v.replace("\"", "\"\"");
		return needQuote ? "\"" + s + "\"" : s;
	}
}
