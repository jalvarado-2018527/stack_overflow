package com.is4tech.base.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.base.domain.Answers;
import com.is4tech.base.domain.Audit;
import com.is4tech.base.domain.Comments;
import com.is4tech.base.domain.Votes;
import com.is4tech.base.dto.*;
import com.is4tech.base.exception.BadRequestException;
import com.is4tech.base.exception.GenericException;
import com.is4tech.base.exception.NoContentException;
import com.is4tech.base.repository.AnswerRepository;
import com.is4tech.base.repository.AuditRepository;
import com.is4tech.base.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor

public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepository repository;
    private final AnswerRepository answerRepository;
    private final AuditRepository auditRepository;
    private final ObjectMapper objectMapper;

    public Comments save(CommentDTO dto) throws GenericException, BadRequestException{
        try {
            Optional<Answers> validAnswer = answerRepository.findById(dto.getAnswerId());
            if (validAnswer.isEmpty()){
                throw new BadRequestException("Answer does not exist");
            }
            var entity = new Comments();
            BeanUtils.copyProperties(dto, entity);
            entity.setCommentId(null);


           var saved = this.repository.save(entity);

           this.saveAudit(saved, null, "POST");

           return saved;
        }catch (BadRequestException e){
            throw e;
        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    private void saveAudit(Comments old, CommentDTO newBo , String operation) {
        try {
            var dto = new AuditDTO();
            var au = new Audit();
            dto.setEntity("COMMENTS");
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


    private CommentAuditDTO getJsonAudit(Comments old, CommentDTO newBo, String operation) {
        var dto = new CommentAuditDTO();
        if (operation.equals("POST") || operation.equals("DELETE")) {
            BeanUtils.copyProperties(old, dto, "commentId");
        } else {
            dto.setComment(newBo.getComment());
            dto.setCreatedAt(newBo.getCreatedAt());
            dto.setAnswerId(newBo.getAnswerId());
            if (!Objects.equals(old.getComment(), newBo.getComment())) {
                dto.setComment(newBo.getComment());
            }
            if (!Objects.equals(old.getCreatedAt(), newBo.getCreatedAt())) {
                dto.setCreatedAt(newBo.getCreatedAt());
            }
            if (!Objects.equals(old.getAnswerId(), newBo.getAnswerId())) {
                dto.setAnswerId(newBo.getAnswerId());
            }
        }
        return dto;
    }


    public void update(Integer id, CommentDTO dto) throws GenericException, BadRequestException{
        try {
            Optional<Comments> valid = this.repository.findById(id);
            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }
            Optional<Answers> validAnswer = this.answerRepository.findById(dto.getAnswerId());
            if (validAnswer.isEmpty()){
                throw new BadRequestException("Answer does not exist" );
            }
            BeanUtils.copyProperties(dto, valid.get(), "commentId");
           var updated = this.repository.save(valid.get());

            this.saveAudit(updated, dto, "PUT");
        }catch (BadRequestException e){
            throw e;
        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public CommentDTO findOne(Integer id) throws BadRequestException, GenericException{

        try {
            Optional<Comments> valid = this.repository.findById(id);
            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }
            var dto = new CommentDTO();
            BeanUtils.copyProperties(valid.get(), dto);
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
            Optional<Comments> valid = this.repository.findById(id);
            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }
            this.repository.delete(valid.get());

            this.saveAudit(valid.get(),null , "DELETE");
        }catch (BadRequestException e){
            throw e;
        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public Page<Comments> findAll(String search, Pageable page ) throws GenericException, NoContentException{
        try {
            Page<Comments> response;
            if (search != null && !search.isEmpty()) {
                response = this.repository.findAllByCommentNotContainsIgnoreCase(search, page);
            } else {
                response = this.repository.findAll(page);
            }

            if (response.getContent().isEmpty()) {
                throw new NoContentException("No registers found");
            }
            return response;
        }catch (NoContentException e) {
            logger.error("Error processing", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }

    }

    public Page<Comments> findAllByAnswerId(Integer answerId, String search, Pageable page ) throws GenericException, NoContentException{
        try {
            Page<Comments> response;
            if (search != null && !search.isEmpty()) {
                response = this.repository.findAllByAnswerId(answerId, search, page);
            } else {
                response = this.repository.findAll(page);
            }

            if (response.getContent().isEmpty()) {
                throw new NoContentException("No registers found");
            }
            return response;
        }catch (NoContentException e) {
            logger.error("Error processing", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }

    }
}













