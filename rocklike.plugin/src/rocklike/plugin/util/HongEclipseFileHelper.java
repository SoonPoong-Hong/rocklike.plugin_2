package rocklike.plugin.util;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author hong
 * @date 2014. 7. 5.
 */
public class HongEclipseFileHelper {
 
	public static IWorkspaceRoot getWorkspaceRoot(){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		return root;
	}
	
	
	public static File convertToOsFile(IFile f){
		return f.getRawLocation().toFile();
	}
	
	
	public static void createFileAndFolderRecursively(IFile f, String contents) throws CoreException{
		IContainer con = f.getParent();
		if(con instanceof IFolder){
			createFolderRecursively((IFolder)con);
			if(!f.exists()){
				f.create(new ByteArrayInputStream(contents.getBytes()), true, null);
			}
		}
	}
	
	public static void createFolderRecursively(IFolder f) throws CoreException{
		if(f==null){
			return;
		}
		if(!f.exists()){
			IContainer con = f.getParent();
			if(con instanceof IFolder){
				createFolderRecursively((IFolder)con);
			}
			f.create(true, true, null);
		}
	}

}
