package com.um.eventosbackend.service.notify;

public class EventoNotifyDTO {

    private Long idCatedra;

    public EventoNotifyDTO() {}

    public EventoNotifyDTO(Long idCatedra) {
        this.idCatedra = idCatedra;
    }

    public Long getIdCatedra() {
        return idCatedra;
    }

    public void setIdCatedra(Long idCatedra) {
        this.idCatedra = idCatedra;
    }
}
