package com.um.eventosbackend.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.um.eventosbackend.service.catedra.EventoSyncService;
import com.um.eventosbackend.service.dto.proxy.EventoNotifyDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class EventoNotifyResourceTest {

    @Mock
    private EventoSyncService eventoSyncService;

    @InjectMocks
    private EventoNotifyResource eventoNotifyResource;

    @Test
    void notifyCambioEventoDebeLlamarASyncEventos() {
        EventoNotifyDTO dto = new EventoNotifyDTO();
        dto.setIdCatedra(42L);

        ResponseEntity<Void> response = eventoNotifyResource.notifyCambioEvento(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        verify(eventoSyncService).syncEventos();
    }
}
