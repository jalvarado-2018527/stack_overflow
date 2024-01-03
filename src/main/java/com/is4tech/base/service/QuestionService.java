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
public class QuestionService {
    private static final Logger logger = LoggerFactory.getLogger(QuestionRepository.class);
    private final QuestionRepository repository;
    private final TagRepository TagRepository;
    private final QuestionTagsRepository questionTagRepository;
    private final TechnologyRepository technologyRepository;
    private final VoteRepository voteRepository;
    private final AnswerRepository answerRepository;
    private final AuditRepository auditRepository;
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    public Questions save(QuestionDTO dto) throws GenericException, BadRequestException {
        try {
            Optional<Questions> valid = this.repository.findFirstByQuestionIgnoreCase(dto.getQuestion());
            if (valid.isPresent()) {
                throw new BadRequestException("Register Already exists");
            }
            var entity = new Questions();
            BeanUtils.copyProperties(dto, entity);

            if (!validateQuestionTags(dto.getQuestionTags())) {
                throw new BadRequestException("Tag are not valid");
            }

            if (dto.getQuestionTags().size() > 6) {
                throw new BadRequestException("only 6 tags");
            }

            entity.setQuestionId(null);
            var saved = this.repository.save(entity);
            var question = entity.getQuestion();
            var id = entity.getQuestionId();
            var tech = entity.getTechnology();
            var userQuestion = entity.getQuestionUser();
            var title = entity.getTitle();
            var dateAt = entity.getCreatedAt();
            saveQuestionsTags(dto.getQuestionTags(), id);

            emailService.send(" New question posted",
                    "d@gmail.com",
                    "<html> " +
                            "<body> " +
                            "<h1><strong>New question</strong></h1> <hr> " +
                            "<h3>Id: " + id + "</h3>" +
                            "<h3>tittle: " + title + "</h3>" +
                            "<h3>question: " + question + "</h3>" +
                            "<h3>user: " + userQuestion + "</h3>" +
                            "<h3>Technology: " + tech + "</h3>" +
                            "<h3>date: " + dateAt + "</h3>" +
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

    private void saveAudit(Questions old, QuestionDTO newBo , String operation) {
        try {
            var dto = new AuditDTO();
            var au = new Audit();
            dto.setEntity("QUESTION");
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

    private QuestionAuditDTO getJsonAudit(Questions old, QuestionDTO newBO, String operation) {
        var dto = new QuestionAuditDTO();
        if (operation.equals("POST") || operation.equals("DELETE")) {
            BeanUtils.copyProperties(old, dto, "answerId");
        } else {
            dto.setQuestion(newBO.getQuestion());
            dto.setQuestionUser(newBO.getQuestionUser());
            dto.setCreatedAt(newBO.getCreatedAt());
            dto.setTechnology(newBO.getTechnology());
            dto.setTitle(newBO.getTitle());
            dto.setQuestionTags(newBO.getQuestionTags());
            if (!Objects.equals(old.getQuestion(), newBO.getQuestion())) {
                dto.setQuestion(newBO.getQuestion());
            }
            if (!Objects.equals(old.getQuestionUser(), newBO.getQuestionUser())) {
                dto.setQuestionUser(newBO.getQuestionUser());
            }
            if (!Objects.equals(old.getCreatedAt(), newBO.getCreatedAt())) {
                dto.setCreatedAt(newBO.getCreatedAt());
            }
            if (!Objects.equals(old.getTechnology(), newBO.getTechnology())) {
                dto.setTechnology(newBO.getTechnology());
            }
            if (!Objects.equals(old.getTitle(), newBO.getTitle())) {
                dto.setTitle(newBO.getTitle());
            }

        }
        return dto;
    }


    public void update(Integer id, QuestionDTO dto) throws GenericException, BadRequestException {
        try {
            Optional<Questions> valid = this.repository.findById(id);
            if (valid.isEmpty()) {
                throw new BadRequestException("Register does not exist");
            }
            if (dto.getQuestionTags().size() > 6) {
                throw new BadRequestException("only 6 tags");
            }


            var oldData = valid.get();
            var questionId = oldData.getQuestionId();
            var oldQuestion = oldData.getQuestion();
            var oldTechnology = oldData.getTechnology();
            var oldTitle = oldData.getTitle();
            var oldUser = oldData.getQuestionUser();
            var oldDate = oldData.getCreatedAt();
            BeanUtils.copyProperties(dto, oldData, "questionId");

            if (validateQuestionTags(dto.getQuestionTags())) {


               saveQuestionsTags(dto.getQuestionTags(), id);

                var updated = this.repository.save(valid.get());
                emailService.send("Question",
                        "d@gmail.com",
                        "<html> " +
                                "<body> " +
                                "<h1><strong>Question id: " + questionId + " has been updated</strong></h1> <hr> " +
                                "<h2><strong>Old Data:</h2></strong>" +
                                "<h3>Id: " + id + "</h3>" +
                                "<h3>tittle: " + oldTitle + "</h3>" +
                                "<h3>question: " + oldQuestion + "</h3>" +
                                "<h3>user: " + oldUser + "</h3>" +
                                "<h3>Technology: " + oldTechnology + "</h3>" +
                                "<h3>date: " + oldDate + "</h3>" +
                                "<hr>" +
                                "<h2><strong>New Data:</h2></strong>" +
                                "<h3>tittle: " + updated.getTitle() + "</h3>" +
                                "<h3>question: " + updated.getQuestion() + "</h3>" +
                                "<h3>user: " + updated.getQuestionUser() + "</h3>" +
                                "<h3>Technology: " + updated.getTechnology() + "</h3>" +
                                "<h3>date: " + updated.getCreatedAt() + "</h3>" +
                                "</body> " +
                                "</html>",
                        "j@gmail.com");
                this.saveAudit(updated, dto, "PUT");
            } else {
                throw new BadRequestException("Tag are not valid");
            }

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }

    public void delete(Integer id) throws BadRequestException, GenericException {
        try {
            Optional<Questions> valid = this.repository.findById(id);
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

    public QuestionDTO findOne(Integer id) throws BadRequestException, GenericException {
        try {
            Optional<Questions> valid = this.repository.findById(id);

            if (valid.isEmpty()) {
                throw new BadRequestException("Register does not exist");
            }
            var dto = new QuestionDTO();
            BeanUtils.copyProperties(valid.get(), dto);

            List<Votes> questionVotes = this.voteRepository.findAllByQuestionId(id);
            dto.setQuestionVotes(questionVotes);

            dto.setQuestionTags(new ArrayList<>());
            this.questionTagRepository.findTag(valid.get().getQuestionId()).forEach(cycle -> {
                dto.getQuestionTags().add(cycle.getName());
            });

            return dto;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }


    public List<QuestionVDTO> findByTitle(String title) throws BadRequestException, GenericException {
        try {
            List<Questions> questions = this.repository.findAllByTitleContainsIgnoreCase(title);
            List<QuestionVDTO> valid = new ArrayList<>();
            if (questions.isEmpty()) {
                throw new BadRequestException("No matches found");
            }
            for (Questions question : questions) {
                QuestionVDTO dto = new QuestionVDTO();
                BeanUtils.copyProperties(question, dto);

                dto.setQuestionTags(new ArrayList<>());
                this.questionTagRepository.findTag(question.getQuestionId()).forEach(cycle -> {
                    dto.getQuestionTags().add(cycle.getName());
                });
                var answerCount = this.answerRepository.countAnswers(question.getQuestionId());
                var voteCount = this.voteRepository.sumVotes(question.getQuestionId());
                dto.setVotesCount(voteCount);
                dto.setAnswersCount(answerCount);
                valid.add(dto);
            }

            return valid;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }


    public List<QuestionVDTO> findByUser(String user) throws BadRequestException, GenericException {
        try {
            List<Questions> questions = this.repository.findAllByQuestionUserContainsIgnoreCase(user);
            List<QuestionVDTO> valid = new ArrayList<>();

            for (Questions question : questions) {
                QuestionVDTO dto = new QuestionVDTO();
                BeanUtils.copyProperties(question, dto);

                dto.setQuestionTags(new ArrayList<>());
                this.questionTagRepository.findTag(question.getQuestionId()).forEach(cycle -> {
                    dto.getQuestionTags().add(cycle.getName());
                });
                var answerCount = this.answerRepository.countAnswers(question.getQuestionId());
                var voteCount = this.voteRepository.sumVotes(question.getQuestionId());
                dto.setVotesCount(voteCount);
                dto.setAnswersCount(answerCount);
                valid.add(dto);
            }

            return valid;
        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }


    public Page<QuestionVDTO> findAll(String search, Pageable page) throws GenericException, NoContentException {
        try {
            List<QuestionVDTO> response = new ArrayList<>();
            Page<Questions> questions;

            if (search != null && !search.isEmpty()) {
                questions = this.repository.findAllByQuestionNotContainsIgnoreCase(search, page);
            } else {
                questions = this.repository.findAll(page);
            }

            if (questions.getContent().isEmpty()) {
                throw new NoContentException("not found");
            }

            for (Questions question : questions) {
                QuestionVDTO dto = new QuestionVDTO();
                BeanUtils.copyProperties(question, dto);
                var answerCount = this.answerRepository.countAnswers(question.getQuestionId());
                var voteCount = this.voteRepository.sumVotes(question.getQuestionId());
                dto.setVotesCount(voteCount);
                dto.setAnswersCount(answerCount);

                dto.setQuestionTags(new ArrayList<>());
                this.questionTagRepository.findTag(question.getQuestionId()).forEach(cycle -> {
                    dto.getQuestionTags().add(cycle.getName());
                });

                response.add(dto);
            }

            return new PageImpl<>(response, questions.getPageable(), questions.getTotalElements());

        } catch (Exception e) {
            logger.error("Error processing", e);
            throw new GenericException("Error processing request");
        }
    }


    private void saveQuestionsTags(List<String> questionTags, Integer id) {

        Iterable<Tags> tags = this.TagRepository.findAll();
        questionTags.forEach(cycle -> {
            var questionTag = new QuestionsTags();
            questionTag.setId(new QuestionsTagsId());
            for (Tags tag  : tags) {
                if (tag.getName().equals(cycle)) {
                    questionTag.setId(new QuestionsTagsId(id, tag.getId()));
                    this.questionTagRepository.save(questionTag);
                }
            }
        });
    }




    private boolean validateQuestionTags(List<String> questionTags) {
        Iterable<Tags> tags = this.TagRepository.findAll();
        for (String cycle : questionTags) {
            boolean exists = false;
            for (Tags tag : tags) {
                if (tag.getName().equals(cycle)) {
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

    private boolean validateVote(List<Votes> questionVotes) {
        Iterable<Votes> votes = this.voteRepository.findAll();
        for (Votes cycle : questionVotes) {
            boolean exists = false;
            for (Votes vote : votes) {
                if (vote.getVoteId().equals(cycle.getVoteId())) {
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


}
