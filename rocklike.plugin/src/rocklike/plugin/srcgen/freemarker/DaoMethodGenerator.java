package rocklike.plugin.srcgen.freemarker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;

import rocklike.plugin.srcgen.dao.DaoMethodVO;
import rocklike.plugin.util.HongFreemarkerHelper;

public class DaoMethodGenerator {

	public static String genDaoMethod(DaoMethodVO model){
		Map<String,Object> m = new HashMap();
		m.put("f", new FreemarkerFunction());
		m.put("model", model);
		String contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("rocklike/plugin/srcgen/freemarker/ftl/methodDao.ftl").generate();
		return contents;
	}

	public static String genDaoImplMethod(DaoMethodVO model){
		Map<String,Object> m = new HashMap();
		m.put("f", new FreemarkerFunction());
		m.put("model", model);
		String contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("rocklike/plugin/srcgen/freemarker/ftl/methodDaoImpl.ftl").generate();
		return contents;
	}


	public static void testDaoMethodGenerate() throws CoreException, IOException{
		Map<String,Object> m = new HashMap();
		String contents;

		m.put("f", new FreemarkerFunction());

		DaoMethodVO model = new DaoMethodVO();
		model.setSelectUpdateOption("update");
		model.setOneListOption("one");
		model.setMethodName("mySelectListContsList");
		model.setParameterType("aaa.bbb.ParamType");
		model.setResultType("ttt.uuu.ResultType");
		model.setXmlId("my.xml.id._id");

		m.put("model", model);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("rocklike/plugin/srcgen/freemarker/ftl/methodDaoImpl.ftl").generate();

		System.out.println("===== 결과 =======");
		System.out.println(contents);
	}


	public static void main(String[] args) throws CoreException, IOException {
		testDaoMethodGenerate();
    }

}
