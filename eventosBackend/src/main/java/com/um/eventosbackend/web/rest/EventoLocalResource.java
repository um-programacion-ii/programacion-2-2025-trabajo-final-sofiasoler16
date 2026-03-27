//package com.um.eventosbackend.web.rest;
//
//import com.um.eventosbackend.domain.EventoLocal;
//import com.um.eventosbackend.repository.EventoLocalRepository;
//import java.util.List;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api")
//public class EventoLocalResource {
//
//    private final EventoLocalRepository eventoLocalRepository;
//
//    public EventoLocalResource(EventoLocalRepository eventoLocalRepository) {
//        this.eventoLocalRepository = eventoLocalRepository;
//    }
//
//    @GetMapping("/evento-locals")
//    @PreAuthorize("hasAuthority('ROLE_USER')")
//    public ResponseEntity<List<EventoLocal>> getAllEventoLocals() {
//        List<EventoLocal> list = eventoLocalRepository.findAll(Sort.by(Sort.Direction.ASC, "fecha"));
//        return ResponseEntity.ok(list);
//    }
//
//    @GetMapping("/evento-locals/{id}")
//    @PreAuthorize("hasAuthority('ROLE_USER')")
//    public ResponseEntity<EventoLocal> getEventoLocal(@PathVariable Long id) {
//        return eventoLocalRepository.findById(id)
//            .map(ResponseEntity::ok)
//            .orElse(ResponseEntity.notFound().build());
//    }
//}
