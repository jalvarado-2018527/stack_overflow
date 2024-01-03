package com.is4tech.base.controller;

import com.is4tech.base.domain.Users;
import com.is4tech.base.dto.UserDTO;
import com.is4tech.base.exception.BadRequestException;
import com.is4tech.base.exception.GenericException;
import com.is4tech.base.exception.NoContentException;
import com.is4tech.base.service.UserService;
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
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/api/user")
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<Object> save(HttpServletRequest servletRequest, @RequestBody UserDTO dto) throws BadRequestException, GenericException{
        var response = this.service.save(dto);
        Utilities.infoLog(servletRequest, HttpStatus.CREATED, "ok");
        return ResponseEntity.created(URI.create("/api/user" + response.getUserId())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(HttpServletRequest servletRequest, @RequestBody UserDTO dto, @PathParam("id") Integer id)throws BadRequestException, GenericException{
        this.service.update(id, dto);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findOne(HttpServletRequest servletRequest, @PathParam("id") Integer id) throws BadRequestException, GenericException{
        var response = this.service.findOne(id);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findByEmail/{email}")
    public ResponseEntity<UserDTO> findByEmail(HttpServletRequest servletRequest, @PathParam("email") String email) throws BadRequestException, GenericException{
        var response = this.service.findByEmail(email);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/find/{name}")
    public ResponseEntity<List<Users>> findByName(HttpServletRequest servletRequest, @RequestParam("name")String name) throws BadRequestException, GenericException{
        var response = this.service.findByName(name);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<Users>> findAll(HttpServletRequest servletRequest, @RequestParam(value = "search", required = false) String search, Pageable page) throws GenericException, NoContentException{
        var response = this.service.findAll(search, page);
        Utilities.infoLog(servletRequest, HttpStatus.OK, "ok");
        return ResponseEntity.ok(response);
    }
}