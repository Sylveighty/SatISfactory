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
}