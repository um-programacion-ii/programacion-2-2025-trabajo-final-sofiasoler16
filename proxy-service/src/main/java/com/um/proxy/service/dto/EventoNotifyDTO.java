package com.um.proxy.service.dto;

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
