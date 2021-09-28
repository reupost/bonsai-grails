<div class="col-md-4  widget">
        <label class="control-label" for="${property}">${label} ${required? '*' : '' }</label>
        <g:select id="${property}" name="${property}" class="form-control"
                          from="${bonsaiService.listAll([:])}"
                          optionKey="id" />
</div>