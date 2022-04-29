package com.shopping.service;

import com.shopping.client.OrderClient;
import com.shopping.db.User;
import com.shopping.db.UserDao;
import com.shopping.stubs.order.Order;
import com.shopping.stubs.user.Gender;
import com.shopping.stubs.user.UserRequest;
import com.shopping.stubs.user.UserResponse;
import com.shopping.stubs.user.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private UserDao userDao = new UserDao();

    @Override
    public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        User user = userDao.getUserDetails(request.getUsername());

        UserResponse.Builder userResponseBuilder =
                UserResponse.newBuilder()
                        .setId(user.getId())
                        .setUsername(user.getUsername())
                        .setName(user.getName())
                        .setAge(user.getAge())
                        .setGender(Gender.valueOf(user.getGender()));

        List<Order> orders = getOrders(userResponseBuilder);

        userResponseBuilder.setNoOfOrders(orders.size());
        UserResponse userResponse = userResponseBuilder.build();

        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }

    /**
     * get orders by invoking order client.
     * @param userResponseBuilder
     * @return
     */
    private List<Order> getOrders(UserResponse.Builder userResponseBuilder) {
        log.info("Creating a channel and calling the Order Client");
        ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("localhost:50052")
                                                        .usePlaintext()
                                                        .build();
        OrderClient orderClient = new OrderClient(managedChannel);
        List<Order> orders = orderClient.getOrders(userResponseBuilder.getId());
        try {
            managedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            log.error("Channel didn't shutdown " + exception);
        }
        return orders;
    }
}
