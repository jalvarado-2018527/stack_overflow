package com.is4tech.base.util.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.base.domain.Questions;
import com.is4tech.base.domain.Votes;
import com.is4tech.base.dto.VoteDTO;
import com.is4tech.base.repository.QuestionRepository;
import com.is4tech.base.repository.VoteRepository;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@WebAppConfiguration
public class VoteControllerTest {
    private final String baseUrl = "/api/vote";
    private final String updateUrl = "/api/vote/";

    @Autowired
    protected MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private VoteRepository repository;
    @MockBean
    private QuestionRepository questionRepository;



    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void save() throws Exception{
        Mockito.when(questionRepository.findById(any())).thenReturn(Optional.of(questionsDomain()));
        Mockito.when(repository.save(any())).thenReturn(domain());

        mvc.perform(
                        post(baseUrl)
                                .content(objectMapper.writeValueAsBytes(dto()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveErrorGeneral() throws Exception {
        Mockito.when(questionRepository.findById(any())).thenReturn(Optional.of(questionsDomain()));
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
        Mockito.when(questionRepository.findById(any())).thenReturn(Optional.of(questionsDomain()));
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
        Mockito.when(questionRepository.findById(any())).thenReturn(Optional.of(questionsDomain()));
        mvc.perform(
                        put(updateUrl+1)
                                .content(objectMapper.writeValueAsBytes(dto()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateErrorGeneral() throws Exception{
        Mockito.when(questionRepository.findById(any())).thenReturn(Optional.of(questionsDomain()));
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

    Votes domain() {
        var ro = new Votes();
        ro.setVoteId(1);
        ro.setName("0001");
        ro.setQuestionId(1);
        ro.setVote(1);
        return ro;
    }

    Questions questionsDomain(){
        var que = new Questions();
        que.setQuestionId(1);
        que.setQuestion("asd");
        que.setTitle("asd");
        que.setQuestionUser("asd");
        que.setTechnology("asdd");
        que.setTitle("ads");
        return que;
    }

    VoteDTO dto() {
        var dto = new VoteDTO();
        dto.setVoteId(1);
        dto.setName("0001");
        dto.setVote(1);
        dto.setQuestionId(1);
        return dto;
    }
}
