package com.shoppingcart.admin.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shoppingcart.admin.FileUploadUtil;
import com.shoppingcart.admin.user.export.UserCsvExporter;
import com.shoppingcart.admin.user.export.UserExcelExporter;
import com.shoppingcart.admin.user.export.UserPdfExporter;
import com.shoppingcart.common.entity.Role;
import com.shoppingcart.common.entity.User;

import jxl.read.biff.BiffException;

@Controller//Controller là nơi tiếp nhận yêu cầu(request), controller sẽ gọi service để xử lý nghiệp vụ(logic), service sẽ gọi repository trong trường hợp muốn tương tác với database
public class UserController {
	private static final String UPLOAD_DIRECTORY = "C:\\Uploadtemplate\\";

	private String defaultRedirectURL = "redirect:/users/page/1?sortField=firstName&sortDir=asc";//đường dẫn mặc định

	@Autowired//lấy Spring Bean userService trong IOC và nhúng vào Spring Bean userController -->userService ko cần khởi tạo mà vẫn có thể sử dụng được là do nó đã được khởi tạo trong IOC
	private UserService service;
	


	@GetMapping("/users")//localhost:8082/ShoppingCartAdmin/users
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}

	@GetMapping("/users/page/{pageNum}")//localhost:8082/ShoppingCartAdmin/users/page/1?sortField=firstName&sortDir=asc -->1 là giá trị có thể thay đổi -->phải dùng @PathVariable(name = "pageNum") int pageNum để nó tự động gán giá trị vào biến pageNum 
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword, @Param("template")boolean template) {//@Param("sortField") String sortField dùng để lấy ra giá trị của key sortField -->nó sẽ gán giá trị value vào biến sortField 
		Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword, template);//đối tượng Page<User> chứa các thông tin về phân trang
		List<User> listUsers = page.getContent();//lấy ra tất cả các users trong trang hiện tại

		long startCount = (pageNum - 1) * UserService.USERS_PER_PAGE + 1;//tính thứ tự của user bắt đầu trong trang hiện tại 
		long endCount = startCount + UserService.USERS_PER_PAGE - 1;//tính thứ tự của user kết thúc trong trang hiện tại

		if (endCount > page.getTotalElements()) {//nếu endCount > số users tối đa thì gán endCount = số users tối đa
			endCount = page.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";//trường hợp nhấn sort trên firstName thì sẽ đảo ngược thứ tự tất cả users theo firstName

		model.addAttribute("currentPage", pageNum);//trang hiện tại
		model.addAttribute("totalPages", page.getTotalPages());//tổng số trang
		model.addAttribute("startCount", startCount);//index của user bắt đầu
		model.addAttribute("endCount", endCount);//index của user kết thúc
		model.addAttribute("totalItems", page.getTotalElements());//tổng số users(số users tối đa)
		model.addAttribute("listUsers", listUsers);//tất cả users trong trang hiện tại
		model.addAttribute("sortField", sortField);//thuộc tính cần sắp xếp theo
		model.addAttribute("sortDir", sortDir);//sắp xếp tăng dần hoặc giảm dần
		model.addAttribute("reverseSortDir", reverseSortDir);//đảo ngược sắp xếp
		model.addAttribute("keyword", keyword);//từ khóa tìm kiếm

		return "users/users";//trả về users/users.html
	}

	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> listRoles = service.listRoles();

		User user = new User();
		user.setEnabled(true);

		model.addAttribute("user", user);//khi trả về form user_form.html thì bắt buộc phải khởi tạo 1 đối tượng user, vì trong form có khai báo th:object="${user}"
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");
		return "users/user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,//redirectAttributes chỉ có tác dụng khi dùng "redirect:/.. -->gọi @GetMapping
			@RequestParam("image") MultipartFile multipartFile) throws IOException {//trường hợp submit form có method là POST -->tất cả các giá trị trong form sẽ được gán vào đối tượng user. Nhưng nếu vẫn muốn lấy riêng các giá trị của thẻ input, select,...(những thẻ có thẻ nhập, chọn được) thì có thể dùng @RequestParam("image") -->lưu ý image là name của thẻ cần lấy --> <input type="file" id="fileImage" name="image"/> -->thẻ này có type="file" nên sẽ dùng đối tượng multipartFile để gán giá trị 

		if (!multipartFile.isEmpty()) {//nếu multipartFile empty -->ko nhấn chọn hình
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);//thuộc tính photo chỉ lưu tên hình, còn hình sẽ lưu trong folder user-photos
			User savedUser = service.save(user);

			String uploadDir = "user-photos/" + savedUser.getId();

			FileUploadUtil.cleanDir(uploadDir);//xóa tất cả các file hình nằm bên trong folder hiện tại, vì 1 user chỉ có 1 hình
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);//save hình của user vào folder user-photos
		} else {
			if (user.getPhotos().isEmpty())
				user.setPhotos(null);
			service.save(user);
		}

		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");

		return getRedirectURLtoAffectedUser(user);
	}
	
	@PostMapping("/users/saveUploadUsers")
	public String saveUsersUpload( RedirectAttributes redirectAttributes,//redirectAttributes chỉ có tác dụng khi dùng "redirect:/.. -->gọi @GetMapping
			@RequestParam("image") MultipartFile[] multipartFile,@RequestParam(required=false,name="userId") String[] userIds,@RequestParam("userMail")String[] userMails,Model model) throws IOException, NumberFormatException, UserNotFoundException {//trường hợp submit form có method là POST -->tất cả các giá trị trong form sẽ được gán vào đối tượng user. Nhưng nếu vẫn muốn lấy riêng các giá trị của thẻ input, select,...(những thẻ có thẻ nhập, chọn được) thì có thể dùng @RequestParam("image") -->lưu ý image là name của thẻ cần lấy --> <input type="file" id="fileImage" name="image"/> -->thẻ này có type="file" nên sẽ dùng đối tượng multipartFile để gán giá trị 
		String searchUser = "";
		List<User> listUsers = new ArrayList<User>();
		for(int i =0;i<userIds.length;i++) {
			searchUser += userMails[i] + ",";
			User user = service.get(Integer.parseInt(userIds[i]));
			listUsers.add(user);
		}
		if(!searchUser.isEmpty()) {
			searchUser = searchUser.substring(0, searchUser.length()-1);
		}
		boolean checkSave = service.saveUsersTemplates(listUsers,multipartFile);
		if(checkSave) {
			redirectAttributes.addFlashAttribute("message", "This templates users has been saved successfully.");
		}else {
			redirectAttributes.addFlashAttribute("message", "This templates users has been saved fail.");
		}
		return getRedirectURLtoAffectedUserTemplate(searchUser);
	}

	private String getRedirectURLtoAffectedUser(User user) {
		String firstPartOfEmail = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;//trả về trang list users có keyword là email của user này
	}
	
	private String getRedirectURLtoAffectedUserTemplate(String user) {
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword="+ user+"&template=true";//trả về trang list users có keyword là email của user này
	}
	
	@GetMapping("/users/downloadtemplate")
	public void downloadTemplates(HttpServletRequest request,HttpServletResponse response) {
		UserExcelExporter exporter = new UserExcelExporter();
		try {
			exporter.export(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@PostMapping("/users/uploadTemplates")
	public String uploadTemplates(HttpServletRequest request,HttpServletResponse response,@RequestParam("uploadUser") MultipartFile multipartFile,Model model) {
		String contentType = request.getContentType();
		if(contentType!=null && contentType.indexOf("multipart/form-data")>=0) {
			if(!(multipartFile==null && multipartFile.isEmpty())) {
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				try {
					FileUploadUtil.saveFile(UPLOAD_DIRECTORY, fileName, multipartFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<User> listUsers = null;
				try {
					listUsers = service.readExcel(UPLOAD_DIRECTORY+fileName);
				} catch (BiffException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				model.addAttribute("listUsers",listUsers);
			}
		}
		return "users/userUpload";
	}

	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			User user = service.get(id);
			List<Role> listRoles = service.listRoles();
			
			model.addAttribute("checkUpload", 1);
			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
			model.addAttribute("listRoles", listRoles);

			return "users/user_form";
		} catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}
	

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);

			String userPhotosDir = "user-photos/" + id;
			FileUploadUtil.removeDir(userPhotosDir);//sau khi delete user thì xóa folder chứa hình của user đó

			redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully");

		} catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}

		return defaultRedirectURL;
	}

	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes, Model model) {
		service.updateUserEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The user ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);

		return defaultRedirectURL;
	}

	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {//HttpServletRequest và HttpServletResponse là 2 đối tượng có sẵn trong servlet, HttpServletRequest chứa các thông tin của request gửi đến, HttpServletResponse sẽ trả về thông tin cho request gửi đến
		List<User> listUsers = service.listAll();
		UserCsvExporter exporter = new UserCsvExporter();
		exporter.export(listUsers, response);
	}

	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();

		UserExcelExporter exporter = new UserExcelExporter();
		exporter.export(listUsers, response);
	}

	@GetMapping("/users/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();

		UserPdfExporter exporter = new UserPdfExporter();
		exporter.export(listUsers, response);
	}

}
