package com.netcracker.repositories;

import com.netcracker.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query(value = "select distinct us.room_id from user_rooms us join user_rooms ur on (us.user_id!=ur.user_id and us.room_id=ur.room_id)\n" +
            "where us.user_id in (:ids) and ur.user_id in (:ids)", nativeQuery = true)
    Long getRoomId(@Param("ids") List<Long> ids);

    void deleteAllByIdIsIn(List<Long> ids);

    @Query(value = "delete from rooms where rooms.id in (:ids)", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteAllByIds(@Param("ids") List<Long> ids);

    @Query(value = "delete from user_rooms where user_rooms.room_id in (:ids)", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteAllKeys(@Param("ids") List<Long> ids);

    @Query(value = "select ss.room_id from user_rooms  ss where ss.user_id=:id", nativeQuery = true)
    List<Long> findAllByUserId(@Param("id") Long id);
}
