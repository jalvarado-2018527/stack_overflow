package com.is4tech.base.util.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.base.domain.Questions;
import com.is4tech.base.domain.Votes;
import com.is4tech.base.dto.QuestionDTO;
import com.is4tech.base.dto.QuestionVDTO;
import com.is4tech.base.repository.QuestionRepository;
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
public class QuestionControllerTest {

    private final String baseUrl = "/api/question";
    private final String updateUrl = "/api/question/";

    @Autowired
    protected MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuestionRepository repository;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void save() throws Exception{
        Mockito.when(repository.findFirstByQuestionIgnoreCase(any())).thenReturn(Optional.empty());
        Mockito.when(repository.save(any())).thenReturn(domain());
        mvc.perform(
                post(baseUrl)
                        .content(objectMapper.writeValueAsBytes(dto()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveErrorExists() throws Exception{
        Mockito.when(repository.findFirstByQuestionIgnoreCase(any())).thenReturn(Optional.of(domain()));

        mvc.perform(
                post(baseUrl)
                        .content(objectMapper.writeValueAsBytes(dto()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveErrorGeneral() throws Exception{
        Mockito.when(repository.findFirstByQuestionIgnoreCase(any())).thenThrow(new NullPointerException("...") {});
        mvc.perform(
                post(baseUrl)
                        .content(objectMapper.writeValueAsBytes(dto()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void update() throws Exception{
        Mockito.when(repository.findById(any())).thenReturn(Optional.of((domain())));
        Mockito.when(repository.save(any())).thenReturn(domain());

        mvc.perform(
                put(updateUrl + 1)
                        .content(objectMapper.writeValueAsBytes(dto()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateErrorExists() throws Exception{
        Mockito.when(repository.findById(any())).thenReturn(Optional.empty());

        mvc.perform(
                put(updateUrl + 1)
                        .content(objectMapper.writeValueAsBytes(dto()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateErrorGeneral() throws Exception{
        Mockito.when(repository.findById(any())).thenThrow(new NullPointerException());

        mvc.perform(
                put(updateUrl + 1)
                        .content(objectMapper.writeValueAsBytes(dto()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isInternalServerError());
    }



    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findAllErrorGeneral() throws Exception{

        Mockito.when(repository.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenThrow(new NullPointerException());

        mvc.perform(
                get(baseUrl)
                        .content(objectMapper.writeValueAsBytes(dtoV()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isInternalServerError());
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findOne() throws Exception{

        Mockito.when(repository.findById(any())).thenReturn(Optional.of(domain()));
        mvc.perform(
                        get(updateUrl + 1)
                                .content(objectMapper.writeValueAsBytes(dto()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findOneErrorExists() throws Exception{

        Mockito.when(repository.findById(any())).thenReturn(Optional.empty());
        mvc.perform(
                        get(updateUrl + 1)
                                .content(objectMapper.writeValueAsBytes(dto()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findOneErrorGeneral() throws Exception{

        Mockito.when(repository.findById(any())).thenThrow(new NullPointerException());
        mvc.perform(
                        get(updateUrl + 1)
                                .content(objectMapper.writeValueAsBytes(dto()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    QuestionDTO dto(){
        var dto = new QuestionDTO();
        dto.setQuestionId(1);
        dto.setTechnology("CODE_TEST");
        dto.setQuestion("DESCRIPTION_TEST");
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        dto.setCreatedAt(timestamp);
        dto.setTitle("sad");
        List<String> tags = new ArrayList<>();
        tags.add("1");
        dto.setQuestionTags(tags);

        Votes vo = new Votes();
        vo.setVoteId(2);
        vo.setQuestionId(1);
        vo.setName("name");
        vo.setVote(1);

        return dto;
    }

    QuestionVDTO dtoV(){
        var dto = new QuestionVDTO();
        dto.setQuestionId(1);
        dto.setTechnology("CODE_TEST");
        dto.setQuestion("DESCRIPTION_TEST");
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        dto.setCreatedAt(timestamp);
        dto.setTitle("sad");
        List<String> tags = new ArrayList<>();
        tags.add("1");
        dto.setQuestionTags(tags);

        dto.setVotesCount(1);

        return dto;
    }


    Questions domain(){
        var bo = new Questions();
        bo.setQuestionId(1);
        bo.setTechnology("CODE_TEST");
        bo.setQuestion("DESCRIPTION_TEST");
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        bo.setCreatedAt(timestamp);
        bo.setTitle("sad");
        return bo;
    }
}

