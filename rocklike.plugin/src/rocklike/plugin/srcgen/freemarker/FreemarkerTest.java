package rocklike.plugin.srcgen.freemarker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import rocklike.plugin.srcgen.freemarker.NameAndPathResolver.ClassType;
import rocklike.plugin.util.HongFreemarkerHelper;
import rocklike.plugin.util.HongEclipseUtil;

public class FreemarkerTest {
	
	public static void main(String[] args) {
		FreemarkerTest main = new FreemarkerTest();
	}

	private IProject project ;
	


	@Inject
	void gen(){
		Map<String,Object> m = new HashMap();
		m.put("name", "홍길동");
		
		String[] arr = {"aa","bb","cc"};
		m.put("list", arr);
		
		
		
		String str = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("hong/plugintest/freemarker/test.ftl").generate();
		System.out.println("===== 출력물 =======");
		System.out.println(str);
	}
	
	
	
	public void genOutputModelFile() throws CoreException, IOException{
		Map<String,Object> m = new HashMap();
		String contents;
		
		m.put("f", new FreemarkerFunction());
		
		NameAndPathResolver inputModelResolver = new KtNameAndPathResolver();
		inputModelResolver.setProject(HongEclipseUtil.getSelectedProject());
		inputModelResolver.setClzType(ClassType.Model);
		inputModelResolver.setName("NewOutput5");
		inputModelResolver.setPkg("com.kt.cms.cuc.acq.vv");
		
		m.put("model", inputModelResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("hong/plugintest/freemarker/outputModelNewFile.ftl").generate();
		inputModelResolver.createFileNatively(contents);
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		System.out.printf("== interface 만듦 : %s \n", inputModelResolver.getName());
	}
	
	
	
	
	public void genInputModelFile() throws CoreException, IOException{
		Map<String,Object> m = new HashMap();
		String contents;
		
		m.put("f", new FreemarkerFunction());
		
		NameAndPathResolver inputModelResolver = new KtNameAndPathResolver();
		inputModelResolver.setProject(HongEclipseUtil.getSelectedProject());
		inputModelResolver.setClzType(ClassType.Model);
		inputModelResolver.setName("NewInput3Condition");
		inputModelResolver.setPkg("com.kt.cms.cuc.acq.vv");
		
		m.put("model", inputModelResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("hong/plugintest/freemarker/inputModelNewFile.ftl").generate();
		inputModelResolver.createFileNatively(contents);
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		System.out.printf("== interface 만듦 : %s \n", inputModelResolver.getName());
	}
	
	
	
	public void genControllerFile() throws CoreException, IOException{
		Map<String,Object> m = new HashMap();
		String contents;
		
		m.put("f", new FreemarkerFunction());
		
		NameAndPathResolver controllerResolver = new KtNameAndPathResolver();
		controllerResolver.setProject(HongEclipseUtil.getSelectedProject());
		controllerResolver.setClzType(ClassType.Controller);
		controllerResolver.setName("NewInput3");
		controllerResolver.setPkg("com.kt.cms.cuc.acq.vv");
		
		m.put("model", controllerResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("hong/plugintest/freemarker/controllerNewFile.ftl").generate();
		controllerResolver.createFileNatively(contents);
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		System.out.printf("== interface 만듦 : %s \n", controllerResolver.getName());
	}
	
	
	
	public void genServiceFile() throws CloneNotSupportedException, CoreException, IOException{
		Map<String,Object> m = new HashMap();
		String contents;
		
		m.put("f", new FreemarkerFunction());
		
		NameAndPathResolver interfaceResolver = new KtNameAndPathResolver();
		interfaceResolver.setProject(HongEclipseUtil.getSelectedProject());
		interfaceResolver.setClzType(ClassType.Service);
		interfaceResolver.setName("NewInput2");
		interfaceResolver.setPkg("com.kt.cms.cuc.acq.ca");
		interfaceResolver.setInputModel("com.kt.cms.cuc.dst.dm.domain.MyCondition");
		interfaceResolver.setOutputModel("com.kt.cms.cuc.dst.dm.domain.MyOutput");
		
		m.put("model", interfaceResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("hong/plugintest/freemarker/serviceInterfaceNewFile.ftl").generate();
		interfaceResolver.createFileNatively(contents);
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		System.out.printf("== interface 만듦 : %s \n", interfaceResolver.getName());
		
		NameAndPathResolver implResolver = (NameAndPathResolver) interfaceResolver.clone();
		implResolver.setClzType(ClassType.ServiceImpl);
		m.put("model", implResolver);
		m.put("interface", interfaceResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("hong/plugintest/freemarker/serviceImplNewFile.ftl").generate();
		implResolver.createFileNatively(contents);
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		System.out.printf("== impl 만듦 : %s \n", implResolver.getName());
		
		System.out.println();
	}
	
	
	public void genDaoFile() throws CoreException, CloneNotSupportedException, IOException{
		Map<String,Object> m = new HashMap();
		String contents;
		
		m.put("f", new FreemarkerFunction());
		
		NameAndPathResolver interfaceResolver = new KtNameAndPathResolver();
		interfaceResolver.setProject(HongEclipseUtil.getSelectedProject());
		interfaceResolver.setClzType(ClassType.Dao);
		interfaceResolver.setName("NewInput2");
		interfaceResolver.setPkg("com.kt.cms.cuc.acq.ca");
		interfaceResolver.setInputModel("com.kt.cms.cuc.dst.dm.domain.MyCondition");
		interfaceResolver.setOutputModel("com.kt.cms.cuc.dst.dm.domain.MyOutput");
		
		m.put("model", interfaceResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("hong/plugintest/freemarker/daoInterfaceNewFile.ftl").generate();
		interfaceResolver.createFileNatively(contents);
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		System.out.printf("== interface 만듦 : %s \n", interfaceResolver.getName());
		
		NameAndPathResolver implResolver = (NameAndPathResolver) interfaceResolver.clone();
		implResolver.setClzType(ClassType.DaoImpl);
		m.put("model", implResolver);
		m.put("interface", interfaceResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("hong/plugintest/freemarker/daoImplNewFile.ftl").generate();
		implResolver.createFileNatively(contents);
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		System.out.printf("== impl 만듦 : %s \n", implResolver.getName());
		
		
		NameAndPathResolver mybatisXmlResolver = (NameAndPathResolver) interfaceResolver.clone();
		mybatisXmlResolver.setClzType(ClassType.MybatisXml);
		
		m.put("model", mybatisXmlResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("hong/plugintest/freemarker/mybatisXmlNewFile.ftl").generate();
		mybatisXmlResolver.createFileNatively(contents);
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		System.out.printf("== interface 만듦 : %s \n", mybatisXmlResolver.getName());
		
		
		
		System.out.println();
	}
	

	
	
	public void genModelFile() throws CoreException, IOException{
		Map<String,Object> m = new HashMap();
		String contents;
		
		m.put("f", new FreemarkerFunction());
		
		NameAndPathResolver inputModelResolver = new KtNameAndPathResolver();
		inputModelResolver.setProject(HongEclipseUtil.getSelectedProject());
		inputModelResolver.setClzType(ClassType.Model);
		inputModelResolver.setName("NewInput3Condition");
		inputModelResolver.setPkg("com.kt.cms.cuc.acq.vv");
		
		m.put("model", inputModelResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("hong/plugintest/freemarker/modelNewFile.ftl").generate();
		inputModelResolver.createFileNatively(contents);
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		System.out.printf("== interface 만듦 : %s \n", inputModelResolver.getName());
	}
	
	


	//==== setter/getter
	public IProject getProject() {
		return project;
	}
	
	public void setProject(IProject project) {
		this.project = project;
	}

}
