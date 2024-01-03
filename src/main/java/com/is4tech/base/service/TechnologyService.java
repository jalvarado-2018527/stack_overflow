package com.is4tech.base.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.base.domain.Audit;
import com.is4tech.base.domain.Technology;
import com.is4tech.base.dto.AuditDTO;
import com.is4tech.base.dto.TechnologyAuditDTO;
import com.is4tech.base.dto.TechnologyDTO;
import com.is4tech.base.exception.BadRequestException;
import com.is4tech.base.exception.GenericException;
import com.is4tech.base.exception.NoContentException;
import com.is4tech.base.repository.AuditRepository;
import com.is4tech.base.repository.TechnologyRepository;
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
public class TechnologyService {
    private static final Logger logger = LoggerFactory.getLogger(TechnologyService.class);
    private final TechnologyRepository repository;
    private final EmailService emailService;
    private final AuditRepository auditRepository;
    private final ObjectMapper objectMapper;

    public Technology save(TechnologyDTO dto) throws GenericException, BadRequestException{
        try {
            Optional<Technology> valid = this.repository.findFirstByNameIgnoreCase(dto.getName());
            if (valid.isPresent()){
                throw new BadRequestException("Register Already exists");
            }
            var entity = new Technology();
            BeanUtils.copyProperties(dto, entity);
            entity.setTechnologyId(null);
            var saved = this.repository.save(entity);

            var name = saved.getName();
            var status = saved.getStatus();
            var id = saved.getTechnologyId();
            var abbreviation = saved.getAbbreviation();

            emailService.send(" New Technology posted",
                    "d@gmail.com",
                    "<html> " +
                            "<body> " +
                            "<h1><strong>New Technology</strong></h1> <hr> " +
                            "<h3>Abbreviation: "+ abbreviation +"</h3>"+
                            "<h3>name: " + name + "</h3> " +
                            "<h3>Id: "+ id +"</h3>"+
                            "<h3>Status: "+ status +"</h3>"+
                            "</body> " +
                            "</html>",
                    "j@gmail.com");

            this.saveAudit(saved, null, "POST");
            return saved;
        } catch (BadRequestException e){
            throw e;
        } catch (Exception e){
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    private void saveAudit(Technology old, TechnologyDTO newBo , String operation) {
        try {
            var dto = new AuditDTO();
            var au = new Audit();
            dto.setEntity("TECHNOLOGY");
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


    private TechnologyAuditDTO getJsonAudit(Technology old, TechnologyDTO newBo, String operation) {
        var dto = new TechnologyAuditDTO();
        if (operation.equals("POST") || operation.equals("DELETE")) {
            BeanUtils.copyProperties(old, dto, "technologyId");
        } else {
            dto.setName(newBo.getName());
            dto.setAbbreviation(newBo.getAbbreviation());
            dto.setStatus(newBo.getStatus());
            if (!Objects.equals(old.getName(), newBo.getName())) {
                dto.setName(newBo.getName());
            }
            if (!Objects.equals(old.getAbbreviation(), newBo.getAbbreviation())) {
                dto.setAbbreviation(newBo.getAbbreviation());
            }
            if (!Objects.equals(old.getStatus(), newBo.getStatus())) {
                dto.setStatus(newBo.getStatus());
            }
        }
        return dto;
    }




    public void update(Integer id, TechnologyDTO dto) throws GenericException, BadRequestException{
        try{
            Optional<Technology> valid = this.repository.findById(id);
            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }

            var oldData = valid.get();
            var technologyId = oldData.getTechnologyId();
            var oldName = oldData.getName();
            var oldStatus = oldData.getStatus();
            var oldAbbreviation = oldData.getAbbreviation();

            BeanUtils.copyProperties(dto, oldData, "technologyId");
            var updated = this.repository.save(valid.get());

            emailService.send("Technology updated",
                    "d@gmail.com",
                    "<html> " +
                            "<body> " +
                            "<h1><strong>Register with id: "+technologyId + " has been updated</strong></h1> <hr> " +
                            "<h2><strong>Old Data:</h2></strong>"+
                            "<h3>Name: " + oldName + "</h3> " +
                            "<h3>Abbreviation: " + oldAbbreviation + "</h3> " +
                            "<h3>Status: "+ oldStatus +"</h3>"+
                            "<hr>"+
                            "<h2><strong>New Data:</h2></strong>" +
                            "<h3>Name: " + updated.getName() + "</h3> " +
                            "<h3>Abbreviation: " + updated.getAbbreviation() + "</h3> " +
                            "<h3>Status: "+ updated.getStatus() +"</h3>"+
                            "</body> " +
                            "</html>",
                    "j@gmail.com");
            this.saveAudit(updated, dto, "PUT");
        } catch (BadRequestException e){
            throw e;
        } catch (Exception e){
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }


    public TechnologyDTO findOne(Integer id) throws BadRequestException, GenericException{
        try{
            Optional<Technology> valid = this.repository.findById(id);
            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }
            var dto = new TechnologyDTO();
            BeanUtils.copyProperties(valid.get(), dto);
            return dto;

        } catch (BadRequestException e){
            throw e;
        } catch (Exception e){
            logger.error("Error procesing", e);
            throw new GenericException("Error processing request");
        }
    }


    public List<Technology> findByName(String name) throws BadRequestException, GenericException{
        try {
            List<Technology> valid = this.repository.findAllByNameContains(name);
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

    public Page<Technology> findAll(String search, Pageable page) throws GenericException, NoContentException {
        try {
            Page<Technology> response;
            if (search != null && !search.isEmpty()) {
                response = this.repository.findAllByNameNotContainsIgnoreCase(search, page);
            } else {
                response = this.repository.findAll(page);
            }
            if (response.getContent().isEmpty()) {
                throw new NoContentException("Not registers found");
            }
            return response;
        }catch (NoContentException e){
            logger.error("Error procesing", e);
            throw e;
        } catch (Exception e){
            logger.error("Error procesing", e);
            throw new GenericException("Error processing request");
        }
    }


}
