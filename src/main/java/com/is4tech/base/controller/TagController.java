package com.is4tech.base.controller;

import com.is4tech.base.domain.Tags;
import com.is4tech.base.dto.TagDTO;
import com.is4tech.base.exception.BadRequestException;
import com.is4tech.base.exception.GenericException;
import com.is4tech.base.exception.NoContentException;
import com.is4tech.base.service.TagService;
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
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/api/tag")
public class TagController {
    private final TagService service;

    @PostMapping
    public ResponseEntity<Object> save(HttpServletRequest servletRequest, @RequestBody TagDTO dto) throws BadRequestException, GenericException {
        var response = this.service.save(dto);
        Utilities.infoLog(servletRequest, HttpStatus.CREATED, "ok");
        return ResponseEntity.created(URI.create("/api/tag" + response.getId())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(HttpServletRequest servletRequest, @RequestBody TagDTO dto, @PathParam("id") Integer id) throws BadRequestException, GenericException{
        this.service.update(id, dto);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(HttpServletRequest servletRequest, @RequestBody TagDTO dto, @PathParam("id") Integer id)throws BadRequestException, GenericException{
        this.service.delete(id);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> findOne(HttpServletRequest servletRequest, @PathParam("id") Integer id) throws BadRequestException, GenericException{
        var response = this.service.findOne(id);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/find/{name}")
    public ResponseEntity<List<Tags>> findByName(HttpServletRequest servletRequest, @RequestParam("name") String name) throws BadRequestException, GenericException{
        var response = this.service.findByName(name);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<Tags>> findAll(HttpServletRequest servletRequest, @RequestParam(value = "search", required = false) String search, Pageable page) throws GenericException, NoContentException {
        var response = this.service.findAll(search, page);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }
}
