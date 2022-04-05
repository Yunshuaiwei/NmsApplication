package com.xust.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xust.entity.UserEntity;
import org.springframework.stereotype.Repository;

/**
 * @author ysw
 */
@Repository
public interface DeviceDao extends BaseMapper<UserEntity> {
}
