package com.is4tech.base.controller;

import com.is4tech.base.domain.Comments;
import com.is4tech.base.dto.CommentDTO;
import com.is4tech.base.exception.BadRequestException;
import com.is4tech.base.exception.GenericException;
import com.is4tech.base.exception.NoContentException;
import com.is4tech.base.service.CommentService;
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


@RestController
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/api/comments")
public class CommentController {

    private final CommentService service;

    @PostMapping
    public ResponseEntity<Object> save(HttpServletRequest servletRequest, @RequestBody CommentDTO dto) throws BadRequestException, GenericException {
        var response = this.service.save(dto);
        Utilities.infoLog(servletRequest, HttpStatus.CREATED, "ok");
        return ResponseEntity.created(URI.create("/api/comments" + response.getCommentId())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(HttpServletRequest servletRequest, @PathParam("id") Integer id, @RequestBody CommentDTO dto) throws BadRequestException, GenericException{
        this.service.update(id, dto);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(HttpServletRequest servletRequest, @PathParam("id") Integer id)throws BadRequestException, GenericException{
        this.service.delete(id);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> findOne(HttpServletRequest servletRequest, @PathParam("id") Integer id )throws BadRequestException, GenericException{
        var response = this.service.findOne(id);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<Comments>> findAll(HttpServletRequest servletRequest, @RequestParam(value = "search", required = false) String search, Pageable page) throws GenericException, NoContentException {
        var response = this.service.findAll(search, page);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/find/{answerId}")
    public ResponseEntity<Page<Comments>> findAllByAnswerId(HttpServletRequest servletRequest, @PathParam("answerId") Integer answerId, @RequestParam(value = "search", required = false)String search, Pageable page)throws GenericException, NoContentException{
        var response = this.service.findAllByAnswerId(answerId, search, page);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }
}