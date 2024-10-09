package com.techzen.techlearn.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum SubmitStatus {
    PENDING("Pending"),
    PASS("Pass"),
    FIX_REVIEW("Fix Review"),
    FAIL("Fix Review"),
    ;
   String name;
}