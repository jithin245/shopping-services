package com.shopping.server;

import com.shopping.service.UserServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class UserServer {

    private Server server;

    public void startServer() {
        int port = 50051;
        try {
            server = ServerBuilder.forPort(port)
                            .addService(new UserServiceImpl())
                            .build()
                            .start();
            log.info("User Server started on port " + port);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Clean User server shutdown incase JVM was shutdown");
                try {
                    UserServer.this.stopServer();
                } catch (InterruptedException exception) {
                    log.error("User Server shutdown interrupted " + exception);
                }
            }));
        } catch (IOException exception) {
            log.error("User Server didn't start " + exception);
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
        UserServer userServer = new UserServer();
        userServer.startServer();
        userServer.blockUntilShutdown();
    }
}
