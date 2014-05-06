.g.h:();

gUpdate: {[v;i]
	$[()~i; //check if i is empty
		s:value v; //use the value of the variable
		$[7h = type i; //if the index is an int vector, just use bracket indexing
		    s:(value v)[i:`int$(i)];
		    s:.[value v;i] //use dot indexing
		]];
	(neg .g.h) (`update; v; i; s)}; //send update async

gShow: {[gui]
    (neg .g.h) (`show;gui;value gui)};

gHide: {[name] (neg .g.h) (`hide;name)};

gKill:{(neg .g.h) (enlist `kill); .z.vs::; .g.h::;};

gInit:{
    .g.h::.z.w;
    .z.vs::gUpdate };