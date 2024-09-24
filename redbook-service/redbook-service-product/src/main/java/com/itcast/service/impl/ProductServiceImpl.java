package com.itcast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itcast.mapper.ProductBrowseMapper;
import com.itcast.mapper.ProductMapper;
import com.itcast.mapper.ShopMapper;
import com.itcast.product.dto.ProductDto;
import com.itcast.product.pojo.Product;
import com.itcast.product.pojo.ProductAttribute;
import com.itcast.product.pojo.ProductBrowse;
import com.itcast.product.pojo.Shop;
import com.itcast.product.vo.ProductVo;
import com.itcast.result.Result;
import com.itcast.service.ProductService;
import com.itcast.thread.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ProductBrowseMapper productBrowseMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Result<List<Product>> getProductList() {
        List<Product> products = productMapper.selectList(null);
        return Result.success(products);
    }

    @Override
    public Result<ProductVo> getProduct(Integer productId) {
        // 1.获取商品信息
        Product product = productMapper.selectById(productId);
        // 2.获取店铺信息
        Shop shop = shopMapper.selectById(product.getShopId());
        // 获取商品属性
        ProductAttribute productAttribute
                = mongoTemplate.findOne(new Query(Criteria.where("product_id").is(product.getId())), ProductAttribute.class);
        // 3.设置vo
        ProductVo productVo = new ProductVo();
        BeanUtils.copyProperties(product, productVo);
        productVo.setShop(shop);
        if (productAttribute != null) productVo.setCustomAttributes(productAttribute.getCustomAttributes());
        // 4.保存浏览记录
        try {
            ProductBrowse productBrowse = new ProductBrowse();
            productBrowse.setProductId(productId);
            productBrowse.setUserId(UserThreadLocal.getUserId());
            productBrowseMapper.insert(productBrowse);
        } catch (Exception e) {
            log.error("用户已经访问过，不需要再次插入数据库");
        }
        return Result.success(productVo);
    }

    @Override
    public Result<List<Product>> getProductByShop(Integer productId) {
        // 1.根据产品id获取店铺id
        Product product = productMapper.selectById(productId);
        Integer shopId = product.getShopId();
        // 2.根据店铺id获取该店铺所有产品
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getShopId, shopId);
        List<Product> products = productMapper.selectList(queryWrapper);
        // 3.过滤该产品
        products = products.stream().filter(
                product2 -> !product2.getId().equals(productId)).collect(Collectors.toList());
        return Result.success(products);
    }

    @Override
    public Result<Void> postProduct(ProductDto productDto) {
        // 1.上传商品
        Product product = new Product();

        // 2.上传商品属性
        ProductAttribute productAttribute = productDto.getProductAttribute();
        productAttribute.setProduct_id(product.getId());
        mongoTemplate.insert(productAttribute);

        return Result.success(null);
    }

    @Override
    public Result<Void> updateProduct(Product product) {
        productMapper.updateById(product);
        return Result.success(null);
    }
}
