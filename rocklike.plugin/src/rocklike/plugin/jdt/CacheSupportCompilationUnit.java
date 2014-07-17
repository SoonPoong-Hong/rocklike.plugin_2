package rocklike.plugin.jdt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class CacheSupportCompilationUnit {
	// singleton
	private CacheSupportCompilationUnit(){}
	private Map<ICompilationUnit, CompilationUnit> cached = new HashMap();
	private static CacheSupportCompilationUnit self = new CacheSupportCompilationUnit();
	
	public static CacheSupportCompilationUnit get(){
		return self;
	}
	public CompilationUnit getCompilationUnit(IFile f){
		return getCompilationUnit( JavaCore.createCompilationUnitFrom(f) );
	}
	
	public void clearCache(){
		cached.clear();
	}
	
	public CompilationUnit getCompilationUnit(ICompilationUnit icu){
		CompilationUnit cu = cached.get(icu);
		if(cu!=null){
			return cu; 
		}
		ASTParser p = ASTParser.newParser(AST.JLS4);
		p.setSource(icu);
		p.setKind(ASTParser.K_COMPILATION_UNIT);
		p.setResolveBindings(true);
		p.setBindingsRecovery(true);
		CompilationUnit newCu = (CompilationUnit)p.createAST(null);
		cached.put(icu, newCu);
		return newCu;
	}
}
