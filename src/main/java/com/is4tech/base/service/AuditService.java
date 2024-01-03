package com.is4tech.base.service;

import com.is4tech.base.domain.Audit;
import com.is4tech.base.dto.AuditDTO;
import com.is4tech.base.exception.BadRequestException;
import com.is4tech.base.exception.GenericException;
import com.is4tech.base.exception.NoContentException;
import com.is4tech.base.repository.AuditRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditRepository.class);
    private final AuditRepository repository;
    private final EmailService emailService;



    public void delete(Integer id) throws BadRequestException, GenericException{
        try{
            Optional<Audit> valid = this.repository.findById(id);
            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }
            this.repository.delete(valid.get());

        } catch (BadRequestException e){
            throw e;
        } catch (Exception e){
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public AuditDTO findOne(Integer id) throws BadRequestException, GenericException{
        try{
            Optional<Audit> valid = this.repository.findById(id);
            if (valid.isEmpty()){
                throw new BadRequestException("Register does not exist");
            }
            var dto = new AuditDTO();
            BeanUtils.copyProperties(valid.get(), dto);
            return dto;

        } catch (BadRequestException e){
            throw e;
        } catch (Exception e){
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public List<Audit> findByAuditUser(String auditUser) throws BadRequestException, GenericException{
        try {
            List<Audit> valid = this.repository.findAllByUserAuditContainsIgnoreCase(auditUser);
            if (valid.isEmpty()){
                throw new BadRequestException("No matches found");
            }
            return valid;

        } catch (BadRequestException e){
            throw e;
        } catch (Exception e){
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public List<Audit> findAllByEntity(String entity) throws BadRequestException, GenericException{
        try {
            List<Audit> valid = this.repository.findByEntity(entity);
            if (valid.isEmpty()){
                throw new BadRequestException("No matches found");
            }
            return valid;

        } catch (BadRequestException e){
            throw e;
        } catch (Exception e){
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public List<Audit> findAllByAction(String action) throws BadRequestException, GenericException{
        try {
            List<Audit> valid = this.repository.findByAction(action);
            if (valid.isEmpty()){
                throw new BadRequestException("No matches found");
            }
            return valid;

        } catch (BadRequestException e){
            throw e;
        } catch (Exception e){
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }


    public Page<Audit> findAll(String search, Pageable page) throws GenericException, NoContentException {
        try {
            Page<Audit> response;
            if (search != null && !search.isEmpty()) {
                response = this.repository.findAllByUserAuditNotContainsIgnoreCase(search, page);
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


    public List<Audit> findByDateRange(Timestamp startDate, Timestamp endDate) throws BadRequestException, GenericException {
        try {
            if (startDate == null || endDate == null) {
                throw new BadRequestException("Invalid date range");
            }

            List<Audit> validAudits = repository.findAuditsByDateRange(startDate, endDate);

            if (validAudits.isEmpty()) {
                throw new BadRequestException("No matches found");
            }

            return validAudits;

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }
}
