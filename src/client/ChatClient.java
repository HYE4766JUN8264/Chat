package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;
public class ChatClient {

    private static final String SERVER_ADDRESS = "127.0.0.1"; // 서버 주소를 지정합니다.
    private static final int SERVER_PORT = 12345; // 서버 포트 번호를 지정합니다.
    public static void main(String[] args) {
        new ChatClient().start();
    }

    public void start() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println(in.readline()); // 서버에서 닉네임 요청 시 메세지를 출력합니다.
            String nickname = scanner.nextLine();
            out.println("서버에 연결되었습니다: " + SERVER_ADDRESS + ":" + SERVER_PORT);

            /*
            1. 서버 응답 처리 스레드화
            현재는 사용자 입력과 서버 응답 처리가 동일한 스레드에서 실행됩니다.
            이를 분리하여 서버 응답 처리를 별도 스레드에서 실행하는 것이 효율적입니다.
             */

            new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    System.out.println("서버와의 연결이 종료되었습니다.");
                }
            }).start();

            // 사용자 입력 및 서버와 통신합니다.
            while (scanner.hasNextLine()) {
                out.println(scanner.nextLine());
            }
        } catch (IOException e) {
            System.out.println("서버와 연결 중 오류 발생: " + e.getMessage());
        }
    }
}
