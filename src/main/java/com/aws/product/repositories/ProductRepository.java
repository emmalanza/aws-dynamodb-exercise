package com.aws.product.repositories;


import com.aws.product.models.Product;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ProductRepository {

    private final DynamoDbClient dynamoDbClient;
    private static final String TABLE_NAME = "Product";

    public ProductRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void save(Product product) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.fromS(product.getId()));
        item.put("name", AttributeValue.fromS(product.getName()));
        item.put("price", AttributeValue.fromN(product.getPrice().toString()));

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    public Product findById(String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.fromS(id));

        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .build();

        Map<String, AttributeValue> item = dynamoDbClient.getItem(request).item();
        if (item == null || item.isEmpty()) return null;

        return Product.builder()
                .id(item.get("id").s())
                .name(item.get("name").s())
                .price(Double.parseDouble(item.get("price").n()))
                .build();
    }
}