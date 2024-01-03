package com.is4tech.base.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.base.domain.Audit;
import com.is4tech.base.domain.Tags;
import com.is4tech.base.dto.AuditDTO;
import com.is4tech.base.dto.TagDTO;
import com.is4tech.base.dto.TagsAuditDTO;
import com.is4tech.base.exception.BadRequestException;
import com.is4tech.base.exception.GenericException;
import com.is4tech.base.exception.NoContentException;
import com.is4tech.base.repository.AuditRepository;
import com.is4tech.base.repository.TagRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TagService {
 private static final Logger logger = LoggerFactory.getLogger(TagRepository.class);
    private final TagRepository repository;
    private final EmailService emailService;
    private final AuditRepository auditRepository;

    private final ObjectMapper objectMapper;

    public Tags save(TagDTO dto) throws GenericException, BadRequestException {
        try {
            Optional<Tags> valid = this.repository.findFirstByCodeIgnoreCase(dto.getCode());
            if (valid.isPresent()){
                throw new BadRequestException("Register Already exists");
            }
            var entity  = new Tags();
            BeanUtils.copyProperties(dto, entity);
            entity.setId(null);
            var saved  = this.repository.save(entity);
            var code = entity.getCode();
            var id = entity.getId();
            var status = entity.getStatus();



            emailService.send("New Tag posted",
                    "d@gmail.com",
                    "<html> " +
                            "<body> " +
                            "<h1><stzrong>New tag</strong></h1> <hr> " +
                            "<h3>code: " + code + "</h3>" +
                            "<h3>Id: "+ id + "</h3>"+
                            "<h3>status: "+ status + "</h3>"+
                            "</body> " +
                            "</html>",
                    "j@gmail.com");

            this.saveAudit(saved,  null ,"POST");

            return saved;
        } catch (BadRequestException e){
            throw e;
        } catch (Exception e){
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }


    private void saveAudit(Tags oldTag, TagDTO newBo ,String operation) {
        try {

            var dto = new AuditDTO();
            var au = new Audit();
            dto.setEntity("TAG");
            dto.setChangeDate(Timestamp.valueOf(LocalDateTime.now()));
            dto.setUserAudit("Jairo");
            if (operation.equals("POST")) {
                dto.setStatusCode(201);
            } else {
                dto.setStatusCode(200);
            }
            dto.setAction(operation);
            dto.setRequestBody(objectMapper.writeValueAsString(getJsonAudit(oldTag, newBo ,operation )));
            BeanUtils.copyProperties(dto, au);
            this.auditRepository.save(au);
        } catch (JsonProcessingException e) {
            logger.warn("couldn't represent status");
        } catch (Exception e) {
            logger.warn("error with save audit");
        }
    }

    private TagsAuditDTO getJsonAudit(Tags oldTag, TagDTO newTag, String operation) {
        var dto = new TagsAuditDTO();
        if (operation.equals("POST") || operation.equals("DELETE")) {
            BeanUtils.copyProperties(oldTag, dto, "id");
        } else {
            dto.setCode(newTag.getCode());
            if (!Objects.equals(oldTag.getName(), newTag.getName())) {
                dto.setName(newTag.getName());
            }
            dto.setName(newTag.getName());
            if (!Objects.equals(oldTag.getStatus(), newTag.getStatus())) {
                dto.setStatus(newTag.getStatus());
            }
            dto.setStatus(newTag.getStatus());
            if (!Objects.equals(oldTag.getCode(), newTag.getCode())) {
                dto.setCode(newTag.getCode());
            }
        }
        return dto;
    }

    public void update(Integer id, TagDTO dto) throws GenericException, BadRequestException{
        try{
            Optional<Tags> valid = this.repository.findById(id);
            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }

            var oldData = valid.get();
            var tagId = oldData.getId();
            var oldCode = oldData.getCode();
            var oldName = oldData.getName();
            var oldStatus = oldData.getStatus();
            BeanUtils.copyProperties(dto, oldData, "id");
            var updated = this.repository.save(valid.get());

            emailService.send("Tags",
                    "d@gmail.com",
                    "<html> " +
                            "<body> " +
                            "<h1><strong>Tag id: "+ tagId + " has been updated</strong></h1> <hr> " +
                            "<h2><strong>Old Data:</h2></strong>"+
                            "<h3>code: " + oldCode + "</h3>" +
                            "<h3>name: " + oldName + "</h3>" +
                            "<h3>status: " + oldStatus + "</h3>" +
                            "<hr>"+
                            "<h2><strong>New Data:</h2></strong>" +
                            "<h3>code: " + updated.getCode() + "</h3>" +
                            "<h3>name: " + updated.getName() + "</h3> " +
                            "<h3>status: " + updated.getStatus() + "</h3> " +
                            "</body> " +
                            "</html>",
                    "j@gmail.com");
            this.saveAudit(updated,  dto, "PUT");

        } catch (BadRequestException e){
            throw e;
        } catch (Exception e){
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public void delete(Integer id) throws BadRequestException, GenericException{
        try{
            Optional<Tags> valid = this.repository.findById(id);
            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }
            this.repository.delete(valid.get());
            this.saveAudit(valid.get(),  null, "DELETE" );
        } catch (BadRequestException e){
            throw e;
        } catch (Exception e){
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public TagDTO findOne(Integer id) throws BadRequestException, GenericException{
        try{
            Optional<Tags> valid = this.repository.findById(id);
            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }
            var dto = new TagDTO();
            BeanUtils.copyProperties(valid.get(), dto);
            return dto;

        } catch (BadRequestException e){
            throw e;
        } catch (Exception e){
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public List<Tags> findByName(String name) throws BadRequestException, GenericException{
        try {
            List<Tags> valid = this.repository.findByNameContainsIgnoreCase(name);
            if (valid.isEmpty()){
                throw new BadRequestException("No matches found");
            }
            return valid;
        }catch (BadRequestException e){
            throw e;
        } catch (Exception e){
            logger.error("Error procesing", e);
            throw new GenericException("Error processing request");
        }
    }

    public Page<Tags> findAll(String search, Pageable page) throws GenericException, NoContentException {
        try {
            Page<Tags> response;
            if (search != null && !search.isEmpty()) {
                response = this.repository.findAllByCodeNotContainsIgnoreCase(search, page);
            } else {
                response = this.repository.findAll(page);
            }
            if (response.getContent().isEmpty()) {
                throw new NoContentException("Not registers found");
            }
            return response;
        }catch (NoContentException e){
            logger.error("Error processing", e);
            throw e;
        } catch (Exception e){
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }
}
