package cn.zthz.actor.rest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.tool.common.FileType;
import cn.zthz.tool.common.HashUtils;
import cn.zthz.tool.common.JsonUtils;
import cn.zthz.tool.common.Tuple;
import cn.zthz.tool.picture.PictureService;
import cn.zthz.tool.picture.PictureServiceException;

public class FunctionalRest extends AuthorityRest {

	protected String savePicture(String base64ImageData, HttpServletRequest request, HttpServletResponse response) {

		byte[] pictureData = Base64.decodeBase64(base64ImageData.getBytes());
		String fileType = FileType.getFileTypeByStream(pictureData);
		String md5 = HashUtils.md5(pictureData);
		try {
			return PictureService.instance.save(fileType, md5, pictureData);
		} catch (PictureServiceException e) {
			putError(request, response, "save image failed! " + e.getMessage(), ErrorCodes.SERVER_INNER_ERROR);
			return null;
		}
	}

	protected String save(byte[] data, HttpServletRequest request, HttpServletResponse response) {

		String fileType = FileType.getFileTypeByStream(data);
		String md5 = HashUtils.md5(data);
		try {
			return PictureService.instance.save(fileType, md5, data);
		} catch (PictureServiceException e) {
			putError(request, response, "save image failed! " + e.getMessage(), ErrorCodes.SERVER_INNER_ERROR);
			return null;
		}
	}

	protected Map<String, String> savePictureThumbs(String base64ImageData, HttpServletRequest request, HttpServletResponse response,
			List<Tuple<Integer, Integer>> sizes) {
		return savePictureThumbs(Base64.decodeBase64(base64ImageData.getBytes()), request, response, sizes);
	}

	protected Map<String, String> savePictureThumbs(File file, HttpServletRequest request, HttpServletResponse response,
			List<Tuple<Integer, Integer>> sizes) {

		try {
			return savePictureThumbs(FileUtils.readFileToByteArray(file), request, response, sizes);
		} catch (IOException e) {
			putError(request, response, "save image failed! " + e.getMessage(), ErrorCodes.SERVER_INNER_ERROR);
			return null;
		}
	}

	protected Map<String, String> savePictureThumbs(byte[] pictureData, HttpServletRequest request, HttpServletResponse response,
			List<Tuple<Integer, Integer>> sizes) {

		try {
			String fileType = FileType.getFileTypeByStream(pictureData);
			String md5 = HashUtils.md5(pictureData);
			return PictureService.instance.save(fileType, md5, pictureData, sizes);
		} catch (PictureServiceException e) {
			putError(request, response, "save image failed! " + e.getMessage(), ErrorCodes.SERVER_INNER_ERROR);
			return null;
		}
	}

	protected List<String> savePictures(List<String> base64ImageDatas, HttpServletRequest request, HttpServletResponse response) {
		List<String> result = new ArrayList<>(base64ImageDatas.size());
		for (String base64ImageData : base64ImageDatas) {
			byte[] pictureData = Base64.decodeBase64(base64ImageData.getBytes());
			String fileType = FileType.getFileTypeByStream(pictureData);
			if (null == fileType) {
				continue;
			}
			String md5 = HashUtils.md5(pictureData);
			try {
				result.add(PictureService.instance.save(fileType, md5, pictureData));
			} catch (PictureServiceException e) {
				putError(request, response, "save image failed! " + e.getMessage(), ErrorCodes.SERVER_INNER_ERROR);
				return result;
			}
		}
		return result;
	}

}
