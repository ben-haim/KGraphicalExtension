#K Graphical Extension

###A GUI toolkit that connects to a kdb+ servers, allowing the representation of variables on the server in GUI widgets.

Written as part of my BSc Computer Science and Maths final year project, the objective of this system is to provide a
GUI toolkit that can translate a dictionary in kdb+, describing the components of a GUI and their respective data binding to variables, into a GUI.

The descriptive syntax is similar to K3.x's attributes, except the widget descriptions and the variable are coupled using a `b` attribute.

##Compilation
Compile using `mvn package`, place the jar file wherever.

##KGE Manual
###GUI Dictionary Attributes
The following “attributes” in the GUI dictionary are read by the KGE:
 * `.b` : the “binding” attribute variable, takes a variable name as a symbol, or a char array if the widget is class `button
 * `.c` : the class attribute, which can be `data, `list, `button, `form or `check. Only valid when the parent’s class is `form
 * `.l` : the label attribute, can be either a symbol or a string, will be displayed as the frame title if it’s assigned to a root-level widget
 * `.x`, `.y` : the x and y co-ordinates on the GUI frame, starting at `0,0`, replaces the arrangement system from K3.x
 * `.w`, `.h` : the width and height of the widget on the GUI frame. In K3.x, widgets were distributed on a row or column depending on their location inside the ..a nest. This method gives more precision of the space the widget’s takes
(Note: `x`, `y`, `w`, `h` require their values to be integers, so cast them with an i).

###Start up
Load the `kge.q` file onto the current kdb+ session, and set the port. The KGE will only listen to connections on the local host, so if anything is on a remote machine it needs to be mirrored on the local machine. 
Now in another console, change to the directory that contains the `KGE-1.0.jar` file, and run the following:
`$ java –jar KGE-1.0.jar <port>`

If the console displays `Connected to port:<port>`, the connection was success and you may now start using the GUI. 
Use `gKill []` on the kdb+ session to terminate the KGE process.

###Show and Hide
To show a variable in a widget, define the widget as a dictionary with an entry named `b` and assign the entry to the name of the variable. You don’t need to declare a whole dictionary, just the name of the widget with dot notation, .e.g

```
txtBox.b:`someVar;
```

Now you can show and hide them using the `gShow` and `gHide` commands, respectively, passing them the name of the widget/gui as a symbol. Successfully displayed frames will come up in the KGE shell window.
It is not recommended that you show and hide the root namespace, as GUI functions are stored in it. 

###Classes
To set the class of a widget, it must be contained in (or set to) a dictionary of class `form`. 
 * The button class requires a string of characters, anything else will be ignored and the button will not function.
 * Check button widgets require Boolean atoms.
 * Lists require single array, which can be general. Numeric and character changes will only be reflected in simple lists with their respective types
 * Forms can also be bound to a dictionary, its entries will be placed in a column.
