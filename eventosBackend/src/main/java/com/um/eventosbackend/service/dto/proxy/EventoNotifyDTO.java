package com.um.eventosbackend.service.dto.proxy;

public class EventoNotifyDTO {

    private Long idCatedra;

    public Long getIdCatedra() {
        return idCatedra;
    }

    public void setIdCatedra(Long idCatedra) {
        this.idCatedra = idCatedra;
    }

    @Override
    public String toString() {
        return "EventoNotifyDTO{" +
            "idCatedra=" + idCatedra +
            '}';
    }
}
