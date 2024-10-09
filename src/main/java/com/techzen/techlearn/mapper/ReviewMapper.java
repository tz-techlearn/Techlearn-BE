package com.techzen.techlearn.mapper;

import com.techzen.techlearn.dto.response.ReviewResponseDTO;
import com.techzen.techlearn.entity.SubmitionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewResponseDTO toReviewResponseDTO(SubmitionEntity submition);
}
