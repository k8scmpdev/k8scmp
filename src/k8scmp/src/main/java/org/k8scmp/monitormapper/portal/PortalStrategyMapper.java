package org.k8scmp.monitormapper.portal;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.k8scmp.monitormgmt.domain.alarm.falcon.Strategy;

@Mapper
public interface PortalStrategyMapper {
	
    @Insert("INSERT INTO strategy (metric, tags, max_step, priority, func, op, right_value, note, tpl_id) VALUES (" +
            "#{metric}, #{tags}, #{max_step}, #{priority}, #{func}, #{op}, #{right_value}, #{note}, #{tpl_id})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertStrategy(Strategy strategy);

    @Insert("INSERT INTO strategy (id, metric, tags, max_step, priority, func, op, right_value, note, tpl_id) VALUES (" +
            "#{id}, #{metric}, #{tags}, #{max_step}, #{priority}, #{func}, #{op}, #{right_value}, #{note}, #{tpl_id})")
    int insertStrategyById(Strategy strategy);

    @Delete("DELETE FROM strategy WHERE tpl_id=#{tpl_id}")
    int deleteStrategyByTemplateId(@Param("tpl_id") long tpl_id);

    @Delete("DELETE FROM strategy WHERE tags IN (${containerIds})")
    int deleteStrategyByContainerIds(@Param("containerIds") String containerIds);

    @Select("SELECT DISTINCT RIGHT(tags, 64) FROM strategy WHERE tpl_id=#{tpl_id}")
    List<String> getContainerIdByTemplateId(@Param("tpl_id") long tpl_id);
}
