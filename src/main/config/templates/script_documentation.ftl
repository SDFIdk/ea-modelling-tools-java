# Script Documentation

<#list scriptGroups as scriptGroup>
## ${scriptGroup.name}

<#if scriptGroup.notes??>
${scriptGroup.notes}
</#if>
<#list scriptGroup.scripts as script>
<#if script.isRunnable>
### ${script.name}

<#if script.summary??>
${script.summary}
</#if>

<#if script.description??>
${script.description}
<#else>
⚠️ missing description
</#if>


</#if>

</#list>
</#list>