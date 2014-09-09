package rocklike.plugin.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import rocklike.plugin.jdt.kt.KtCorecmsResolver;

public class HongMybatisHelper {


	public static String extractSelectedValidText()  {
		// 해당 줄의 유효한 range text를 추출 (not whitespace & not 따옴표)
		ISelection sel = HongEclipseUtil.getSelection();
		if(sel instanceof ITextSelection){
			ITextSelection ts = (ITextSelection)sel;
			int line = ts.getStartLine();
			int offset = ts.getOffset();

			IDocument doc = HongEditorHelper.getIDocument();
			String str = extractValidRangeText(doc, offset);
			return str;
		}
		return null;
	}


	public static String extractValidRangeText(IDocument doc, int offset)  {
		try {
			int lineNum = doc.getLineOfOffset(offset);
			String line = doc.get(doc.getLineOffset(lineNum), doc.getLineLength(lineNum));
			int thisOffset = offset - doc.getLineOffset(lineNum);

			// whitespace 또는 따옴표인 경우에 구분
			int start = 0 ;
			char[] arr = line.toCharArray();

			for(int i=thisOffset; i>=0; i--){
				char c = arr[i];
				if(Character.isWhitespace(c) || c=='\'' || c=='\"'){
					start = i+1;
					break;
				}
			}

			int end = line.length();
			for(int i=thisOffset; i<end; i++){
				char c = arr[i];
				if(Character.isWhitespace(c) || c=='\'' || c=='\"' ){
					end = i;
				}
			}

			System.out.printf("%s , %s \n", start, end);
			return line.substring(start, end);
		} catch (BadLocationException e) {
			e.printStackTrace();
			return null;
		}
	}


	public static IFile assumeMybatisQueryXmlFilePath(IProject p , String inputParam){
		// com.kt.cms.cuc.dst.dm.dao.DmDao3.selectDeployedCountFromCompletedTable
		//    => src/main/resource/sqlmap/sql/dst/dm/Dm3.xml

//		IProject p = HongEclipseUtil.getSelectedProject();
//		System.out.printf("== IProject : %s \n", p);

		String path = "src/main/resource/sqlmap/sql/";

		int pos;
		pos = inputParam.lastIndexOf(".");
		if(pos!=-1){
			inputParam = inputParam.substring(0, pos);
		}
		// (?=)
//		System.out.println(inputParam);
		inputParam = inputParam.substring(15).replaceFirst("\\.dao\\.", ".").replaceFirst("Dao([\\d]{0,1})","$1").replaceAll("\\.", "/") + ".xml";
//		System.out.println(inputParam);
//		System.out.println(path + inputParam);
		return p.getFile(path+inputParam);
	}


	public static String assumeMybatisQueryXmlId(String s){
		int pos = s.lastIndexOf(".");
		if(pos!=-1){
			return s.substring(pos+1);
		}else{
			return s;
		}
	}


	public static String assumeMybatisQueryXmlPackageId(IType t){
		String pkgName = t.getPackageFragment().getElementName();
		String clzName = t.getElementName();
		//    com.kt.cms.cuc.acq.ca.dao.impl.AcquireRequestDaoImpl
		// => com.kt.cms.cuc.acq.ca.dao.AcquireRequestDao.selectAcquireRequestInfo
		String result = pkgName.substring(0, pkgName.length()-5);
		result = result + "." + clzName.replace("Impl","");
		return result;
	}


	public static IFileAndId extractMybatisXmlFileAndId(IProject proj, MethodDeclaration md){
		if(md==null){
			return null;
		}
		ExtractMybatisXmlFileAndId v = new ExtractMybatisXmlFileAndId(proj, md);
		md.accept(v);
		return new IFileAndId(v.resultFile, v.resultId, v.inputParam);
	}

	private static class ExtractMybatisXmlFileAndId extends ASTVisitor{
		private IProject proj;
		private MethodDeclaration md;
		private IFile resultFile;
		private String resultId;
		private String inputParam;

		private ExtractMybatisXmlFileAndId(IProject proj, MethodDeclaration md) {
			super();
			this.proj = proj;
			this.md = md;
		}

		@Override
		public boolean visit(MethodInvocation node) {
			if(resultFile!=null){
				return false;
			}
			IMethodBinding mb = node.resolveMethodBinding();
			String declaringClz = mb==null ? null :  mb.getDeclaringClass().getName();
			if("KTDaoImpl".equals(declaringClz) || "SqlSession".equals(declaringClz)){
				StringLiteral sl = (StringLiteral)node.arguments().get(0);
				String param = sl.getLiteralValue();
				inputParam = param;
//				System.out.printf("== mybatis : [%s] [%s]\n", node.arguments().get(0), param);

				IFile xmlFile = assumeMybatisQueryXmlFilePath(proj, param);
				if(xmlFile.exists()){
					resultFile = xmlFile;
					String id = assumeMybatisQueryXmlId(param);
					resultId = id;
					return false;
				}
			}
			return true;
		}
	}

	public static class IFileAndId{
		public IFileAndId(IFile ifile, String id, String inputParam) {
			super();
			this.ifile = ifile;
			this.id = id;
			this.inputParam = inputParam;
		}
		public IFile ifile;
		public String id;
		public String inputParam;
	}



	public static void openMybatisXmlFile(IProject proj, String inputParam){
		IFile xmlFile = assumeMybatisQueryXmlFilePath(proj, inputParam);
		if(xmlFile.exists()){
			String id = assumeMybatisQueryXmlId(inputParam);
			try {
				HongEditorHelper.openXmlEditorAndSelectById(xmlFile, id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}



	public static void openMybatisXmlFile(IProject proj, MethodDeclaration md){
		if(md==null){
			return;
		}
		IFileAndId result = extractMybatisXmlFileAndId(proj, md);
		if(result==null){
			return;
		}
		try {
			HongEditorHelper.openXmlEditorAndSelectById(result.ifile, result.id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public static IFile assumeDaoImplPath(IProject proj, IFile f){
		IPath p = f.getProjectRelativePath();
		String path = p.toString();
		int lastPos = path.lastIndexOf("/");
		IFile tmpFile;

		String tmpPath;

		tmpPath = path;
		tmpPath = tmpPath.substring(0, lastPos) + "/impl" + tmpPath.substring(lastPos);
		tmpPath = tmpPath.replaceFirst("\\.java", "Impl.java");
		tmpFile = proj.getFile(tmpPath);
		if(tmpFile.exists()){
			return tmpFile;
		}

		// 추가 예외처리
		//    MetaDeployViewDao2
		// => MetaDeployViewDaoImpl2
		tmpPath = path;
		tmpPath = tmpPath.substring(0, lastPos) + "/impl" + tmpPath.substring(lastPos).replace("Dao", "DaoImpl");
		tmpFile = proj.getFile(tmpPath);
		if(tmpFile.exists()){
			return tmpFile;
		}

		return null;
	}




	public static boolean isSelectionIsDaoImplClass() throws JavaModelException{
		ICompilationUnit icu = HongJdtHelper.getSelectedICompilationUnit();
		IType type = icu.getTypes()[0];
		if(!KtCorecmsResolver.isBelongsToCorecmsClass(type)){
			return false;
		}

		String packageName = type.getPackageFragment().getElementName();
		if(packageName.endsWith(".dao.impl")){
			return true;
		}else{
			return false;
		}
	}


	public static void saveXml(IDOMModel domModel) throws UnsupportedEncodingException, IOException, CoreException{
		domModel.save();
	}



	public static IDOMModel appendToLastInMybatisXml(IFile xmlFile, String tag, String id, String parameterType
			, String resultType, String content) throws IOException, CoreException{

		IStructuredModel sm = StructuredModelManager.getModelManager().getModelForRead(xmlFile);
		if(sm instanceof IDOMModel){
			IDOMModel domModel = (IDOMModel)sm;
			IDOMDocument domDocument = domModel.getDocument();

			NodeList nodeList = domDocument.getElementsByTagName("mapper");
			Node mapperNode = nodeList.item(0);

			Text textNode = domDocument.createTextNode("\n\n");
			mapperNode.appendChild(textNode);

			XmlElementBuilder builder = new XmlElementBuilder(domDocument, tag);

			if(id!=null){
				builder.addAttr("id", id);
			}
			if(parameterType!=null){
				builder.addAttr("parameterType", parameterType);
			}
			if("select".equals(tag)){
				builder.addAttr("resultType", resultType);
				builder.addAttr("useCache", "false");
			}
			if(content!=null){
				builder.setContent(content);
			}

			ElementImpl elem = builder.build();

			mapperNode.appendChild(elem);
			mapperNode.appendChild(domDocument.createTextNode("\n\n"));

			return domModel;
		}
		return null;
	}



	public static void main(String[] args) {
		String path = "src/main/resource/sqlmap/sql/";
		String s = "com.kt.cms.cuc.dst.dm.dao.DmDao3.selectTargetAid";
		int pos;
		pos = s.lastIndexOf(".");
		if(pos!=-1){
			s = s.substring(0, pos);
		}
		// (?=)
		System.out.println(s);
//		s = s.substring(15).replaceFirst("\\.dao\\.", ".").replaceFirst("Dao(?=[\\w]{0,1}\\.)",".").replaceAll("\\.", "/");
		s = s.substring(15).replaceFirst("\\.dao\\.", ".").replaceFirst("Dao([\\d]{0,1})","$1").replaceAll("\\.", "/") + ".xml";
		System.out.println(s);
		System.out.println(path + s);
	}
}
