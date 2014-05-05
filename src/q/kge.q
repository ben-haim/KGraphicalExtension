.g.h:();

gUpdate: {[v;i]
	$[()~i; //check if i is empty
		s:value v; //use the whole variable
		$[7h = type i;
		    s:(value v)[i:`int$(i)];
		    s:.[value v;i]
		]];
	(neg .g.h) (`update; v; i; s)}; //send update async

gShow: {[gui]
    (neg .g.h) (`show;gui;value gui)};

gHide: {[name] (neg .g.h) (`hide;name)};

gKill:{(neg .g.h) (enlist `kill); .z.vs::; .g.h::;};

gInit:{
    .g.h::.z.w;
    .z.vs::gUpdate };