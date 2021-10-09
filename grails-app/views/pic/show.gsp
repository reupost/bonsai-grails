<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'pic.label', default: 'Pic')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#show-pic" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="show-pic" class="content scaffold-show" role="main">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            Entity: <f:display bean="pic" property="entityType"/><br/>
            Entity id: <f:display bean="pic" property="entityId"/><br/>
            Title: <f:display bean="pic" property="title"/><br/>
            Date: <f:display bean="pic" property="dateTaken"/><br/>
            <a href="${createLink(controller:'pic', action:'viewImage', id:pic.id)}" target="_new">
              <img class="Photo" style="border-width:0px" src="${createLink(controller:'pic', action:'viewImageThumb', id:pic.id)}" height="${pic.dimythumb}" width="${pic.dimxthumb}" />
            </a>

            <g:form resource="${this.pic}" method="DELETE">
                <fieldset class="buttons">
                    <g:link class="edit" action="edit" resource="${this.pic}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                    <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
