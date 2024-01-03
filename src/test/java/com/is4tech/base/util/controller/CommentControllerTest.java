package com.is4tech.base.util.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.base.domain.Answers;
import com.is4tech.base.domain.Comments;
import com.is4tech.base.dto.CommentDTO;
import com.is4tech.base.repository.AnswerRepository;
import com.is4tech.base.repository.CommentRepository;
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

import java.sql.Timestamp;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@WebAppConfiguration
public class CommentControllerTest {
    private final String baseUrl = "/api/tag";
    private final String updateUrl = "/api/tag/";

    @Autowired
    protected MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CommentRepository repository;
    @MockBean
    private  AnswerRepository answerRepository;
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void save() throws Exception{
        Mockito.when(answerRepository.findById(any())).thenReturn(Optional.of(answersDomain()));
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
        Mockito.when(answerRepository.findById(any())).thenReturn(Optional.of(answersDomain()));

        mvc.perform(
                        post(baseUrl)
                                .content(objectMapper.writeValueAsBytes(dto()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveErrorGeneral() throws Exception {
        Mockito.when(repository.findById(any())).thenThrow(new NullPointerException("...") {});
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

    Comments domain() {
        var ta = new Comments();
        ta.setCommentId(1);
        ta.setComment("0001");
        ta.setAnswerId(1);
        ta.setUserComment("asd");
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        ta.setCreatedAt(timestamp);
        return ta;
    }

    CommentDTO dto() {
        var dto = new CommentDTO();
        dto.setCommentId(1);
        dto.setComment("0001");
        dto.setAnswerId(1);
        dto.setUserComment("asd");
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        dto.setCreatedAt(timestamp);
        return dto;
    }

    Answers answersDomain(){
        var an = new Answers();
        an.setAnswerId(1);
        an.setAnswer("asdasdasdas");
        an.setAnswerUser("ajajaj");
        an.setQuestionId(1);
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        an.setCreatedAt(timestamp);
        return an;
    }
}




