# Script Documentation

<#list scriptGroups?sort_by("name") as scriptGroup>
## ${scriptGroup.name}

<#if scriptGroup.notes??>
${scriptGroup.notes}
</#if>

<#list scriptGroup.scripts?sort_by("name") as script>
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