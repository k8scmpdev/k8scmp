package org.k8scmp.mapper.monitor.portal;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.k8scmp.monitormgmt.domain.alarm.falcon.Mockcfg;

/**
 * Created by baokangwang on 2016/4/14.
 */
@Mapper
public interface PortalMockcfgMapper {

    @Select("SELECT * FROM mockcfg WHERE name=#{name}")
    Mockcfg getMockcfgByName(@Param("name") String name);

    @Insert("INSERT INTO mockcfg (name, obj, obj_type, metric, tags, dstype, step, mock, creator, t_create, t_modify) " +
            "VALUES (#{name}, #{obj}, #{obj_type}, #{metric}, #{tags}, #{dstype}, #{step}, #{mock}, #{creator}, #{t_create}, #{t_modify})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertMockcfg(Mockcfg mockcfg);

    @Update("UPDATE mockcfg SET obj=#{obj} WHERE name=#{name}")
    int updateObjByName(@Param("name") String name, @Param("obj") String obj);
}
