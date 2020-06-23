package com.netcracker.repositories;

import com.netcracker.models.Room;
import com.netcracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByEmail(String email);

    Optional<User> findById(Long id);

    List<User> findByRoomsIn(List<Room> rooms);

    @Query(value = "select us.user_id from user_rooms us\n" +
            "where us.room_id=:room_id and us.user_id!=:id", nativeQuery = true)
    Long findByRooms_IdAndUser_Id(@Param("room_id") Long room_id, @Param("id") Long id);
}
