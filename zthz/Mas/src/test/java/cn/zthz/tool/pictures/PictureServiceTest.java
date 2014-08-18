package cn.zthz.tool.pictures;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.alibaba.fastjson.util.Base64;

import cn.zthz.tool.common.HashUtils;
import cn.zthz.tool.common.JsonUtils;
import cn.zthz.tool.picture.Picture;
import cn.zthz.tool.picture.PictureService;
import cn.zthz.tool.picture.PictureServiceException;

public class PictureServiceTest {

//	@Test
//	public void testSave() throws PictureServiceException {
//		PictureService.instance.save("p1.jpg", "image/jpeg", "111111", 86, "/home/uzoice/Pictures/3.jpg");
//	}
	@Test
	public void testSaveBytes() throws PictureServiceException, FileNotFoundException, IOException {
		byte[] b = IOUtils.toByteArray(new FileInputStream("/home/uzoice/Pictures/1.jpg"));
		String md5 = HashUtils.md5(b);
		System.out.println(PictureService.instance.save("jpg",  md5, b));
		
		
	}

	@Test
	public void testGet() throws PictureServiceException {
		Picture picture = PictureService.instance.getPicture("ff8080813c0ba780013c0ba780ef0000");
		System.out.println(JsonUtils.toJsonString(picture));
	}

	@Test
	public void testGetPictures() throws PictureServiceException {
		List<String> ids = new LinkedList<>();
		ids.add("ff8080813c0ba780013c0ba780ef0000");
		ids.add("ff8080813c0ba543013c0ba544080000");
		List<Picture> pictures = PictureService.instance.getPictures(ids);
		System.out.println(JsonUtils.toJsonString(pictures));
	}


	@Test
	public void testUpdate() throws PictureServiceException {
		Map<String, Object> newProperties = new HashMap<String, Object>(1);
		newProperties.put("status", 3);
		PictureService.instance.update("ff8080813c0ba543013c0ba544080000", newProperties);
	}
	@Test
	public void testDelete() throws PictureServiceException {
		
		PictureService.instance.deletePicture("ff8080813c0ba543013c0ba544080000");
	}
	
	@Test
	public void testBase64() throws FileNotFoundException, IOException{
		byte[] b = org.apache.commons.codec.binary.Base64.encodeBase64(IOUtils.toByteArray(new FileInputStream("/home/uzoice/Pictures/1.jpg")));
		String bs= new String(b);
		System.out.println(bs);
		
		
	}
}
