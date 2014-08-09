package rocklike.plugin.srcgen.freemarker;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;

import rocklike.plugin.util.FileHelper;
import rocklike.plugin.util.HongEclipseFileHelper;
import rocklike.plugin.util.HongFreemarkerHelper;
import rocklike.plugin.util.StaticListHolder;

public class KtNameAndPathResolver extends NameAndPathResolver{
	
	@Override
	public String getPackage(){
		switch (clzType) {
		case Model:
		case InputModel:
		case OutputListModel:
		case OutputRowModel:
			// com.kt.cms.cuc.dst.dm.controller
			return pkg + ".domain";
		case Controller:
			// com.kt.cms.cuc.dst.dm.controller
			return pkg + ".controller";
		case Service:
			// com.kt.cms.cuc.dst.dm.service
			return pkg + ".service";
		case ServiceImpl:
			// com.kt.cms.cuc.dst.dm.service.impl
			return pkg + ".service.impl";
		case Dao:
			// com.kt.cms.cuc.dst.dm.dao
			return pkg + ".dao";
		case DaoImpl:
			 // com.kt.cms.cuc.dst.dm.dao.impl
			return pkg + ".dao.impl";
		case MybatisXml:
			// com.kt.cms.cuc.dst.dm.dao.DmDao
			return pkg + ".dao."+name+"Dao";
		default:
			throw new UnsupportedOperationException("여기는 뭬냐..");
		}
	}
	
	
	@Override
	public String getFilename(){
		switch (clzType) {
		case Model:
		case InputModel:
		case OutputListModel:
		case OutputRowModel:
		case Controller:
		case Service:
		case ServiceImpl:
		case Dao:
		case DaoImpl:
			return getName() + ".java";
		case MybatisXml:
			return getName() + ".xml";
		default:
			throw new UnsupportedOperationException("여기는 뭬냐..");
		}
	}
	
	@Override
	public String getName(){
		switch (clzType) {
		case Model:
			return name ;
		case InputModel:
			return name + "Condition" ;
		case OutputListModel:
			return name + "List" ;
		case OutputRowModel:
			return name ;
		case Controller:
			return name + "Controller";
		case Service:
			return name + "Service";
		case ServiceImpl:
			return name + "ServiceImpl";
		case Dao:
			return name + "Dao";
		case DaoImpl:
			return name + "DaoImpl";
		case MybatisXml:
			return name ;
		default:
			throw new UnsupportedOperationException("여기는 뭬냐..");
		}
	}
	
	@Override
	public String getFolderPath(){
		String basePath = null;
		if(this.clzType==ClassType.MybatisXml){
			basePath = "/src/main/resource/sqlmap/sql/";
		}else{
			basePath = "/src/main/java/";
		}
		
		switch (clzType) {
		case Model:
		case InputModel:
		case OutputListModel:
		case OutputRowModel:
			// com.kt.cms.cuc.dst.dm.controller
			return packageToPath(basePath + pkg + ".domain");
		case Controller:
			// com.kt.cms.cuc.dst.dm.controller
			return packageToPath(basePath + pkg + ".controller");
		case Service:
			// com.kt.cms.cuc.dst.dm.service
			return packageToPath(basePath + pkg + ".service");
		case ServiceImpl:
			// com.kt.cms.cuc.dst.dm.service.impl
			return packageToPath(basePath + pkg + ".service.impl");
		case Dao:
			// com.kt.cms.cuc.dst.dm.dao
			return packageToPath(basePath + pkg + ".dao");
		case DaoImpl:
			 // com.kt.cms.cuc.dst.dm.dao.impl
			return packageToPath(basePath + pkg + ".dao.impl");
		case MybatisXml:
			// com.kt.cms.cuc.dst.dm
			return packageToPath(basePath + pkg);
		default:
			throw new UnsupportedOperationException("여기는 뭬냐..");
		}
	}
	
	@Override
	public String packageToPath(String path){
		String thisPath = path;
		thisPath = thisPath.replaceAll("[.\\\\]","/");
		if(!thisPath.startsWith("/")){
			thisPath = "/"+thisPath;
		}
		if(!thisPath.endsWith("/")){
			thisPath += "/";
		}
		
		switch (this.clzType) {
		case MybatisXml:
			//  com.kt.cms.cuc.dst.dm =>  /dst/dm/
			return thisPath.replace("/com/kt/cms/cuc", "");
		default:
			//  com.kt.cms.cuc.dst.dm => /com/kt/cms/cuc/dst/dm/
			return thisPath;
		}
	}
	
	@Override
	public boolean isExists(){
		IFile f = getIFile();
		return f.exists();
	}
	
	@Override
	public String getFilePath(){
		return getFolderPath() + getFilename();
	}
	
	
	@Override
	public IFile getIFile(){
		IFile f = project.getFile(getFilePath());
		System.out.printf("== 파일경로 [%s] \n", f.getLocation());
		return f;
	}

	
	@Override
    public void createFileNatively(String contents) throws CoreException, IOException {
		IFile ifile = getIFile();
		File f = ifile.getLocation().toFile();
		if(!f.getParentFile().exists()){
			f.getParentFile().mkdirs();
		}
		
		StaticListHolder.instance().add(ifile);
		
		FileHelper.writeFile(f, contents);
    }
	

	@Override
    public IFile createFile(String contents) throws CoreException {
		IFile file = getIFile();
		HongEclipseFileHelper.createFolderRecursively((IFolder)file.getParent());
		HongEclipseFileHelper.createFile(file, contents);
		return file;
    }
	
	
	/**
	 * com.kt.cms.cuc.dst.dm.domain.AdditionalDeployCondition => com.kt.cms.cuc.dst.dm.domain
	 */
	private String extractPackage(String fullname){
		int idx = fullname.lastIndexOf(".");
		return fullname.substring(0, idx);
	}
	
	/**
	 * com.kt.cms.cuc.dst.dm.domain.AdditionalDeployCondition => AdditionalDeployCondition
	 */
	private String extractClassName(String fullname){
		int idx = fullname.lastIndexOf(".");
		return fullname.substring(idx+1);
	}
	
	@Override
    public String getInputModelPackage() {
		return extractPackage(inputModel);
    }


	@Override
    public String getInputModelName() {
		return extractClassName(inputModel);
    }


	@Override
    public String getOutputModelPackage() {
		return extractPackage(outputModel);
    }


	@Override
    public String getOutputModelName() {
		return extractClassName(outputModel);
    }



}
