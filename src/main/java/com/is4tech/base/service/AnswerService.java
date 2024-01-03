package com.is4tech.base.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.base.domain.*;
import com.is4tech.base.dto.*;
import com.is4tech.base.exception.BadRequestException;
import com.is4tech.base.exception.GenericException;
import com.is4tech.base.exception.NoContentException;
import com.is4tech.base.repository.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AnswerService {
    private static final Logger logger = LoggerFactory.getLogger(AnswerRepository.class);
    private final AnswerRepository repository;
    private final QuestionRepository questionRepository;
    private final AnswerVotesRepository answerVotesRepository;
    private final CommentRepository commentRepository;
    private final EmailService emailService;
    private final AuditRepository auditRepository;
    private final ObjectMapper objectMapper;

    public Answers save(AnswerDTO dto) throws GenericException, BadRequestException {
        try {
            Optional<Questions> validQuestion = questionRepository.findById(dto.getQuestionId());
            if (validQuestion.isEmpty()) {
                throw new BadRequestException("Question does not exist");
            }
            var entity = new Answers();
            BeanUtils.copyProperties(dto, entity);
            entity.setAnswerId(null);

            var saved = this.repository.save(entity);

            this.saveAudit(saved, null, "POST");

            return saved;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }



    private void saveAudit(Answers old, AnswerDTO newBo , String operation) {
        try {
            var dto = new AuditDTO();
            var au = new Audit();
            dto.setEntity("ANSWER");
            dto.setChangeDate(Timestamp.valueOf(LocalDateTime.now()));
            dto.setUserAudit("Jairo");
            if (operation.equals("POST")) {
                dto.setStatusCode(201);
            } else {
                dto.setStatusCode(200);
            }
            dto.setAction(operation);
            dto.setRequestBody(objectMapper.writeValueAsString(getJsonAudit(old, newBo ,operation )));
            BeanUtils.copyProperties(dto, au);
            this.auditRepository.save(au);

        } catch (JsonProcessingException e) {
            logger.warn("couldn't represent status");
        } catch (Exception e) {
            logger.warn("error with save audit");
        }
    }

    private AnswerAuditDTO getJsonAudit(Answers old, AnswerDTO newBO, String operation) {
        var dto = new AnswerAuditDTO();
        if (operation.equals("POST") || operation.equals("DELETE")) {
            BeanUtils.copyProperties(old, dto, "answerId");
        } else {
            dto.setAnswer(newBO.getAnswer());
            dto.setAnswerUser(newBO.getAnswerUser());
            dto.setCreatedAt(newBO.getCreatedAt());
            dto.setAnswerVotes(newBO.getAnswerVotes());
            dto.setQuestionId(newBO.getQuestionId());
            if (!Objects.equals(old.getAnswer(), newBO.getAnswer())) {
                dto.setAnswer(newBO.getAnswer());
            }
            if (!Objects.equals(old.getAnswerUser(), newBO.getAnswerUser())) {
                dto.setAnswerUser(newBO.getAnswerUser());
            }
            if (!Objects.equals(old.getCreatedAt(), newBO.getCreatedAt())) {
                dto.setCreatedAt(newBO.getCreatedAt());
            }
            if (!Objects.equals(old.getQuestionId(), newBO.getQuestionId())) {
                dto.setQuestionId(newBO.getQuestionId());
            }
        }
        return dto;
    }

    public void update(Integer id, AnswerDTO dto) throws GenericException, BadRequestException{
        try {
            Optional<Answers> valid = this.repository.findById(id);
            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }
            Optional<Questions> validQuestion = questionRepository.findById(dto.getQuestionId());
            if (validQuestion.isEmpty()){
                throw new BadRequestException("Question does not exist");
            }
            BeanUtils.copyProperties(dto, valid.get(), "answerId");
            var updated = this.repository.save(valid.get());
            this.saveAudit(updated, dto, "PUT");
        }catch (BadRequestException e){
            throw e;
        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public AnswerDTO findOne(Integer id) throws BadRequestException, GenericException{
        try {
            Optional<Answers> valid = this.repository.findById(id);

            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }

            var dto = new AnswerDTO();
            BeanUtils.copyProperties(valid.get(), dto);

            List<AnswerVotes> answerVotes = this.answerVotesRepository.findAllByAnswerId(id);

            dto.setAnswerVotes(answerVotes);

            return dto;
        }catch (BadRequestException e){
            throw e;
        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public void delete(Integer id) throws BadRequestException, GenericException{
        try {
            Optional<Answers> valid = this.repository.findById(id);

            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }
            this.repository.delete(valid.get());
            this.saveAudit(valid.get(), null ,"DELETE");
        }catch (BadRequestException e){
            throw e;
        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public Page<AnswerVDTO> findAll(String search, Pageable page) throws GenericException, NoContentException{
        try {
            List<AnswerVDTO> response = new ArrayList<>();
            Page<Answers> answers;

            if (search != null && !search.isEmpty()){
                answers = this.repository.findAllByAnswerNotContainsIgnoreCase(search, page);
            } else{
                answers = this.repository.findAll(page);
            }
            if (answers.getContent().isEmpty()){
                throw new NoContentException("No registers found");
            }

            for(Answers answer : answers){
                AnswerVDTO dto = new AnswerVDTO();
                BeanUtils.copyProperties(answer, dto);
                var answerVotesCount = this.answerVotesRepository.sumAnswerVotes(answer.getAnswerId());
                var commentsCount = this.commentRepository.countComments(answer.getAnswerId());
                dto.setAnswerVotesCount(answerVotesCount);
                dto.setCommentsCount(commentsCount);
                response.add(dto);
            }
            return new PageImpl<>(response, answers.getPageable(), answers.getTotalElements());
        }catch (NoContentException e){
            logger.error("Error processing", e);
            throw e;
        }catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public Page<Answers> findAllByQuestionId(Integer questionId, String search, Pageable page) throws GenericException, NoContentException{
        try {
            Page<Answers> response;

            if (search != null && !search.isEmpty()) {
                response = this.repository.findAllByQuestionId(questionId, search, page);
            } else {
                response = this.repository.findAll(page);
            }

            if (response.getContent().isEmpty()) {
                throw new NoContentException("No registers found");
            }

            return response;

        }catch (NoContentException e){
            logger.error("Error processing", e);
            throw e;
        }catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

}























