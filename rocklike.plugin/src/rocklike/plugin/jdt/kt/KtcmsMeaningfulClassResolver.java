package rocklike.plugin.jdt.kt;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import rocklike.plugin.jdt.MeaningfulClassResolver;

public class KtcmsMeaningfulClassResolver implements MeaningfulClassResolver {

	@Override
	public boolean isMeaningful(MethodInvocation md) {
		IMethodBinding mb = md.resolveMethodBinding();
		if(mb==null){
			return false;
		}
		String methodName = mb.getName();
		ITypeBinding declaringClass = mb.getDeclaringClass();
		String clzNm = declaringClass.getName();

		IPackageBinding pb = declaringClass.getPackage();
		String pckNm = pb.getName();


		if(declaringClass.isNested()){
			return false;
		}

		if(clzNm.equals("SchemaToApiConverter")){
			if(methodName.startsWith("update")
				|| methodName.startsWith("update")
				|| methodName.startsWith("delete")
				|| methodName.startsWith("productize")
				|| methodName.startsWith("bind")
				|| methodName.startsWith("find")
					){
				return true;
			}
			return false;
		}
		
		if(clzNm.equals("CommUtil")
				|| clzNm.equals("OsbHeader")
				|| clzNm.equals("OsbExceptionUtil")
				|| clzNm.equals("ServiceFaultMessage")
				|| clzNm.equals("ExceptionBuilder")
				|| clzNm.equals("StringUtils")
				){
			return false;
		}

		if(!pckNm.startsWith("com.kt.cms.")){
			return false;
		}

		if(pckNm.endsWith(".domain")  ){
			return false;
		}

		if(pckNm.endsWith(".aspect")  ){
			return false;
		}

		if(pckNm.endsWith(".common.xml")  ){
			return false;
		}

		if(pckNm.indexOf(".unmarshalling")>-1){
			return false;
		}

		if(pckNm.startsWith("com.kt.cms.aif.")){
			if(clzNm.equals("ResponseData") || clzNm.equals("RequestData")){
				return false;
			}
		}

		//		 System.out.printf("== pck : %s \n", pckNm);
		return true;
	}

	
	private boolean contains(String v, String strContainer){
		String[] arr = strContainer.split(",");
		for(String s : arr){
			if(s.equals(v)){
				return true;
			}
		}
		return false;
	}
}
