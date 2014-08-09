package rocklike.plugin.srcgen.freemarker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import rocklike.plugin.srcgen.freemarker.NameAndPathResolver.ClassType;
import rocklike.plugin.srcgen.wizards.GenAllModelPage.AllModel;
import rocklike.plugin.util.HongFreemarkerHelper;
import rocklike.plugin.util.HongEditorHelper;

public class SourceGenerator {
	
	private IProject project ;

	public SourceGenerator(IProject project) {
	    this.project = project;
    }

	@Inject
	void gen(){
		Map<String,Object> m = new HashMap();
		m.put("name", "홍길동");
		
		String[] arr = {"aa","bb","cc"};
		m.put("list", arr);
		
		String str = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("rocklike/plugin/srcgen/freemarker/ftl/test.ftl").generate();
		System.out.println("===== 출력물 =======");
		System.out.println(str);
	}
	
	
	public void genNewFile(SrcGenModel model, AllModel.GenTarget t){
		try {
			switch (t) {
			case Model:
				genModelFile(model);
				break;			
			case InputModel:
				genInputModelFile(model);
				break;
			case OutputListModel:
				genOutputListModelFile(model);
				break;
			case OutputRowModel:
				genOutputRowModelFile(model);
				break;
			case Controller:
				genControllerFile(model);
				break;
			case Service:
				genServiceFile(model);
				break;
			case Dao:
				genDaoFile(model);
				break;
			default:
				throw new RuntimeException("뭬냐. 여기로 오면 안된다.");
			}
	        
        } catch (Exception e) {
        	e.printStackTrace();
        	throw new RuntimeException(e);
        }
	}
	
	
	public boolean isExists(SrcGenModel model, AllModel.GenTarget t){
		try {
			NameAndPathResolver r = new KtNameAndPathResolver();
			r.setProject(project);
			r.setName(model.getClassName());
			r.setPkg(model.getPkgName());
			r.setDescription(model.getDescription());
			
			switch (t) {
			case Model:
				r.setClzType(ClassType.Model);
				break;
			case InputModel:
				r.setClzType(ClassType.InputModel);
				break;
			case OutputListModel:
				r.setClzType(ClassType.OutputListModel);
				break;
			case OutputRowModel:
				r.setClzType(ClassType.OutputRowModel);
				break;
			case Controller:
				r.setClzType(ClassType.Controller);
				break;
			case Service:
				r.setClzType(ClassType.Service);
				break;
			case Dao:
				r.setClzType(ClassType.Dao);
				break;
			default:
				throw new RuntimeException("뭬냐. 여기로 오면 안된다.");
			}
			
			return r.isExists();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	public void genOutputListModelFile(SrcGenModel model) throws CoreException, IOException{
		Map<String,Object> m = new HashMap();
		String contents;
		
		m.put("f", new FreemarkerFunction());
		
		NameAndPathResolver modelResolver = new KtNameAndPathResolver();
		modelResolver.setProject(project);
		modelResolver.setClzType(ClassType.OutputListModel);
		modelResolver.setName(model.getClassName());
		modelResolver.setPkg(model.getPkgName());
		modelResolver.setDescription(model.getDescription());
		
		m.put("model", modelResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("rocklike/plugin/srcgen/freemarker/ftl/outputListModelNewFile.ftl").generate();
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		
		modelResolver.createFileNatively(contents);
//		HongEditorHelper.openTextEditorAndSelect(modelResolver.createFileNatively(contents));
	}
	
	
	
	public void genOutputRowModelFile(SrcGenModel model) throws CoreException, IOException{
		Map<String,Object> m = new HashMap();
		String contents;
		
		m.put("f", new FreemarkerFunction());
		
		NameAndPathResolver modelResolver = new KtNameAndPathResolver();
		modelResolver.setProject(project);
		modelResolver.setClzType(ClassType.OutputRowModel);
		modelResolver.setName(model.getClassName());
		modelResolver.setPkg(model.getPkgName());
		modelResolver.setDescription(model.getDescription());
		
		m.put("model", modelResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("rocklike/plugin/srcgen/freemarker/ftl/outputRowModelNewFile.ftl").generate();
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		
		modelResolver.createFileNatively(contents);
//		HongEditorHelper.openTextEditorAndSelect( modelResolver.createFileNatively(contents) );
	}
	
	
	
	
	public void genInputModelFile(SrcGenModel model) throws CoreException, IOException{
		Map<String,Object> m = new HashMap();
		String contents;
		
		m.put("f", new FreemarkerFunction());
		
		NameAndPathResolver inputModelResolver = new KtNameAndPathResolver();
		inputModelResolver.setProject(project);
		inputModelResolver.setClzType(ClassType.InputModel);
		inputModelResolver.setName(model.getClassName());
		inputModelResolver.setPkg(model.getPkgName());
		inputModelResolver.setDescription(model.getDescription());
		
		m.put("model", inputModelResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("rocklike/plugin/srcgen/freemarker/ftl/inputModelNewFile.ftl").generate();
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		
		inputModelResolver.createFileNatively(contents);
//		HongEditorHelper.openTextEditorAndSelect( inputModelResolver.createFileNatively(contents) );
	}
	
	
	
	public void genControllerFile(SrcGenModel model) throws CoreException, IOException{
		Map<String,Object> m = new HashMap();
		String contents;
		
		m.put("f", new FreemarkerFunction());
		
		NameAndPathResolver controllerResolver = new KtNameAndPathResolver();
		controllerResolver.setProject(project);
		controllerResolver.setClzType(ClassType.Controller);
		controllerResolver.setName(model.getClassName());
		controllerResolver.setPkg(model.getPkgName());
		controllerResolver.setDescription(model.getDescription());
		
		m.put("model", controllerResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("rocklike/plugin/srcgen/freemarker/ftl/controllerNewFile.ftl").generate();
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		
		controllerResolver.createFileNatively(contents);
//		HongEditorHelper.openTextEditorAndSelect( controllerResolver.createFileNatively(contents) );
	}
	
	
	
	public void genServiceFile(SrcGenModel model) throws CloneNotSupportedException, CoreException, IOException{
		Map<String,Object> m = new HashMap();
		String contents;
		
		m.put("f", new FreemarkerFunction());
		
		NameAndPathResolver interfaceResolver = new KtNameAndPathResolver();
		interfaceResolver.setProject(project);
		interfaceResolver.setClzType(ClassType.Service);
		interfaceResolver.setName(model.getClassName());
		interfaceResolver.setPkg(model.getPkgName());
		interfaceResolver.setDescription(model.getDescription());
		
		m.put("model", interfaceResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("rocklike/plugin/srcgen/freemarker/ftl/serviceInterfaceNewFile.ftl").generate();
		interfaceResolver.createFileNatively(contents);
//		HongEditorHelper.openTextEditorAndSelect( interfaceResolver.createFileNatively(contents) );
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		
		NameAndPathResolver implResolver = (NameAndPathResolver) interfaceResolver.clone();
		implResolver.setClzType(ClassType.ServiceImpl);
		m.put("model", implResolver);
		m.put("interface", interfaceResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("rocklike/plugin/srcgen/freemarker/ftl/serviceImplNewFile.ftl").generate();
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		
		implResolver.createFileNatively(contents);
//		HongEditorHelper.openTextEditorAndSelect( implResolver.createFileNatively(contents) );
	}
	
	
	public void genDaoFile(SrcGenModel model) throws CoreException, CloneNotSupportedException, IOException{
		Map<String,Object> m = new HashMap();
		String contents;
		
		m.put("f", new FreemarkerFunction());
		
		NameAndPathResolver interfaceResolver = new KtNameAndPathResolver();
		interfaceResolver.setProject(project);
		interfaceResolver.setClzType(ClassType.Dao);
		interfaceResolver.setName(model.getClassName());
		interfaceResolver.setPkg(model.getPkgName());
		interfaceResolver.setDescription(model.getDescription());
		
		m.put("model", interfaceResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("rocklike/plugin/srcgen/freemarker/ftl/daoInterfaceNewFile.ftl").generate();
		interfaceResolver.createFileNatively(contents);
//		HongEditorHelper.openTextEditorAndSelect( interfaceResolver.createFileNatively(contents) );
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		
		NameAndPathResolver implResolver = (NameAndPathResolver) interfaceResolver.clone();
		implResolver.setClzType(ClassType.DaoImpl);
		m.put("model", implResolver);
		m.put("interface", interfaceResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("rocklike/plugin/srcgen/freemarker/ftl/daoImplNewFile.ftl").generate();
		implResolver.createFileNatively(contents) ;
//		HongEditorHelper.openTextEditorAndSelect( implResolver.createFileNatively(contents) );
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
		
		NameAndPathResolver mybatisXmlResolver = (NameAndPathResolver) interfaceResolver.clone();
		mybatisXmlResolver.setClzType(ClassType.MybatisXml);
		
		m.put("model", mybatisXmlResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("rocklike/plugin/srcgen/freemarker/ftl/mybatisXmlNewFile.ftl").generate();
		mybatisXmlResolver.createFileNatively(contents);
//		HongEditorHelper.openTextEditorAndSelect( mybatisXmlResolver.createFileNatively(contents) );
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
	}
	
	
	public void genModelFile(SrcGenModel model) throws CoreException, IOException{
		Map<String,Object> m = new HashMap();
		String contents;
		
		m.put("f", new FreemarkerFunction());
		
		NameAndPathResolver inputModelResolver = new KtNameAndPathResolver();
		inputModelResolver.setProject(project);
		inputModelResolver.setClzType(ClassType.Model);
		inputModelResolver.setName(model.getClassName());
		inputModelResolver.setPkg(model.getPkgName());
		inputModelResolver.setDescription(model.getDescription());
		
		m.put("model", inputModelResolver);
		contents = HongFreemarkerHelper.configure().setContentMap(m).setFtlPath("rocklike/plugin/srcgen/freemarker/ftl/modelNewFile.ftl").generate();
		inputModelResolver.createFileNatively(contents);
//		HongEditorHelper.openTextEditorAndSelect( inputModelResolver.createFileNatively(contents) );
		
		System.out.println("===== 결과 =======");
		System.out.println(contents);
	}
	
	
	public static class SrcGenModel{
		String pkgName;
		String className;
		String description;
		
		public String getPkgName() {
			return pkgName;
		}
		public void setPkgName(String pkgName) {
			this.pkgName = pkgName;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	}
	

}
