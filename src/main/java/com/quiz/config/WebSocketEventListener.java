package com.quiz.config;

import com.quiz.controllers.GameController;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RequiredArgsConstructor
public class WebSocketEventListener {
    private final GameController gameController;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        int [] userId = (int[]) headerAccessor.getSessionAttributes().get("userId");
        gameController.handleUserDisconnection(userId[0], userId[1]);
    }
}
