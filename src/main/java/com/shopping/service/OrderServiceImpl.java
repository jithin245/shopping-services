package com.shopping.service;

import com.google.protobuf.util.Timestamps;
import com.shopping.db.Order;
import com.shopping.db.OrderDao;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
    private OrderDao orderDao = new OrderDao();

    @Override
    public void getOrdersForUser(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        List<Order> orders = orderDao.getOrders(request.getUserId());

        log.info("Got orders from OrderDao and converting to OrderResponse proto object");

        List<com.shopping.stubs.order.Order> protoOrders = orders.stream()
                .map(order -> com.shopping.stubs.order.Order.newBuilder()
                                                        .setUserId(order.getUserId())
                                                        .setOrderId(order.getOrderId())
                                                        .setNoOfItems(order.getNoOfItems())
                                                        .setTotalAmount(order.getTotalAmount())
                                                        .setOrderDate(Timestamps.fromMillis(order.getOrderDate().getTime()))
                                                        .build())
                .collect(Collectors.toList());
        OrderResponse orderResponse = OrderResponse.newBuilder()
                .addAllOrder(protoOrders)
                .build();

        responseObserver.onNext(orderResponse);
        responseObserver.onCompleted();
    }
}
