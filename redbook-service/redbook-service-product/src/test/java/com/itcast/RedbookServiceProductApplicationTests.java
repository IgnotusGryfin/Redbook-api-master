package com.itcast;

import com.itcast.product.pojo.CustomAttribute;
import com.itcast.product.pojo.ProductAttribute;
import com.itcast.product.vo.ProductVo;
import com.itcast.result.Result;
import com.itcast.service.ProductService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class RedbookServiceProductApplicationTests {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProductService productService;

    @Test
    void contextLoads() {
    }

    private static int stock = 50;

    @Test
    void testRedission() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                RLock lock = redissonClient.getLock("itcast");
                try {
                    boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
                    if (res) {
                        System.out.println(Thread.currentThread().getName() + "获取锁成功");
                        if (stock > 0) {
                            stock -= 1;
                        }
                    } else {
                        System.out.println(Thread.currentThread().getName() + "获取锁失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
                latch.countDown();
            }).start();
        }

        latch.await();
        System.out.println("所有线程完成");
        System.out.println(stock);
    }

    @Test
    void testPostProduct() {
        List<CustomAttribute> tempList = new ArrayList<>();

        CustomAttribute customAttribute = new CustomAttribute();
        customAttribute.setLabel("颜色");
        customAttribute.setValue(Arrays.asList("红色", "蓝色", "黑色"));
        tempList.add(customAttribute);

        CustomAttribute customAttribute2 = new CustomAttribute();
        customAttribute2.setLabel("尺寸");
        customAttribute2.setValue(Arrays.asList("S", "M", "L"));
        tempList.add(customAttribute2);

        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setProduct_id(3);
        productAttribute.setCustomAttributes(tempList);

        mongoTemplate.insert(productAttribute);
    }

    @Test
    void testGetProduct() {
        Result<ProductVo> product = productService.getProduct(2);
        ProductVo productVo = product.getData();
        System.out.println(productVo.getCustomAttributes().toString());
    }
}
