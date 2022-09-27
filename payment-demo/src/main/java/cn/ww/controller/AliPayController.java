package cn.ww.controller;

import cn.ww.entity.OrderInfo;
import cn.ww.service.AliPayService;
import cn.ww.service.OrderInfoService;
import cn.ww.vo.R;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Su-wenwu
 * @createTime 2022-09-21-13:52
 */

@CrossOrigin    
@RestController
@RequestMapping("/api/ali-pay")
@Api(tags = "网站支付宝支付")
@Slf4j
public class AliPayController {


    @Resource
    private AliPayService aliPayService;

    @Resource
    private Environment config;
    
    @Resource
    private OrderInfoService orderInfoService;


    @ApiOperation("统一收单并支付页面接口的调用")
    @PostMapping("/trade/page/pay/{productId}")
    public R tradePagePay(@PathVariable Long productId){

        log.info("统一收单并支付页面接口的调用");
        /**
         * 支付宝开放平台接受request请求
         * 会为开发者生成一个html 形式的 form表单 包含自动提交脚本
         */
        String formStr= aliPayService.tradeCreate(productId);
        /**
         * 将form表单返回给前端 前端将调用 自动提交脚本
         * 表单会自动提交到action属性指向的支付宝开放平台 从而生成支付页面
        */
        return R.ok().data("formStr",formStr);
    }

    @ApiOperation("支付通知")
    @PostMapping("/trade/notify")
    public String tradeNotify(@RequestParam Map<String, String> params){
        log.info("支付通知正在执行");
        log.info("通知参数 ===>{}",params);
        
        String result= "failure";

        try {
            //异步通知验签
            boolean signVerified= AlipaySignature.rsaCheckV1(
                        params, config.getProperty("alipay.alipay-public-key"), AlipayConstants.CHARSET_UTF8,AlipayConstants.SIGN_TYPE_RSA2);

            //调用SDK验证签名
            if(!signVerified){
    
                // 验签失败则记录异常日志，并在response中返回failure.
                log.error("只付成功,异步通知验前失败");
                
                return result;
               
            }

            // 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
            log.info("支付成功,异步通知验签成功");

            //1.商家需要验证该通知数据中的 out_trade_no 是否为商家系统中创建的订单号。
            String out_trade_no = params.get("out_trade_no");
            OrderInfo order = orderInfoService.getOrderByOrderNo(out_trade_no); 
            if(order == null){
                log.error("订单校验失败");
                return result;
            }
            
            //2.判断 total_amount 是否确实为该订单的实际金额（即商家订单创建时的金额）。
            String totalAmount = params.get("total_amount");
            int totalAmountInt = new BigDecimal(totalAmount).multiply(new BigDecimal("100")).intValue();
            int totalFee = order.getTotalFee();
            if(totalAmountInt != totalFee){
                log.error("金额校验失败");
                return result;
            }
            
            //3.校验通知中的 seller_id（或者 seller_email) 是否为 out_trade_no 这笔单据的对应的操作方（有的时候，一个商家可能有多个 seller_id/seller_email）
            String seller_id = params.get("seller_id");
            String sellerIdProperty = config.getProperty("alipay.seller-id");
            if(!seller_id.equals(sellerIdProperty)){
                log.error("商家Pid校验失败");
                return result;
            }
            
            //4.验证 app_id 是否为该商家本身。
            String app_id = params.get("app_id");
            String appIdProperty = config.getProperty("alipay.app-id");
            if(!app_id.equals(appIdProperty)){
                log.error("app-id校验失败");
                return result;
            }
            
            //在支付宝的业务通知中，只有交易通知状态为 TRADE_SUCCESS 或 TRADE_FINISHED 时，支付宝才会认定为买家付款成功。
            String trade_status = params.get("trade_status");
            if(!"TRADE_SUCCESS".equals(trade_status)){
                log.error("支付失败");
                return result;
            }
            
            //处理业务 修改订单状态 记录支付日志
            aliPayService.processOrder(params);
                    
            result = "success";
            
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return result;
    }
}
