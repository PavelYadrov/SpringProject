package com.netcracker.repositories;

import com.netcracker.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query(value = "select distinct us.room_id from user_rooms us join user_rooms ur on (us.user_id!=ur.user_id and us.room_id=ur.room_id)\n" +
            "where us.user_id in (:ids) and ur.user_id in (:ids)", nativeQuery = true)
    Long getRoomId(@Param("ids") List<Long> ids);

}
