package org.k8scmp.mapper.alarm;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.k8scmp.monitormgmt.domain.alarm.StrategyInfo;

@Mapper
public interface StrategyInfoMapper {
	
	@Insert("INSERT INTO alarm_strategy_info(templateId, metric, tag, pointNum, aggregateType, operator, rightValue, note, maxStep, createTime) VALUES (" +
            "#{templateId}, #{metric}, #{tag}, #{pointNum}, #{aggregateType}, #{operator}, #{rightValue}, #{note}, #{maxStep}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	int addStrategyInfo(StrategyInfo strategyInfo);
	
	@Select("SELECT * FROM alarm_strategy_info WHERE templateId=#{templateId}")
    List<StrategyInfo> listStrategyInfoByTemplateId(@Param("templateId") int templateId);

	@Delete("DELETE FROM alarm_strategy_info WHERE templateId=#{templateId}")
    int deleteStrategyInfoByTemplateId(@Param("templateId") int templateId);
	
}
