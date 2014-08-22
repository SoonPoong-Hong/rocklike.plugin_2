package rocklike.plugin.jdt.quickassist;


import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import rocklike.plugin.util.HongEclipseUtil;
import rocklike.plugin.util.HongEditorHelper;
import rocklike.plugin.util.HongJdtHelper;
import rocklike.plugin.util.StaticImageHelper;

public class HongIJavaCompletionProposal implements IJavaCompletionProposal, ICompletionProposalExtension {

	private IQuickAssistInvocationContext fContext;

	int k = 0;

	@Override
	public void apply(IDocument doc) {
	}


	@Override
	public Point getSelection(IDocument document) {
		return null;
	}

	@Override
	public String getAdditionalProposalInfo() {
		return "각종 잡다구리한 utility들";
	}

	@Override
	public String getDisplayString() {
		return "홍";
	}

	@Override
	public Image getImage() {
		return StaticImageHelper.RED_IMG;
	}

	@Override
	public IContextInformation getContextInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRelevance() {
		return 0;
	}


	@Override
    public void apply(IDocument document, char trigger, int offset) {
		System.out.printf("== apply 2(%s) (offset:%s) \n", trigger, offset);
		System.out.printf("sel text : %s \n", HongEclipseUtil.getTextSelection().getText());

		ICompilationUnit icu = HongJdtHelper.getSelectedICompilationUnit();
		IDocument doc = HongEditorHelper.getIDocument();
		ICompilationUnitAndOffset param = new ICompilationUnitAndOffset(icu, doc, offset);

		HongContextMenuDialog dialog = new HongContextMenuDialog(Display.getDefault().getActiveShell());
		dialog.setICompilationUnitAndOffset(param);
		dialog.open();
    }


	@Override
    public boolean isValidFor(IDocument document, int offset) {
	    // TODO Auto-generated method stub
	    return false;
    }


	@Override
    public char[] getTriggerCharacters() {
	    // TODO Auto-generated method stub
	    return null;
    }


	@Override
    public int getContextInformationPosition() {
	    // TODO Auto-generated method stub
	    return 0;
    }

}
