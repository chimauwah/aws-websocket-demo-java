package com.chimauwah.aws.websocket.shared.config;


/**
 * ENUM used by the Dababase class to hold configuration details.
 */
public enum ConfigurationHolder {
    instance;

    private Configuration configuration;

    /**
     * Constructor that loads the DB configuration YAML
     */
    ConfigurationHolder() {
        load();
    }

    /**
     * Populate datasource properties from environment, currently listed in the template.yml
     */
    private void load() {
        Configuration.DataSourceProperties dataSourceProperties = new Configuration.DataSourceProperties();
        dataSourceProperties.setUrl(System.getenv("DATASOURCE_URL"));
        dataSourceProperties.setDriverClassName(System.getenv("DATASOURCE_CLASS"));
        dataSourceProperties.setUsername(System.getenv("DATASOURCE_USERNAME"));
        dataSourceProperties.setPassword(System.getenv("DATASOURCE_PASSWORD"));
        configuration = new Configuration();
        configuration.setDataSourceProperties(dataSourceProperties);
    }

    /**
     * Getter for the Configuration POJO
     *
     * @return the configuration object
     */
    public Configuration configuration() {
        return this.configuration;
    }
}
