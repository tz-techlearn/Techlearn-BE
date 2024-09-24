package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.dto.CalendarDTO;
import com.techzen.techlearn.dto.request.TeacherCalendarRequestDTO2;
import com.techzen.techlearn.dto.response.TeacherCalendarResponseDTO2;
import com.techzen.techlearn.entity.Mentor;
import com.techzen.techlearn.entity.Teacher;
import com.techzen.techlearn.entity.TeacherCalendar;
import com.techzen.techlearn.entity.UserEntity;
import com.techzen.techlearn.enums.CalendarStatus;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.exception.AppException;
import com.techzen.techlearn.mapper.TeacherCalendarMapper;
import com.techzen.techlearn.repository.*;
import com.techzen.techlearn.service.MailService;
import com.techzen.techlearn.service.StudentCalendarService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentCalendarServiceImpl implements StudentCalendarService {

    UserRepository userRepository;
    StudentCalendarRepository studentCalendarRepository;
    TeacherCalendarRepository teacherCalendarRepository;
    TeacherCalendarMapper teacherCalendarMapper;
    MailService gmailService;
    TeacherRepository teacherRepository;
    MentorRepository mentorRepository;

    private boolean isTeacher(UUID id) {
        return teacherRepository.existsById(id);
    }

    private boolean isMentor(UUID id) {
        return mentorRepository.existsById(id);
    }

    @Transactional
    @Override
    public TeacherCalendarResponseDTO2 addStudentCalendar(TeacherCalendarRequestDTO2 request) throws MessagingException, IOException {
        TeacherCalendar calendar = teacherCalendarMapper.toEntity(request);

        UUID ownerId = UUID.fromString(request.getOwnerId());
        Teacher teacher;
        Mentor mentor;

        if (isTeacher(ownerId)) {
            teacher = teacherRepository.findById(ownerId).orElseThrow(
                    () -> new AppException(ErrorCode.TEACHER_NOT_EXISTED)
            );
            calendar.setTeacher(teacher);
        } else if (isMentor(ownerId)) {
            mentor = mentorRepository.findById(ownerId).orElseThrow(
                    () -> new AppException(ErrorCode.MENTOR_NOT_EXISTED)
            );
            calendar.setMentor(mentor);
        }

        UserEntity user = userRepository.findUserById(UUID.fromString(request.getUserId())).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        if (user.getPoints() <= 0) {
            throw new AppException(ErrorCode.POINTS_NOT_ENOUGH);
        }

        calendar.setStatus(CalendarStatus.BOOKED);
        user.setPoints(user.getPoints() - 1);
        calendar.setUser(user);

//        CalendarDTO calendarDTO = CalendarDTO.builder()
//                .attendees(List.of("tieuvi200904@gmail.com"))
//                .subject("Thông báo lịch hỗ trợ online 1v1")
//                .description("Chúng tôi xin thông báo rằng bạn đã đặt lịch hỗ trợ 1v1 online thành công. Đây là cơ hội để bạn được hỗ trợ trực tiếp bởi giảng viên/tư vấn viên của chúng tôi, giải đáp mọi thắc mắc và giúp bạn đạt được mục tiêu học tập của mình.") // Mô tả của sự kiện
//                .summary("lịch hỗ trợ online 1v1 ")
//                .meetingLink("https://example.com/meeting")
//                .eventDateTime(LocalDateTime.now().plusMinutes(10)) // (10p sau) // LocalDateTime.of(2024, 09, 05, 14, 30)) Ngày cụ thể: 05/09/2024 lúc 14:30
//                .build();
//        gmailService.sendScheduleSuccessEmail(calendarDTO);

        return teacherCalendarMapper.toDTO(teacherCalendarRepository.save(calendar));
    }

    @Override
    public TeacherCalendarResponseDTO2 cancelCalendarStudentById(UUID id) {
        studentCalendarRepository.findIdUserCalendar(id)
                .orElseThrow(() -> new AppException(ErrorCode.CALENDAR_NOT_EXISTED));
        studentCalendarRepository.cancelByUserIdCalendar(id);
        return null;
    }

    @Override
    public List<TeacherCalendarResponseDTO2> getStudentCalendarsByUserId(UUID id) {
        List<TeacherCalendar> calendars = studentCalendarRepository.findAllByUserCalendar(id);
        return calendars.stream()
                .map(teacherCalendarMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Integer cancelBooking(Integer bookingId) {
        TeacherCalendar teacherCalendar = teacherCalendarRepository.findById(bookingId).orElseThrow(
                ()-> new AppException(ErrorCode.CALENDAR_NOT_EXISTED)
        );
        if (teacherCalendar.getStatus().equals(CalendarStatus.BOOKED)){
            teacherCalendar.setStatus(CalendarStatus.CANCELLED);
            studentCalendarRepository.save(teacherCalendar);
            UserEntity user = teacherCalendar.getUser();
            user.setPoints(user.getPoints() + 1);
            userRepository.save(user);
            return user.getPoints();
        }else {
            throw new AppException(ErrorCode.CALENDAR_NOT_EXISTED);
        }

    }

}
