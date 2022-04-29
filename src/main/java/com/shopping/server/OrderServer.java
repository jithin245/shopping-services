package com.shopping.server;

import com.shopping.service.OrderServiceImpl;
import com.shopping.service.UserServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OrderServer {

    private Server server;

    public void startServer() {
        int port = 50052;
        try {
            server = ServerBuilder.forPort(port)
                            .addService(new OrderServiceImpl())
                            .build()
                            .start();
            log.info("Order Server started on port " + port);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Clean Order server shutdown incase JVM was shutdown");
                try {
                    OrderServer.this.stopServer();
                } catch (InterruptedException exception) {
                    log.error("Order Server shutdown interrupted " + exception);
                }
            }));
        } catch (IOException exception) {
            log.error("Order Server didn't start " + exception);
        }
    }

    public void stopServer() throws InterruptedException {
        if (Objects.nonNull(server)) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (Objects.nonNull(server)) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        OrderServer orderServer = new OrderServer();
        orderServer.startServer();
        orderServer.blockUntilShutdown();
    }
}
