package rocklike.plugin.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class TestScrolledComposite {

	public static void main(String[] args) {
		Display display = new Display();
		Image image1 = display.getSystemImage(SWT.ICON_WORKING);
		Image image2 = display.getSystemImage(SWT.ICON_QUESTION);
		Image image3 = display.getSystemImage(SWT.ICON_ERROR);

		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		final ScrolledComposite scrollComposite = new ScrolledComposite(shell, SWT.V_SCROLL | SWT.BORDER);

		final Composite parent = new Composite(scrollComposite, SWT.NONE);
		for (int i = 0; i <= 100; i++) {
			Label label = new Label(parent, SWT.NONE);
			if (i % 3 == 0)
				label.setImage(image1);
			if (i % 3 == 1)
				label.setImage(image2);
			if (i % 3 == 2)
				label.setImage(image3);
		}
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.wrap = true;
		parent.setLayout(layout);

		scrollComposite.setContent(parent);
		scrollComposite.setExpandVertical(true);
		scrollComposite.setExpandHorizontal(true);
		scrollComposite.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle r = scrollComposite.getClientArea();
				scrollComposite.setMinSize(parent.computeSize(r.width, SWT.DEFAULT));
			}
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}




	 public static void main3 (String [] args) {
	      Display display = new Display ();
	      Color red = display.getSystemColor(SWT.COLOR_RED);
	      Color blue = display.getSystemColor(SWT.COLOR_BLUE);
	      Shell shell = new Shell (display);
	      shell.setLayout(new FillLayout());

	      // set the size of the scrolled content - method 1
	      final ScrolledComposite sc1 = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	      final Composite c1 = new Composite(sc1, SWT.NONE);
	      sc1.setContent(c1);
	      c1.setBackground(red);
	      GridLayout layout = new GridLayout();
	      layout.numColumns = 4;
	      c1.setLayout(layout);
	      Button b1 = new Button (c1, SWT.PUSH);
	      b1.setText("first button");
	      c1.setSize(c1.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	      // set the minimum width and height of the scrolled content - method 2
	      final ScrolledComposite sc2 = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	      sc2.setExpandHorizontal(true);
	      sc2.setExpandVertical(true);
	      final Composite c2 = new Composite(sc2, SWT.NONE);
	      sc2.setContent(c2);
	      c2.setBackground(blue);
	      layout = new GridLayout();
	      layout.numColumns = 4;
	      c2.setLayout(layout);
	      Button b2 = new Button (c2, SWT.PUSH);
	      b2.setText("first button");
	      sc2.setMinSize(c2.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	      Button add = new Button (shell, SWT.PUSH);
	      add.setText("add children");
	      final int[] index = new int[]{0};
	      add.addListener(SWT.Selection, new Listener() {
	          public void handleEvent(Event e) {
	              index[0]++;
	              Button button = new Button(c1, SWT.PUSH);
	              button.setText("button "+index[0]);
	              // reset size of content so children can be seen - method 1
	              c1.setSize(c1.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	              c1.layout();

	              button = new Button(c2, SWT.PUSH);
	              button.setText("button "+index[0]);
	              // reset the minimum width and height so children can be seen - method 2
	              sc2.setMinSize(c2.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	              c2.layout();
	          }
	      });

	      shell.open ();
	      while (!shell.isDisposed ()) {
	          if (!display.readAndDispatch ()) display.sleep ();
	      }
	      display.dispose ();
	 }


}