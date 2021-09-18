<table>
    <thead>
         <tr>
            <td/>
            <g:each in="${domainProperties}" var="p" status="i">
                <g:sortableColumn property="${p.property}" title="${p.label}" />
            </g:each>
        </tr>
    </thead>
    <tbody>
        <g:each in="${collection}" var="bean" status="i">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                <td><g:link method="GET" resource="${bean}">View</g:link></td>
                <g:each in="${domainProperties}" var="p" status="j">
                     <td><f:display bean="${bean}" property="${p.property}"  displayStyle="${displayStyle?:'table'}" theme="${theme}"/></td>
                </g:each>
            </tr>
        </g:each>
    </tbody>
</table>