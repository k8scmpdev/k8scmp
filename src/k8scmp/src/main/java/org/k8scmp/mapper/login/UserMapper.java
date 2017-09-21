package org.k8scmp.mapper.login;

import org.apache.ibatis.annotations.*;
import org.k8scmp.login.domain.User;

import java.util.List;

/**
 * Created by feiliu206363 on 2016/4/5.
 */
@Mapper
public interface UserMapper {
    @Select("SELECT * FROM users WHERE id=#{userId}")
    User getUserById(@Param("userId") long userId);

    @Select("SELECT * FROM users WHERE loginName=#{loginname} ")
    User getUserByName(@Param("loginname") String loginname);
    
    @Select("SELECT * FROM users WHERE loginName like #{keyword} or username like #{keyword} or email like #{keyword} ")
    List<User> listUsersByKW(@Param("keyword") String keyword);

    @Select("SELECT id, loginName,username, email, phone, loginType, createTime FROM users WHERE" +
            " username=#{userName} and state='NORMAL'")
    User getUserInfoByName(@Param("loginname") String loginname);

    @Select("SELECT id, loginName,username, email, phone, loginType, createTime FROM users WHERE id=#{userId}")
    User getUserInfoById(@Param("userId") int userId);

    @Select("SELECT id, loginName,username, email, phone, loginType, createTime FROM users ")
    List<User> listAllUserInfo();

    @Insert("INSERT INTO users (loginName,username, password, salt, email, phone, loginType, createTime, lastModifiedTime) VALUES (" +
            "#{loginname},#{username}, #{password}, #{salt}, #{email}, #{phone}, #{loginType}, #{createTime},#{lastModifiedTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addUser(User user);

    @Update("UPDATE users set username=#{username}, " +
            "email=#{email}, phone=#{phone}, lastModifiedTime=#{lastModifiedTime} WHERE loginname=#{loginname}")
    boolean modifyUser(User user);

    @Update("UPDATE users set password=#{password}, salt=#{salt}, lastModifiedTime=#{lastModifiedTime} WHERE" +
            " loginName=#{loginname} ")
    boolean changePassword(User user);

    @Update("delete from users where loginName=#{loginname} ")
    int deleteUser(User user);

    @Select("SELECT role FROM admin_roles ar,users u WHERE u.username=#{username} and u.state='NORMAL' and u.id = ar.userId")
    List<String> getRole(@Param("username") String username);

    @Select("SELECT username FROM users WHERE id=#{id}")
    String getUserNameById(@Param("id") int id);
}
