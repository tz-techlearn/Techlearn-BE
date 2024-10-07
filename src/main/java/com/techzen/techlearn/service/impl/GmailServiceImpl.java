package com.techzen.techlearn.service.impl;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VAlarm;
import biweekly.component.VEvent;
import biweekly.property.Method;
import biweekly.property.Trigger;
import biweekly.util.Duration;
import com.techzen.techlearn.dto.CalendarDTO;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GmailServiceImpl implements MailService {

    JavaMailSender javaMailSender;
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
    public void sendEmails(List<String> recipientEmails, String subject, String title, String description,
                           LocalDateTime startTime, LocalDateTime endTime, String actionUrl, String actionText, String primaryColor) throws MessagingException {
        String htmlTemplate = """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Calendar Event Notification</title>
                        <style>
                            body { font-family: Arial, sans-serif; line-height: 1.6; color: #fff; max-width: 600px; margin: 0 auto; padding: 20px; }
                            h1 { color: %1$s; }
                            .event-details { background-color: #f9f9f9; border-left: 4px solid %1$s; padding: 15px; margin-bottom: 20px; }
                            .event-time { font-weight: bold; color: %1$s; }
                            .btn { display: inline-block; padding: 10px 20px; background-color: %1$s; color: #ffffff; text-decoration: none; border-radius: 5px; }
                        </style>
                    </head>
                    <body>
                        <h1>%2$s</h1>
                        <div class="event-details">
                            <h2>%3$s</h2>
                            <p>%4$s</p>
                            <p class="event-time">Bắt đầu: %5$s</p>
                            <p class="event-time">Kết thúc: %6$s</p>
                        </div>
                        <a href="%7$s" class="btn">%8$s</a>
                    </body>
                    </html>
                """;

        String formattedHtml = String.format(htmlTemplate,
                primaryColor,
                subject,
                title,
                description,
                startTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                endTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
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
