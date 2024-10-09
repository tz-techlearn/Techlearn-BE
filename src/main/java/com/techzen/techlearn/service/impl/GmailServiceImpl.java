package com.techzen.techlearn.service.impl;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VAlarm;
import biweekly.component.VEvent;
import biweekly.property.Method;
import biweekly.property.Trigger;
import biweekly.util.Duration;
import com.techzen.techlearn.dto.CalendarDTO;
import com.techzen.techlearn.dto.response.PointResponseDTO;
import com.techzen.techlearn.dto.response.UserResponseDTO;
import com.techzen.techlearn.entity.TeacherCalendar;
import com.techzen.techlearn.entity.UserEntity;
import com.techzen.techlearn.service.MailService;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.*;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GmailServiceImpl implements MailService {

    JavaMailSender javaMailSender;
    private static final String REMINDER_HTML_MAIL_TEMPLATE_PATH = "/template/event-reminder-template.html";
    private static final String SUPPORT_POINTS_MAIL_TEMPLATE_PATH = "/template/support-points-template.html";

    private static final String ORGANIZATION_NAME = "TechLearn";
    private static final String MANAGER_EMAIL = "thanhtuanle939@gmail.com";

    @Override
    public void sendScheduleSuccessEmail(CalendarDTO calenderDto) throws MessagingException, IOException {
        // Send email
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        mimeMessage.setRecipients(Message.RecipientType.TO, getToAddress(calenderDto.getAttendees()));
        mimeMessage.setSubject(calenderDto.getSubject());

        MimeMultipart mimeMultipart = new MimeMultipart("mixed");

        mimeMultipart.addBodyPart(createCalenderMimeBody(calenderDto));

        mimeMessage.setContent(mimeMultipart);
        javaMailSender.send(mimeMessage);
    }

    @Override
    public void sendEmails(List<String> recipientEmails, String subject, String title, String actionUrl, String actionText, String primaryColor, TeacherCalendar calendar) throws MessagingException {
        String htmlTemplate = """
                  <!DOCTYPE html>
                    <html lang="en">
                      <head>
                        <meta charset="UTF-8">
                          <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Calendar Event Notification</title>
                              <style>
                                body {
                                  font-family: Arial, sans-serif;
                                  line-height: 1.6;
                                  background-color: #f4f4f4;
                                  max-width: 600px;
                                  margin: 0 auto;
                                  padding: 20px;
                                  }
                                h1 {
                                  color: %1$s;
                                  text-align: center;
                                  font-size: 24px;
                                }
                                .event-details {
                                  background-color: #ffffff;
                                  border-left: 4px solid %1$s;
                                  padding: 20px;
                                  margin: 20px 0;
                                  border-radius: 8px;
                                  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                                }
                                .event-details h2 {
                                  color: #333;
                                  font-size: 20px;
                                  margin-top: 0;
                                }
                                .event-details p {
                                  color: #666;
                                  margin: 10px 0;
                                  font-size: 16px;
                                }
                                .event-time {
                                  font-weight: bold;
                                  color: %1$s;
                                  font-size: 16px;
                                }
                                .btn {
                                  display: inline-block;
                                  padding: 12px 25px;
                                  background-color: %1$s;
                                  color: #ffffff !important;
                                  text-decoration: none;
                                  border-radius: 6px;
                                  font-size: 16px;
                                  text-align: center;
                                  margin-top: 20px;
                                  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                                  transition: background-color 0.3s ease;
                                }
                                .btn:hover {
                                  background-color: #004080;
                                }
                              </style>
                      </head>
                      <body>
                          <h1>%2$s</h1>
                          <div class="event-details">
                              <h2>%3$s</h2>
                              <p><b>Người tham gia:</b> %4$s</p>
                              <p><b>Ghi chú:</b> %5$s</p>
                              <p><b>Bắt đầu:</b> %6$s</p>
                              <p><b>Kết thúc:</b> %7$s</p>
                          </div>
                          <a href="%8$s" class="btn">%9$s</a>
                      </body>
                    </html>
                """;

        String formattedHtml = String.format(htmlTemplate,
                primaryColor,
                subject,
                title,
                calendar.getTeacher().getName(),
                calendar.getDescription() != null ? calendar.getDescription() : "Không có ghi chú",
                calendar.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                calendar.getEndTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                actionUrl,
                actionText);


        for (String recipientEmail : recipientEmails) {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("thanhtuanle939@gmail.com");
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(formattedHtml, true);

            javaMailSender.send(message);
        }
    }

    @Override
    public void sendReminder(TeacherCalendar event) throws MessagingException {
        String subject = "Lời nhắc: " + event.getTitle();

        List<String> attendees = new ArrayList<>();
        attendees.add(event.getUser().getEmail());

        if (event.getTeacher() != null) {
            attendees.add(event.getTeacher().getEmail());
        } else if(event.getMentor() != null) {
            attendees.add(event.getMentor().getEmail());
        }

        for (String recipientEmail : attendees) {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = prepareReminderEmailContent(event, recipientEmail);

            helper.setFrom(MANAGER_EMAIL);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        }
    }

    @Override
    public void sendMailSupportPoints(PointResponseDTO point, UserResponseDTO user) throws MessagingException {
        String subject = "Thông báo mua gói hỗ trợ điểm";
        List<String> recipients = List.of(user.getEmail(), MANAGER_EMAIL);

        for (String recipientEmail : recipients) {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = prepareSupportPointsEmailContent(point, user, recipientEmail);

            helper.setFrom(MANAGER_EMAIL);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        }
    }


    @Override
    public void trialCourseMail(String userName, String courseName, String toEmail, String subject, String title, String description) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        String htmlBody = buildEmailTrialContent(userName,courseName);

        helper.setFrom("thanhtuanle939@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        javaMailSender.send(message);
    }

    @Override
    public void buyCourseMail(String studentName, List<String> teacherName, String courseName, List<String> teacherEmailAddress, String userEmailAddress) throws MessagingException {
        MimeMessage studentMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(studentMessage, true, "UTF-8");
        String htmlBody = buildEmailUserBuyCourse(studentName,courseName);

        helper.setFrom("thanhtuanle939@gmail.com");
        helper.setTo(userEmailAddress);
        helper.setSubject("Thông báo mua khóa học thành công");
        helper.setText(htmlBody, true);

        javaMailSender.send(studentMessage);
        for(int i=0; i<teacherName.size(); i++){
            htmlBody = buildEmailTeacherBuyCourse(teacherName.get(i),studentName,courseName);

            helper.setFrom("thanhtuanle939@gmail.com");
            helper.setTo(teacherEmailAddress.get(i));
            helper.setSubject("Thông báo có học viên mới đăng kí khóa học");
            helper.setText(htmlBody, true);

            javaMailSender.send(studentMessage);
        }
    }

    private Address[] getToAddress(List<String> attendees) {
        return attendees.stream().map(email -> {
            Address address = null;
            try {
                address = new InternetAddress(email);
            } catch (AddressException e) {
                e.printStackTrace();
            }
            return address;
        }).toArray(Address[]::new);
    }

    private Date getStartDate(LocalDateTime eventDateTime) {
        Instant instant = eventDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    private String createCalendar(CalendarDTO calendarDto) {
        ICalendar icalendar = new ICalendar();
        icalendar.addProperty(new Method(Method.REQUEST));
        icalendar.setUrl(calendarDto.getMeetingLink());

        VEvent event = new VEvent();
        event.setUrl(calendarDto.getMeetingLink());
        event.setSummary(calendarDto.getSummary());
        event.setDescription(calendarDto.getMeetingLink());
        event.setDateStart(getStartDate(calendarDto.getEventDateTime()));
        event.setDuration(new Duration.Builder()
                .minutes(10)
                .build());
        event.setOrganizer(calendarDto.getOrganizer());

        Trigger trigger = new Trigger(getStartDate(calendarDto.getEventDateTime().minusMinutes(5)));
        VAlarm alarm = VAlarm.email(trigger, "Reminder for your meeting", "", calendarDto.getAttendees());
        alarm.setDescription("This is reminder for your meeting: " + calendarDto.getSummary());
        event.addAlarm(alarm);

//        VAlarm alarm = new VAlarm(new Duration.Builder().minutes(-5).build());
//        alarm.setDescription("This is a reminder that the event is starting soon.");
//        event.addAlarm(alarm);
//
//        for (String email : calendarDto.getAttendees()) {
//            Attendee attendee = new Attendee(email);
//            event.addAttendee(attendee);
//        }

        icalendar.addEvent(event);
        return Biweekly.write(icalendar).go();
    }

    private BodyPart createCalenderMimeBody(CalendarDTO calenderDto) throws IOException, MessagingException {
        MimeBodyPart calenderBody = new MimeBodyPart();

        final DataSource source = new ByteArrayDataSource(createCalendar(calenderDto), "text/calender; charset=UTF-8");
        calenderBody.setDataHandler(new DataHandler(source));
        calenderBody.setHeader("Content-Type", "text/calendar; charset=UTF-8; method=REQUEST");

        return calenderBody;
    }

    private String prepareReminderEmailContent(TeacherCalendar event, String recipientEmail) {
        String template = loadHtmlTemplate(REMINDER_HTML_MAIL_TEMPLATE_PATH);

        return String.format(template,
                recipientEmail,
                event.getTitle(),
                formatDateTime(event.getStartTime()),
                formatDateTime(event.getEndTime()),
                event.getTeacher() != null ? String.format("<p><strong>Giảng viên:</strong> %s</p>", event.getTeacher().getName()) : "",
                event.getMentor() != null ? String.format("<p><strong>Người hướng dẫn:</strong> %s</p>", event.getMentor().getName()) : "",
                event.getDescription() != null ? String.format("<p><strong>Mô tả:</strong> %s</p>", event.getDescription()) : "",
                event.getMeetingUrl() != null ? String.format("<p><strong>Link google meet:</strong> %s</p>", event.getMeetingUrl()) : "",
                ORGANIZATION_NAME
        );
    }

    private String prepareSupportPointsEmailContent(PointResponseDTO point, UserResponseDTO user, String managerEmail) {
        String template = loadHtmlTemplate(SUPPORT_POINTS_MAIL_TEMPLATE_PATH);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        if(managerEmail.equals("thanhtuanle939@gmail.com")) {
            return String.format(
                    template,
                    managerEmail,
                    "Có yêu cầu mua gói hỗ trợ điểm",
                    user.getFullName(),
                    point.getPoints(),
                    point.getPrice(),
                    dtf.format(now),
                    ORGANIZATION_NAME
            );
        }

        return String.format(
                template,
                user.getEmail(),
                "Cảm ơn bạn đã mua điểm hỗ trợ. Dưới đây là chi tiết giao dịch của bạn:",
                user.getFullName(),
                point.getPoints(),
                point.getPrice(),
                dtf.format(now),
                ORGANIZATION_NAME
        );
    }

    private String loadHtmlTemplate(String path) {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IOException("Cannot find template file: " + path);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template", e);
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a"));
    }

    private String buildEmailTrialContent(String userName, String courseName){
        return
                "<!DOCTYPE html>" +
                        "<html lang=\"en\">" +
                        "<head>" +
                        "    <meta charset=\"UTF-8\">" +
                        "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                        "    <title>Email Confirm</title>" +
                        "    <style>" +
                        "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                        "        .container { width: 100%; max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); border: 0.5px solid black }" +
                        "        .header { background-color: #007bff; padding: 10px; text-align: center; color: #ffffff; border-top-left-radius: 10px; border-top-right-radius: 10px; }" +
                        "        .content { padding: 20px; text-align: left; color: #333333; }" +
                        "        .content h2 { color: #007bff; }" +
                        "        .content p { line-height: 1.6; }" +
                        "        .button { display: inline-block; padding: 10px 20px; background-color: #007bff; color: #ffffff; text-decoration: none; border-radius: 5px; margin-top: 20px; }" +
                        "        .footer { text-align: center; padding: 20px; font-size: 12px; color: #aaaaaa; }" +
                        "    </style>" +
                        "</head>" +
                        "<body>" +
                        "    <div class=\"container\">" +
                        "        <div class=\"header\">" +
                        "            <h1>Xác nhận đăng kí khóa học</h1>" +
                        "        </div>" +
                        "        <div class=\"content\">" +
                        "            <h2>Xin chào! " + userName + ",</h2>" +
                        "            <p>Chúc mừng! Bạn đã đăng kí học thử thành công khóa học <strong>" + courseName + "</strong>. Chúng tôi rất vui mừng chào đón bạn.</p>" +
                        "        </div>" +
                        "        <div class=\"footer\">" +
                        "            <p>Cảm ơn bạn tin tưởng chúng tôi. Chúng tôi mong muốn đồng hành cùng bạn trong xuyên suốt quá trình học tập!</p>" +
                        "            <p>TechLearn, 6 Trần Phú, Thạch Thang, Hải Châu, Đà Nẵng</p>" +
                        "        </div>" +
                        "    </div>" +
                        "</body>" +
                        "</html>";
    }

    private String buildEmailUserBuyCourse(String userName, String courseName){
        return
                "<!DOCTYPE html>" +
                        "<html lang=\"en\">" +
                        "<head>" +
                        "    <meta charset=\"UTF-8\">" +
                        "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                        "    <title>Email Confirm</title>" +
                        "    <style>" +
                        "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                        "        .container { width: 100%; max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); border: 0.5px solid black }" +
                        "        .header { background-color: #28a745; padding: 10px; text-align: center; color: #ffffff; border-top-left-radius: 10px; border-top-right-radius: 10px; }" +
                        "        .content { padding: 20px; text-align: left; color: #333333; }" +
                        "        .content h2 { color: #28a745; }" +
                        "        .content p { line-height: 1.6; }" +
                        "        .button { display: inline-block; padding: 10px 20px; background-color: #28a745; color: #ffffff; text-decoration: none; border-radius: 5px; margin-top: 20px; }" +
                        "        .footer { text-align: center; padding: 20px; font-size: 12px; color: #aaaaaa; }" +
                        "    </style>" +
                        "</head>" +
                        "<body>" +
                        "    <div class=\"container\">" +
                        "        <div class=\"header\">" +
                        "            <h1>Mua khóa học thành công</h1>" +
                        "        </div>" +
                        "        <div class=\"content\">" +
                        "            <h2>Xin chào! " + userName + ",</h2>" +
                        "            <p>Chúc mừng! Bạn đã mua thành công khóa học <strong>" + courseName + "</strong>. Chúng tôi rất vui mừng đồng hành cùng bạn trong suốt hành trình học tập này.</p>" +
                        "            <p>Hãy kiểm tra lại thông tin khóa học và thời gian bắt đầu để đảm bảo bạn không bỏ lỡ bất kỳ điều gì nhé.</p>" +
                        "        </div>" +
                        "        <div class=\"footer\">" +
                        "            <p>Cảm ơn bạn đã tin tưởng và lựa chọn nền tảng của chúng tôi.</p>" +
                        "            <p>TechLearn, 6 Trần Phú, Thạch Thang, Hải Châu, Đà Nẵng</p>" +
                        "        </div>" +
                        "    </div>" +
                        "</body>" +
                        "</html>";
    }


    private String buildEmailTeacherBuyCourse(String teacherName, String studentName, String courseName){
        return
                "<!DOCTYPE html>" +
                        "<html lang=\"en\">" +
                        "<head>" +
                        "    <meta charset=\"UTF-8\">" +
                        "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                        "    <title>Email Confirm</title>" +
                        "    <style>" +
                        "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                        "        .container { width: 100%; max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); border: 0.5px solid black }" +
                        "        .header { background-color: #ffc107; padding: 10px; text-align: center; color: #ffffff; border-top-left-radius: 10px; border-top-right-radius: 10px; }" +
                        "        .content { padding: 20px; text-align: left; color: #333333; }" +
                        "        .content h2 { color: #ffc107; }" +
                        "        .content p { line-height: 1.6; }" +
                        "        .button { display: inline-block; padding: 10px 20px; background-color: #ffc107; color: #ffffff; text-decoration: none; border-radius: 5px; margin-top: 20px; }" +
                        "        .footer { text-align: center; padding: 20px; font-size: 12px; color: #aaaaaa; }" +
                        "    </style>" +
                        "</head>" +
                        "<body>" +
                        "    <div class=\"container\">" +
                        "        <div class=\"header\">" +
                        "            <h1>Thông báo học viên tham gia khóa học</h1>" +
                        "        </div>" +
                        "        <div class=\"content\">" +
                        "            <h2>Xin chào giáo viên " + teacherName + ",</h2>" +
                        "            <p>Khóa học <strong>" + courseName + "</strong> vừa được mua thành công bởi một học viên <strong> "+ studentName +"</strong> </p>" +
                        "            <p>Hãy chuẩn bị giáo án và sẵn sàng cho buổi học sắp tới. Chúng tôi luôn sẵn sàng hỗ trợ bạn trong quá trình giảng dạy.</p>" +
                        "        </div>" +
                        "        <div class=\"footer\">" +
                        "            <p>Cảm ơn bạn đã đồng hành cùng nền tảng của chúng tôi!</p>" +
                        "            <p>TechLearn, 6 Trần Phú, Thạch Thang, Hải Châu, Đà Nẵng</p>" +
                        "        </div>" +
                        "    </div>" +
                        "</body>" +
                        "</html>";
    }

}
