package com.dudu.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chaojiewang on 4/29/18.
 */
public class CryptoUtilTest {

    @Test
    public void test() {
        String text = "text123";
        String hashed = CryptoUtil.sha256base64(text);
        String correct = "G5aQs1BAznuVteR3ym9CyrHlBQdCLWznzsZDqr/1KiQ=";
        Assert.assertEquals(correct, hashed);
    }

    @Test
    public void play() {
        String text = "test123";
        System.out.println(CryptoUtil.sha256base64(text));
    }
}
