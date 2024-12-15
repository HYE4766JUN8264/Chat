package common;

public class ChatProtocol {
    public static final String JOIN = "JOIN";
    public static final String MESSAGE = "MESSAGE";
    public static final String LEAVE = "LEAVE";

    public static String formatMessage(String type, String content) {
        return type + ":" + content;
    }
}