package client;

import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "127.0.0.1"; // 서버 주소
    private static final int SERVER_PORT = 12345; // 서버 포트 번호

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("서버에 연결되었습니다: " + SERVER_ADDRESS + ":" + SERVER_PORT);

            // 사용자 입력 및 서버와 통신
            String input;
            while ((input = userInput.readLine()) != null) {
                System.out.println("사용자 입력: " + input);
                out.println(input); // 서버로 메시지 전송
                System.out.println("서버 응답: " + in.readLine()); // 서버로부터 응답 수신
            }
        } catch (IOException e) {
            System.out.println("서버와 연결 중 오류 발생: " + e.getMessage());
        }
    }
}
