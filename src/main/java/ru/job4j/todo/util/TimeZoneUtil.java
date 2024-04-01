package ru.job4j.todo.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public final class TimeZoneUtil {
    public static Set<String> getTimeZoneIds() {
        return new TreeSet<>(ZoneId.getAvailableZoneIds());
    }

    public static Map<String, String> getTimeZonesForDisplay() {
        return getTimeZoneIds().stream().collect(
                TreeMap::new,
                (map, zoneId) -> map.put(ZoneId.of(zoneId).getId(), String.format(
                        "[%s] - %s",
                        ZonedDateTime.now(ZoneId.of(zoneId)).format(DateTimeFormatter.ofPattern("HH:mm")),
                        zoneId
                )),
                Map::putAll
        );
    }
}
