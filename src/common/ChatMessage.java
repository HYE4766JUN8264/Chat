package common; // 서버와 클라이언트의 공통 사용 코드, 데이터 모델과 통신 프로토콜 구현

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private String sender;
    private String content;

    public ChatMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return sender + ": " + content;
    }
}

