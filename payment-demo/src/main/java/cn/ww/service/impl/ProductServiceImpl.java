package cn.ww.service.impl;


import cn.ww.entity.Product;
import cn.ww.mapper.ProductMapper;
import cn.ww.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

}
