package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.client.TeacherClient;
import com.techzen.techlearn.dto.request.TeacherRequestDTO;
import com.techzen.techlearn.dto.response.PageResponse;
import com.techzen.techlearn.dto.response.TeacherResponseDTO;
import com.techzen.techlearn.entity.CourseEntity;
import com.techzen.techlearn.entity.Teacher;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.exception.AppException;
import com.techzen.techlearn.mapper.TeacherMapper;
import com.techzen.techlearn.repository.CourseRepository;
import com.techzen.techlearn.repository.TeacherRepository;
import com.techzen.techlearn.service.TeacherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeacherServiceImpl implements TeacherService {

    TeacherRepository teacherRepository;
    TeacherMapper teacherMapper;
    CourseRepository courseRepository;
    TeacherClient teacherClient;

    @Override
    public PageResponse<?> findAll(int page, int pageSize) {

        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);
        Page<Teacher> teachers = teacherRepository.findAll(pageable);
        List<TeacherResponseDTO> list = teachers.map(teacherMapper::toTeacherResponseDTO).stream().collect(Collectors.toList());
        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(teachers.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public List<TeacherResponseDTO> findAll() {
        return teacherRepository.findAll().stream().map(teacherMapper::toTeacherResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeacherResponseDTO> filterTeacherByCourse(Long id) {
        List<Teacher> teachers = teacherRepository.findTeacherByCourse(id);
        if (teachers.isEmpty()) {
            throw new AppException(ErrorCode.TEACHER_NOT_EXISTED);
        } else return teachers.stream()
                .map(teacherMapper::toTeacherResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TeacherResponseDTO addTeacher(TeacherRequestDTO request) {
        Teacher teacher = teacherMapper.toTeacherEntity(request);

        if (teacher.getId() == null) {
            teacher.setId(UUID.randomUUID());
        }

        return teacherMapper.toTeacherResponseDTO(teacherRepository.save(teacher));
    }

    @Override
    public void addTeacherToCourse(UUID teacherId, Long courseId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_EXISTED));

        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        teacher.getCourses().add(course);

        teacherRepository.save(teacher);
    }

    public List<TeacherResponseDTO> getTeacherByCourseId(Long courseId) {
        // Lấy phản hồi từ teacherClient
        Map<String, Object> response = (Map<String, Object>) teacherClient.getTeacherByIdCourse(courseId).getBody();

        // In ra phản hồi để kiểm tra
        System.out.println("Response from teacherClient: " + response);

        // Kiểm tra nếu response không null và có chứa "data"
        if (response != null && response.containsKey("data")) {
            List<Map<String, Object>> teachers = (List<Map<String, Object>>) response.get("data");

            // Chuyển đổi danh sách giáo viên từ Map sang TeacherResponseDTO
            return teachers.stream()
                    .map(teacher -> TeacherResponseDTO.builder()
                            .id(UUID.fromString((String) teacher.get("id")))
                            .name((String) teacher.get("name"))
                            .avatar((String) teacher.get("avatar"))
                            .color((String) teacher.get("color"))
                            .email((String) teacher.get("email"))
                            .build())
                    .collect(Collectors.toList());
        }

        // Trả về danh sách rỗng nếu không có dữ liệu
        return new ArrayList<>();
    }
}