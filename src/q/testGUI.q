system("p 5000"); //set the port to 5000

a:`$"hello world";
b: 10;
c: "chararray"
d: `a`b`c! 10 20 30;


//a simple hello world text box
txtA.b:`a

//a more complex GUI
gui1.grpA.b:`a;
gui1.grpB.b:`b;
gui1.grpC.b:`c;

//a gui from a dictionary
gui2.b:`d;
gui2.c:`panel;

system("l src/q/kge.q")