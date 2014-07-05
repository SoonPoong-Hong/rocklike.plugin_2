package rocklike.plugin.mybatis.highlight;

import org.eclipse.swt.graphics.RGB;

/**
 * @author Hong SoonPoong (rocklike@gmail.com)
 * @date 2014. 7. 5.
 */
public class RgbConstants {
	public static final RGB COMMENT = new RGB(0X8B, 0X8B, 0X83);  // 회색  
	public static final RGB QUOTED_STRING = new RGB(0X00, 0X00, 0XFF);  // 따옴표 - 파랑 0000FF
	public static final RGB KEYWORD_ORACLE = new RGB(0X7D, 0X26, 0XCD);   // 짙은 보라  (7D26CD)
	public static final RGB KEYWORD_SQL = new RGB(0, 0, 0);   // 검정
	public static final RGB DEFAULT = new RGB(0, 0, 0);   // 검정
	public static final RGB TAG = new RGB(0X0, 0X64, 0X0);   // 짙은 파랑
}
