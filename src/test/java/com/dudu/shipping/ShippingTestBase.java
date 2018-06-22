package com.dudu.shipping;

import com.dudu.common.TestBase;
import org.junit.Before;

import java.io.FileInputStream;
import java.util.Properties;

public class ShippingTestBase extends TestBase {

    @Before
    public void setup() {
        super.setup();
        if (ready) {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream("./conf/shipping.conf")) {
                props.load(in);
                Configuration.configure(props);
            } catch (Exception e) {
                println(e.toString());
                ready = false;
            }
        }
    }

}
