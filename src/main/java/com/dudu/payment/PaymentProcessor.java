package com.dudu.payment;

/**
 * Created by Chaojie (Jack) Wang on 5/17/18.
 */
interface PaymentProcessor {
    void charge(String cardId, double amt) throws Exception;
}
