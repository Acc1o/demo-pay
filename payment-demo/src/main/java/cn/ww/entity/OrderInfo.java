package cn.ww.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Su-wenwu
 * @createTime 2022-09-16-15:25
 */

@Data
@TableName("t_order_info")
public class OrderInfo  extends BaseEntity{

    private String title;//订单标题

    private String orderNo;//商户订单编号

    private Long userId;//用户id

    private Long productId;//支付产品id

    private String paymentType;//支付方式

    private Integer totalFee;//订单金额(分)

    private String codeUrl;//订单二维码连接

    private String orderStatus;//订单状态
}
