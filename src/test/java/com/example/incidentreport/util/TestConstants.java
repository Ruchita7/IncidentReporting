package com.example.incidentreport.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public final class TestConstants {

    private TestConstants() {}

    public static final String VALID_USERNAME = RandomStringUtils.randomAlphanumeric(10);
    public static final String VALID_FIRSTNAME = RandomStringUtils.randomAlphabetic(10);
    public static final String VALID_LASTNAME = RandomStringUtils.randomAlphabetic(10);

    public static final String VALID_USERNAME_1 = RandomStringUtils.randomAlphanumeric(10);
    public static final String VALID_FIRSTNAME_1 = RandomStringUtils.randomAlphabetic(10);
    public static final String VALID_LASTNAME_1 = RandomStringUtils.randomAlphabetic(10);


    public static final String INVALID_USERNAME = RandomStringUtils.randomAlphanumeric(10);
    public static final String INVALID_FIRSTNAME = RandomStringUtils.randomAlphabetic(10);
    public static final String INVALID_LASTNAME = RandomStringUtils.randomAlphabetic(10);

    public static final String VALID_INCIDENT_TITLE = RandomStringUtils.randomAlphanumeric(10);

    public static final String VALID_INCIDENT_TITLE_1 = RandomStringUtils.randomAlphanumeric(10);
    public static final String INVALID_STATUS = RandomStringUtils.randomAlphabetic(10);
    public static final Integer INVALID_INCIDENT_ID = new Random().nextInt();
    public static final Integer VALID_INCIDENT_ID = new Random().nextInt();

    public static final Long INVALID_INCIDENT_ID_LONG = new Random().nextLong();
    public static final Long VALID_INCIDENT_ID_LONG = new Random().nextLong();
    public static final Long VALID_USER_ID = new Random().nextLong();

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER= 0;
}
