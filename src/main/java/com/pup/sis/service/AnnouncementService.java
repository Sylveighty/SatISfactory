package com.pup.sis.service;

import com.pup.sis.entity.Announcement;
import com.pup.sis.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public List<Announcement> findAll() {
        return announcementRepository.findAllByOrderByPublishDateDesc();
    }

    public List<Announcement> findAllActive() {
        return announcementRepository.findByActiveTrueOrderByPublishDateDesc();
    }

    public Optional<Announcement> findById(Long id) {
        return announcementRepository.findById(id);
    }

    @Transactional
    public Announcement save(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    @Transactional
    public void delete(Long id) {
        announcementRepository.deleteById(id);
    }

    @Transactional
    public void toggleActive(Long id) {
        announcementRepository.findById(id).ifPresent(a -> {
            a.setActive(!a.isActive());
            announcementRepository.save(a);
        });
    }
}