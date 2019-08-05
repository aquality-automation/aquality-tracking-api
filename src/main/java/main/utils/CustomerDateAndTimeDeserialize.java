package main.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.TimeZone;

public class CustomerDateAndTimeDeserialize extends JsonDeserializer<Date> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Date deserialize(JsonParser paramJsonParser,  DeserializationContext paramDeserializationContext) throws IOException {
        String str = paramJsonParser.getText().trim();
        try {
            if (str.contains("T")) {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
                TemporalAccessor accessor = timeFormatter.parse(str);

                return Date.from(Instant.from(accessor));
            }
            else if (str.contains(":")) {
                return dateFormat.parse(str);
            } else {
                return new Date(Long.valueOf(str));
            }
        } catch (ParseException e) {
            throw new IOException("Can't parse date: " + str);
        }
    }
}