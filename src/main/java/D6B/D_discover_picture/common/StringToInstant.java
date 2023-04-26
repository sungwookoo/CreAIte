package D6B.D_discover_picture.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class StringToInstant {

    public static Instant S2Ins(String s) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate date = LocalDate.parse(s, formatter);
        return date.atStartOfDay(ZoneOffset.UTC).toInstant();
    }
}
