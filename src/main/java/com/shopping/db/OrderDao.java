package com.shopping.db;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OrderDao {

    public List<Order> getOrders(int userId) {
        Connection connection = null;
        List<Order> orderList = new ArrayList<>();

        try {
            connection = H2DatabaseConnection.getConnectionToDatabase();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("select * from orders where user_id=?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                orderList.add(
                        Order.builder()
                            .orderId(resultSet.getInt("order_id"))
                            .userId(resultSet.getInt("user_id"))
                            .noOfItems(resultSet.getInt("no_of_items"))
                            .totalAmount(resultSet.getDouble("total_amount"))
                            .orderDate(resultSet.getDate("order_date"))
                            .build());
            }
        } catch (SQLException exception) {
            log.error("Could execute the query " + exception);
        }

        return orderList;
    }
}
