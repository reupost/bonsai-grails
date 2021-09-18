<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'bonsai.label', default: 'Bonsai')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#list-bonsai" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-bonsai" class="content scaffold-list" role="main">
            <div style="clear:both;display:block">
              <span style="float:left">
              <h1><g:message code="default.list.label" args="[entityName]" /></h1>
              </span>
              <span style="float:right;margin-right:10px">
              <form action="">
              <input type="hidden" id="offset" name="offset" value="${params.offset}"/>
              <input type="hidden" id="max" name="max" value="${params.max}"/>
              <input type="hidden" id="sort" name="sort" value="${params.sort}"/>
              <input type="hidden" id="order" name="order" value="${params.order}"/>
              Search: <input type="text" id="searchFilter" name="searchFilter" width="20"/>
              <input type="submit" value="Go"/>
              <input type="reset" value="Clear"/>
              </form>
              </span>
            </div>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <f:table collection="${bonsaiList}" />

            <div class="pagination">
                <g:paginate total="${bonsaiCount ?: 0}" />
            </div>
        </div>
    </body>
</html>