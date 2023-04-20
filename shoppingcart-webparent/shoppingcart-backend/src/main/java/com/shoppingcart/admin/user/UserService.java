package com.shoppingcart.admin.user;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shoppingcart.admin.FileUploadUtil;
import com.shoppingcart.common.entity.Role;
import com.shoppingcart.common.entity.RoleTemp;
import com.shoppingcart.common.entity.User;
import com.shoppingcart.common.entity.UserTemp;

import jxl.read.biff.BiffException;

@Service//ko khai báo tên Spring Bean thì sẽ tạo Spring Bean tên là userService
@Transactional//khi thực hiện INSERT/UPDATE/DELETE bắt buộc khai báo @Transactional
public class UserService {

	public static final int USERS_PER_PAGE = 4;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserTempRepository userTempRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private RoleTempRepository roleTempRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<User> listAll() {
		return (List<User>) userRepo.findAll(Sort.by("firstName").ascending());
	}
	
	public List<UserTemp> listAllUsersTemp() {
		return (List<UserTemp>) userTempRepo.findAll(Sort.by("id").ascending());
	}

	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword, boolean template) {//Page là đối tượng trong Spring Data JPA
		Sort sort = Sort.by(sortField);//đối tượng sort sẽ sắp xếp các giá trị trả về theo biến sortField tăng dần(asc) hoặc giảm dần(desc)
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		
		
		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);//Giả sử có tổng cộng 10 records, 1 trang có 4 records -->có tổng cộng 3 trang

		if (keyword != null) {//nếu có truyền keyword thì lấy tất tìm user theo keyword
			if(template) {
				List<String> listKeywords = Arrays.asList(keyword.split(","));
				return userRepo.findByListKeywords(listKeywords,pageable);
			}else {
				return userRepo.findAll(keyword, pageable);
			}
		}
		

		return userRepo.findAll(pageable);//tìm tất cả users nếu ko nhập keyword
	}

	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll();//.findAll() trả về Iterable<Role> -->ép kiểu thành List<Role>
	}

	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);//nếu id == null -->Create, nếu id != null -->Update

		if (isUpdatingUser) {//trường hợp edit
			User existingUser = userRepo.findById(user.getId()).get();//trường hợp update thì lấy user theo id

			if (user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());//nếu ko nhập password thì lấy password cũ để save lại
			} else {
				encodePassword(user);//nếu có nhập password thì mã hóa password
			}

		} else {
			encodePassword(user);//trường hợp create -->luôn mã hóa password
		}

		return userRepo.save(user);//save user xuống db
	}
	
	public User saveTemplaes(User user) {
		encodePassword(user);//trường hợp create -->luôn mã hóa password

		return userRepo.save(user);//save user xuống db
	}
	
	public List<User> readExcel(String fileName) throws BiffException{
		List<User> listUser = new ArrayList<User>();
		FileInputStream inputWorkbook = null;
		try {
			inputWorkbook = new FileInputStream(fileName);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(inputWorkbook);
			XSSFSheet sheet = workbook.getSheetAt(0);
			userTempRepo.deleteUsers_Temp();
			//chỉ đọc file duoi xls
			int totalRow = sheet.getPhysicalNumberOfRows();
			int count = 1;
			for(int i = 2; i<totalRow; i++) {
				User user = new User();
				//Lấy user
				String email = getValue(sheet, 0, i).trim();
				String firstName = getValue(sheet, 1, i).trim();
				String lastName = getValue(sheet, 2, i).trim();
				String password = getValue(sheet, 3, i).trim();
				String roles = getValue(sheet, 4, i).trim();
				Set<Role> setRoles = new HashSet<Role>();
				if(!roles.isEmpty()) {
					String [] roleSplit = roles.split(";");
					for(int j = 0; j<roleSplit.length; j++) {
						Role role = roleRepo.findById(Integer.parseInt(roleSplit[j])).get();
						setRoles.add(role);
					}
				}
				boolean enabled = (getValue(sheet, 5, i).trim()).equals("1")?true:false;
				//Gán vào user
				user.setEmail(email);
				user.setFirstName(firstName);
				user.setLastName(lastName);
				user.setPassword(password);
				user.setRoles(setRoles);
				user.setEnabled(enabled);
				userRepo.save(user);
				// Thêm vào List User
				listUser.add(user);
				count++;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listUser;
	}
	
	private String getValue(XSSFSheet sheet, int column, int row) {
		NumberFormat formatter = new DecimalFormat("#######");
		Row rowIndex = sheet.getRow(row);
		org.apache.poi.ss.usermodel.Cell cell;
		cell = rowIndex.getCell(column);
		try {
			return cell.getStringCellValue();
		} catch (Exception e) {
			return formatter.format(cell.getNumericCellValue());
			// TODO: handle exception
		}
		
	}

	private void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());//mã hóa password bằng BCrypt
		user.setPassword(encodedPassword);
	}

	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();//lấy user theo id
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID " + id);//khai báo exception do mình tự tạo sẽ giúp cho code dễ đọc, dễ hiểu, dễ debug hơn
		}
	}
	public UserTemp getTemp(Integer id) throws UserNotFoundException {
		try {
			return userTempRepo.findById(id).get();//lấy user theo id
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID " + id);//khai báo exception do mình tự tạo sẽ giúp cho code dễ đọc, dễ hiểu, dễ debug hơn
		}
	}

	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id);
		if (countById == null || countById == 0) {//nếu count == null hoặc count == 0 -->ko tồn tại user với id này
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}

		userRepo.deleteById(id);
	}

	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepo.updateEnabledStatus(id, enabled);
	}

	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepo.getUserByEmail(email);//lấy user theo email

		if (userByEmail == null)//nếu userByEmail == null -->ko bị trùng email
			return true;

		boolean isCreatingNew = (id == null);//trường hợp nếu trùng email thì xem xét đang create hay update

		if (isCreatingNew) {
			if (userByEmail != null)//nếu là create thì ko được trùng
				return false;
		} else {
			if (userByEmail.getId() != id) {//nếu là edit thì có thể trùng
				return false;
			}
		}

		return true;
	}

	public User getByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}

	public User updateAccount(User userInForm) {
		User userInDB = userRepo.findById(userInForm.getId()).get();

		if (!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword());
			encodePassword(userInDB);
		}

		if (userInForm.getPhotos() != null) {
			userInDB.setPhotos(userInForm.getPhotos());
		}

		userInDB.setFirstName(userInForm.getFirstName());
		userInDB.setLastName(userInForm.getLastName());

		return userRepo.save(userInDB);
	}
	
	public boolean saveUsersTemplates(List<User> listUsers, MultipartFile[] multipartFile) {
		for(int i =0;i<listUsers.size();i++) {
			if (!multipartFile[i].isEmpty()) {//nếu multipartFile empty -->ko nhấn chọn hình
				String fileName = StringUtils.cleanPath(multipartFile[i].getOriginalFilename());
				listUsers.get(i).setPhotos(fileName);//thuộc tính photo chỉ lưu tên hình, còn hình sẽ lưu trong folder user-photos
				User savedUser = this.saveTemplaes(listUsers.get(i));

				String uploadDir = "user-photos/" + savedUser.getId();

				FileUploadUtil.cleanDir(uploadDir);//xóa tất cả các file hình nằm bên trong folder hiện tại, vì 1 user chỉ có 1 hình
				try {
					FileUploadUtil.saveFile(uploadDir, fileName, multipartFile[i]);
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}//save hình của user vào folder user-photos
			} else {
				if (listUsers.get(i).getPhotos()==null ||listUsers.get(i).getPhotos().isEmpty())
					listUsers.get(i).setPhotos(null);
				this.saveTemplaes(listUsers.get(i));
			}
		}
		return true;
	}

}
