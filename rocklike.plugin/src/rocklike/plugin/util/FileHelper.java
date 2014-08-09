package rocklike.plugin.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.eclipse.core.resources.IFile;


public class FileHelper {
	public static boolean writeFile(File f, String contents) throws IOException{
		if(f.exists()){
			return false;
		}
		
		FileOutputStream fos = new FileOutputStream(f, true);
		fos.write( contents.getBytes(Charset.forName("UTF8")) );
		fos.flush();
		fos.close();
		return true;
	}
}
