package rocklike.plugin.util;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class TreeTableCellIndexResolver {

	public static int resolveTreeColumnIndex(Tree t, Point p){
		int rowCnt = t.getItemCount();
		int colCnt = t.getColumnCount();
		
		for(int r=0; r<rowCnt; r++){
			TreeItem item = t.getItem(r);
			for(int c=0; c<colCnt; c++){
				Rectangle rect = item.getBounds(c);
				if(p.x>= rect.x && p.x<= (rect.x+rect.width)){
					return c;
				}
			}
		}
		return -1;
	}

}
