package cn.zthz.tool.picture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.crypto.RuntimeCryptoException;

import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.tool.common.FileType;
import cn.zthz.tool.common.HashUtils;
import cn.zthz.tool.common.ImageUtils;
import cn.zthz.tool.common.JsonUtils;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.common.Tuple;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.DbOperations;
import cn.zthz.tool.db.ObjectTableInfo;
import cn.zthz.tool.db.SimpleObjectMapping;
import cn.zthz.tool.user.UserException;

public class PictureService {
	public static final Log log = LogFactory.getLog(Picture.class);
	public static final PictureService instance = new PictureService();

	public static String localRootDir = StringUtils.ensureNotEndWith(GlobalConfig.get("image.localRootDir"), "/");
	public static String localDeleteRootDir = StringUtils.ensureNotEndWith(GlobalConfig.get("image.localDeleteRootDir"), "/");
	public static String server = StringUtils.ensureNotEndWith(GlobalConfig.get("image.server"), "/");
	public static String serverRootDir = StringUtils.ensureStartWith(
			StringUtils.ensureNotEndWith(GlobalConfig.get("image.serverRootDir"), "/"), "/");

	// public String save(String name, String contentType , String md5 , int
	// size , String absolutePath) throws PictureServiceException{
	// String filePath= getPath(md5)+"/"+md5+"."+StringUtils.getSuffix(name);
	// File newLocalFile = new File(localRootDir+filePath);
	// try {
	// FileUtils.moveFile(new File(absolutePath), newLocalFile);
	// } catch (IOException e) {
	// log.error("failed to move file" ,e);
	// throw new PictureServiceException("failed to move file",e);
	// }
	// Picture picture = new Picture();
	// picture.setContentType(contentType);
	// picture.setCreateTime(new Timestamp(System.currentTimeMillis()));
	// picture.setId(HashUtils.uuid());
	// picture.setLocalPath(newLocalFile.getAbsolutePath());
	// picture.setMd5(md5);
	// picture.setName(name);
	// picture.setRemotePath(getRemotePath(filePath));
	// picture.setSize(size);
	// picture.setStatus(0);
	// Connection connection = null;
	// try {
	// connection = Connections.instance.get();
	// DbOperations.instance.save(connection, picture, true);
	// return picture.getId();
	// } catch (SQLException e) {
	// log.error(LogUtils.format("picture", picture), e);
	// throw new PictureServiceException("save picture failed!", e);
	// }finally {
	// if (null != connection)
	// try {
	// connection.close();
	// } catch (SQLException e) {
	// log.error("close connect error!" ,e);
	// }
	// }
	// }
	public static void main(String[] args) throws IOException, PictureServiceException {
		List<Tuple<Integer, Integer>> sizes = IconSizes.REQUIREMENT_ICON_SIZES;
		byte[] data = FileUtils.readFileToByteArray(new File("/home/uzoice/Pictures/13d96b8845d"));
		String fileType = FileType.getFileTypeByStream(data);
		System.out.println(fileType);
		Map<String, String> r = instance.save(fileType, "123456789", data , sizes);
		System.out.println(JsonUtils.toJsonString(r));
	}
	public Map<String, String> save(String type, String md5, byte[] data, List<Tuple<Integer, Integer>> sizes) throws PictureServiceException{
		FileOutputStream outputStream = null;
		Map<String, String> result = new HashMap<>();
		try {
			result.put("raw" ,save(type , md5 , data));
			for (Tuple<Integer, Integer> tuple : sizes) {
				String sizeString = tuple.join("x") ;
				String filePath = getPath(md5) + "/" + md5+"."+sizeString+ StringUtils.ensureStartWith(type, ".");
				File file = new File(localRootDir + filePath);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				outputStream = new FileOutputStream(file);
				byte[] resizedData = ImageUtils.resize(data, tuple.key, tuple.value, "jpg".equals(type)?"jpeg":type);
				if(null == resizedData){
					continue;
				}
				IOUtils.write(resizedData, outputStream);
				result.put(sizeString, getRemotePath(filePath));
			}
			return result;
		} catch (IOException e) {
			log.error("failed to move file", e);
			throw new PictureServiceException("failed to move file", e);
		} finally {
			if (null != outputStream)
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	public String save(String type, String md5, byte[] data) throws PictureServiceException {
		String filePath = getPath(md5) + "/" + md5 + ((null == type) ? ".jpg" : StringUtils.ensureStartWith(type, "."));
		FileOutputStream outputStream = null;
		File file = new File(localRootDir + filePath);
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			outputStream = new FileOutputStream(file);
			IOUtils.write(data, outputStream);
			return getRemotePath(filePath);
		} catch (IOException e) {
			log.error("failed to move file", e);
			throw new PictureServiceException("failed to move file", e);
		} finally {
			if (null != outputStream)
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}
	
	public String getPictureLocalePath(String type, String md5){
		String filePath = getPath(md5) + "/" + md5 + ((null == type) ? ".jpg" : StringUtils.ensureStartWith(type, "."));
		return getLocalPath(filePath);
	}

	// public String save(String name, String type , String md5 , byte[] data)
	// throws PictureServiceException{
	// String filePath= getPath(md5)+"/"+md5+((null==type)?"":"."+type);
	// FileOutputStream outputStream = null;
	// File file = new File(localRootDir+filePath);
	// try {
	// if(!file.getParentFile().exists()){
	// file.getParentFile().mkdirs();
	// }
	// outputStream = new FileOutputStream(file);
	// IOUtils.write(data, outputStream);
	// // FileUtils.moveFile(new File(absolutePath), new
	// File(localRootDir+filePath));
	// } catch (IOException e) {
	// log.error("failed to move file" ,e);
	// throw new PictureServiceException("failed to move file",e);
	// }finally{
	// if(null!=outputStream)
	// try {
	// outputStream.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// Picture picture = new Picture();
	// picture.setContentType(type);
	// picture.setCreateTime(new Timestamp(System.currentTimeMillis()));
	// picture.setId(HashUtils.uuid());
	// picture.setLocalPath(file.getAbsolutePath());
	// picture.setMd5(md5);
	// picture.setName(name);
	// picture.setRemotePath(getRemotePath(filePath));
	// picture.setSize(data.length);
	// picture.setStatus(0);
	// Connection connection = null;
	// try {
	// connection = Connections.instance.get();
	// DbOperations.instance.save(connection, picture, true);
	// return picture.getId();
	// } catch (SQLException e) {
	// log.error(LogUtils.format("picture", picture), e);
	// throw new PictureServiceException("save picture failed!", e);
	// }finally {
	// if (null != connection)
	// try {
	// connection.close();
	// } catch (SQLException e) {
	// log.error("close connect error!" ,e);
	// }
	// }
	// }

	public void deletePicture(String id) throws PictureServiceException {
		Picture picture = getPicture(id);
		String targetPath = localDeleteRootDir + getPictureRelativePath(picture.getMd5(), picture.getContentType());
		try {
			FileUtils.moveFile(new File(picture.getLocalPath()), new File(targetPath));
		} catch (IOException e) {
			log.error("failed to move file", e);
			throw new PictureServiceException("failed to move file", e);
		}
		Map<String, Object> args = new HashMap<>(2);
		args.put("status", 1);
		args.put("localPath", targetPath);
		update(id, args);
	}

	public void update(String id, Map<String, Object> newProperties) throws PictureServiceException {
		Connection connection = null;
		try {
			connection = Connections.instance.get();
			Map<String, Object> conditionMap = new HashMap<>(1);
			conditionMap.put("id", id);
			DbOperations.instance.update(connection, ObjectTableInfo.getTableName(Picture.class), conditionMap, newProperties);
		} catch (SQLException e) {
			log.error("update pictures failed" + LogUtils.format("id", id, "newProperties", newProperties), e);
			throw new PictureServiceException("update picture failed!", e);
		} finally {
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}
		}
	}

	public static String getPath(String md5) {
		return "/" + md5.substring(0, 2) + "/" + md5.substring(2, 4);
	}

	public static String getPictureRelativePath(String md5, String type) {
		return getLocalPath(md5) + "/" + md5 + "." + type;
	}

	public Picture getPicture(String id) throws PictureServiceException {
		Connection connection = null;
		try {
			connection = Connections.instance.get();
			return DbOperations.instance.get(connection, id, Picture.class);
		} catch (SQLException e) {
			log.error(LogUtils.format("id", id), e);
			throw new PictureServiceException("get picture failed!", e);
		} finally {
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}
		}
	}

	public List<Picture> getPictures(List<String> ids) throws PictureServiceException {
		if (null == ids || ids.isEmpty()) {
			return new LinkedList<>();
		}
		Connection connection = null;
		try {
			connection = Connections.instance.get();
			String sql = createGetPicturesSql(ids);
			return DbOperations.instance.query(connection, sql, null, Picture.class, SimpleObjectMapping.instance, ids.size());
		} catch (SQLException e) {
			log.error(LogUtils.format("ids", ids), e);
			throw new PictureServiceException("get picture failed!", e);
		} finally {
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("close connect error!", e);
				}
		}
	}

	private String createGetPicturesSql(List<String> ids) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from Picture where status=0 and ");
		sql.append(" id in (");
		for (String id : ids) {
			sql.append('\'');
			sql.append(id);
			sql.append('\'');
			sql.append(',');
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");
		return sql.toString();
	}

	public static String getRemotePath(String path) {
		return "http://" + server + serverRootDir + path;
	}

	public static String getLocalPath(String path) {
		return localRootDir + path;
	}

}
