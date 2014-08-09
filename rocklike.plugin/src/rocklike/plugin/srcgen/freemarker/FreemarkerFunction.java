package rocklike.plugin.srcgen.freemarker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FreemarkerFunction {
	public String today(){
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
		return f.format(new Date());
	}
}
