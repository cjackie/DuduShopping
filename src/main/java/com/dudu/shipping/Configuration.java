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
            throw new IllegalArgumentException("Missing configurations for UPS");

        UPSProxy.configure(upsUrl, upsAccessKey, upsUsername, userPassword);

        String fedexUrl = properties.getProperty("FEDEX_URL");
        String fedexKey = properties.getProperty("FEDEX_KEY");
        String fedexPassword = properties.getProperty("FEDEX_PASSWORD");
        String fedexAccountNumber = properties.getProperty("FEDEX_ACCOUNT_NUMBER");
        String fedexMeterNumber = properties.getProperty("FEDEX_METER_NUMBER");

        if (fedexUrl == null || fedexKey == null || fedexPassword == null || fedexAccountNumber == null || fedexMeterNumber == null )
            throw new IllegalArgumentException("Missing configuration for FEDEX");

        FedexProxy.configure(fedexUrl, fedexKey, fedexPassword, fedexAccountNumber, fedexMeterNumber);
    }
}
