package com.is4tech.base.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.base.domain.AnswerVotes;
import com.is4tech.base.domain.Answers;
import com.is4tech.base.domain.Audit;
import com.is4tech.base.domain.Votes;
import com.is4tech.base.dto.*;
import com.is4tech.base.exception.BadRequestException;
import com.is4tech.base.exception.GenericException;
import com.is4tech.base.exception.NoContentException;
import com.is4tech.base.repository.AnswerRepository;
import com.is4tech.base.repository.AnswerVotesRepository;
import com.is4tech.base.repository.AuditRepository;
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
public class AnswerVotesService {

    private static final Logger logger = LoggerFactory.getLogger(AnswerVotesService.class);
    private final AnswerVotesRepository repository;
    private final AnswerRepository answerRepository;
    private final AuditRepository auditRepository;
    private final ObjectMapper objectMapper;

    public AnswerVotes save(AnswerVotesDTO dto) throws GenericException, BadRequestException{
        try {
            Optional<Answers> validAnswer = answerRepository.findById(dto.getAnswerId());
            if (validAnswer.isEmpty()){
                throw new BadRequestException("Answer does not exist");
            }

            var entity = new AnswerVotes();
            BeanUtils.copyProperties(dto, entity);
            entity.setAnswerVoteId(null);
            var saved = this.repository.save(entity);

            this.saveAudit(saved, null, "POST");

            return saved;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    private void saveAudit(AnswerVotes old, AnswerVotesDTO newBo , String operation) {
        try {
            var dto = new AuditDTO();
            var au = new Audit();
            dto.setEntity("ANSWRER VOTES");
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


    private AnswerVotesAuditDTO getJsonAudit(AnswerVotes old, AnswerVotesDTO newBo, String operation) {
        var dto = new AnswerVotesAuditDTO();
        if (operation.equals("POST") || operation.equals("DELETE")) {
            BeanUtils.copyProperties(old, dto, "answerVoteId");
        } else {
            dto.setName(newBo.getName());
            dto.setVote(newBo.getVote());
            dto.setAnswerVoteId(newBo.getAnswerVoteId());
            if (!Objects.equals(old.getName(), newBo.getName())) {
                dto.setName(newBo.getName());
            }
            if (!Objects.equals(old.getVote(), newBo.getVote())) {
                dto.setVote(newBo.getVote());
            }
            if (!Objects.equals(old.getAnswerVoteId(), newBo.getAnswerVoteId())) {
                dto.setAnswerVoteId(newBo.getAnswerVoteId());
            }
        }
        return dto;
    }
    public void update(Integer id, AnswerVotesDTO dto) throws GenericException, BadRequestException{
        try {
            Optional<AnswerVotes> valid = this.repository.findById(id);
            Optional<Answers> validAnswer = answerRepository.findById(dto.getAnswerId());

            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }

            if (validAnswer.isEmpty()){
                throw new BadRequestException("answer does not exist");
            }

            BeanUtils.copyProperties(dto, valid.get(), "answerVoteId");
           var updated = this.repository.save(valid.get());
           this.saveAudit(updated,dto , "PUT");

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public void delete(Integer id) throws BadRequestException, GenericException{
        try {
            Optional<AnswerVotes> valid = this.repository.findById(id);
            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }
            this.repository.delete(valid.get());

            this.saveAudit(valid.get(), null, "DELETE");
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public List<AnswerVotes> findAllByAnswerId(Integer id) throws BadRequestException, GenericException{
        try {
            List<AnswerVotes> valid = this.repository.findAllByAnswerId(id);
            if (valid.isEmpty()){
                throw new BadRequestException("No register found");
            }
            return valid;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public Page<AnswerVotes> findAll(String search, Pageable page) throws GenericException, NoContentException{
        try {
            Page<AnswerVotes> response;

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






























