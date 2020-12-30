package me.seungyeol.date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Jsr310Test {

    @Test
    @DisplayName("1일 후 구하기")
    void shouldGetAfterOneDay() {
        LocalDate theDay = IsoChronology.INSTANCE.date(1582,10,4);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        assertEquals(theDay.format(formatter), "1582.10.04");

        LocalDate nextDay = theDay.plusDays(1);
        assertEquals(nextDay.format(formatter), "1582.10.05");

    }

    @Test
    @DisplayName("서머타임 반영하여 1시간 후 구하기")
    void shouldGetAfterOneHour() {
        ZoneId seoul = ZoneId.of("Asia/Seoul");
        ZonedDateTime theTime = ZonedDateTime.of(1988,5,8,1,0,0,0,seoul);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

        assertEquals(theTime.format(formatter), "1988.05.08 01:00");
        ZoneRules seoulRules = seoul.getRules();
        assertThat(seoulRules.isDaylightSavings(Instant.from(theTime))).isFalse();

        ZonedDateTime after1Hour = theTime.plusHours(1);
        assertThat(after1Hour.format(formatter)).isEqualTo("1988.05.08 03:00");
        assertThat(seoulRules.isDaylightSavings(Instant.from(after1Hour))).isTrue();
    }

    @Test
    @DisplayName("UTC+0800에서 UTC+0830 변경시 에 1분 더하기")
    void shouldGetAfterOneMinute() {
        ZoneId seoul = ZoneId.of("Asia/Seoul");
        ZonedDateTime theTime = ZonedDateTime.of(1961,8,9,23,59,59,0,seoul);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        assertThat(theTime.format(formatter)).isEqualTo("1961.08.09 23:59");

        ZonedDateTime after1Minute = theTime.plusMinutes(1);
        assertThat(after1Minute.format(formatter)).isEqualTo("1961.08.10 00:30");
    }

    @Test
    @DisplayName("윤초가 특별히 더해지지 않는 2초 후 구하기")
    void shouldGetAfterTwoSecond() {
        ZoneId utc = ZoneId.of("UTC");
        ZonedDateTime theTime = ZonedDateTime.of(2012,6,30,23,59,59,0,utc);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

        assertThat(theTime.format(formatter)).isEqualTo("2012.06.30 23:59:59");
        ZonedDateTime after2Seconds = theTime.plusSeconds(2);
        assertThat(after2Seconds.format(formatter)).isEqualTo("2012.07.01 00:00:01");

    }

    @Test
    @DisplayName("1999년 12월 31일을 지정하는 코드")
    void shouldGetDate() {
        LocalDate theDay = LocalDate.of(1999,12,31);

        assertThat(theDay.getYear()).isEqualTo(1999);
        assertThat(theDay.getMonthValue()).isEqualTo(12);
        assertThat(theDay.getDayOfMonth()).isEqualTo(31);
    }

    @Test()
    @DisplayName("1999년 12월 31일을 지정하는 코드의 실수")
    void shouldNotAcceptWrongDate() {
        assertThrows(DateTimeException.class, () -> {
            LocalDate.of(1999,13,31);
        });
    }

    @Test
    @DisplayName("요일 확인하기")
    void shouldGetDayOfWeek() {
        LocalDate theDay = LocalDate.of(2014,1,1);

        DayOfWeek dayOfWeek = theDay.getDayOfWeek();
        assertThat(dayOfWeek).isEqualByComparingTo(DayOfWeek.WEDNESDAY);
    }

    @Test
    @DisplayName("잘못 지정한 시간대 아이디")
    void shouldThrowExceptionWhenWrongTimeZoneId() {
        assertThrows(ZoneRulesException.class, () -> ZoneId.of("Seould/Asia"));
    }

}