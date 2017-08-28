package org.k8scmp.mapper.global;

import org.apache.ibatis.annotations.*;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;

import java.util.List;

/**
 * Created by feiliu206363 on 2016/1/20.
 */
@Mapper
public interface GlobalMapper {
    @Insert("INSERT INTO globalconfig (type, value, createTime, lastUpdate, description) values (" +
            "#{type}, #{value}, #{createTime}, #{lastUpdate}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addGlobalInfo(GlobalInfo globalInfo);

    @Select("SELECT * FROM globalconfig WHERE type=#{type}")
    GlobalInfo getGlobalInfoByType(@Param("type") GlobalType globalType);

    @Select("SELECT * FROM globalconfig WHERE type=#{type} AND id=#{id}")
    GlobalInfo getGlobalInfoByTypeAndId(@Param("type") GlobalType globalType, @Param("id") int id);

    @Select("SELECT * FROM globalconfig WHERE type=#{type}")
    List<GlobalInfo> listGlobalInfoByType(@Param("type") GlobalType globalType);

    @Select("SELECT * FROM globalconfig WHERE id=#{id}")
    GlobalInfo getGlobalInfoById(@Param("id") int id);

    @Delete("DELETE FROM globalconfig WHERE type=#{type}")
    int deleteGlobalInfoByType(@Param("type") GlobalType globalType);

    @Delete("DELETE FROM globalconfig WHERE id=#{id}")
    int deleteGlobalInfoById(@Param("id") int id);

    @Update("UPDATE globalconfig SET value=#{value}, lastUpdate=#{lastUpdate}, description=#{description} WHERE type=#{type} AND id=#{id}")
    int updateGlobalInfoById(GlobalInfo globalInfo);

    @Update("UPDATE globalconfig SET value=#{value}, lastUpdate=#{lastUpdate} WHERE type=#{type}")
    int updateGlobalInfoByType(GlobalInfo globalInfo);
}
