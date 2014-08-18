package cn.zthz.tool.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

public class ImageUtils {

	public static byte[] resize(byte[] data, int width, int height, String imageType) {
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(new ByteArrayInputStream(data));
			if (null == bufferedImage) {
				throw new RuntimeException("read image data error!");
			}
		} catch (IOException e) {
			throw new RuntimeException("read image data error!");
		}
		ByteArrayOutputStream output = new ByteArrayOutputStream(10240);
		int x = 0;
		int y = 0;
		double ratio = -1;
		int srcWidth = bufferedImage.getWidth();
		int srcHeight = bufferedImage.getHeight();
		double widthRatio = (double) width / srcWidth;
		double heightRatio = (double) height / srcHeight;
		int middleWidth = 0;
		int middleHeight = 0;
		if (widthRatio > 1 && heightRatio > 1) {
			// return bufferdImage;
			return null;
		}
		if (widthRatio == 1 && heightRatio == 1) {
			// return bufferdImage;
			return data;
		}
		if (Math.abs(widthRatio - 1) < Math.abs(heightRatio - 1)) {

			ratio = widthRatio;
			middleWidth = (int) (srcWidth * ratio);
			middleHeight = (int) (srcHeight * ratio);
			x = 0;
			y = (int) ((middleHeight - height) / 2 * 0.25);

		} else {
			ratio = heightRatio;
			middleWidth = (int) (srcWidth * ratio);
			middleHeight = (int) (srcHeight * ratio);
			x = (int) ((middleWidth - width) / 2);
			y = 0;
		}
		try {
			Thumbnails.of(Thumbnails.of(bufferedImage).scale(ratio).asBufferedImage()).scale(1).sourceRegion(x, y, width, height)
					.outputFormat(imageType).outputQuality(1).toOutputStream(output);
			return output.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException("failed to create thumbnail");
		}
	}

}
