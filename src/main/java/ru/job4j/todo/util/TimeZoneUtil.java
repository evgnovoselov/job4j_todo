package ru.job4j.todo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class TimeZoneUtil {
    public static Map<String, String> getTimeZonesForDisplay() {
        return Arrays.stream(TimeZone.getAvailableIDs()).collect(
                LinkedHashMap::new,
                (map, timezoneId) -> {
                    TimeZone timeZone = TimeZone.getTimeZone(timezoneId);
                    map.put(timeZone.getID(), String.format(
                            "%s - [%s] - %s",
                            timeZone.getID(),
                            LocalDateTime.now(timeZone.toZoneId()).format(DateTimeFormatter.ofPattern("HH:mm")),
                            timeZone.getDisplayName()
                    ));
                },
                Map::putAll
        );
    }
}
