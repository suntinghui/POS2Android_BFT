package com.bft.pos.util;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

/**
 * 
 * 
 * description: 动态验证码生成<br/>
 * 
 * author: maple <br/>
 * 
 * date:20140418 <br/>
 * 
 */
public class SecurityCodeUtil {

	private static final int DEFAULT_CODE_LENGTH = 4;
	private static final int DEFAULT_FONT_SIZE = 25;
	private static final int DEFAULT_LINE_NUMBER = 3;
	private static final int BASE_PADDING_LEFT = 5;
	private static final int RANGE_PADDING_LEFT = 10;
	private static final int BASE_PADDING_TOP = 20;
	private static final int RANGE_PADDING_TOP = 10;
	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_HEIGHT = 39;
	private static final int PADDING_LEFT = 7;
	private static final int PADDING_TOP = 20;
	

	private static final char[] CHARS = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
		'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
		'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
		'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
		'X', 'Y', 'Z' };

	private static SecurityCodeUtil securityCodeUtil = null;

	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
	private int base_padding_left = BASE_PADDING_LEFT;
	private int base_padding_top = BASE_PADDING_TOP;
	private int range_padding_left = RANGE_PADDING_LEFT;
	private int range_padding_top = RANGE_PADDING_TOP;
	private int codeLength = DEFAULT_CODE_LENGTH;
	private int lineNumber = DEFAULT_LINE_NUMBER;
	private int fontSize = DEFAULT_FONT_SIZE;

	private String code;
	private int padding_left = PADDING_LEFT;
	private int padding_top = PADDING_TOP;
	/**
	 * 1：验证码生成为随机生成
	 * 2：验证码生成为自定义字符串
	 * */
	private int type = 2; 
	
	
	private Random random = new Random();

	public SecurityCodeUtil() {
	}

	public static SecurityCodeUtil getInstance() {
		if (securityCodeUtil == null) {
			securityCodeUtil = new SecurityCodeUtil();
		}
		return securityCodeUtil;
	}

	public String createCode() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < codeLength; i++) {
			buffer.append(CHARS[random.nextInt(CHARS.length)]);
		}
		return buffer.toString();
	}

	/**生成图片验证码
	 * @param captcha	字符验证码：
	 * captcha 	=	null:	自动生成验证码
	 * captcha != 	null：	自定义验证码
	 * @return
	 */
	public Bitmap createCodeBitmap(String captcha) {
		base_padding_left = width / codeLength;
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		code = createCode();
		Log.i("code:", code);
		canvas.drawColor(Color.WHITE);
		/**字体的样式style*/
		Paint paint = new Paint();
		//设置字体大小
		paint.setTextSize(fontSize);
//		设置字体颜色
		paint.setColor(Color.BLUE);
		if(captcha != null)
			type = 2;
		
		switch (type) {
		case 1:
			for (int i = 0; i < code.length(); i++) {
				randomTextStyle(paint);
				randomPadding(i);
				canvas.drawText(String.valueOf(code.charAt(i)), padding_left,
						base_padding_top + range_padding_top, paint);
			}
			break;
		case 2:
				randomTextStyle(paint);
				/**字体在整个图片中的位置坐标
				 * 参数
				 * 1：字符验证码
				 * 2：x坐标左右填充
				 * 3：y坐标上下填充
				 * 4：字体的一个样式 style
				 * */
				canvas.drawText(captcha, padding_left,
						base_padding_top + range_padding_top, paint);
			break;
		default:
			break;
		}
		
	
		for (int i = 0; i < lineNumber; i++) {
			drawLine(canvas, paint);
		}
		for (int i = 0; i < 255; i++) {
			drawPoints(canvas, paint);
		}
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return bitmap;
	}

	public String getCode() {
		return code;
	}

	private int randomColor(int rate) {
		int red = random.nextInt(256) / rate;
		int green = random.nextInt(256) / rate;
		int blue = random.nextInt(256) / rate;
		return Color.rgb(red, green, blue);
	}

	private int randomColor() {
		return randomColor(1);
	}

	private void randomTextStyle(Paint paint) {
		int color = randomColor();
		paint.setColor(color);
		paint.setFakeBoldText(random.nextBoolean());
		float skewX = random.nextInt(11) / 10;
		skewX = random.nextBoolean() ? skewX : -skewX;
		paint.setTextSkewX(skewX);
	}

	private void randomPadding() {
		padding_left += base_padding_left + random.nextInt(range_padding_left);
		// padding_top += base_padding_top + random.nextInt(range_padding_top);
	}

	private void randomPadding(int i) {
		padding_left = base_padding_left * i
				+ random.nextInt(range_padding_left);
	}

	private void drawLine(Canvas canvas, Paint paint) {
		int color = randomColor();
		int startX = random.nextInt(width);
		int startY = random.nextInt(height);
		int stopX = random.nextInt(width);
		int stopY = random.nextInt(height);
		paint.setStrokeWidth(1);
		paint.setColor(color);
		canvas.drawLine(startX, startY, stopX, stopY, paint);
	}

	private void drawPoints(Canvas canvas, Paint paint){
		int color = randomColor();
		int stopX = random.nextInt(width);
		int stopY = random.nextInt(height);
		paint.setStrokeWidth(1);
		paint.setColor(color);
		canvas.drawPoint(stopX, stopY, paint);
	}

}
