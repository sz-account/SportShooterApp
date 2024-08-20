package com.example.dsp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConverterTest {

    Date testDate;
    Long testLong;

    @Before
    public void setUp() throws ParseException {
        String dateStr = "1994-05-02";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        testDate = sdf.parse(dateStr);
        testLong = 767829600000L;
    }

    @Test
    public void converter_fromLongToDate()  {

        Date response = Converters.fromLong(null);
        assertNull(response);

        response = Converters.fromLong(testLong);
        assertEquals(testDate, response);
    }

    @Test
    public void converter_fromDateToLong() throws ParseException {

        Long response = Converters.fromDate(null);
        assertNull(response);

        response = Converters.fromDate(testDate);
        assertEquals(testLong, response);
    }
}
