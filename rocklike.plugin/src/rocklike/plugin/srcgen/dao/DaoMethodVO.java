package rocklike.plugin.srcgen.dao;


public class DaoMethodVO {
	String selectUpdateOption;
	String oneListOption;

	String methodName;
	String xmlId;
	String parameterType;
	String resultType ;

	public String getSelectUpdateOption() {
		return selectUpdateOption;
	}
	public void setSelectUpdateOption(String selectUpdateOption) {
		this.selectUpdateOption = selectUpdateOption;
	}
	public String getOneListOption() {
		return oneListOption;
	}
	public void setOneListOption(String oneListOption) {
		this.oneListOption = oneListOption;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getParameterType() {
		return parameterType;
	}
	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public String getSimpleParameterType(){
		if(parameterType==null){
			return null;
		}
		int pos = parameterType.lastIndexOf(".");
		return parameterType.substring(pos+1);
	}

	public String getSimpleResultType(){
		if(resultType==null){
			return null;
		}
		int pos = resultType.lastIndexOf(".");
		return resultType.substring(pos+1);
	}
	public String getXmlId() {
		return xmlId;
	}
	public void setXmlId(String xmlId) {
		this.xmlId = xmlId;
	}
	@Override
    public String toString() {
	    return "DaoMethodVO [selectUpdateOption=" + selectUpdateOption + ", oneListOption=" + oneListOption
	            + ", methodName=" + methodName + ", parameterType=" + parameterType + ", resultType="
	            + resultType + "]";
    }
}
