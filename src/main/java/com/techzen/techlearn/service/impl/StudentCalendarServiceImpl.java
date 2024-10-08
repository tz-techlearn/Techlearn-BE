package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.dto.CalendarDTO;
import com.techzen.techlearn.dto.request.TeacherCalendarRequestDTO2;
import com.techzen.techlearn.dto.response.TeacherCalendarResponseDTO2;
import com.techzen.techlearn.entity.*;
import com.techzen.techlearn.enums.CalendarStatus;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.exception.AppException;
import com.techzen.techlearn.mapper.TeacherCalendarMapper;
import com.techzen.techlearn.repository.*;
import com.techzen.techlearn.service.GoogleMeetService;
import com.techzen.techlearn.service.MailService;
import com.techzen.techlearn.service.StudentCalendarService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
    GoogleMeetService googleMeetService;

    private boolean isTeacher(UUID id) {
        return teacherRepository.existsById(id);
    }

    private boolean isMentor(UUID id) {
        return mentorRepository.existsById(id);
    }

    @Transactional
    @Override
    public TeacherCalendarResponseDTO2 addStudentCalendar(TeacherCalendarRequestDTO2 request) throws MessagingException, IOException, GeneralSecurityException {

        LocalDate dateStart = LocalDate.parse(request.getStartTime().substring(0, 10));
        LocalDate dateEnd = LocalDate.parse(request.getEndTime().substring(0, 10));

        if (dateStart.isBefore(LocalDate.now()) || dateEnd.isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.DATE_APPOINTMENT_NOT_SUITABLE);
        }

        if (dateStart.equals(LocalDate.now()) && dateEnd.equals(LocalDate.now())) {
            if (dateStart.isBefore(LocalDate.now()) || dateEnd.isBefore(LocalDate.now())) {
                throw new AppException(ErrorCode.DATE_APPOINTMENT_NOT_SUITABLE);
            }
        }

        TeacherCalendar calendar = teacherCalendarMapper.toEntity(request);
        UUID ownerId = UUID.fromString(request.getOwnerId());
        Teacher teacher;
        Mentor mentor;
        List<String> recipientEmails = new ArrayList<>();

        if (isTeacher(ownerId)) {
            teacher = teacherRepository.findById(ownerId).orElseThrow(
                    () -> new AppException(ErrorCode.TEACHER_NOT_EXISTED)
            );
            calendar.setTeacher(teacher);
            recipientEmails.add(
                    userRepository.findById(teacher.getId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)).getEmail()
            );
        } else if (isMentor(ownerId)) {
            mentor = mentorRepository.findById(ownerId).orElseThrow(
                    () -> new AppException(ErrorCode.MENTOR_NOT_EXISTED)
            );
            calendar.setMentor(mentor);

            recipientEmails.add(
                    userRepository.findById(mentor.getId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)).getEmail()
            );
        }

        UserEntity user = userRepository.findUserById(UUID.fromString(request.getUserId())).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        recipientEmails.add(
                userRepository.findById(user.getId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)).getEmail()
        );

        if (user.getPoints() <= 0) {
            throw new AppException(ErrorCode.POINTS_NOT_ENOUGH);
        }

        calendar.setStatus(CalendarStatus.BOOKED);
        user.setPoints(user.getPoints() - 1);
        calendar.setUser(user);

        calendar.setMeetingUrl(createGoogleMeetUrl(calendar, recipientEmails));

        // send email
        gmailService.sendEmails(
                recipientEmails,
                "Lịch đặt mới",
                calendar.getTitle(),
                calendar.getMeetingUrl(),
                "Tham gia cuộc họp",
                "#3498db",  // Màu xanh
                calendar
        );

        return teacherCalendarMapper.toDTO(teacherCalendarRepository.save(calendar));
    }

    private String createGoogleMeetUrl(TeacherCalendar teacherCalendar, List<String> recipientEmails) throws GeneralSecurityException, IOException {

        return googleMeetService.createGoogleMeetEvent(teacherCalendar, recipientEmails);
    }

    @Override
    public TeacherCalendarResponseDTO2 cancelCalendarStudentById(Integer id) {
        TeacherCalendar calendar = studentCalendarRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.CALENDAR_NOT_EXISTED)
        );

        Teacher teacher = calendar.getTeacher();
        Mentor mentor = calendar.getMentor();

        UserEntity user = null;
        if (calendar.getStatus().equals(CalendarStatus.BOOKED) & Duration.between(LocalDateTime.now(),calendar.getStartTime()).toMinutes() > 5){
            calendar.setStatus(CalendarStatus.CANCELLED);
            user = calendar.getUser();
            user.setPoints(user.getPoints() + 1);
            userRepository.save(user);
        } else {
            throw new AppException(ErrorCode.CALENDAR_CAN_NOT_DELETE);
        }

        List<String> recipientEmails = new ArrayList<>();

        if(teacher != null){
            recipientEmails.add(teacher.getEmail());
        } else if (mentor != null){
            recipientEmails.add(mentor.getEmail());
        }

        recipientEmails.add(user.getEmail());
        try {
            gmailService.sendEmails(
                    recipientEmails,
                    "Lịch đã bị hủy bởi " + user.getEmail(),
                    calendar.getTitle(),
                    calendar.getDescription(),
                    "Chi tiết",
                    "#e74c3c",  // Màu đỏ
                    calendar
            );
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }

        return teacherCalendarMapper.toDTO(studentCalendarRepository.save(calendar));
    }

    @Override
    public List<TeacherCalendarResponseDTO2> getStudentCalendarsByUserId(UUID id) {
        List<TeacherCalendar> calendars = studentCalendarRepository.findAllByUserCalendar(id);
        if (calendars.isEmpty()) {
            throw new AppException(ErrorCode.CALENDAR_NOT_EXISTED);
        } else return calendars.stream()
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
