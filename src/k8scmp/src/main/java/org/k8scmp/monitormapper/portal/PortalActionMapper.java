package org.k8scmp.monitormapper.portal;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.k8scmp.monitormgmt.domain.alarm.falcon.Action;

@Mapper
public interface PortalActionMapper {

    @Insert("INSERT INTO action (uic, url, callback, before_callback_sms, before_callback_mail, after_callback_sms, " +
            "after_callback_mail) VALUES (#{uic}, #{url}, #{callback}, #{before_callback_sms}, #{before_callback_mail}, " +
            " #{after_callback_sms}, #{after_callback_mail})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertAction(Action action);

    @Delete("DELETE FROM action WHERE id=#{id}")
    int deleteActionById(@Param("id") long id);

    @Select("SELECT * FROM action WHERE id=#{id}")
    Action getActionById(@Param("id") long id);

}
