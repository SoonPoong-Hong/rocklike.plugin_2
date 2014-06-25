package rocklike.plugin.mybatis.highlight.scanner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class HongColorProvider {


	protected Map<RGB, Color> colorTable= new HashMap(10);

	public void dispose() {
		Iterator<Color> e= colorTable.values().iterator();
		while (e.hasNext()){
			e.next().dispose();
		}
	}

	public Color getColor(RGB rgb) {
		Color color= colorTable.get(rgb);
		if (color == null) {
			color= new Color(Display.getCurrent(), rgb);
			colorTable.put(rgb, color);
		}
		return color;
	}
}
