<#if model.selectUpdateOption=="update">
    @Override
    public int ${model.methodName}(${model.simpleParameterType} param){
        return this.getSqlSession().update("${model.xmlId}", param);
    }
<#else>
	<#if model.oneListOption=="one">
    @Override
    public ${model.simpleResultType!} ${model.methodName}(${model.simpleParameterType} param){
        return (${model.simpleResultType!})this.selectOne("${model.xmlId}", param);
    }
    <#else>
    @Override
    public List<${model.simpleResultType!}> ${model.methodName}(${model.simpleParameterType} param){
        return (List<${model.simpleResultType!}>)this.selectList("${model.xmlId}", param);
    }
    </#if>
</#if>