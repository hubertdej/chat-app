package com.chat.server.infrastructure

import com.chat.server.infrastructure.websocket.Message
import lombok.AccessLevel
import lombok.AllArgsConstructor
import lombok.experimental.FieldDefaults
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandler
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.Transport
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import spock.lang.Specification

import java.lang.reflect.Type
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.fail

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
                properties = ["server.port = 8089"])
class InfrastructureIntegrationSpec extends Specification {

    private int port = 8089;

    private SockJsClient sockJsClient;

    private WebSocketStompClient stompClient;

    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    void setup() {
        List<Transport> transports = new ArrayList<>()
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        this.sockJsClient = new SockJsClient(transports);

        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }


    def "user receives public chat messages after subscribing"(){
        given: "user connected and connnected to server and subscribed to /topic/public"
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        StompSessionHandler handler = new TestSessionHandler("/server/public", "topic/public", "hello", failure, latch)

        this.stompClient.connect("ws://localhost:{port}/server-stomp", this.headers, handler, this.port)

        if (latch.await(3, TimeUnit.SECONDS)) {
            if (failure.get() != null) {
                throw new AssertionError("", failure.get())
            }
        }
        else {
            fail("message not received")
        }
    }

    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    class TestSessionHandler extends StompSessionHandlerAdapter {
        String messageDestination
        String topicToSubscribe
        String expectedContents
        AtomicReference<Throwable> failure
        CountDownLatch latch;

        TestSessionHandler(String messageDestination, String topicToSubscribe, String expectedContents, AtomicReference<Throwable> failure, CountDownLatch latch) {
            this.messageDestination = messageDestination
            this.topicToSubscribe = topicToSubscribe
            this.expectedContents = expectedContents
            this.failure = failure
            this.latch = latch
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            this.failure.set(new Exception(headers.toString()))
        }

        @Override
        public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
            this.failure.set(ex)
        }

        @Override
        public void handleTransportError(StompSession session, Throwable ex) {
            this.failure.set(ex)
        }

        @Override
        public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
            session.subscribe(topicToSubscribe, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return Message.class
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    Message message= (Message) payload
                    try {
                        assertEquals(expectedContents, message.getContent())
                    } catch (Throwable t) {
                        failure.set(t)
                    } finally {
                        session.disconnect()
                        latch.countDown()
                    }
                }
            })
            try {
                session.send(messageDestination, new Message(expectedContents))
            } catch (Throwable t) {
                failure.set(t)
                latch.countDown()
            }
        }
    }

}
