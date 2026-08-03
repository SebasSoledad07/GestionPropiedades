package com.example.gestionpropiedades.controller;

import com.example.gestionpropiedades.dto.DashboardResponse;
import com.example.gestionpropiedades.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST del dashboard de métricas.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse obtenerResumen() {
        return dashboardService.obtenerResumen();
    }
}
