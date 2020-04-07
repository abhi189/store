package com.budderfly.sites.service;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * TimezoneServiceTest
 */
@RunWith(Parameterized.class)
public class TimezoneServiceTest {

    TimezoneService timezoneService;

    @Parameter
    public String latitude;

    @Parameter(value = 1)
    public String longitude;

    @Parameter(value = 2)
    public String expectedTimezone;

    @Parameter(value = 3)
    public String scenario;

    @Before
    public void setUp() {
        timezoneService = new TimezoneService();
    }

    @Parameters(name = "{3}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "41.6985607", "-71.1580749", "America/New_York", "Valid New York Coordinates" },
                { "32.8310858", "-117.1271647", "America/Los_Angeles", "Valid Los Angeles Coordinates" },
                { "41.1350838", "-104.8157998", "America/Denver", "Valid Denver Coordinates" },
                { null, null, null, "Null latitude and longitude" }, { null, "-104.8157998", null, "Null latitude" },
                { "32.8310858", null, null, "Null longitude" }, { "123456", "-104.8157998", null, "Invalid latitude" },
                { "32.8310858", "-123456", null, "Invalid longitude" } });
    }

    /**
     * Given strings parsable to Double as latitude and longitude, TimeZoneId must be correctly fetched.
     *  Given bad data as latitude and longitude, TimeZoneId must be null.
     */
    @Test
    public void shouldGetTimezoneBasedOnCoordinates() {
        String timezone = this.timezoneService.getTimeZoneId(latitude, longitude);
        assertEquals(expectedTimezone, timezone);
    }

}