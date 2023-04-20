package com.shoppingcart.admin.user.export;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.shoppingcart.admin.AbstractExporter;
import com.shoppingcart.common.entity.User;

public class UserExcelExporter extends AbstractExporter {

	private XSSFWorkbook workbook;

	private XSSFSheet sheet;

	public UserExcelExporter() {
		workbook = new XSSFWorkbook();
	}

	private void writeHeaderLine() {
		sheet = workbook.createSheet("Users");
		XSSFRow row = sheet.createRow(0);

		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(16);
		cellStyle.setFont(font);

		createCell(row, 0, "User Id", cellStyle);
		createCell(row, 1, "E-mail", cellStyle);
		createCell(row, 2, "First Name", cellStyle);
		createCell(row, 3, "Last Name", cellStyle);
		createCell(row, 4, "Roles", cellStyle);
		createCell(row, 5, "Enabled", cellStyle);

	}
	
	private void writeHeaderLineTemplates() {
		sheet = workbook.createSheet("Users");
		XSSFRow row = sheet.createRow(0);


		createCellTemplate(row, 0, "E-mail", 16,true,"");
		createCellTemplate(row, 1, "First Name", 16,true,"");
		createCellTemplate(row, 2, "Last Name", 16,true,"");
		createCellTemplate(row, 3, "Password", 16,true,"");
		createCellTemplate(row, 4, "Roles", 16,true,"");
		createCellTemplate(row, 5, "Enabled", 16,true,"");
		
		row = sheet.createRow(1);
		createCellTemplate(row, 0, "Text (Required)", 12,false,"1");
		createCellTemplate(row, 1, "Text (Required)", 12,false,"1");
		createCellTemplate(row, 2, "Text (Required)", 12,false,"1");
		createCellTemplate(row, 3, "Text (Required)",12,false,"1");
		createCellTemplate(row, 4, "NUMBER(1->5) (Required)", 12,false,"1");
		createCellTemplate(row, 5, "TRUE/FALSE (Not Required)", 12,false,"1");
	}

	private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style) {
		XSSFCell cell = row.createCell(columnIndex);
		sheet.autoSizeColumn(columnIndex);

		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}
	
	private void createCellTemplate(XSSFRow row, int columnIndex, Object value, int size,boolean bold, String Color) {
		XSSFCell cell = row.createCell(columnIndex);
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(bold);
		font.setFontHeight(size);
		cellStyle.setFont(font);
		sheet.autoSizeColumn(columnIndex);
		sheet.setColumnWidth(columnIndex, 25 * 300);

		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else {
			cell.setCellValue((String) value);
		}
		cellStyle.setBorderTop(BorderStyle.MEDIUM);
		cellStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellStyle.setBorderRight(BorderStyle.MEDIUM);
		if(Color.trim().length()>0) {
			cellStyle.setFillForegroundColor(IndexedColors.TAN.index);
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		cell.setCellStyle(cellStyle);
	}

	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "application/octet-stream", ".xlsx", "users_");

		writeHeaderLine();
		writeDataLines(listUsers);

		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}
	
	public void export(HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "application/octet-stream", ".xls", "Template");
		writeHeaderLineTemplates();
		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}

	private void writeDataLines(List<User> listUsers) {
		int rowIndex = 1;

		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(14);
		cellStyle.setFont(font);

		for (User user : listUsers) {
			XSSFRow row = sheet.createRow(rowIndex++);
			int columnIndex = 0;

			createCell(row, columnIndex++, user.getId(), cellStyle);
			createCell(row, columnIndex++, user.getEmail(), cellStyle);
			createCell(row, columnIndex++, user.getFirstName(), cellStyle);
			createCell(row, columnIndex++, user.getLastName(), cellStyle);
			createCell(row, columnIndex++, user.getRoles().toString(), cellStyle);
			createCell(row, columnIndex++, user.isEnabled(), cellStyle);
		}
	}
}
