package cn.ww;

import cn.ww.config.WxPayConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.security.PrivateKey;

@SpringBootTest
class PaymentDemoApplicationTests {

    @Resource
    WxPayConfig wxPayConfig;

    /**
     * 获取商户信息
     */

   /* @Test
    void toGetMchId() {
        //获取私钥路径
        String privateKeyPath = wxPayConfig.getPrivateKeyPath();
        //获取私钥
        PrivateKey privateKey = wxPayConfig.getPrivateKey(privateKeyPath);

        System.out.println(privateKey);
    }*/

}
