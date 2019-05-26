package com.chimauwah.aws.websocket.shared.datasource;

import java.util.Set;

public interface DatasourceResource {

    Set<String> getAll() throws Exception;

    void insert(String connectionId) throws Exception;

    void delete(String connectionId) throws Exception;

}