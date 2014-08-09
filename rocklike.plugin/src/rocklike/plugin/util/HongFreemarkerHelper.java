package rocklike.plugin.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Freemarker 템플릿엔진을 좀더 간단하게 호출할수 있게 만든 Helper class.
 * 
 * 사용법 : 
 * 
		String result = FreemarkerHelper.configure()
						.setClasspathClz(this.getClass())  // 이 메소드를 지정하지 않으면, FreemarkerHelper.class 를 셋팅해서  찾는다. 
						.setContentMap(map)
						.setFtlPath("/rocklike/freemarker/test/test.ftl")
						.generate();
 * 
 * @author 홍순풍(rocklike@gmail.com)
 * @created 2010. 1. 26.
 */
public class HongFreemarkerHelper {

	private static String generate(Config simpleCfg) {
		
		String result;
        try {
	        Configuration cfg = new Configuration();
	        
	        String ftlPath = simpleCfg.ftlPath;
	        String templatePath = null;
	        String templateFile = null;
	        
	        if(ftlPath==null || ftlPath.equals("")){
	        	throw new IllegalArgumentException("템플릿 파일(ftlPath)을 셋팅하지 않았습니다.");
	        }
	        
	        if(ftlPath.indexOf("/")==-1){
	        	templatePath= "";
	        	templateFile = ftlPath;
	        }else if(ftlPath.indexOf("/")!=0){
	        	templatePath = "/" + ftlPath.substring(0, ftlPath.lastIndexOf("/"));
	        	templateFile = ftlPath.substring(ftlPath.lastIndexOf("/")+1);
	        }else{
	        	templatePath = ftlPath.substring(0, ftlPath.lastIndexOf("/"));
	        	templateFile = ftlPath.substring(ftlPath.lastIndexOf("/")+1);
	        }
	        
	        cfg.setClassForTemplateLoading( (simpleCfg.clz==null ? HongFreemarkerHelper.class : simpleCfg.clz), templatePath);
	        cfg.setObjectWrapper(new DefaultObjectWrapper());
	        cfg.setEncoding(Locale.KOREAN, "UTF-8");

	        Template temp = cfg.getTemplate(templateFile);

	        StringWriter out = new StringWriter();
	        temp.process(simpleCfg.contentMap, out);
	        out.flush();
	        result = out.toString(); 
	        out.close();
	        return result;
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
	}
	
	public static Config configure() {
		Config config = Config.newInstance();
		return config;
	}
	
	public static class Config{
		private Class clz;
		private String ftlPath;
		private Map contentMap;
		
		private Config(){			
		}
		
		public static Config newInstance(){
			return new Config();
		}
		
		public String generate() {
			return HongFreemarkerHelper.generate(this);
		}
		
		/**
		 * 해당 ftl을 찾기에 충분한 classloader를 가지고 있는 class.
		 * 만일 셋팅을 안한다면, FreemarkerHelper.class를 쓴다.
		 * @param clz
		 * @return
		 */
		public Config setClasspathClz(Class clz) {
        	this.clz = clz;
        	return this;
        }
		
		/**
		 * 템플릿 파일의 위치 (경로를 포함)
		 * (예 : /rocklike/freemarker/test/test.ftl )
		 * @param ftlPath
		 * @return
		 */
		public Config setFtlPath(String ftlPath) {
        	this.ftlPath = ftlPath;
        	return this;
        }
		
		/**
		 * 템플릿에 들어갈 데이타를 포함하고 있는 Map.
		 * @param contentMap
		 * @return
		 */
		public Config setContentMap(Map contentMap) {
        	this.contentMap = contentMap;
        	return this;
        }
	}
	
}
