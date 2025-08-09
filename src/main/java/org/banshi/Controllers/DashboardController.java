package org.banshi.Controllers;

import org.banshi.Dtos.ApiResponse;
import org.banshi.Dtos.DashboardResponse;
import org.banshi.Services.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class DashboardController {

    private final DashboardService dashboardService;
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getFinanceSummary() {
        logger.info("Fetching finance summary for admin dashboard");
        try {
            DashboardResponse summary = dashboardService.getDashboardDetails();
            logger.info("Finance summary fetched successfully");
            return ResponseEntity.ok(new ApiResponse<>("success", "Finance summary fetched", summary));
        } catch (Exception e) {
            logger.error("Error fetching finance summary: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>("error", "Unable to fetch finance summary", null));
        }
    }

}
