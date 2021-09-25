<div class="col-md-4  widget">
        <label class="control-label" for="${property}">${label} ${required? '*' : '' }</label>
        <g:select id="${property}" name="${property}" class="form-control"
                          from="${taxonService.list([:])}"
                          value="${value.id}"
                          optionKey="id" />
</div>