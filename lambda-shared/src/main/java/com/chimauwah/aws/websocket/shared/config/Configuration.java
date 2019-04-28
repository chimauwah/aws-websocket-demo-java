package com.chimauwah.aws.websocket.shared.config;

import lombok.Data;

/**
 * POJO for the Database class
 */
@Data
public class Configuration {

    private DataSourceProperties dataSourceProperties;

    /**
     * POJO for Database class
     */
    @Data
    public static final class DataSourceProperties {
        private String driverClassName;
        private String url;
        private String username;
        private String password;
    }
}
