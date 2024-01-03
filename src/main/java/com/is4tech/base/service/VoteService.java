package com.is4tech.base.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.base.domain.Audit;
import com.is4tech.base.domain.Questions;
import com.is4tech.base.domain.Technology;
import com.is4tech.base.domain.Votes;
import com.is4tech.base.dto.AuditDTO;
import com.is4tech.base.dto.VoteAuditDTO;
import com.is4tech.base.dto.VoteDTO;
import com.is4tech.base.exception.BadRequestException;
import com.is4tech.base.exception.GenericException;
import com.is4tech.base.exception.NoContentException;
import com.is4tech.base.repository.AuditRepository;
import com.is4tech.base.repository.QuestionRepository;
import com.is4tech.base.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VoteService {

    private static final Logger logger = LoggerFactory.getLogger(VoteService.class);
    private final VoteRepository repository;
    private final QuestionRepository questionRepository;
    private final AuditRepository auditRepository;
    private final ObjectMapper objectMapper;
    public Votes save(VoteDTO dto) throws GenericException {
        try {
            Optional<Questions> validQuestion = questionRepository.findById(dto.getQuestionId());
            if (validQuestion.isEmpty()){
                throw new BadRequestException("question does not exist");
            }

            var entity = new Votes();
            BeanUtils.copyProperties(dto, entity);
            entity.setVoteId(null);
            var saved = this.repository.save(entity);
            this.saveAudit(saved,null, "POST");

            return saved;

        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }

    }
    private void saveAudit(Votes old, VoteDTO newBo , String operation) {
        try {
            var dto = new AuditDTO();
            var au = new Audit();
            dto.setEntity("VOTE");
            dto.setChangeDate(Timestamp.valueOf(LocalDateTime.now()));
            dto.setUserAudit("Jairo");
            if (operation.equals("POST")) {
                dto.setStatusCode(201);
            } else {
                dto.setStatusCode(200);
            }
            dto.setAction(operation);
            dto.setRequestBody(objectMapper.writeValueAsString(getJsonAudit(old, newBo , operation)));
            BeanUtils.copyProperties(dto, au);
            this.auditRepository.save(au);
        } catch (JsonProcessingException e) {
            logger.warn("couldn't represent status");
        } catch (Exception e) {
            logger.warn("error with save audit");
        }
    }


    private VoteAuditDTO getJsonAudit(Votes old, VoteDTO newBo, String operation) {
        var dto = new VoteAuditDTO();
        if (operation.equals("POST") || operation.equals("DELETE")) {
            BeanUtils.copyProperties(old, dto, "voteId");
        } else {
            dto.setName(newBo.getName());
            dto.setVote(newBo.getVote());
            dto.setQuestionId(newBo.getQuestionId());
            if (!Objects.equals(old.getName(), newBo.getName())) {
                dto.setName(newBo.getName());
            }
            if (!Objects.equals(old.getVote(), newBo.getVote())) {
                dto.setVote(newBo.getVote());
            }
            if (!Objects.equals(old.getQuestionId(), newBo.getQuestionId())) {
                dto.setQuestionId(newBo.getQuestionId());
            }
        }
        return dto;
    }

    public void update(Integer id, VoteDTO dto) throws GenericException, BadRequestException {
        try {
            Optional<Votes> valid = this.repository.findById(id);
            Optional<Questions> validQuestion = questionRepository.findById(dto.getQuestionId());
            if (validQuestion.isEmpty()){
                throw new BadRequestException("question does not exist");
            }

            if (valid.isEmpty()) {
                throw new BadRequestException("Register does not exist");
            }

            BeanUtils.copyProperties(dto, valid.get(), "voteId");
            var updated = this.repository.save(valid.get());
            this.saveAudit(updated, dto, "PUT");
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public void delete(Integer id) throws BadRequestException, GenericException {
        try {
            Optional<Votes> valid = this.repository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("Register does not exist");
            }
            this.repository.delete(valid.get());
            this.saveAudit(valid.get(), null, "DELETE");

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public List<Votes> findAllByQuestionId(Integer id) throws BadRequestException, GenericException{
        try {
            List<Votes> valid = this.repository.findAllByQuestionId(id);
            if (valid.isEmpty()){
                throw new BadRequestException("No registers found");
            }
            return valid;
        }catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public Page<Votes> findAll(String search, Pageable page) throws GenericException, NoContentException {
        try {
            Page<Votes> response;

            if (search != null && !search.isEmpty()) {
                response = this.repository.findAllByVoteNotContainsIgnoreCase(search, page);
            } else {
                response = this.repository.findAll(page);
            }

            if (response.getContent().isEmpty()) {
                throw new NoContentException("No registers found");
            }
            return response;
        } catch (NoContentException e) {
            logger.error("Error processing", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }

    }
}

