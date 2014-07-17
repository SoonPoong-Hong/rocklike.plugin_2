package rocklike.plugin.util;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TableCellIndexResolver {

	public static Point resolveCellIndex(Table t, Point p){
		int rowCnt = t.getItemCount();
		int colCnt = t.getColumnCount();
		
		for(int r=0; r<rowCnt; r++){
			TableItem item = t.getItem(r);
		
			for(int c=0; c<colCnt; c++){
				Rectangle rect = item.getBounds();
				if(rect.contains(p)){
					return new Point(c, r);
				}
			}
		}
		return null;
	}
	
}
