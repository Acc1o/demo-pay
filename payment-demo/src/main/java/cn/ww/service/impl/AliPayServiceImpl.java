package cn.ww.service.impl;

import cn.ww.entity.OrderInfo;
import cn.ww.enums.OrderStatus;
import cn.ww.service.AliPayService;
import cn.ww.service.OrderInfoService;
import cn.ww.service.PaymentInfoService;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Su-wenwu
 * @createTime 2022-09-21-13:57
 */
@Service
@Slf4j
public class AliPayServiceImpl implements AliPayService {
    
    @Resource
    private OrderInfoService orderInfoService;

    @Resource
    private PaymentInfoService paymentInfoService;
    
    @Resource
    private AlipayClient alipayClient;
    
    @Resource
    private Environment config;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String tradeCreate(Long productId) {

        try {
            //生成订单
            log.info("生成订单");
            OrderInfo orderInfo = orderInfoService.createOrderByProductId(productId);

            //调用支付宝接口
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            //配置需要的公共参数
            
            request.setNotifyUrl(config.getProperty("alipay.notify-url"));
            request.setReturnUrl(config.getProperty("alipay.return-url"));

            //组装当前业务需要的请求参数
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", orderInfo.getOrderNo());

            BigDecimal total = new BigDecimal(orderInfo.getTotalFee().toString()).divide(new BigDecimal(100));

            bizContent.put("total_amount", total);
            bizContent.put("subject", orderInfo.getTitle());
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");


            request.setBizContent(bizContent.toString());
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if(response.isSuccess()){
                System.out.println("调用成功");
                log.info("调用成功, 返回结果===> {}",response.getBody());
            } else {
                System.out.println("调用失败");
                log.info("调用失败, 状态码===> {}",response.getCode());
                log.info("调用失败,返 回描述===> {}",response.getMsg());
            }
            return response.getBody();
            
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new RuntimeException("创建支付交易失败");
        }
    }

    /**
     * 处理订单
     * @param params
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processOrder(Map<String, String> params) {
        
        log.info("处理订单");
            
        //获取订单号
        String orderNo = params.get("out_trade_no");
        
        //更新订单状态
        orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
        
        //记录支付日志
        paymentInfoService.createPaymentInfoFroAliPay(params);
    }
}
