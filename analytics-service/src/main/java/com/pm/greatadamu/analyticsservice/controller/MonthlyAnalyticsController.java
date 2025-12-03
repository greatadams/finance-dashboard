package com.pm.greatadamu.analyticsservice.controller;
import com.pm.greatadamu.analyticsservice.dto.AnalyticsResponseDTO;
import com.pm.greatadamu.analyticsservice.model.Month;
import com.pm.greatadamu.analyticsservice.service.MonthlyAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class MonthlyAnalyticsController {
    private final MonthlyAnalyticsService monthlyAnalyticsService;

    @GetMapping
    public ResponseEntity<AnalyticsResponseDTO> getMonthlyAnalytics(
            @RequestParam Long customerId,
            @RequestParam Month month,
            @RequestParam int year
    ){
        //call method from service to get monthly analytics and pass to entity
      AnalyticsResponseDTO responseDTO = monthlyAnalyticsService.getMonthlyAnalytics(customerId, month, year);

        //entity is then map to responseDto by mapper then spring convert to JSON for user
        return ResponseEntity.ok(responseDTO);

    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AnalyticsResponseDTO>> getAllMonthlyAnalytics(
            @PathVariable Long customerId
    ){
        //call method from service to get all months / all years in DB)
        List<AnalyticsResponseDTO> responseDTO = monthlyAnalyticsService.getAllMonthlyAnalytics(customerId);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/customer/{customerId}/year/{year}")
    public ResponseEntity<List<AnalyticsResponseDTO>> getMonthlyAnalyticsByCustomerAndYear(
            @PathVariable Long customerId,
            @PathVariable int year

    ){
        List<AnalyticsResponseDTO> responseDTO=monthlyAnalyticsService.getMonthlyAnalyticsByYear(customerId,year);

        return ResponseEntity.ok(responseDTO);


    }

}
