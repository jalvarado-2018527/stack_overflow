package com.is4tech.base.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.base.domain.Audit;
import com.is4tech.base.dto.AuditDTO;
import com.is4tech.base.dto.RoleAuditDTO;
import com.is4tech.base.repository.AuditRepository;
import com.is4tech.base.repository.RoleRepository;
import org.springframework.stereotype.Service;
import com.is4tech.base.domain.Roles;
import com.is4tech.base.dto.RoleDTO;
import com.is4tech.base.exception.BadRequestException;
import com.is4tech.base.exception.GenericException;
import com.is4tech.base.exception.NoContentException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.management.relation.Role;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleRepository.class);
    private final RoleRepository repository;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final AuditRepository auditRepository;


    public Roles save(RoleDTO dto) throws GenericException, BadRequestException{
        try {
            Optional<Roles> valid = this.repository.findFirstByCodeIgnoreCase(dto.getCode());
            if (valid.isPresent()){
                throw new BadRequestException("Register Already exists");
            }
            var entity  = new Roles();
            BeanUtils.copyProperties(dto, entity);
            entity.setRoleId(null);
            var saved = this.repository.save(entity);
            var code = entity.getCode();
            var id = entity.getRoleId();
            emailService.send(" New Role posted",
                    "d@gmail.com",
                    "<html> " +
                            "<body> " +
                            "<h1><strong>New Role</strong></h1> <hr> " +
                            "<h3>code: " + code + "</h3>" +
                            "<h3>Id: "+ id + "</h3>"+
                            "</body> " +
                            "</html>",
                    "j@gmail.com");
            saveAudit(saved, null, "POST");

            return saved;
        }catch (BadRequestException e){
            throw e;
        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    private void saveAudit(Roles oldRole, RoleDTO newBo ,String operation) {
        try {

            var dto = new AuditDTO();
            var au = new Audit();
            dto.setEntity("ROLE");
            dto.setChangeDate(Timestamp.valueOf(LocalDateTime.now()));
            dto.setUserAudit("Jairo");
            if (operation.equals("POST")) {
                dto.setStatusCode(201);
            } else {
                dto.setStatusCode(200);
            }
            dto.setAction(operation);
            dto.setRequestBody(objectMapper.writeValueAsString(getJsonAudit(oldRole, newBo ,operation )));
            BeanUtils.copyProperties(dto, au);
            this.auditRepository.save(au);
        } catch (JsonProcessingException e) {
            logger.warn("couldn't represent status");
        } catch (Exception e) {
            logger.warn("error with save audit");
        }
    }


    private RoleAuditDTO getJsonAudit(Roles oldRole, RoleDTO newRole, String operation) {
        var dto = new RoleAuditDTO();
        if (operation.equals("POST") || operation.equals("DELETE")) {
            BeanUtils.copyProperties(oldRole, dto, "roleId");
        } else {
            dto.setCode(newRole.getCode());
            dto.setDescription(newRole.getDescription());
            if (!Objects.equals(oldRole.getDescription(), newRole.getDescription())) {
                dto.setDescription(newRole.getDescription());
            }
        }
        return dto;
    }




    public void update(Integer id, RoleDTO dto) throws GenericException, BadRequestException{
        try{
            Optional<Roles> valid = this.repository.findById(id);
            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }
            var oldData = valid.get();
            var roleId = oldData.getRoleId();
            var oldCode = oldData.getCode();
            var oldDescription = oldData.getDescription();
            BeanUtils.copyProperties(dto, oldData, "roleId");
            var updated = this.repository.save(valid.get());
            saveAudit(updated, dto, "PUT");
            emailService.send("Role",
                    "d@gmail.com",
                    "<html> " +
                            "<body> " +
                            "<h1><strong>Role id: "+ roleId + " has been updated</strong></h1> <hr> " +
                            "<h2><strong>Old Data:</h2></strong>"+
                            "<h3>code: " + oldCode + "</h3>" +
                            "<h3>Description: " + oldDescription + "</h3>" +
                            "<hr>"+
                            "<h2><strong>New Data:</h2></strong>" +
                            "<h3>code: " + updated.getCode() + "</h3> " +
                            "<h3>code: " + updated.getDescription() + "</h3> " +
                            "</body> " +
                            "</html>",
                    "j@gmail.com");


        }catch (BadRequestException e){
            throw e;
        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public void delete(Integer id) throws BadRequestException, GenericException{
        try{
            Optional<Roles> valid = this.repository.findById(id);
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

    public RoleDTO findOne(Integer id) throws BadRequestException, GenericException{
        try{
            Optional<Roles> valid = this.repository.findById(id);
            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }
            var dto = new RoleDTO();
            BeanUtils.copyProperties(valid.get(), dto);
            return dto;

        }catch (BadRequestException e){
            throw e;
        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public List<Roles> findByCode(String code) throws BadRequestException, GenericException{
        try {
            List<Roles> valid = this.repository.findAllByCodeContains(code);
            if (valid.isEmpty()){
                throw new BadRequestException("No matches found");
            }
            return valid;
        }catch (BadRequestException e){
            throw e;
        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public Page<Roles> findAll(String search, Pageable page) throws GenericException, NoContentException {
        try {
            Page<Roles> response;
            if (search != null && !search.isEmpty()) {
                response = this.repository.findAllByCodeNotContainsIgnoreCase(search, page);
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
