system("p 5000"); //set the port to 5000

a:`$"hello world";
b: 10;
c: "chararray"
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
gui2.c:`panel;

//a gui with attributes
gui3.wA.b: `a;
gui3.wB.x: 1i;
gui3.wB.b: `b;
gui3.wC.y: 1i;
gui3.wC.w: 2i;
gui3.wC.b: `c;
gui3.wE.y: 2i;
gui3.wE.h: 2i;
gui3.wE.b: `e;
gui3.wV.b: `v;
gui3.wV.h: 2i;
gui3.wV.x: 1i;
gui3.wV.y: 2i;
gui3.l:"Super GUI";
gui3.c:`panel;



system("l src/q/kge.q")