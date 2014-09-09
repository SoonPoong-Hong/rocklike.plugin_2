package rocklike.plugin.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class HongEditorHelper {

	public static ITextEditor findTextEditor(IEditorPart editorPart) {   // => XML multi page인 경우에, text editor로 열리게.
		if (editorPart instanceof ITextEditor) {
			return (ITextEditor) editorPart;
		}

		if (editorPart instanceof MultiPageEditorPart) {
			MultiPageEditorPart multi = (MultiPageEditorPart) editorPart;

            try {
            	StructuredTextEditor ste = (StructuredTextEditor)multi.getAdapter(StructuredTextEditor.class);
            	if(ste!=null){
            		multi.setActiveEditor(ste);
            		return ste;
            	}

				IEditorPart[] editors = multi.findEditors(editorPart.getEditorInput());
				for (IEditorPart editor : editors) {
					ITextEditor thisEditor = findTextEditor(editor);
					if(thisEditor!=null){
						Method method = MultiPageEditorPart.class.getDeclaredMethod( "getActiveEditor", null);
						method.setAccessible(true);
						editorPart = (IEditorPart) method.invoke(multi, null);
						if(editorPart!=null){
							return thisEditor;
						}else{
							continue;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (editorPart instanceof FormEditor){
			FormEditor fe = (FormEditor)editorPart;
			IEditorPart[] editors = fe.findEditors(editorPart.getEditorInput());
			for (IEditorPart editor : editors) {
				ITextEditor thisEditor = findTextEditor(editor);
				if(thisEditor!=null){
					return thisEditor;
				}
			}
		}

		return null;
	}


	public static void openEditor(ITextEditor e, IRegion r){
		if(e==null){
			return;
		}
		e.selectAndReveal(r.getOffset(), r.getLength());
	}


	public static void openTextEditorAndSelect(IFile ifile){
		openTextEditorAndSelect(ifile, 0, 0);
	}


	public static void openTextEditorAndSelect(IFile ifile, int offset, int length){
		try {
			ITextEditor ite = findTextEditor(IDE.openEditor(HongEclipseUtil.getActivePage(), ifile));
			openEditor(ite, new Region(offset, length));
		} catch (PartInitException e) {
			System.out.println("== TextEditor를 열다가 에러 발생");
			e.printStackTrace();
		}
	}


	public static void openXmlEditorAndSelectById(IFile ifile, String id) throws IOException, CoreException{
		IStructuredModel sm = StructuredModelManager.getModelManager().getModelForRead(ifile);
		if(sm instanceof IDOMModel){
			IDOMModel m = (IDOMModel)sm;
			IDOMDocument dom = m.getDocument();

			Element elem =  dom.getElementById(id);
			if(elem instanceof ElementImpl){
				ElementImpl ei = (ElementImpl)elem;
				int offset = ei.getStartOffset();
				HongEditorHelper.openTextEditorAndSelect(ifile, offset, 0);

			}else if(elem ==null){
				HongEditorHelper.openTextEditorAndSelect(ifile, 0, 0);
			}
		}
	}


	public static String getXmlContents(IFile ifile, String id) throws IOException, CoreException{
		IStructuredModel sm = StructuredModelManager.getModelManager().getModelForRead(ifile);
		if(sm instanceof IDOMModel){
			IDOMModel m = (IDOMModel)sm;
			IDOMDocument dom = m.getDocument();

			Element elem =  dom.getElementById(id);
			if(elem instanceof ElementImpl){
				ElementImpl ei = (ElementImpl)elem;
				int offset = ei.getStartOffset();
				return ei.getSource();

			}
		}
		return null;
	}



	public static String getXmlContentsIncludingRef(IFile ifile, String id) throws IOException, CoreException{
		IStructuredModel sm = StructuredModelManager.getModelManager().getModelForRead(ifile);

		Map<String, String> includeMatched = new HashMap();

		if(sm instanceof IDOMModel){
			IDOMModel m = (IDOMModel)sm;
			IDOMDocument dom = m.getDocument();

			ElementImpl foundInclude = null;
			Element elem =  dom.getElementById(id);

			if(elem instanceof ElementImpl){
				ElementImpl ei = (ElementImpl)elem;
				NodeList nl = ei.getElementsByTagName("include");
				for(int i=0; i<nl.getLength(); i++){
					ElementImpl includeEi = (ElementImpl)nl.item(i);
					String refid = includeEi.getAttribute("refid");
					foundInclude = getIncludingContents(dom, refid);
					if(foundInclude!=null){
						String source = dom.getSource().substring(includeEi.getStartOffset(), includeEi.getEndOffset());
						String replacing = dom.getSource().substring(foundInclude.getStartOffset(), foundInclude.getEndOffset());
						includeMatched.put(source, replacing);
					}
					break;
				}

				String result = ei.getSource();
				Iterator<Entry<String, String>> ite = includeMatched.entrySet().iterator();
				while(ite.hasNext()){
					Entry<String, String> e = ite.next();
					result = result.replace(e.getKey(), e.getValue());
				}
				return result;

			}
		}
		return null;
	}




	public static ElementImpl getIncludingContents(IDOMDocument doc, String refid){
		NodeList nl = doc.getElementsByTagName("sql");
		int cnt = nl.getLength();
		for(int i=0; i<cnt; i++){
			ElementImpl el = (ElementImpl)nl.item(i);
			String attr = el.getAttribute("id");
			if(refid!=null && refid.equals(attr)){
				String contents = el.getSource();
				return el;
			}
		}
		return null;
	}


	public static IDocument getIDocument(){
		IDocument doc ;
		doc = ((ITextEditor)HongEclipseUtil.getActiveEditor()).getDocumentProvider().getDocument(HongEclipseUtil.getActiveEditor().getEditorInput());
		return doc;
	}



	public static void writeToNextLine(IDocument doc, int currLine, String msg) throws BadLocationException{
		int nextLineOffset = doc.getLineOffset(currLine) + doc.getLineLength(currLine);
		doc.replace(nextLineOffset, 0, msg + "\n");
	}



//	public static void main(String[] args) {
//		char ch;
//		ch = '.';
//		System.out.printf("[%s] %s \n", ch, Character.isLetter(ch));
//		ch = '.';
//		System.out.printf("[%s] %s \n", ch, Character.isJavaIdentifierPart(ch));
//
//
//		String s = "sb 1\"2(3.532.tke b";
//		System.out.println(s);
//		char[] arr = s.toCharArray();
//		int pos = 8;
//
//		int start =-1;
//		int end = -1;
//
//		for(int i=8; i>=0; i--){
//			char c = arr[i];
//			if( Character.isWhitespace(c) || c=='\"' ){
//				start = i+1;
//				break;
//			}
//		}
//
//		for(int i=8; i<s.length(); i++){
//			char c = arr[i];
//			if(Character.isWhitespace(c) || c=='\"'){
//				end = i-1;
//				break;
//			}
//		}
//
//		String str = s.substring(start, end+1);
//		System.out.printf("[%s] \n", str);
//
//	}

}
