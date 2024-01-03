package com.is4tech.base.controller;

import com.is4tech.base.domain.Answers;
import com.is4tech.base.dto.AnswerDTO;
import com.is4tech.base.dto.AnswerVDTO;
import com.is4tech.base.exception.BadRequestException;
import com.is4tech.base.exception.GenericException;
import com.is4tech.base.exception.NoContentException;
import com.is4tech.base.service.AnswerService;
import com.is4tech.base.util.Utilities;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import jdk.jshell.execution.Util;
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
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/api/answers")
public class AnswerController {

    private final AnswerService service;

    @PostMapping
    public ResponseEntity<Object> save(HttpServletRequest servletRequest, @RequestBody AnswerDTO dto) throws BadRequestException, GenericException{
        var response = this.service.save(dto);
        Utilities.infoLog(servletRequest, HttpStatus.CREATED, "ok");
        return ResponseEntity.created(URI.create("/api/answers" + response.getAnswerId())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(HttpServletRequest servletRequest, @PathParam("id") Integer id, @RequestBody AnswerDTO dto) throws BadRequestException, GenericException{
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
    public ResponseEntity<AnswerDTO> findOne(HttpServletRequest servletRequest, @PathParam("id") Integer id )throws BadRequestException, GenericException{
        var response = this.service.findOne(id);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<AnswerVDTO>> findAll(HttpServletRequest servletRequest, @RequestParam(value = "search", required = false) String search, Pageable page) throws GenericException, NoContentException{
        var response = this.service.findAll(search, page);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/find/{questionId}")
    public ResponseEntity<Page<Answers>> findAllByQuestionId(HttpServletRequest servletRequest,@PathParam("id") Integer questionId,  @RequestParam(value = "search", required = false) String search, Pageable page ) throws GenericException, NoContentException{
        var response = this.service.findAllByQuestionId(questionId, search, page);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }
}
