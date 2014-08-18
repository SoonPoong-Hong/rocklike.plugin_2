package rocklike.plugin.jdt.quickassist;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.IDocument;

public class ICompilationUnitAndOffset{
	public ICompilationUnit icu;
	public IDocument doc;
	public int offset;

	public ICompilationUnitAndOffset(ICompilationUnit icu, IDocument doc, int offset) {
		this.icu = icu;
		this.doc = doc;
		this.offset = offset;
	}
}