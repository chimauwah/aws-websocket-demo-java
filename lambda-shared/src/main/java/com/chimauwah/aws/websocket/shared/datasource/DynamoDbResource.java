package com.chimauwah.aws.websocket.shared.datasource;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Dynamodb Resource for accessing table and items.
 */
public class DynamoDbResource implements DatasourceResource {

    private DynamoDbClient dynamoDb;
    private String DYNAMODB_TABLE_NAME = "connections";
    private String DYNAMODB_PRIMARY_KEY = "connectionId";

    public DynamoDbResource() {
        this.dynamoDb = DynamoDbClient.builder().build();
    }

    public Set<String> getAll() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(DYNAMODB_TABLE_NAME)
                .build();
        ScanResponse scanResponse = dynamoDb.scan(scanRequest);
        List<Map<String, AttributeValue>> items = scanResponse.items();
        return items.stream()
                .map(stringAttributeValueMap -> stringAttributeValueMap.get("connectionId").s())
                .collect(Collectors.toSet());

    }

    public void insert(String connectionId) {
        AttributeValue value = AttributeValue.builder()
                .s(connectionId)
                .build();
        Map<String, AttributeValue> itemToInsert = new HashMap<>();
        itemToInsert.put(DYNAMODB_PRIMARY_KEY, value);
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(DYNAMODB_TABLE_NAME)
                .item(itemToInsert)
                .build();
        dynamoDb.putItem(putItemRequest);
    }

    public void delete(String connectionId) {
        AttributeValue value = AttributeValue.builder()
                .s(connectionId)
                .build();
        Map<String, AttributeValue> itemToDelete = new HashMap<>();
        itemToDelete.put(DYNAMODB_PRIMARY_KEY, value);
        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .tableName(DYNAMODB_TABLE_NAME)
                .key(itemToDelete)
                .build();
        dynamoDb.deleteItem(deleteItemRequest);
    }

}
