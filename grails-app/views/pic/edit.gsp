<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'pic.label', default: 'Pic')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#edit-pic" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="edit-pic" class="content scaffold-edit" role="main">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.pic}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.pic}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:uploadForm resource="${this.pic}" method="PUT" >
                <g:hiddenField name="version" value="${this.pic?.version}" />
                <fieldset class="form">
                    <f:field bean="pic" property="entityType"/>
                    <f:field bean="pic" property="entityId"/>
                    <f:field bean="pic" property="dateTaken"/>
                    <f:field bean="pic" property="title"/>
                    <a href="${createLink(controller:'pic', action:'viewImage', id:pic.id)}" target="_new">
                      <img class="Photo" style="border-width:0px" src="${createLink(controller:'pic', action:'viewImageThumb', id:pic.id)}" height="${pic.dimythumb}" width="${pic.dimxthumb}" />
                    </a>
                    <input type='file' name="imgFile"/>
                </fieldset>
                <fieldset class="buttons">
                    <input class="save" type="submit" value="${message(code: 'default.button.update.label', default: 'Update')}" />
                </fieldset>
            </g:uploadForm>
        </div>
    </body>
</html>
