package com.example.microservicioreportes.controller;

import com.example.microservicioreportes.service.AdminGestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminFuncionarioController {

    @Autowired
    private AdminGestionService adminGestionService;

    /**
     * GET /admin/funcionarios
     * Lista simple de funcionarios (UIDs detectados en reportes activos)
     */
    @GetMapping("/funcionarios")
    public ResponseEntity<List<Map<String, Object>>> getFuncionarios() {
        return ResponseEntity.ok(adminGestionService.getFuncionarios());
    }
}
