package cn.ww.service;

import java.util.Map;

/**
 * @author Su-wenwu
 * @createTime 2022-09-21-13:57
 */
public interface AliPayService {

    String tradeCreate(Long productId);

    void processOrder(Map<String, String> params);

    void cancelOrder(String orderNo);

    String queryOrder(String orderNo);

    void checkOrderStatus(String orderNo);

    void refund(String orderNo, String reason);

    String queryRefund(String orderNo);

    String queryBill(String billDate, String type);
}
