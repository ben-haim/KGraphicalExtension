.g.h:();

gUpdate: {[v;i]
	s: $[()~i; //check if i is empty
		value v; //use the whole variable
		.[value v; i]; //use the indexed variable
		];
	show s;
	(neg .g.h) (`update; v; i; s)}; //send update async

gShow: {[gui]
    (neg .g.h) (`show;gui;value gui)};

gHide: {[name] (neg .g.h) (`hide;name)};

gKill:{(neg .g.h) (enlist `kill); .z.vs::; .g.h::;};

gInit:{.g.h::.z.w; .z.vs::gUpdate};