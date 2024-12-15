package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private Set<Socket> clientSockets = new HashSet<>(); // 클라이언트 소켓 관리

    public static void main(String[] args) {
        new ChatServer().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("서버가 시작되었습니다. 포트: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);
                System.out.println("새 클라이언트 연결: " + clientSocket.getInetAddress());

                // 새로운 스레드에서 클라이언트와의 통신 처리
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            System.out.println("클라이언트 처리 스레드 시작: " + clientSocket.getInetAddress());

            String input;
            while ((input = in.readLine()) != null) {
                System.out.println("클라이언트로부터 수신: " + input);
                out.println("서버로부터 응답: " + input); // 클라이언트에 메시지 응답
            }

            System.out.println("클라이언트 연결 종료: " + clientSocket.getInetAddress());
        } catch (IOException e) {
            System.out.println("클라이언트 처리 중 오류 발생: " + e.getMessage());
        }
    }
}
