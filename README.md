# DigitalTwin_Composition
## Modeling file
The file ```cooperative_DTs.owl``` contains the ontology for the compositional approach.

## Robot Behavioral Computation
The computation of the behavioral models is processed externally with Python.  
The folder ```robot_behavioral_computational_server``` contains the Python code for the robot models and the interfaces for the integration with the flexcell application.

## Flexcell Application
The main application for the implementation of the modeling approach is in the folder ```flexcell_application```.  
In that folder, you find the AASX asset files, and the source files for the application, which inherit from the ontology definition, generate the task assembly with skills, and use the Eclipse BaSyx Java SDK.