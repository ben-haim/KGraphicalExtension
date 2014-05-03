#Q Graphical Extension

##A GUI toolkit that connects to a kdb+ servers, allowing the representation of data on the server as widget's screen.

Written as part of my BSc Computer Science and Maths final year project, the objective of this system is to provide a
GUI toolkit that can convert different models of GUI representation on the kdb+ server to it's standard model format.
This functionality is performed by implementations of the Translator interface, which contains methods to translate data
back and forth between the server and client. Subclasses of the Controller class can be used to create widgets that work
with the internal Data Modelling System.