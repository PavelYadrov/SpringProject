package com.netcracker.controllers.users;

import com.netcracker.dto.DTOHelper;
import com.netcracker.dto.MessageDTO;
import com.netcracker.dto.RoomDTO;
import com.netcracker.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/user/")
public class UserChatController {

    private ChatService chatService;

    @Autowired
    public UserChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("getUserRooms")
    public ResponseEntity<List<RoomDTO>> getUserRoomsById() {
        return ResponseEntity.ok(chatService.findAllRoomsByUser());
    }

    @PostMapping("getRoomMessages")
    public ResponseEntity<List<MessageDTO>> getMessagesByRoomId(@RequestBody String id) {
        return ResponseEntity.ok(chatService.findAllMessagesByRoomId(id));
    }

    @PostMapping("receiveMessage")
    public ResponseEntity<MessageDTO> receiveMessage(@RequestBody DTOHelper helper) {
        return ResponseEntity.ok(chatService.receiveMessage(helper));
    }

    @GetMapping("hasUnreadMessages")
    public ResponseEntity<Boolean> hasUnreadMessages() {
        return ResponseEntity.ok(chatService.hasUnreadMessages());
    }

    @PostMapping("getRoomById")
    public ResponseEntity<RoomDTO> getRoomById(@RequestBody String id) {
        return ResponseEntity.ok(chatService.findRoomById(id));
    }

    @PostMapping("setRead")
    public ResponseEntity<String> setRead(@RequestBody String id) {
        chatService.setMessageRead(id);
        return ResponseEntity.ok("Read set");
    }

}
