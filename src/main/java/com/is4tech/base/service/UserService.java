package com.is4tech.base.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.base.domain.Audit;
import com.is4tech.base.domain.Profiles;
import com.is4tech.base.domain.Roles;
import com.is4tech.base.domain.Users;
import com.is4tech.base.dto.AuditDTO;
import com.is4tech.base.dto.RoleDTO;
import com.is4tech.base.dto.UserAuditDTO;
import com.is4tech.base.dto.UserDTO;
import com.is4tech.base.exception.BadRequestException;
import com.is4tech.base.exception.GenericException;
import com.is4tech.base.exception.NoContentException;
import com.is4tech.base.repository.AuditRepository;
import com.is4tech.base.repository.ProfileRepository;
import com.is4tech.base.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final AuditRepository auditRepository;
    private final ObjectMapper objectMapper;



    public Users save(UserDTO dto) throws GenericException, BadRequestException {
        try {
            Optional<Users> valid = this.repository.findFirstByEmailIgnoreCase(dto.getName());
            Optional<Profiles> validProfile = profileRepository.findById(dto.getProfileId());
            if (valid.isPresent()){
                throw new BadRequestException("Register Already exists");
            }
            if (validProfile.isEmpty()){
                throw new BadRequestException("Profile provided does not exist");
            }
            var entity  = new Users();
            BeanUtils.copyProperties(dto, entity);
            entity.setUserId(null);
            entity.setPassword(Users.generatePassword());

            var name = entity.getName();
            var surname = entity.getSurname();
            var id = entity.getUserId();
            var email = entity.getEmail();
            var password = entity.getPassword();
            var status = entity.getStatus();
            var profile = this.profileRepository.findById(entity.getProfileId());
            var profileCode = profile.get().getCode();
            var profileDescription = profile.get().getDescription();

            entity.setPassword(entity.passwordEncoder().encode(password));
            var saved = this.repository.save(entity);

            emailService.send(" New User posted",
                    "d@gmail.com",
                    "<html> " +
                                "<body> " +
                                    "<h1><strong>New User</strong></h1> <hr> " +
                                    "<h3>Id: "+ id +"</h3>"+
                                    "<h3>Name: " + name + " "+ surname +  "</h3> " +
                                    "<h3>Email: "+ email +"</h3>"+
                                    "<h3>Status: "+ status +"</h3>"+
                                    "<h3>Profile code: "+ profileCode +"</h3>"+
                                    "<h3>Profile Description: "+ profileDescription +"</h3>"+
                                "</body> " +
                            "</html>",
                    "j@gmail.com");

            emailService.send("This is your password",
                    email,
                    "<html> " +
                            "<body> " +
                            "<h3>Password: "+ password +"</h3>"+
                            "</body> " +
                            "</html>",
                    "j@gmail.com");
            this.saveAudit(saved, null, "POST");
            return saved;

        }catch (BadRequestException e){
            throw e;
        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    private void saveAudit(Users old, UserDTO newBo , String operation) {
        try {
            var dto = new AuditDTO();
            var au = new Audit();
            dto.setEntity("USER");
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

    private UserAuditDTO getJsonAudit(Users old, UserDTO newBO, String operation) {
        var dto = new UserAuditDTO();
        if (operation.equals("POST") || operation.equals("DELETE")) {
            BeanUtils.copyProperties(old, dto, "roleId");
        } else {
            dto.setName(newBO.getName());
            dto.setSurname(newBO.getSurname());
            dto.setEmail(newBO.getEmail());
            dto.setProfileId(newBO.getProfileId());
            dto.setStatus(newBO.getStatus());
            if (!Objects.equals(old.getName(), newBO.getName())) {
                dto.setName(newBO.getName());
            }
            if (!Objects.equals(old.getSurname(), newBO.getSurname())) {
                dto.setSurname(newBO.getSurname());
            }
            if (!Objects.equals(old.getEmail(), newBO.getEmail())) {
                dto.setEmail(newBO.getEmail());
            }
            if (!Objects.equals(old.getProfileId(), newBO.getProfileId())) {
                dto.setUserId(newBO.getProfileId());
            }
        }
        return dto;
    }

    public void update(Integer id, UserDTO dto) throws GenericException, BadRequestException {
        try {
            Optional<Users> valid = this.repository.findById(id);
            Optional<Profiles> validProfile = profileRepository.findById(dto.getProfileId());
            if (valid.isEmpty()){
                throw new BadRequestException("Register Already exists");
            }
            if (validProfile.isEmpty()){
                throw new BadRequestException("Profile provided does not exist");
            }

            var oldData = valid.get();
            var userId = oldData.getUserId();
            var name = oldData.getName();
            var surname = oldData.getSurname();
            var email = oldData.getEmail();
            var status = oldData.getStatus();
            var profileId = oldData.getProfileId();
            var profile = this.profileRepository.findById(profileId);
            var profileCode = profile.get().getCode();
            var profileDescription = profile.get().getDescription();

            BeanUtils.copyProperties(dto, valid.get(), "userId");
            var updated = this.repository.save(valid.get());
            var updatedName = updated.getName();
            var updatedSurname = updated.getSurname();
            var updatedEmail = updated.getEmail();
            var updatedStatus = updated.getStatus();
            var updatedProfileId = updated.getProfileId();
            var updatedProfile = this.profileRepository.findById(updatedProfileId);
            var updatedProfileCode = updatedProfile.get().getCode();
            var updatedProfileDescription = updatedProfile.get().getDescription();



            emailService.send("User Updated",
                    "d@gmail.com",
                    "<html> " +
                            "<body> " +
                                "<h1><strong>User with id: "+userId + " has been updated</strong></h1> <hr> " +
                                "<h2><strong>Old Data:</h2></strong>"+
                                "<h3>Name: " + name + " "+ surname +  "</h3> " +
                                "<h3>Email: "+ email +"</h3>"+
                                "<h3>Status: "+ status +"</h3>"+
                                "<h3>Profile code: "+ profileCode +"</h3>"+
                                "<h3>Profile Description: "+ profileDescription +"</h3>"+
                                "<hr>"+
                                "<h2><strong>New Data:</h2></strong>" +
                                "<h3>Name: " + updatedName + " "+ updatedSurname +  "</h3> " +
                                "<h3>Email: "+ updatedEmail +"</h3>"+
                                "<h3>Status: "+ updatedStatus+"</h3>"+
                                "<h3>Profile code: "+ updatedProfileCode +"</h3>"+
                                "<h3>Profile Description: "+ updatedProfileDescription +"</h3>"+
                            "</body> " +
                            "</html>",
                    "j@gmail.com");
            this.saveAudit(updated, dto, "PUT");
        }catch (BadRequestException e){
            throw e;
        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }


    public UserDTO findOne(Integer id) throws BadRequestException, GenericException{
        try {
            Optional<Users> valid = this.repository.findById(id);

            if (valid.isEmpty()) {
                throw new BadRequestException("Register does not exist");
            }
                var dto = new UserDTO();
                BeanUtils.copyProperties(valid.get(), dto);
                return dto;

        }catch (BadRequestException e){
            throw e;
        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public UserDTO findByEmail(String email) throws BadRequestException, GenericException{
        try {
            Optional<Users> valid = this.repository.findByEmail(email);
            if (valid.isEmpty()){
                throw new BadRequestException("Register does not Exist");
            }
            var dto = new UserDTO();
            BeanUtils.copyProperties(valid.get(), dto);
            return dto;
        }catch (BadRequestException e){
            throw e;
        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public List<Users> findByName(String name) throws BadRequestException, GenericException{
        try {
            List<Users> valid = this.repository.findAllByNameContainsIgnoreCase(name);
            if (valid.isEmpty()){
                throw new BadRequestException("No Matches Found");
            }
            return valid;
        }catch (BadRequestException e){
            throw e;
        } catch(Exception e){
            logger.error("Error Processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public Page<Users> findAll(String search, Pageable page) throws GenericException, NoContentException{
        try{
            Page<Users> response;

            if (search != null && !search.isEmpty()){
                response = this.repository.findAllByEmailNotContainsIgnoreCase(search, page);
            } else{
                response = this.repository.findAll(page);
            }

            if (response.getContent().isEmpty()){
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
