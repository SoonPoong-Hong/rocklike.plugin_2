package rocklike.plugin.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

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
	
	
	public static IFile getFileFromWorkspaceRoot(String pathFromWorkspace){
		IWorkspaceRoot root = getWorkspaceRoot();
		IPath p = Path.fromOSString(pathFromWorkspace);
		return root.getFile(p);
	}
	
	
	public static IFolder getFolderFromWorkspaceRoot(String pathFromWorkspace){
		IWorkspaceRoot root = getWorkspaceRoot();
		IPath p = Path.fromOSString(pathFromWorkspace);
		return root.getFolder(p);
	}


	public static IFile getFile(IProject proj, String path){
		return proj.getFile(path);
	}
	
	
	public static void createFile(IFile f, String contents ) throws CoreException{
		InputStream is = new ByteArrayInputStream(contents.getBytes(Charset.forName("UTF8")));
		f.create(is, true, null);
	}
	
	
	public static void createFileIfNotExists(IFile f, String contents ) throws CoreException{
		if(!f.exists()){
			createFile(f, contents);
		}
	}
	
	
	public static boolean exists(IProject proj, String path){
		IFile f = proj.getFile(path);
		return f.exists();
	}
	
}
