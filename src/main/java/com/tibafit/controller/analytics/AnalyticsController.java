package com.tibafit.controller.analytics;

import com.tibafit.dto.analytics.SeriesResponse;
import com.tibafit.service.analytics.AnalyticsService;
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
  @GetMapping(value = {"", "/series"}, produces = MediaType.APPLICATION_JSON_VALUE)
  public SeriesResponse getSeries(@RequestParam String metric,
                                  @RequestParam String range,
                                  @RequestParam(required = false) Long userId) {
    long uid = (userId != null) ? userId : 1L;
    return analyticsService.buildSeries(metric, range, uid);
  }
}
