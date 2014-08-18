package rocklike.plugin.util;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class StaticImageHelper {

	public static final Image RED_IMG = getImage("/icons/red.png");

	private static Image getImage(String path){
		Bundle b = FrameworkUtil.getBundle(StaticImageHelper.class);
		URL url = FileLocator.find(b, new Path(path), null);
		ImageDescriptor imgDesc = ImageDescriptor.createFromURL(url);
		return imgDesc.createImage();
	}

}
