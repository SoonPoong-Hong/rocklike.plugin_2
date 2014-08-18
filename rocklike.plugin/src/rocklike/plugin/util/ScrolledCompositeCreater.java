package rocklike.plugin.util;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;

public class ScrolledCompositeCreater {

	public static Composite createScrolledComposite(Composite parent){
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.NULL);
		GridDataFactory.fillDefaults().grab(true,true).align(SWT.FILL, SWT.FILL).applyTo(sc);
		Composite inside = new Composite(sc, SWT.NULL);
		sc.setContent(inside);
		return inside;
	}


	public static void setInsideCompositeSize(Composite inside){
		inside.setSize(inside.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		inside.layout();
	}
}
