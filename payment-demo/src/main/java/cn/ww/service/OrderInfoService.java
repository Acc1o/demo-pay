package cn.ww.service;



import cn.ww.entity.OrderInfo;
import cn.ww.enums.OrderStatus;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface OrderInfoService extends IService<OrderInfo> {

    OrderInfo createOrderByProductId(Long productId, String paymentType);

    void saveCodeUrl(String orderNo, String codeUrl);

    List<OrderInfo> listOrderByCreateTimeDesc();

    void updateStatusByOrderNo(String orderNo, OrderStatus orderStatus);

    String getOrderStatus(String orderNo);

    List<OrderInfo> getNoPayOrderByDuration(int minutes, String paymentType);

    OrderInfo getOrderByOrderNo(String orderNo);
}
