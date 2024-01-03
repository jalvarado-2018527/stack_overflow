package com.is4tech.base.controller;

import com.is4tech.base.domain.Audit;
import com.is4tech.base.dto.AuditDTO;
import com.is4tech.base.exception.BadRequestException;
import com.is4tech.base.exception.GenericException;
import com.is4tech.base.exception.NoContentException;
import com.is4tech.base.service.AuditService;
import com.is4tech.base.util.Utilities;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.Timestamp;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/api/audit")
public class AuditController {
    private final AuditService service;



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(HttpServletRequest servletRequest,@RequestBody AuditDTO dto, @PathParam("id") Integer id)throws BadRequestException, GenericException{
        this.service.delete(id);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditDTO> findOne(HttpServletRequest servletRequest, @PathParam("id") Integer id) throws BadRequestException, GenericException{
        var response = this.service.findOne(id);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/find/{auditUser}")
    public ResponseEntity<List<Audit>> findByAuditUser(HttpServletRequest servletRequest, @RequestParam("auditUser") String auditUser) throws BadRequestException, GenericException{
        var response = this.service.findByAuditUser(auditUser);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/entity/{entity}")
    public ResponseEntity<List<Audit>> findByAuditEntity(HttpServletRequest servletRequest, @RequestParam("entity") String entity) throws BadRequestException, GenericException{
        var response = this.service.findAllByEntity(entity);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<Audit>> findAllByAction(HttpServletRequest servletRequest, @RequestParam("action") String action) throws BadRequestException, GenericException{
        var response = this.service.findAllByAction(action);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<Audit>> findAll(HttpServletRequest servletRequest, @RequestParam(value = "search", required = false) String search, Pageable page) throws GenericException, NoContentException {
        var response = this.service.findAll(search, page);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/dates")
    public ResponseEntity<List<Audit>> findByDateRange(@RequestParam("startDate") Timestamp startDate, @RequestParam("endDate") Timestamp endDate) throws BadRequestException, GenericException {
        List<Audit> response = service.findByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
