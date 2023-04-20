package com.shoppingcart.admin.user;

import org.springframework.data.repository.CrudRepository;

import com.shoppingcart.common.entity.Role;
import com.shoppingcart.common.entity.RoleTemp;
//CrudRepository có các phương thức dùng để SELECT, CREATE, UPDATE, DELETE
public interface RoleTempRepository extends CrudRepository<RoleTemp, Integer> {//tham số thứ 1 là entity, tham số thứ 2 là kiểu dữ liệu của khóa chính(Primary key)

}
