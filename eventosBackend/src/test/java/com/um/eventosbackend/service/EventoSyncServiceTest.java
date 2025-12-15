package com.um.eventosbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import com.um.eventosbackend.domain.EventoLocal;
import com.um.eventosbackend.domain.EventoTipoLocal;
import com.um.eventosbackend.domain.IntegranteLocal;
import com.um.eventosbackend.repository.EventoLocalRepository;
import com.um.eventosbackend.repository.EventoTipoLocalRepository;
import com.um.eventosbackend.repository.IntegranteLocalRepository;
import com.um.eventosbackend.service.catedra.CatedraClient;
import com.um.eventosbackend.service.catedra.EventoSyncService;
import com.um.eventosbackend.service.dto.catedra.CatedraEventoDetalleDTO;
import com.um.eventosbackend.service.dto.catedra.CatedraEventoTipoDTO;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventoSyncServiceTest {

    @Mock
    private CatedraClient catedraClient;

    @Mock
    private EventoLocalRepository eventoLocalRepository;

    @Mock
    EventoTipoLocalRepository eventoTipoLocalRepository;

    @InjectMocks
    private EventoSyncService eventoSyncService;

    @Mock
    private IntegranteLocalRepository integranteLocalRepository;



    // --- helpers -------------------------------------------------------------

    private CatedraEventoDetalleDTO crearEventoRemoto(Long id, String titulo) {
        CatedraEventoDetalleDTO dto = new CatedraEventoDetalleDTO();
        dto.setId(id);
        dto.setTitulo(titulo);
        dto.setResumen("resumen " + titulo);
        dto.setDescripcion("descripcion " + titulo);
        dto.setFecha(Instant.parse("2025-01-01T10:00:00Z"));
        dto.setDireccion("Direccion " + titulo);
        dto.setImagen("imagen-" + id + ".jpg");
        dto.setFilaAsientos(10);
        dto.setColumnAsientos(12);
        dto.setPrecioEntrada(BigDecimal.valueOf(100));

        CatedraEventoTipoDTO tipo = new CatedraEventoTipoDTO();
        tipo.setNombre("Tipo " + titulo);
        tipo.setDescripcion("Desc tipo " + titulo);
        dto.setEventoTipo(tipo);

        dto.setIntegrantes(null);

        return dto;
    }


    @Test
    void debeCrearNuevoEventoLocalCuandoNoExiste() {
        CatedraEventoDetalleDTO remoto = crearEventoRemoto(1L, "Evento remoto 1");
        when(catedraClient.listarEventos()).thenReturn(List.of(remoto));

        when(eventoLocalRepository.findAll()).thenReturn(Collections.emptyList());
        when(eventoLocalRepository.findByIdCatedra(1L)).thenReturn(Optional.empty());

        // El service siempre necesita resolver/crear el tipo
        when(eventoTipoLocalRepository.findOneByNombre(anyString())).thenReturn(Optional.empty());
        when(eventoTipoLocalRepository.save(any(EventoTipoLocal.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        eventoSyncService.syncEventos();

        verify(eventoLocalRepository).save(argThat(local ->
            local.getIdCatedra().equals(1L)
                && "Evento remoto 1".equals(local.getTitulo())
                && local.getEventoTipo() != null
                && "Tipo Evento remoto 1".equals(local.getEventoTipo().getNombre())
        ));

        verify(eventoLocalRepository, never()).delete(any(EventoLocal.class));
        verifyNoInteractions(integranteLocalRepository);
    }

    @Test
    void debeActualizarEventoLocalCuandoYaExiste() {
        CatedraEventoDetalleDTO remoto = crearEventoRemoto(2L, "Titulo nuevo");
        when(catedraClient.listarEventos()).thenReturn(List.of(remoto));

        EventoLocal existente = new EventoLocal();
        existente.id(10L);
        existente.idCatedra(2L);
        existente.titulo("Titulo viejo");

        when(eventoLocalRepository.findAll()).thenReturn(List.of(existente));
        when(eventoLocalRepository.findByIdCatedra(2L)).thenReturn(Optional.of(existente));

        EventoTipoLocal tipoExistente = new EventoTipoLocal();
        tipoExistente.setNombre("Tipo Titulo nuevo");
        tipoExistente.setDescripcion("Desc tipo Titulo nuevo");
        when(eventoTipoLocalRepository.findOneByNombre("Tipo Titulo nuevo"))
            .thenReturn(Optional.of(tipoExistente));

        eventoSyncService.syncEventos();

        assertThat(existente.getTitulo()).isEqualTo("Titulo nuevo");
        assertThat(existente.getEventoTipo()).isNotNull();
        assertThat(existente.getEventoTipo().getNombre()).isEqualTo("Tipo Titulo nuevo");

        verify(eventoLocalRepository).save(existente);
        verify(eventoLocalRepository, never()).delete(any(EventoLocal.class));
        verifyNoInteractions(integranteLocalRepository);
    }

    @Test
    void debeEliminarEventoLocalCuandoYaNoExisteEnCatedra() {
        when(catedraClient.listarEventos()).thenReturn(Collections.emptyList());

        EventoLocal local = new EventoLocal();
        local.id(100L);
        local.idCatedra(5L);
        local.titulo("Evento obsoleto");

        when(eventoLocalRepository.findAll()).thenReturn(List.of(local));

        eventoSyncService.syncEventos();

        verify(eventoLocalRepository).delete(local);
        verify(eventoLocalRepository, never()).save(any(EventoLocal.class));

        verifyNoInteractions(eventoTipoLocalRepository);
        verifyNoInteractions(integranteLocalRepository);
    }
}
