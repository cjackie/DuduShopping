package com.dudu.shipping;

import java.util.Properties;

/**
 * single source of configurations for this package.
 */
public class Configuration {

    /**
     *
     * @param properties
     */
    public static void configure(Properties properties) throws Exception {
        String upsUrl = properties.getProperty("UPS_URL");
        String upsAccessKey = properties.getProperty("UPS_ACCESS_KEY");
        String upsUsername = properties.getProperty("UPS_USERNAME");
        String userPassword = properties.getProperty("UPS_PASSWORD");

        if (upsUrl == null || upsAccessKey == null || upsUsername == null || userPassword == null)
            throw new IllegalStateException("Missing configurations for UPS");

        UPSProxy.configure(upsUrl, upsAccessKey, upsUsername, userPassword);
    }
}
