package com.netcracker.services;

import com.netcracker.dto.DTOHelper;
import com.netcracker.dto.MessageDTO;
import com.netcracker.dto.RoomDTO;
import com.netcracker.models.Message;
import com.netcracker.models.Room;
import com.netcracker.models.User;
import com.netcracker.repositories.MessageRepository;
import com.netcracker.repositories.RoomRepository;
import com.netcracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private MessageRepository messageRepository;

    private RoomRepository roomRepository;

    private UserService userService;

    private UserRepository userRepository;

    @Autowired
    public ChatService(MessageRepository messageRepository, RoomRepository roomRepository,
                       UserService userService, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }


    private RoomDTO fromRoom(Room room) {
        User user = userService.getCurrentUser();

        User receiver = userRepository.findById(userRepository.findByRooms_IdAndUser_Id(room.getId(), user.getId())).get();

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setUserId(receiver.getId());
        roomDTO.setId(room.getId());
        roomDTO.setUrl(receiver.getAvatar());
        roomDTO.setLastUpdate(room.getLastUpdate());
        roomDTO.setUsername(receiver.getUsername());
        roomDTO.setText(messageRepository.findFirstByRoom_IdOrderByMessageDateDesc(room.getId()).getText());
        roomDTO.setFirstName(receiver.getFirstName());
        roomDTO.setLastName(receiver.getLastName());
        return roomDTO;
    }

    public MessageDTO fromMessage(Message message, Long receiverId) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setText(message.getText());
        messageDTO.setMessageDate(message.getMessageDate());
        messageDTO.setSenderId(message.getUser().getId());
        messageDTO.setRoomId(message.getRoom().getId());
        messageDTO.setUsername(message.getUser().getUsername());
        messageDTO.setRead(message.getRead());
        messageDTO.setFirstName(message.getUser().getFirstName());
        messageDTO.setLastName(message.getUser().getLastName());
        messageDTO.setReceiverId(receiverId);
        return messageDTO;
    }

    public List<RoomDTO> findAllRoomsByUser() {
        User user = userService.getCurrentUser();
        return user.getRooms().stream()
                .sorted(Comparator.comparing(Room::getLastUpdate).reversed())
                .map(this::fromRoom)
                .peek(roomDTO -> roomDTO.setUnread(roomHasUnreadMessages(roomDTO, user)))
                .sorted(Comparator.comparing(RoomDTO::getLastUpdate).reversed())
                .collect(Collectors.toList());
    }

    public List<MessageDTO> findAllMessagesByRoomId(String roomId) {
        User user = userService.getCurrentUser();
        Long id = Long.parseLong(roomId);
        Long receiverId = userRepository.findById(userRepository.findByRooms_IdAndUser_Id(id, user.getId())).get().getId();

        List<Room> rooms = user.getRooms().stream()
                .filter(room -> room.getId().equals(id))
                .collect(Collectors.toList());
        if (rooms.isEmpty()) return null;
        else {
            List<Message> messages = messageRepository.findAllByRoom_IdOrderByMessageDate(rooms.get(0).getId());
            messages.forEach(message -> {
                if (!user.getId().equals(message.getUser().getId())) {
                    message.setRead(true);
                    messageRepository.save(message);
                }
            });
            return messages.stream()
                    .map(message -> fromMessage(message, receiverId))
                    .collect(Collectors.toList());
        }
    }

    public MessageDTO receiveMessage(DTOHelper helper) {
        Long id = Long.parseLong(helper.getFirstLine());
        List<Long> ids = new ArrayList<>();

        User currentUser = userService.getCurrentUser();
        User messageReceiver = userService.findById(id);
        ids.add(currentUser.getId());
        ids.add(id);

        Long roomId = roomRepository.getRoomId(ids);
        Room room = null;

        if (roomId != null) {
            room = roomRepository.findById(roomId).orElse(null);
        }

        Message message = new Message();
        message.setMessageDate(new Date());
        message.setUser(currentUser);
        message.setText(helper.getSecondLine());
        message.setRead(false);

        if (room == null) {
            room = new Room();

            room.setLastUpdate(new Date());
            roomRepository.save(room);

            message.setRoom(room);
            messageRepository.save(message);

            currentUser.getRooms().add(room);
            userRepository.save(currentUser);

            messageReceiver.getRooms().add(room);
            userRepository.save(messageReceiver);

        } else {
            message.setRoom(room);
            messageRepository.saveAndFlush(message);
            room.setLastUpdate(new Date());
            roomRepository.saveAndFlush(room);
        }
        return fromMessage(message, id);
    }

    public Boolean hasUnreadMessages() {
        User user = userService.getCurrentUser();
        AtomicReference<Boolean> marker = new AtomicReference<>(false);
        List<RoomDTO> rooms = findAllRoomsByUser();
        for (RoomDTO roomDTO : rooms) {
            marker.set(roomHasUnreadMessages(roomDTO, user));
            if (marker.get().equals(true)) break;
        }
        return marker.get();
    }

    public Boolean roomHasUnreadMessages(RoomDTO roomDTO, User user) {
        Long receiverId = userRepository.findById(userRepository.findByRooms_IdAndUser_Id(roomDTO.getId(), user.getId()))
                .get().getId();
        AtomicReference<Boolean> marker = new AtomicReference<>(false);
        List<MessageDTO> messages = messageRepository.findAllByRoom_IdOrderByMessageDate(roomDTO.getId())
                .stream()
                .map(message -> fromMessage(message, receiverId))
                .filter(messageDTO -> !messageDTO.getRead())
                .filter(messageDTO -> !messageDTO.getSenderId().equals(user.getId()))
                .collect(Collectors.toList());
        if (!messages.isEmpty()) {
            marker.set(true);
        }
        return marker.get();
    }

    public RoomDTO findRoomById(String roomId) {
        User user = userService.getCurrentUser();
        Long id = Long.parseLong(roomId);

        List<Room> rooms = user.getRooms().stream()
                .filter(room -> room.getId().equals(id))
                .collect(Collectors.toList());

        if (!rooms.isEmpty()) {
            RoomDTO room = rooms.stream().map(this::fromRoom).collect(Collectors.toList()).get(0);
            room.setUnread(true);
            return room;
        } else {
            return null;
        }
    }

}
