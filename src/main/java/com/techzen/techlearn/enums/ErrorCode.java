package com.techzen.techlearn.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1009, "User not found", HttpStatus.BAD_REQUEST),
    GET_SUCCESSFUL(1010, "Get successful", HttpStatus.OK),
    ADD_SUCCESSFUL(1011, "Add successful", HttpStatus.OK),
    DELETE_SUCCESSFUL(1012, "Delete successful", HttpStatus.OK),
    UPDATE_SUCCESSFUL(1013, "Update successful", HttpStatus.OK),
    INVALID_DATA(1014, "Invalid data", HttpStatus.BAD_REQUEST),
    FULL_NAME_INVALID(1014, "Full name must be not blank", HttpStatus.BAD_REQUEST),
    TEACHER_NOT_EXISTED(1015, "Teacher not existed", HttpStatus.NOT_FOUND),
    CALENDAR_NOT_EXISTED(1016, "calendar not existed", HttpStatus.NOT_FOUND),
    TIME_NOT_SUITABLE(1016, "time start must time end equals ten minutes", HttpStatus.NOT_FOUND),
    TEACHER_CALENDAR_DATE_APPOINTMENT_EXISTED(1017, "teacher or calendar or date appointment  existed", HttpStatus.BAD_REQUEST),
    NAME_TEACHER_OR_TECHNICAL_AND_CURRENT_DATE_NOT_EXISTED(1018, "technical or teacher or current date not existed", HttpStatus.BAD_REQUEST),
    DATE_APPOINTMENT_NOT_SUITABLE(1019, "This smaller set date is now", HttpStatus.BAD_REQUEST),
    TIME_START_SUITABLE(1020, "This smaller set time is now", HttpStatus.BAD_REQUEST),
    ;

    Integer code;
    String message;
    HttpStatusCode statusCode;
}
