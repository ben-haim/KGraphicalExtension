system("p 5000"); //set the port to 5000

a:`$"hello world";
b: 10;
c: 1b;
s: "SOME STRING";
d: `a`b`c! 10 20 30;
e: `a`b`c`d;
v: 11 12 13 14 15;

//a simple hello world text box
txtA.b:`a

//a more complex GUI
gui1.grpA.b:`a;
gui1.grpB.b:`b;
gui1.grpC.b:`c;

//a gui from a dictionary
gui2.b:`d;
gui2.c:`form;

//a gui with attributes
gui3.wA.b: `a;
gui3.wA.w: 2i;

gui3.wC.c:`check;
gui3.wC.b: `c;
gui3.wC.y: 1i;

gui3.wB.y: 1i;
gui3.wB.x: 1i;
gui3.wB.b: `b;

gui3.wS.b:`s;
gui3.wS.y: 2i;
gui3.wS.w: 2i;

gui3.wE.x: 2i;
gui3.wE.h: 2i;
gui3.wE.b: `e;
gui3.wE.c: `list;

gui3.wBut.y: 2i;
gui3.wBut.x: 2i;
gui3.wBut.b: "b+:5";
gui3.wBut.c: `button;
gui3.wBut.l: "Increment b";

gui3.wV.b: `v;
gui3.wV.h: 3i;
gui3.wV.x: 3i;
gui3.wV.c: `list;
gui3.l:"Super GUI";
gui3.c:`form;

//system("l gui/kge.q") //load the kge.q script manually for now, need to figure out env variables with system()
