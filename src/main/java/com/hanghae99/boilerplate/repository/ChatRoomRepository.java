package com.hanghae99.boilerplate.repository;

import com.hanghae99.boilerplate.model.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {


//    private Map<Long, ChatRoom> chatRoomMap;

//    @PostConstruct
//    private void init() {
//        chatRoomMap = new LinkedHashMap<>();
//    }
//
//    public List<ChatRoom> findAllRoom() {
//        // 채팅방 생성순서 최근 순으로 반환
//        List chatRooms = new ArrayList<>(chatRoomMap.values());
//        Collections.reverse(chatRooms);
//        return chatRooms;
//    }
//
//    public ChatRoom findRoomById(String id) {
//        return chatRoomMap.get(id);
//    }
//
//    public ChatRoom createChatRoom(ChatRoomDto chatRoomDto) {
//        ChatRoom chatRoom = new ChatRoom(chatRoomDto);
//        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
//        return chatRoom;
//    }
}