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

        // NOTE: Same-faculty-per-subject-section is enforced by auto-syncing
        // the faculty across sibling schedule rows (lecture/lab) instead of
        // blocking the edit here. See ScheduleService.syncFacultyAcrossSiblings,
        // called by AdminScheduleController after a successful save. Blocking
        // here would make it impossible to ever reassign a subject's faculty
        // once both lecture and lab rows exist.

        return null; // no conflict
    }

    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    /**
     * Propagates a schedule's faculty assignment to all sibling schedule
     * rows for the same subject + section + term (e.g. lecture and lab
     * meeting times). This keeps the same-faculty-per-subject-section
     * invariant intact without ever blocking an admin from reassigning a
     * subject's faculty — editing any one meeting time (lecture or lab)
     * updates all of them together.
     *
     * Should be called after saving a schedule whose faculty was changed.
     */
    public void syncFacultyAcrossSiblings(Schedule schedule) {
        if (schedule.getSubject() == null || schedule.getSection() == null
                || schedule.getFaculty() == null) {
            return;
        }

        List<Schedule> siblings = scheduleRepository.findOtherSchedulesForSameSubjectSection(
                schedule.getSubject(),
                schedule.getSection(),
                schedule.getSchoolYear(),
                schedule.getSemester(),
                schedule.getId() != null ? schedule.getId() : -1L);

        for (Schedule sibling : siblings) {
            if (sibling.getFaculty() == null
                    || !sibling.getFaculty().getId().equals(schedule.getFaculty().getId())) {
                sibling.setFaculty(schedule.getFaculty());
                scheduleRepository.save(sibling);
            }
        }
    }

    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }
}