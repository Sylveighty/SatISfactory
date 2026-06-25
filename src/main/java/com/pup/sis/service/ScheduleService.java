package com.pup.sis.service;

import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.Schedule;
import com.pup.sis.entity.Section;
import com.pup.sis.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    public Optional<Schedule> findById(Long id) {
        return scheduleRepository.findById(id);
    }

    public List<Schedule> findBySection(Section section) {
        return scheduleRepository.findBySection(section);
    }

    public List<Schedule> findByFaculty(Faculty faculty) {
        return scheduleRepository.findByFaculty(faculty);
    }

    public List<Schedule> findByFacultyAndTerm(
            Faculty faculty, String schoolYear, String semester) {
        return scheduleRepository.findByFacultyAndSchoolYearAndSemester(
                faculty, schoolYear, semester);
    }

    public List<Schedule> findBySectionAndTerm(
            Section section, String schoolYear, String semester) {
        return scheduleRepository.findBySectionAndSchoolYearAndSemester(
                section, schoolYear, semester);
    }

    /**
     * Checks for room and faculty conflicts for a given schedule.
     * Returns a description of the conflict, or null if no conflict found.
     * Used to show a warning banner on the admin schedules page.
     * Hard blocking is deferred to Step 5.
     */
    public String checkConflict(Schedule schedule) {
        Long excludeId = schedule.getId() != null ? schedule.getId() : -1L;

        // Check room conflict
        if (schedule.getRoom() != null && !schedule.getRoom().isBlank()) {
            List<?> roomConflicts = scheduleRepository.findRoomConflicts(
                    schedule.getRoom(),
                    schedule.getDay(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    excludeId);
            if (!roomConflicts.isEmpty()) {
                return "Room conflict: " + schedule.getRoom()
                        + " is already booked on " + schedule.getDay()
                        + " at the same time.";
            }
        }

        // Check faculty conflict
        if (schedule.getFaculty() != null) {
            List<?> facultyConflicts = scheduleRepository.findFacultyConflicts(
                    schedule.getFaculty(),
                    schedule.getDay(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    excludeId);
            if (!facultyConflicts.isEmpty()) {
                return "Faculty conflict: "
                        + schedule.getFaculty().getFullName()
                        + " already has a class on " + schedule.getDay()
                        + " at the same time.";
            }
        }

        return null; // no conflict
    }

    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }
}