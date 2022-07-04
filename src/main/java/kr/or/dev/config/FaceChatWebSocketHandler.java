package kr.or.dev.config;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Component
public class FaceChatWebSocketHandler extends TextWebSocketHandler {

    // 연결된 peer에 이름주기 (peer id)
    private static Map<String, WebSocketSession> sessions = new HashMap<String, WebSocketSession>();

    // 서로 연결된 peer들 저장 (서로간 연결 성립시)
    private static Map<WebSocketSession, WebSocketSession> sessionConnections = new HashMap<WebSocketSession, WebSocketSession>();

    public FaceChatWebSocketHandler() {
        System.out.println("FaceChatWebSocketHandler 생성됨...");
    }

    // json To map
    private static Map<String, Object> jsonStrToMap(String jsonStr) {
        System.out.println("요청 메세지 : " + jsonStr);
        JSONObject json = new JSONObject(jsonStr.replace("\n", ""));
        return json.toMap();
    }

    // map To json
    private static String mapToJsonStr(Map<String, Object> map) {
        JSONObject json = new JSONObject();
        for(String key : map.keySet()){
            Object val = map.get(key);
            json.put(key, val);
        }
        return json.toString();
    }

    // get name By session
    private static String getNameBySession(WebSocketSession session) {
        for(String key : sessions.keySet()){
            WebSocketSession val = sessions.get(key);
            if(val == session) return key;
        }
        return null;
    }

    // connection 삭제 (peer와 연결해제)
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(getNameBySession(session));
        System.out.println("remove session!");
    }

    // connection 생성 (peer가 접속함)
    @Override
    public void afterConnectionEstablished(WebSocketSession session)
            throws Exception {
        sessions.put(session.getId(), session);
        System.out.println("add session!");
    }

    /*
     * 요청 메시지 핸들링
     * 메시지는 json 타입
     * 메세지의 type값 : 요청타입을 의미
     * 메세지의 name값 : type이 login, leave일 경우 >> 자신, 아닐경우 >> 상대방 (내가 연결할)
     * 메세지의 offer, answer 값 : sdp를 의미
     * 메세지의 candidate 값 : candidate 의미 (통신을 위한 서로간 프로토콜 옵션 동기화 데이터)
     * 메세지의 leave 값 : 로그아웃을 의미 (세션제거)
     */
    @Override
    public void handleMessage(WebSocketSession session,
                              WebSocketMessage<?> message) throws Exception {

        // 요청 데이터
        Map<String, Object> data = jsonStrToMap(message.getPayload().toString());
        String type = (String) data.get("type");
        String name = (String) data.get("name");


        // 응답 데이터
        Map<String, Object> result = new HashMap<String, Object>();


        // login
        if(type.equals("login")){
            System.out.println("사용자 로그인됨 : " + name);
            result.put("type", "login");
            if(sessions.containsKey(name)){
                result.put("success", "false");
            } else {
                sessions.put(name, session);
                result.put("success", "true");
            }
            session.sendMessage(new TextMessage(mapToJsonStr(result)));
        } else


            // offer
            if(type.equals("offer")){
                String myName = getNameBySession(session);
                System.out.println(myName + "이/가 연결요청함, 상대방 : " + name);
                Object offer = data.get("offer");
                System.out.println("Offer data : " + offer);
                WebSocketSession target = sessions.get(name);
                if(target != null) {
                    sessionConnections.put(session, target);
                    result.put("type", "offer");
                    result.put("offer", offer);
                    result.put("name", myName);
                    TextMessage response = new TextMessage(mapToJsonStr(result));
                    System.out.println("연결요청 데이터 보냄 : " + response);
                    target.sendMessage(response);
                }
            } else

                // answer
                if(type.equals("answer")){
                    System.out.println("응답요청함, 상대방 : " + name);
                    Object answer = data.get("answer");
                    WebSocketSession target = sessions.get(name);
                    if(target != null) {
                        sessionConnections.put(session, target);
                        result.put("type", "answer");
                        result.put("answer", answer);
                        target.sendMessage(new TextMessage(mapToJsonStr(result)));
                    }
                } else

                    // candidate
                    if(type.equals("candidate")){
                        System.out.println("Sending candidate to : " + name);
                        Object candidate = data.get("candidate");
                        WebSocketSession target = sessions.get(name);
                        if(target != null) {
                            result.put("type", "candidate");
                            result.put("candidate", candidate);
                            try {
                                synchronized (target) {
                                    target.sendMessage(new TextMessage(mapToJsonStr(result)));
                                }
                            } catch(Exception e) {

                            }
                        }
                    } else

                        // leave
                        if(type.equals("leave")) {
                            System.out.println("Disconnecting from : " + name);
                            WebSocketSession target = sessions.get(name);
                            sessionConnections.put(target, null);
                            if(target != null){
                                result.put("type", "leave");
                                target.sendMessage(new TextMessage(mapToJsonStr(result)));
                            }
                        } else

                        // message error
                        {
                            result.put("type", "error");
                            result.put("message", "Command not found: " + type);
                            session.sendMessage(new TextMessage(mapToJsonStr(result)));
                        }


    }


    // socket error
    @Override
    public void handleTransportError(WebSocketSession session,
                                     Throwable exception) throws Exception {
        System.out.println("소켓 에러남!!" + exception);
    }



    // echo
    public void sendMessage(String message) {
        // 모든 connection에 보내기
        for(String key : sessions.keySet()){
            WebSocketSession session = sessions.get(key);
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (Exception ignored) {
                    System.out.println("fail to send message! " + ignored);
                }
            }
        }

//		for (WebSocketSession session : this.sessions) {
//			if (session.isOpen()) {
//				try {
//					session.sendMessage(new TextMessage(message));
//				} catch (Exception ignored) {
//					this.logger.error("fail to send message!", ignored);
//				}
//			}
//		}
    }
}
