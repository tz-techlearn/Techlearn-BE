package com.techzen.techlearn.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.techzen.techlearn.entity.TeacherCalendar;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class GoogleMeetService {

    private static final String APPLICATION_NAME = "TechLearn";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(com.google.api.services.calendar.CalendarScopes.CALENDAR);

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GoogleMeetService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new IOException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public String createGoogleMeetEvent(TeacherCalendar calendar, List<String> recipientEmails) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(HTTP_TRANSPORT);
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        Event event = new Event()
                .setSummary(calendar.getTitle())
                .setDescription(calendar.getDescription());

        ZoneId zoneId = ZoneId.of(calendar.getStartTimezone());
        OffsetDateTime startOffset = calendar.getStartTime().atZone(zoneId).toOffsetDateTime();
        OffsetDateTime endOffset = calendar.getEndTime().atZone(zoneId).toOffsetDateTime();

        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startOffset.toInstant().toString()))
                .setTimeZone(calendar.getStartTimezone());
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(endOffset.toInstant().toString()))
                .setTimeZone(calendar.getEndTimezone());
        event.setEnd(end);

        List<EventAttendee> attendees = new ArrayList<>();
        for (String email : recipientEmails) {
            EventAttendee attendee = new EventAttendee()
                    .setEmail(email)
                    .setOptional(true)
                    .setResponseStatus("accepted");
            attendees.add(attendee);
        }
        event.setAttendees(attendees);

        // Setting access type to OPEN for anyone to join without knocking
        ConferenceSolutionKey conferenceSolutionKey = new ConferenceSolutionKey()
                .setType("hangoutsMeet");

        CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest()
                .setRequestId(UUID.randomUUID().toString())
                .setConferenceSolutionKey(conferenceSolutionKey);

        Map<String, Object> spaceConfig = new HashMap<>();
        spaceConfig.put("accessType", "OPEN");
        spaceConfig.put("entryPointAccess", "ALL");

        ConferenceData conferenceData = new ConferenceData()
                .setCreateRequest(createConferenceRequest);
        conferenceData.set("spaceConfig", spaceConfig);

        event.setConferenceData(conferenceData)
                .setGuestsCanInviteOthers(true)
                .setGuestsCanModify(true)
                .setGuestsCanSeeOtherGuests(true)
                .setVisibility("public");

        Event createdEvent = service.events()
                .insert("primary", event)
                .setConferenceDataVersion(1)
                .execute();

        // Extract the Google Meet URL
        ConferenceData createdConferenceData = createdEvent.getConferenceData();
        if (createdConferenceData != null && createdConferenceData.getEntryPoints() != null) {
            for (EntryPoint entryPoint : createdConferenceData.getEntryPoints()) {
                if ("video".equals(entryPoint.getEntryPointType())) {
                    return entryPoint.getUri();
                }
            }
        }
        return "No Google Meet URL found.";
    }
}
