package cn.ww.service;

import java.util.Map;

/**
 * @author Su-wenwu
 * @createTime 2022-09-21-13:57
 */
public interface AliPayService {

    String tradeCreate(Long productId);

    void processOrder(Map<String, String> params);
}
