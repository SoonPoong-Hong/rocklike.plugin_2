package rocklike.plugin.srcgen.freemarker;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import rocklike.plugin.util.HongEclipseFileHelper;

public abstract class NameAndPathResolver implements Cloneable{
	public enum ClassType {Model, InputModel, OutputListModel, OutputRowModel, Controller, Service, ServiceImpl, Dao, DaoImpl, MybatisXml};
	// Controller, Service, ServiceImpl, Dao, DaoImpl, MybatisXml
	
	protected IProject project;
	
	// Controller, Service, ServiceImpl, Dao, DaoImpl, MybatisXml
	protected ClassType clzType;
	/** 예 : com.kt.cms.cuc.dst.dm  */
	protected String pkg;
	/** 예 : Dm */
	protected String name;
	
	protected String model;
	protected String method;
	protected String description;
	
	/** 예 : com.kt.cms.cuc.dst.dm.domain.AdditionalDeployCondition  */
	protected String inputModel;
	/** 예 : com.kt.cms.cuc.dst.dm.domain.AdditionalDeployList  */
	protected String outputModel;
	
	
	public abstract String getPackage();

	public abstract String getFilename();

	public abstract String getFolderPath();

	public abstract String packageToPath(String path);

	public abstract String getFilePath();

	public abstract IFile getIFile();
	
	abstract public boolean isExists();
	
	abstract public String getInputModelPackage();
	abstract public String getInputModelName();
	
	abstract public String getOutputModelPackage();
	abstract public String getOutputModelName();
	
	public void createFolder() throws CoreException {
		IFolder f = project.getFolder(packageToPath(pkg));
		if(!f.exists()){
			HongEclipseFileHelper.createFolderRecursively(f);
		}
	}
	
	public abstract void createFileNatively(String contents) throws CoreException, IOException;
	public abstract IFile createFile(String contents) throws CoreException;
	

	@Override
    public Object clone() throws CloneNotSupportedException {
	    return super.clone();
    }
	
	//==== setter/getter
	
	public String getName() {
		return name;
	}
	public String getPkg() {
		return pkg;
	}
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public ClassType getClzType() {
		return clzType;
	}
	public void setClzType(ClassType clzType) {
		this.clzType = clzType;
	}
	public IProject getProject() {
		return project;
	}
	public void setProject(IProject project) {
		this.project = project;
	}
	public String getInputModel() {
		return inputModel;
	}
	public void setInputModel(String inputModel) {
		this.inputModel = inputModel;
	}
	public String getOutputModel() {
		return outputModel;
	}
	public void setOutputModel(String outputModel) {
		this.outputModel = outputModel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
