package com.shoppingcart.admin.order;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

public class Barcode_Image {
	public static void createImage(String image_name, String barcode) throws IOException {
		Code128Bean code128Bean = new Code128Bean();
		code128Bean.setHeight(15f);
		code128Bean.setModuleWidth(0.3);
		code128Bean.setQuietZone(10);
		code128Bean.doQuietZone(true);
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BitmapCanvasProvider canvas = new BitmapCanvasProvider(byteArrayOutputStream, "image/x-png", 300, BufferedImage.TYPE_BYTE_BINARY, false, 0);
		code128Bean.generateBarcode(canvas, barcode);
		canvas.finish();
		
		FileOutputStream fileOutputStream = new FileOutputStream("C:\\lcphuoc\\projects\\shoppingcart-project\\shoppingcart-webparent\\shoppingcart-backend\\barcode\\"+image_name);
		fileOutputStream.write(byteArrayOutputStream.toByteArray());
		fileOutputStream.flush();
		fileOutputStream.close();
	}
}
