package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate  stringRedisTemplate;

    @Override
    public List<ShopType> queryTypeList() {
        //1.从Redis查询商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get("shopType");
        //2.判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {
            //3.存在，拼接为list后直接返回
            List<ShopType> shopTypes = JSONUtil.toList(shopJson, ShopType.class);
            return shopTypes;
        }
        //4.不存在，根据id查询数据库
        List<ShopType> shopTypes = query().orderByAsc("sort").list();
        //5.存在，写入Redis
        stringRedisTemplate.opsForValue().set("shopType", JSONUtil.toJsonStr(shopTypes));
        //6.返回
        return shopTypes;
    }

}
