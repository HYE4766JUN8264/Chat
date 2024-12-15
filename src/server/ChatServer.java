package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private Set<Socket> clientSockets = new HashSet<>(); // 클라이언트 소켓 관리합니다.
    private Map<Socket, String> clientNames = new HashMap<>(); // 클라이언트 이름 관리합니다.
    private Map<String, Set<Socket>> chatRooms = new HashMap<>(); // 서버가 여러 개의 채팅방을 관리하도록 확장합니다.

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

                new Thread(() -> handleClient(clientSocket)).start(); // 새로운 스레드에서 클라이언트와의 통신 처리
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    1. 다중 클라이언트 브로드캐스트 기능
    현재 서버는 에코 방식(서버가 받은 메시지를 클라이언트에 그대로 반환)입니다.
    이를 개선해 모든 클라이언트에게 메시지 브로드캐스트를 추가하세요.
     */

    private void broadcastMessage(String message) {
        logMessage(message); // 로그 저장하여 디버깅을 수행합니다.
        for (Socket client : clientSockets) {
            try (PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
                out.println(message);
            } catch (IOException e) {
                System.out.println("메시지 브로드캐스트 중 오류 발생: " + e.getMessage());
            }
        }
    }

    /*
    2. 클라이언트 목록 관리
    서버에서 현재 접속 중인 클라이언트 목록을 유지하고 이를 요청할 수 있도록 합니다.
     */

    public synchronized void broadcastClientList() {
        String clients = String.join(", ", clientNames.values());
        broadcastMessage("현재 접속 중인 사용자: " + clients);
    }

    /*
    3. 채팅 로그 저장
    서버에서 주고받은 메시지를 로그 파일에 저장합니다:
     */

    private void sendMessage(Socket client, String message) {
        try {
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            System.out.println("클라이언트에게 메시지 전송 중 오류 발생: " + e.getMessage());
        }
    }

    private void logMessage(String message) {
        try (FileWriter fw = new FileWriter("chat_log.txt", true)) {
            fw.write(message + "\n");
        } catch (IOException e) {
            System.out.println("로그 저장 중 오류 발생: " + e.getMessage());
        }
    }

    /*
    4. 멀티 채팅방 개설 기능
    서버가 여러 개의 채팅방(Room)을 관리하도록 확장합니다:
    채팅방 생성/삭제 및 라이언트가 특정 채팅방에 참가할 수 있도록 구현합니다.
    */

    public synchronized void createRoom(String roomName) {
        chatRooms.putIfAbsent(roomName, new HashSet<>());
        System.out.println("채팅방 생성: " + roomName);
    }

    public synchronized void joinRoom(String roomName, Socket clientSocket) {
        if (!chatRooms.containsKey(roomName)) { // 채팅방 존재 여부를 확인하고, 없는 경우 적절한 메시지를 반환합니다.
            out.println("존재하지 않는 채팅방입니다: " + roomName);
            return;
        } else {
            chatRooms.get(roomName).add(clientSocket);
            sendMessage(clientSocket, "채팅방 [" + roomName + "]에 입장했습니다.");
            broadcastMessage("[" + clientNames.get(clientSocket) + "] 님이 채팅방 [" + roomName + "]에 입장했습니다.");
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                out.println("닉네임을 입력하세요:"); // 클라이언트가 서버에 연결할 때 닉네임을 설정하도록 수정합니다.
                String nickname = in.readLine();
                clientNames.put(socket, nickname);
                broadcastMessage("[" + nickname + "] 님이 입장하셨습니다!");

                String input;
                while ((input = in.readLine()) != null) {
                    if (input.startsWith(ChatProtocol.CREATE_ROOM)) {
                        String roomName = input.split(":", 2)[1];
                        createRoom(roomName);
                    } else if (input.startsWith(ChatProtocol.JOIN_ROOM)) {
                        String roomName = input.split(":", 2)[1];
                        joinRoom(roomName, socket);
                    } else {
                        broadcastMessage("[" + nickname + "]: " + input);
                    }
                }
            } catch (IOException e) {
                System.out.println("클라이언트 연결 종료: " + e.getMessage());
            } finally {
                clientSockets.remove(socket);
                clientNames.remove(socket);
                broadcastMessage("[" + clientNames.get(socket) + "] 님이 나가셨습니다.");
            }
        }
    }
}
