package com.pup.sis.repository;

import com.pup.sis.entity.Message;
import com.pup.sis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRecipientOrderBySentAtDesc(User recipient);

    long countByRecipientAndReadFalse(User recipient);
}