package com.pup.sis.repository;

import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.Schedule;
import com.pup.sis.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // All schedules for a section
    List<Schedule> findBySection(Section section);

    // All schedules for a faculty member
    List<Schedule> findByFaculty(Faculty faculty);

    // Schedules for a faculty member in a specific term
    List<Schedule> findByFacultyAndSchoolYearAndSemester(
            Faculty faculty, String schoolYear, String semester);

    // Schedules for a section in a specific term
    List<Schedule> findBySectionAndSchoolYearAndSemester(
            Section section, String schoolYear, String semester);

    // Room conflict check - same room, same day, overlapping time
    // excludeId allows excluding the current schedule when editing
    @Query("SELECT s FROM Schedule s WHERE s.room = :room " +
           "AND s.day = :day " +
           "AND s.startTime < :endTime " +
           "AND s.endTime > :startTime " +
           "AND s.id != :excludeId")
    List<Schedule> findRoomConflicts(
            @Param("room") String room,
            @Param("day") String day,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId);

    // Faculty conflict check - same faculty, same day, overlapping time
    @Query("SELECT s FROM Schedule s WHERE s.faculty = :faculty " +
           "AND s.day = :day " +
           "AND s.startTime < :endTime " +
           "AND s.endTime > :startTime " +
           "AND s.id != :excludeId")
    List<Schedule> findFacultyConflicts(
            @Param("faculty") Faculty faculty,
            @Param("day") String day,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId);

    // Other schedule rows for the same subject + section + term (e.g. the
    // lecture row when creating/editing the lab row, or vice versa).
    // Used to enforce that the same subject/section is always taught by the
    // same faculty member across all of its meeting times (lec and lab),
    // since grades are recorded per subject, not per meeting type — having
    // two different faculty would mean one silently overwrites the other's
    // submitted grades.
    @Query("SELECT s FROM Schedule s WHERE s.subject = :subject " +
           "AND s.section = :section " +
           "AND s.schoolYear = :schoolYear " +
           "AND s.semester = :semester " +
           "AND s.id != :excludeId")
    List<Schedule> findOtherSchedulesForSameSubjectSection(
            @Param("subject") com.pup.sis.entity.Subject subject,
            @Param("section") Section section,
            @Param("schoolYear") String schoolYear,
            @Param("semester") String semester,
            @Param("excludeId") Long excludeId);
}