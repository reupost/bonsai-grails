<div class="col-md-4  widget">
        <label class="control-label" for="${property}">${label} ${required? '*' : '' }</label>
        <g:select id="${property}" name="${property}" class="form-control"
                          from="${userService.listAll([:])}"
                          value="${value.id}"
                          optionKey="id" />
</div>