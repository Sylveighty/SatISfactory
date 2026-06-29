package com.pup.sis.service;

import com.pup.sis.entity.Message;
import com.pup.sis.entity.Role;
import com.pup.sis.entity.User;
import com.pup.sis.repository.MessageRepository;
import com.pup.sis.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public List<Message> findByRecipient(User recipient) {
        return messageRepository.findByRecipientOrderBySentAtDesc(recipient);
    }

    public long countUnread(User recipient) {
        return messageRepository.countByRecipientAndReadFalse(recipient);
    }

    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    @Transactional
    public void send(User sender, User recipient, String subject, String body) {
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setSubject(subject);
        message.setBody(body);
        messageRepository.save(message);
    }

    @Transactional
    public void broadcast(User sender, Role role, String subject, String body) {
        List<User> recipients = userRepository.findByRole(role);
        for (User recipient : recipients) {
            send(sender, recipient, subject, body);
        }
    }

    @Transactional
    public void markAsRead(Long id) {
        messageRepository.findById(id).ifPresent(m -> {
            m.setRead(true);
            messageRepository.save(m);
        });
    }

    @Transactional
    public void delete(Long id) {
        messageRepository.deleteById(id);
    }
}