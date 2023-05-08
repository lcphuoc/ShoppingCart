package com.shoppingcart.admin.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shoppingcart.common.entity.ShipperDTO;
import com.shoppingcart.common.entity.User;
//PagingAndSortingRepository kế thừa từ CrudRepository -->có các phương thức để SELECT, CREATE, UPDATE, DELETE và phân trang
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {//tham số thứ 1 là entity, tham số thứ 2 là kiểu dữ liệu của khóa chính(Primary key)

	@Query("SELECT u FROM User u WHERE u.email = :email")//Spring Data JPA sẽ truy vấn theo entity và thuộc tính
	public User getUserByEmail(@Param("email") String email);//@Param("email") String email sẽ gán giá trị của biến email vào vị trí :email
	
	public Long countById(Integer id);
	
	//@Query("SELECT u FROM User u WHERE u.firstName LIKE %?1% OR u.lastName LIKE %?1% OR u.email LIKE %?1%")
	@Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' '," + " u.lastName) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);//pageable đối tượng chứa các thông tin phân trang và sắp xếp

	@Query("SELECT u FROM User u WHERE u.email in (:keyword)")
	public Page<User> findAllTemplates(@Param("keyword") List<String> listKeywords, Pageable pageable);
	
	@Query(nativeQuery =true,value = "SELECT * FROM USERS as u WHERE u.EMAIL IN (:listKeywords)")   // 3. Spring JPA In cause using native query
	public Page<User> findByListKeywords(@Param("listKeywords") List<String> listKeywords,Pageable pageable);
	
	@Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying//khi INSERT/UPDATE/DELETE thì bắt buộc khai báo @Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);//dùng ?1 để gán giá trị của tham số thứ 1 vào vị trí này 
	
	@Query(nativeQuery = true, value = "select b.* from users_roles a\r\n"
	        + "inner join users b on a.user_id = b.id\r\n"
	        + "where a.role_id = '4'")
	public List<User> listShipper();

	

}
