package rocklike.plugin.util;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;

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
	
	
//	/**
//	 * @deprecated 이건 삭제할 것임.
//	 */
//	public static class MethodDeclarationResolverByName extends ASTVisitor{
//		private ObjectHolder<MethodDeclaration> holder = new ObjectHolder();
//		private String methodName;
//		private boolean finished = false;
//		private CompilationUnit cu;
//		
//		public MethodDeclarationResolverByName(CompilationUnit cu, String methodName) {
//			super();
//			this.methodName = methodName;
//			this.cu = cu;
//		}
//		
//		public static MethodDeclaration resolve(CompilationUnit cu, String methodName){
//			MethodDeclarationResolverByName v = new MethodDeclarationResolverByName(cu, methodName);
//			cu.accept(v);
//			return v.holder.get();
//		}
//
//		@Override
//		public boolean visit(MethodDeclaration node) {
//			if(finished){
//				return false;
//			}
//			String thisName = node.getName().toString();
//			if(methodName.equals(thisName)){
//				holder.put(node);
//				finished = true;
//				return false;
//			}
//			return super.visit(node);
//		}
//
//		
//	}
	
	
	
	public static MethodDeclaration resolveImplMethod(CompilationUnit cu, IMethodBinding methodBinding){
		MethodImplResolver v = new MethodImplResolver(cu, methodBinding);
		cu.accept(v);
		return v.holder.get();
	}
	
	private static class MethodImplResolver extends ASTVisitor{
		private ObjectHolder<MethodDeclaration> holder = new ObjectHolder();
		private IMethodBinding intfMethod;
		private boolean finished = false;
		private CompilationUnit cu;
		
		public MethodImplResolver(CompilationUnit cu, IMethodBinding methodBinding) {
			super();
			this.intfMethod = methodBinding;
			this.cu = cu;
		}
		
		
		@Override
		public boolean visit(MethodDeclaration node) {
			if(finished){
				return false;
			}
			// 메소드명이 똑같고, 파라미터 타입도 똑같을때
			if(intfMethod.getName().equals(node.resolveBinding().getName())){
				String[] intfParams = ((IMethod)intfMethod.getJavaElement()).getParameterTypes(); 
				String[] thisParams = ((IMethod)(node.resolveBinding().getJavaElement())).getParameterTypes(); 
				if(Arrays.equals(intfParams, thisParams)){
					holder.put(node);
					finished = true;
					return false;
				}
			}
			return super.visit(node);
		}
		
		
	}
	

	
	public static class ExtractMybatisXmlFileAndId extends ASTVisitor{
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
		
		public static IFileAndId execute(IProject proj, MethodDeclaration md){
			if(md==null){
				return null;
			}
			ExtractMybatisXmlFileAndId v = new ExtractMybatisXmlFileAndId(proj, md);
			md.accept(v);
			return new IFileAndId(v.resultFile, v.resultId, v.inputParam);
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
		IFileAndId result = ExtractMybatisXmlFileAndId.execute(proj, md);
		if(result==null){
			return;
		}
		try {
			HongEditorHelper.openXmlEditorAndSelectById(result.ifile, result.id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static class MethodInvocationResolverByPosition extends ASTVisitor{
		private CompilationUnit cu;
		private ObjectHolder<MethodInvocation> holder = new ObjectHolder();
		private int pos ;

		private MethodInvocationResolverByPosition(CompilationUnit cu, int pos) {
			super();
			this.cu = cu;
			this.pos = pos;
		}
		
		public static MethodInvocation resolve(CompilationUnit cu, int pos){
			MethodInvocationResolverByPosition v = new MethodInvocationResolverByPosition(cu, pos);
			cu.accept(v);
			return v.holder.get();
		}

		@Override
		public boolean visit(MethodInvocation node) {
			// [주의] 안에 있는 메소드까지 다 검사해야 하기 때문에, 발견했다고 false를 리턴하면 안됨.
			int start = node.getStartPosition();
			int end = start + node.getLength();
			if(start<=pos && pos<end){
				holder.put(node);
			}
			return true;
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
