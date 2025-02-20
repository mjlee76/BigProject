package com.bigProject.tellMe.controller.manager;

import com.bigProject.tellMe.enumClass.ReportStatus;
import com.bigProject.tellMe.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ManagerRestController {
    private final ReportService reportService;

    @PostMapping("/report/updateStatus/{reportId}/confirm")
    public ResponseEntity<Void> confirmReport(@PathVariable Long reportId) {
        reportService.updateReportStatus(reportId, ReportStatus.확인완료);
        return ResponseEntity.ok().build();
    }
}
