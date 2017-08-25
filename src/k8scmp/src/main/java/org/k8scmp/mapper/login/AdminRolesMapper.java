package org.k8scmp.mapper.login;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.k8scmp.login.domain.AdminRole;

/**
 * Created by jason on 2017/8/24.
 */
@Mapper
public interface AdminRolesMapper {
    @Select("SELECT * FROM admin_roles WHERE userId=#{userId}")
    AdminRole getAdminById(@Param("userId") long userId);
}
