package cn.ww.service;

import java.util.Map;

public interface PaymentInfoService {

    void createPaymentInfo(String plainText);

    void createPaymentInfoFroAliPay(Map<String, String> params);
}
