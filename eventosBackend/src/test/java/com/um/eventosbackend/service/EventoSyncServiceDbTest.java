package com.um.eventosbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.um.eventosbackend.domain.EventoLocal;
import com.um.eventosbackend.repository.EventoLocalRepository;
import com.um.eventosbackend.service.catedra.CatedraClient;
import com.um.eventosbackend.service.catedra.EventoSyncService;
import com.um.eventosbackend.service.dto.catedra.CatedraEventoDetalleDTO;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource(properties = "app.sync.catedra.delete-missing=true")
class EventoSyncServiceDbTest {

    @MockBean
    private CatedraClient catedraClient;

    // Si JHipster te lo pide para levantar contexto, se deja
    @MockBean
    private com.um.eventosbackend.repository.search.UserSearchRepository userSearchRepository;

    @Autowired
    private EventoSyncService eventoSyncService;

    @Autowired
    private EventoLocalRepository eventoLocalRepository;


    @BeforeEach
    void setUp() {
        eventoLocalRepository.deleteAll();
    }

    @Test
    void debeCrearEventoLocalCuandoNoExiste() {
        // given
        var remoto = dto(1L, "Recital", "Resumen", "Desc", new BigDecimal("15000"));

        when(catedraClient.listarEventos()).thenReturn(List.of(remoto));

        // when
        eventoSyncService.syncEventos();

        // then
        var guardados = eventoLocalRepository.findAll();
        assertThat(guardados).hasSize(1);

        var e = guardados.get(0);
        assertThat(e.getIdCatedra()).isEqualTo(1L);
        assertThat(e.getTitulo()).isEqualTo("Recital");
        assertThat(e.getResumen()).isEqualTo("Resumen");
        assertThat(e.getDescripcion()).isEqualTo("Desc");
        assertThat(e.getPrecioEntrada()).isEqualByComparingTo("15000");
        assertThat(e.getFilasAsientos()).isEqualTo(10);
        assertThat(e.getColumnasAsientos()).isEqualTo(12);
    }

    @Test
    void debeActualizarEventoLocalCuandoYaExiste() {
        // given
        var local = new EventoLocal();
        local.setIdCatedra(2L);
        local.setTitulo("Titulo viejo");
        local.setPrecioEntrada(new BigDecimal("1000"));
        eventoLocalRepository.saveAndFlush(local);

        var remoto = dto(2L, "Titulo nuevo", "Nuevo resumen", "Nueva desc", new BigDecimal("20000"));
        when(catedraClient.listarEventos()).thenReturn(List.of(remoto));

        // when
        eventoSyncService.syncEventos();

        // then
        var actualizado = eventoLocalRepository.findByIdCatedra(2L).orElseThrow();
        assertThat(actualizado.getTitulo()).isEqualTo("Titulo nuevo");
        assertThat(actualizado.getResumen()).isEqualTo("Nuevo resumen");
        assertThat(actualizado.getDescripcion()).isEqualTo("Nueva desc");
        assertThat(actualizado.getPrecioEntrada()).isEqualByComparingTo("20000");
    }

    @Test
    void debeEliminarLocalesQueYaNoEstanEnCatedra_porDefecto() {
        // given
        var a = new EventoLocal();
        a.setIdCatedra(10L);
        a.setTitulo("A");
        eventoLocalRepository.save(a);

        var b = new EventoLocal();
        b.setIdCatedra(11L);
        b.setTitulo("B");
        eventoLocalRepository.saveAndFlush(b);

        when(catedraClient.listarEventos()).thenReturn(
            List.of(dto(10L, "A", "r", "d", BigDecimal.TEN))
        );

        // when
        eventoSyncService.syncEventos();

        // then
        assertThat(eventoLocalRepository.findByIdCatedra(10L)).isPresent();
        assertThat(eventoLocalRepository.findByIdCatedra(11L)).isEmpty();
    }

    @Test
    void siCatedraFalla_noDebeModificarLaDB() {
        // given
        var local = new EventoLocal();
        local.setIdCatedra(99L);
        local.setTitulo("No tocar");
        eventoLocalRepository.saveAndFlush(local);

        when(catedraClient.listarEventos())
            .thenThrow(new RuntimeException("Cátedra caída"));

        // when
        eventoSyncService.syncEventos();

        // then
        var siguen = eventoLocalRepository.findAll();
        assertThat(siguen).hasSize(1);
        assertThat(siguen.get(0).getTitulo()).isEqualTo("No tocar");
    }

    private static CatedraEventoDetalleDTO dto(
        Long id,
        String titulo,
        String resumen,
        String descripcion,
        BigDecimal precio
    ) {
        var d = new CatedraEventoDetalleDTO();
        d.setId(id);
        d.setTitulo(titulo);
        d.setResumen(resumen);
        d.setDescripcion(descripcion);
        d.setFecha(Instant.now().plusSeconds(3600));
        d.setDireccion("Calle falsa 123");
        d.setImagen("img.png");
        d.setFilaAsientos(10);
        d.setColumnAsientos(12);
        d.setPrecioEntrada(precio);
        return d;
    }
}
