package com.xust.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xust.entity.form.InterfacesAvgSpeed;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ysw
 */
@Repository
public interface InterfacesAvgSpeedDao extends BaseMapper<InterfacesAvgSpeed> {
    @Select("select * from interfaces_avg_speed where ip=#{ip} order by create_time desc limit 6")
    List<InterfacesAvgSpeed> selectLimt(@Param("ip") String ip);
}
