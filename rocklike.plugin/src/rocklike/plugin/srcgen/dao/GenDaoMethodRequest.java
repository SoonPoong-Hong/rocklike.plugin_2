package rocklike.plugin.srcgen.dao;

import org.eclipse.jdt.core.IType;


public class GenDaoMethodRequest{
	enum SelectUpdateOption{select, update}
	enum OneListOption{one, list}

	public String methodName;
	public String parameterType;
	public String resultType ;
	public SelectUpdateOption selectUpdateOption;
	public OneListOption oneListOption;
	public IType type;

	@Override
    public String toString() {
	    return "GenDaoMethodResult [methodName=" + methodName + ", parameterType=" + parameterType
	            + ", resultType=" + resultType + ", curlType=" + selectUpdateOption + ", oneListType=" + oneListOption
	            + "]";
    }

}