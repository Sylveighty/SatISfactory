package com.pup.sis.repository;

import com.pup.sis.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByActiveTrueOrderByPublishDateDesc();

    List<Announcement> findAllByOrderByPublishDateDesc();
}