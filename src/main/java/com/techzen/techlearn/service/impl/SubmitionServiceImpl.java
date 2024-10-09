package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.dto.response.PageResponse;
import com.techzen.techlearn.dto.response.ReviewResponseDTO;
import com.techzen.techlearn.dto.response.SubmittionResponseDTO;
import com.techzen.techlearn.entity.SubmitionEntity;
import com.techzen.techlearn.entity.UserEntity;
import com.techzen.techlearn.enums.SubmitStatus;
import com.techzen.techlearn.mapper.ReviewMapper;
import com.techzen.techlearn.mapper.SubmittionMapper;
import com.techzen.techlearn.repository.SubmitionRepository;
import com.techzen.techlearn.repository.UserRepository;
import com.techzen.techlearn.service.SubmitionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmitionServiceImpl implements SubmitionService {

    SubmitionRepository submitionRepository;
    UserRepository userRepository;
    ReviewMapper reviewMapper;
    SubmittionMapper submittionMapper;

    @Override
    public void addSubmit(String linkGithub, String resultReview, String id, String idAss) {
        UserEntity userEntity = userRepository.findUserById(
                        UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        SubmitStatus status;
        if (resultReview.contains("Pass")) {
            status = SubmitStatus.PASS;
        } else if (resultReview.contains("Fail")) {
            status = SubmitStatus.FIX_REVIEW;
        } else {
            status = SubmitStatus.PENDING;
        }
        var submit = SubmitionEntity.builder()
                .linkGithub(linkGithub)
                .review(resultReview)
                .user(userEntity)
                .isDeleted(false)
                .status(status)
                .assignmentId(Long.parseLong(idAss))
                .build();
        submitionRepository.save(submit);
    }

    @Override
    public PageResponse<?> getAllReviews(int page, int pageSize, UUID userId, long assignmentId) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);
        Page<SubmitionEntity> reviews = submitionRepository
                .findAllByAssignmentIdAndUserId(pageable,assignmentId, userId);
        System.out.println(reviews.getContent().toString());
        List<ReviewResponseDTO> list = reviews.getContent().stream().map(reviewMapper::toReviewResponseDTO)
                .collect(Collectors.toList());
        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(reviews.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public ReviewResponseDTO getReviewById(long assignment, UUID id) {
        return reviewMapper.toReviewResponseDTO(
                submitionRepository.findTopByAssignmentIdAndUserIdOrderByIdDesc(assignment, id));
    }

    @Override
    public PageResponse<?> getAllSubmitByStatus(int page, int pageSize, UUID userId, Long assignmentId, String status) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);
        SubmitStatus submitStatus = SubmitStatus.valueOf(status);
        List<SubmittionResponseDTO> list;
        Page<SubmitionEntity> reviews = submitionRepository
                .findAllByUserIdAndStatus(pageable, userId, submitStatus);
        if (assignmentId != null) {
            list = reviews.stream()
                    .filter(c -> c.getAssignmentId().equals(assignmentId))
                    .map(submittionMapper::toSubmittionResponseDTO)
                    .collect(Collectors.toList());
        } else {
            list = reviews.map(submittionMapper::toSubmittionResponseDTO)
                    .stream().collect(Collectors.toList());
        }
        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(reviews.getTotalPages())
                .items(list)
                .build();
    }


}
