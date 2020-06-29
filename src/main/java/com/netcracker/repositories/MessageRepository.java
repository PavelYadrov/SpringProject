package com.netcracker.repositories;

import com.netcracker.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByRoom_IdOrderByMessageDate(Long id);

    Message findFirstByRoom_IdOrderByMessageDateDesc(Long id);

    void deleteAllByRoomIdIsIn(List<Long> ids);
}
