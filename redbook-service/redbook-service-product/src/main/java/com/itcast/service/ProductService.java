package com.itcast.service;

import com.itcast.product.dto.ProductDto;
import com.itcast.product.pojo.Product;
import com.itcast.product.vo.ProductVo;
import com.itcast.result.Result;

import java.util.List;

public interface ProductService {
    Result<List<Product>> getProductList();

    Result<ProductVo> getProduct(Integer productId);

    Result<List<Product>> getProductByShop(Integer productId);

    Result<Void> postProduct(ProductDto productDto);

    Result<Void> updateProduct(Product product);
}
