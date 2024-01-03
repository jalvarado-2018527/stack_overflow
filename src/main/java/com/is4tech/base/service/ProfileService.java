package com.is4tech.base.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.base.domain.*;
import com.is4tech.base.dto.AuditDTO;
import com.is4tech.base.dto.ProfileAuditDTO;
import com.is4tech.base.dto.ProfileDTO;
import com.is4tech.base.exception.BadRequestException;
import com.is4tech.base.exception.GenericException;
import com.is4tech.base.exception.NoContentException;
import com.is4tech.base.repository.AuditRepository;
import com.is4tech.base.repository.ProfileRepository;
import com.is4tech.base.repository.ProfilesRolesRepository;
import com.is4tech.base.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
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
public class ProfileService {
    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);
    private final ProfileRepository repository;
    private final RoleRepository roleRepository;
    private final ProfilesRolesRepository profilesRolesRepository;
    private final EmailService emailService;
    private final AuditRepository auditRepository;
    private final ObjectMapper objectMapper;
    public Profiles save(ProfileDTO dto) throws GenericException, BadRequestException {
        try {

            Optional<Profiles> valid = this.repository.findFirstByCodeIgnoreCase(dto.getCode());
            if (valid.isPresent()) {
                throw new BadRequestException("Register already exists");
            }
            if (dto.getResources().isEmpty()) {
                throw new BadRequestException("Roles are required");
            }

            var entity = new Profiles();
            BeanUtils.copyProperties(dto, entity);
            entity.setProfileId(null);

            if (!validateResources(dto.getResources())) {
                throw new BadRequestException("Roles are not valid");
            }
            var saved = this.repository.save(entity);
            var code = entity.getCode();
            var id = saved.getProfileId();
            var status = entity.getStatus();
            saveProfileResource(dto.getResources(), id);

            emailService.send(" New Profile posted",
                    "d@gmail.com",
                    "<html> " +
                            "<body> " +
                            "<h1><strong>New Profile</strong></h1> <hr> " +
                            "<h3>Profile Code: " + code + "</h3> " +
                            "<h3>Id: " + id + "</h3>" +
                            "<h3>Status: " + status + "</h3>" +
                            "</body> " +
                            "</html>",
                    "j@gmail.com");

            this.saveAudit(saved, null, "POST");

            return saved;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    private void saveAudit(Profiles old, ProfileDTO newBo , String operation) {
        try {
            var dto = new AuditDTO();
            var au = new Audit();
            dto.setEntity("PROFILE");
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


    private ProfileAuditDTO getJsonAudit(Profiles old, ProfileDTO newBo, String operation) {
        var dto = new ProfileAuditDTO();
        if (operation.equals("POST") || operation.equals("DELETE")) {
            BeanUtils.copyProperties(old, dto, "profileId");
        } else {
            dto.setCode(newBo.getCode());
            dto.setStatus(newBo.getStatus());
            dto.setDescription(newBo.getDescription());
            dto.setResources(newBo.getResources());
            if (!Objects.equals(old.getCode(), newBo.getCode())) {
                dto.setCode(newBo.getCode());
            }

            if (!Objects.equals(old.getStatus(), newBo.getStatus())) {
                dto.setStatus(newBo.getStatus());
            }
            if (!Objects.equals(old.getDescription(), newBo.getDescription())) {
                dto.setDescription(newBo.getDescription());
            }
        }
        return dto;
    }


    public void update(Integer id, ProfileDTO dto) throws GenericException, BadRequestException {
        try {
            Optional<Profiles> valid = this.repository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("Register does not exist");
            }

            var oldData = valid.get();
            var profileId = oldData.getProfileId();
            var oldCode = oldData.getCode();
            var oldStatus = oldData.getStatus();
            var oldDescription = oldData.getDescription();

            BeanUtils.copyProperties(dto, oldData, "profileId");

            if (validateResources(dto.getResources())) {

                saveProfileResource(dto.getResources(), id);

                var updated = this.repository.save(valid.get());
                emailService.send("Profile updated",
                        "d@gmail.com",
                        "<html> " +
                                "<body> " +
                                "<h1><strong>Profile with id: " + profileId + " has been updated</strong></h1> <hr> " +
                                "<h2><strong>Old Data:</h2></strong>" +
                                "<h3>Profile Code: " + oldCode + "</h3> " +
                                "<h3>Description: " + oldDescription + "</h3>" +
                                "<h3>Description: " + oldStatus + "</h3>" +
                                "<hr>" +
                                "<h2><strong>New Data:</h2></strong>" +
                                "<h3>Profile Code: " + updated.getCode() + "</h3> " +
                                "<h3>Description: " + updated.getDescription() + "</h3>" +
                                "<h3>Description: " + updated.getStatus() + "</h3>" +
                                "</body> " +
                                "</html>",
                        "j@gmail.com");

                this.saveAudit(updated, dto, "PUT");
            } else {
                throw new BadRequestException("Roles are not valid");
            }


        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");

        }
    }

    private void saveProfileResource(List<String> resources, Integer id) {

        Iterable<Roles> roles = this.roleRepository.findAll();
        resources.forEach(cycle -> {
            var profileResource = new ProfilesRoles();
            profileResource.setId(new ProfilesRolesId());
            for (Roles role : roles) {
                if (role.getCode().equals(cycle)) {
                    profileResource.setId(new ProfilesRolesId(id, role.getRoleId()));
                    this.profilesRolesRepository.save(profileResource);
                }

            }
        });
    }


    private boolean validateResources(List<String> resources) {
        Iterable<Roles> roles = this.roleRepository.findAll();
        for (String cycle : resources) {
            boolean exists = false;
            for (Roles role : roles) {
                if (role.getCode().equals(cycle)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                return false;
            }
        }
        return true;
    }

    public void delete(Integer id) throws BadRequestException, GenericException {
        try {
            Optional<Profiles> valid = this.repository.findById(id);
            if (valid.isEmpty()) {
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
    public ProfileDTO findOne(Integer id) throws BadRequestException, GenericException {
        try {
            Optional<Profiles> valid = this.repository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("Register does not exist");
            }

            var dto = new ProfileDTO();
            BeanUtils.copyProperties(valid.get(), dto);

            dto.setResources(new ArrayList<>());
            this.profilesRolesRepository.findRoles(id).forEach(cycle -> {
                dto.getResources().add(cycle.getCode());
            });

            return dto;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");

        }
    }

    public List<Profiles> findByCode(String code) throws BadRequestException, GenericException{
        try {
            List<Profiles> valid = this.repository.findAllByCodeContains(code);
            if (valid.isEmpty()){
                throw new BadRequestException("No matches found");
            }
            return valid;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");

        }
    }

    public Page<Profiles> findAll(String search, Pageable page) throws GenericException, NoContentException {
        try {
            Page<Profiles> response;

            if (search != null && !search.isEmpty()) {
                response = this.repository.findAllByCodeNotContainsIgnoreCase(search, page);
            } else {
                response = this.repository.findAll(page);
            }
            if (response.getContent().isEmpty()) {
                throw new NoContentException("No registers found");
            }
            return response;
        } catch (NoContentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");

        }
    }
}


