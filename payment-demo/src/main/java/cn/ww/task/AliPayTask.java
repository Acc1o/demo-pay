package cn.ww.task;

import cn.ww.entity.OrderInfo;
import cn.ww.enums.PayType;
import cn.ww.service.AliPayService;
import cn.ww.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Su-wenwu
 * @createTime 2022-09-28-11:33
 */

@Slf4j
@Component
public class AliPayTask {

    @Resource
    private OrderInfoService orderInfoService;
    
    @Resource
    private AliPayService aliPayService;

    /**
     * 从第0秒开始每隔30秒执行1次，查询创建超过1分钟，并且未支付的订单
     */
    //@Scheduled(cron = "0/30 * * * * ?")
    public void orderConfirm(){
        log.info("orderConfirm 被执行");

        List<OrderInfo> orderInfoList = orderInfoService.getNoPayOrderByDuration(1, PayType.ALIPAY.getType());

        for (OrderInfo orderInfo : orderInfoList) {
            String orderNo = orderInfo.getOrderNo();
            log.warn("超时订单 ===> {}", orderNo);

            //核实订单状态：调用微信支付查单接口
            aliPayService.checkOrderStatus(orderNo);
        }
    }
}
