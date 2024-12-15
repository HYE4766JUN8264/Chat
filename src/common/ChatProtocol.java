package common;

public class ChatProtocol {
    public static final String CREATE_ROOM = "CREATE_ROOM"; // 채팅방을 개설합니다.
    public static final String JOIN_ROOM = "JOIN_ROOM"; // 채팅방에 입장합니다.
    public static final String LEAVE_ROOM = "LEAVE_ROOM"; // 현재 머물고 있는 채팅방에서 나갑니다.

    public static String formatMessage(String type, String sender, String content) {
        return String.format("{\"type\":\"%s\",\"sender\":\"%s\",\"content\":\"%s\"}", type, sender, content);
    }
}