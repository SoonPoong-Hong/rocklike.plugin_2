<#if model.selectUpdateOption=="update">
    public int ${model.methodName}(${model.simpleParameterType} param);
<#else>
	<#if model.oneListOption=="one">
    public ${model.simpleResultType!} ${model.methodName}(${model.simpleParameterType} param);
    <#else>
    public List<${model.simpleResultType!}> ${model.methodName}(${model.simpleParameterType} param);
    </#if>
</#if>
