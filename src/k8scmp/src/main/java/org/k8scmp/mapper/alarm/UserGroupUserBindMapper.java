package org.k8scmp.mapper.alarm;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.k8scmp.login.domain.User;

/**
 * Created by KaiRen on 2016/4/13.
 */
@Mapper
public interface UserGroupUserBindMapper {

    @Delete("DELETE FROM alarm_user_group_user_bind WHERE userGroupId=#{userGroupId}")
    int deleteUserGroupUserBindByUserGroupId(@Param("userGroupId") int userGroupId);

    @Delete("DELETE FROM alarm_user_group_user_bind WHERE userGroupId=#{userGroupId} AND userId=#{userId}")
    int deleteUserGroupUserBind(@Param("userGroupId") int userGroupId, @Param("userId") int userId);

    @Select("SELECT bindTime FROM alarm_user_group_user_bind WHERE userGroupId=#{userGroupId} AND userId=#{userId}")
    String getUserGroupUserBindTime(@Param("userGroupId") int userGroupId, @Param("userId") int userId);

    @Insert("INSERT INTO alarm_user_group_user_bind(userGroupId, userId, bindTime) VALUES (" +
            "#{userGroupId}, #{userId}, #{bindTime})")
    int addUserGroupUserBind(@Param("userGroupId") int userGroupId, @Param("userId") int userId, @Param("bindTime") String bindTime);

    @Update("UPDATE alarm_user_group_user_bind SET bindTime=#{bindTime} WHERE userGroupId=#{userGroupId} AND userId=#{userId}")
    int updateUserGroupUserBind(@Param("userGroupId") int userGroupId, @Param("userId") int userId, @Param("bindTime") String bindTime);

    @Select("SELECT id, username, email, phone FROM users LEFT OUTER JOIN alarm_user_group_user_bind ON " +
            "users.id = alarm_user_group_user_bind.userId WHERE alarm_user_group_user_bind.userGroupId " +
            "= #{userGroupId} order by alarm_user_group_user_bind.bindTime")
    List<User> getUserInfoByUserGroupId(@Param("userGroupId") int userGroupId);
}
