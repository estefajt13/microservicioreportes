package com.example.microservicioreportes.service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;

@Service
public class AnaliticaClient {

    private final RestTemplate restTemplate;
    private final String analiticaBaseUrl;

    public AnaliticaClient(RestTemplate restTemplate,
                           @Value("${analitica.service.url:https://microservicioanalitica-production.up.railway.app}") String analiticaBaseUrl) {
        this.restTemplate = restTemplate;
        this.analiticaBaseUrl = analiticaBaseUrl;
    }

    public Long enviarNuevoReporte(AnaliticaNuevoReporteDTO evento) {
        String url = analiticaBaseUrl + "/analitica/clusters/nuevo-reporte";
        try {
            AnaliticaClusterResponseDTO response = restTemplate.postForObject(url, evento, AnaliticaClusterResponseDTO.class);
            return response != null ? response.getId() : null;
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Error llamando a analítica: " + e.getResponseBodyAsString(), e);
        }
    }
}
