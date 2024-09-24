package com.itcast.controller;

import com.itcast.product.dto.ProductDto;
import com.itcast.product.pojo.Product;
import com.itcast.product.vo.ProductVo;
import com.itcast.result.Result;
import com.itcast.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/getProductList")
    public Result<List<Product>> getProductList() {
        return productService.getProductList();
    }

    @GetMapping("/getProduct/{productId}")
    public Result<ProductVo> getProduct(@PathVariable("productId") Integer productId) {
        return productService.getProduct(productId);
    }

    @GetMapping("/getProductsByShop/{productId}")
    public Result<List<Product>> getProductsByShop(@PathVariable("productId") Integer productId) {
        return productService.getProductByShop(productId);
    }

    @PostMapping("/postProduct")
    public Result<Void> postProduct(@RequestBody ProductDto productDto) {
        return productService.postProduct(productDto);
    }

    @PutMapping("/updateProduct")
    public Result<Void> updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }
}
