package com.example.microservicioreportes.controller;

import com.example.microservicioreportes.model.Area;
import com.example.microservicioreportes.model.EstadoReporte;
import com.example.microservicioreportes.model.Reporte;
import com.example.microservicioreportes.model.TipoReporte;
import com.example.microservicioreportes.repository.AreaRepository;
import com.example.microservicioreportes.repository.HistorialRepository;
import com.example.microservicioreportes.repository.ReporteRepository;
import com.example.microservicioreportes.repository.TipoReporteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private TipoReporteRepository tipoReporteRepository;

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private HistorialRepository historialRepository;

    private Area area;

    private TipoReporte tipo;

    @BeforeEach
    void setUp() {
        area = areaRepository.save(createArea("Infraestructura Vial"));
        tipo = tipoReporteRepository.save(createTipoReporte("Bache", area));
    }

    @AfterEach
    void limpiar() {
        historialRepository.deleteAll();
        reporteRepository.deleteAll();
        tipoReporteRepository.deleteAll();
        areaRepository.deleteAll();
    }

    // ─── helpers ──────────────────────────────────────────────────────────

    private static Area createArea(String nombre) {
        Area a = new Area();
        a.setNombre(nombre);
        return a;
    }

    private static TipoReporte createTipoReporte(String nombre, Area area) {
        TipoReporte t = new TipoReporte();
        t.setNombre(nombre);
        t.setArea(area);
        return t;
    }

    private Reporte createReporte(String uid, TipoReporte tipo) {
        Reporte r = new Reporte();
        r.setUidCiudadano(uid);
        r.setTipoReporte(tipo);
        r.setAsunto("Bache en la calle");
        r.setDescripcion("Hay un bache grande frente al numero 123");
        r.setLatitud(-34.61);
        r.setLongitud(-58.38);
        r.setDireccion("Av. Siempre Viva 123");
        r.setZona("Centro");
        return r;
    }

    private String toJson(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    // ═════════════════════════════════════════════════════════════════════
    // Caso 1: Crear reporte valido
    // ═════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Caso 1: POST /reportes con datos validos -> 201, estado pendiente")
    void crearReporte_Valido_Retorna201() throws Exception {
        Reporte input = createReporte("uid-ciudadano-1", tipo);
        input.setPrioridad("alta");

        mockMvc.perform(post("/reportes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.estado").value("pendiente"))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.uidCiudadano").value("uid-ciudadano-1"));

        List<Reporte> todos = reporteRepository.findAll();
        assertThat(todos).hasSize(1);
        assertThat(todos.get(0).getEstado()).isEqualTo(EstadoReporte.pendiente);
    }

    // ═════════════════════════════════════════════════════════════════════
    // Caso 2: Crear reporte sin campos obligatorios
    // ═════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Caso 2: POST /reportes sin campos obligatorios -> 400")
    void crearReporte_SinCamposObligatorios_Retorna400() throws Exception {
        mockMvc.perform(post("/reportes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ═════════════════════════════════════════════════════════════════════
    // Caso 3: Consultar reporte existente
    // ═════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Caso 3: GET /reportes/{id} existente -> 200, datos correctos")
    void consultarReporte_Existente_Retorna200() throws Exception {
        Reporte guardado = reporteRepository.save(createReporte("uid-3", tipo));

        mockMvc.perform(get("/reportes/{id}", guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(guardado.getId()))
                .andExpect(jsonPath("$.uidCiudadano").value("uid-3"))
                .andExpect(jsonPath("$.asunto").value("Bache en la calle"))
                .andExpect(jsonPath("$.estado").value("pendiente"))
                .andExpect(jsonPath("$.nombreArea").value("Infraestructura Vial"));
    }

    // ═════════════════════════════════════════════════════════════════════
    // Caso 4: Consultar reporte inexistente
    // ═════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Caso 4: GET /reportes/{id} inexistente -> 404")
    void consultarReporte_Inexistente_Retorna404() throws Exception {
        mockMvc.perform(get("/reportes/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    // ═════════════════════════════════════════════════════════════════════
    // Caso 5: Editar reporte existente
    // ═════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Caso 5: PUT /reportes/{id} -> 200, cambio persiste en BD")
    void editarReporte_Existente_Retorna200() throws Exception {
        Reporte guardado = reporteRepository.save(createReporte("uid-5", tipo));

        String nuevoAsunto = "Asunto editado";
        String nuevaDesc = "Descripcion modificada";
        Reporte cambios = new Reporte();
        cambios.setAsunto(nuevoAsunto);
        cambios.setDescripcion(nuevaDesc);

        mockMvc.perform(put("/reportes/{id}", guardado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(cambios)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.asunto").value(nuevoAsunto));

        Reporte enBd = reporteRepository.findById(guardado.getId()).orElseThrow();
        assertThat(enBd.getAsunto()).isEqualTo(nuevoAsunto);
        assertThat(enBd.getDescripcion()).isEqualTo(nuevaDesc);
    }

    // ═════════════════════════════════════════════════════════════════════
    // Caso 6: Eliminar reporte (baja logica)
    // ═════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Caso 6: DELETE /reportes/{id} -> 200, activo=false en BD")
    void eliminarReporte_Existente_Retorna200() throws Exception {
        Reporte guardado = reporteRepository.save(createReporte("uid-6", tipo));

        mockMvc.perform(delete("/reportes/{id}", guardado.getId()))
                .andExpect(status().isOk());

        Reporte enBd = reporteRepository.findById(guardado.getId()).orElseThrow();
        assertThat(enBd.getActivo()).isFalse();
    }

    // ═════════════════════════════════════════════════════════════════════
    // Caso 7: Listar reportes de un ciudadano con datos
    // ═════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Caso 7: GET /reportes/ciudadano/{uid} con datos -> 200, lista con resultados")
    void listarPorCiudadano_ConDatos_Retorna200() throws Exception {
        String uid = "uid-con-datos";
        reporteRepository.save(createReporte(uid, tipo));
        reporteRepository.save(createReporte(uid, tipo));

        mockMvc.perform(get("/reportes/ciudadano/{uid}", uid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ═════════════════════════════════════════════════════════════════════
    // Caso 8: Listar reportes de un ciudadano sin reportes
    // ═════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Caso 8: GET /reportes/ciudadano/{uid} sin datos -> 200, lista vacia")
    void listarPorCiudadano_SinDatos_Retorna200() throws Exception {
        mockMvc.perform(get("/reportes/ciudadano/{uid}", "uid-sin-datos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
