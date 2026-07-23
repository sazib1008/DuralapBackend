package com.example.duralap.analytics.controller

import com.example.duralap.analytics.service.AnalyticsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = ["*"])
class AnalyticsController(
    private val analyticsService: AnalyticsService
) {

    @GetMapping("/metrics")
    fun getMetrics(): ResponseEntity<Map<String, Any>> {
        val summary = analyticsService.getMetricsSummary()
        return ResponseEntity.ok(summary)
    }
}
