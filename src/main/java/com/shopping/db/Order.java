package com.shopping.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    private int userId;
    private int orderId;
    private int noOfItems;
    private double totalAmount;
    private Date orderDate;
}
