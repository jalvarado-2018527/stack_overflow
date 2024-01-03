package com.is4tech.base.util.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.base.domain.Roles;
import com.is4tech.base.dto.RoleDTO;
import com.is4tech.base.repository.RoleRepository;
import com.is4tech.base.util.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@WebAppConfiguration
class RoleControllerTest {
    private final String baseUrl = "/api/role";
    private final String updateUrl = "/api/role/";

    @Autowired
    protected MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RoleRepository repository;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void save() throws Exception{
        Mockito.when(repository.findFirstByCodeIgnoreCase(any())).thenReturn(Optional.empty());
        Mockito.when(repository.save(any())).thenReturn(domain());

        mvc.perform(
                        post(baseUrl)
                                .content(objectMapper.writeValueAsBytes(dto()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveErrorExists() throws Exception {
        Mockito.when(repository.findFirstByCodeIgnoreCase(any())).thenReturn(Optional.of(domain()));

        mvc.perform(
                        post(baseUrl)
                                .content(objectMapper.writeValueAsBytes(dto()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveErrorGeneral() throws Exception {
        Mockito.when(repository.findFirstByCodeIgnoreCase(any())).thenThrow(new NullPointerException("...") {});
        mvc.perform(
                        post(baseUrl)
                                .content(objectMapper.writeValueAsBytes(dto()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void update() throws Exception {
        Mockito.when(repository.findById(any())).thenReturn(Optional.of((domain())));
        Mockito.when(repository.save(any())).thenReturn(domain());

        mvc.perform(
                        put(updateUrl + 1)
                                .content(objectMapper.writeValueAsBytes(dto()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateErrorExists() throws Exception{

        Mockito.when(repository.findById(any())).thenReturn(Optional.empty());

        mvc.perform(
                        put(updateUrl+1)
                                .content(objectMapper.writeValueAsBytes(dto()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateErrorGeneral() throws Exception{

        Mockito.when(repository.findById(any())).thenThrow(new NullPointerException());

        mvc.perform(
                        put(updateUrl+1)
                                .content(objectMapper.writeValueAsBytes(dto()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleted() throws Exception{

        Mockito.when(repository.findById(any())).thenReturn(Optional.of(domain()));
        mvc.perform(
                        delete(updateUrl + 1)
                                .content(objectMapper.writeValueAsBytes(dto()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteErrorExists() throws Exception{

        Mockito.when(repository.findById(any())).thenReturn(Optional.empty());

        mvc.perform(
                        delete(updateUrl+1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteErrorGeneral() throws Exception{

        Mockito.when(repository.findById(any())).thenThrow(new NullPointerException());

        mvc.perform(
                        delete(updateUrl+1)
                                .content(objectMapper.writeValueAsBytes(dto()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findOne() throws Exception{

        Mockito.when(repository.findById(any())).thenReturn(Optional.of(domain()));
        mvc.perform(
                        get(updateUrl + 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findOneErrorExists() throws Exception{

        Mockito.when(repository.findById(any())).thenReturn(Optional.empty());
        mvc.perform(
                        get(updateUrl + 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findOneErrorGeneral() throws Exception{

        Mockito.when(repository.findById(any())).thenThrow(new NullPointerException());
        mvc.perform(
                        get(updateUrl + 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findAll() throws Exception{
        Mockito.when(repository.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>(List.of(domain()))));

        mvc.perform(
                get(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findAllErrorExists() throws Exception{

        Mockito.when(repository.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mvc.perform(
                get(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findAllErrorGeneral() throws Exception{

        Mockito.when(repository.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenThrow(new NullPointerException());

        mvc.perform(
                get(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isInternalServerError());
    }

    Roles domain() {
        var ro = new Roles();
        ro.setRoleId(1);
        ro.setCode("0001");
        ro.setDescription("DIVISA_TEST");
        return ro;
    }

    RoleDTO dto() {
        var dto = new RoleDTO();
        dto.setRoleId(1);
        dto.setCode("0001");
        dto.setDescription("DIVISA_TEST");
        return dto;
    }
}
