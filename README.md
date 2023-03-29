# Composition of Digital Twins
## Modeling file
The file ```cooperative_DTs.owl``` contains the ontology for the compositional approach.

## Robot Behavioral Computation
The computation of the behavioral models is processed externally with Python.  
The folder ```robot_behavioral_computational_server``` contains the Python code for the robot models and the interfaces for the integration with the flexcell application.

The models for the robots use the [Robotics Toolbox for Python](https://github.com/petercorke/robotics-toolbox-python).

## Flexcell Java Application
The main application for the implementation of the modeling approach is in the folder ```flexcell_application```.  
In that folder, you find the AASX asset files, and the source files for the application, which inherit from the ontology definition, generate the task assembly with skills, and use the Eclipse BaSyx Java SDK.  

The application contains the following internal packages: ```SkillBasedDTOntology```, which contains the generated Java code from the ontology model; ```CompositionManager```, which extends the interfaces of the ontology for the functional aspects and the integration with the Digital Twins; ```DTManager```, which contains the manager of the Digital Twins and Asset Administration Shell; and ```dtflexcell```, which contains the files for the flex-cell case study with two assembly processes.
  
The programming follows the skill-based engineering architecture (see [Robot skills for manufacturing: From concept to industrial deployment](http://dx.doi.org/10.1016/j.rcim.2015.04.002)) for the implementation of the functional aspects of the operations.

## Unity Application
The folder ```UR5e_Kuka_Flexcell``` contains the assets for the Unity application that is used, i.e., the assets for the robotic arms and grippers. The flex-cell object is too heavy to be uploaded to GitHub.  
It also contains the C# scripts for the Unity-to-Java application connection.

## Video
The video ```dissemination/video_implementation.mp4``` shows the running implementation of all the components, including the visualization with Unity, and some explanations about the modules.

## Setup
1. Run the *Eclipse BaSyx* ``aas`` and ``registry`` servers (available on Docker as well) [AAS docker images](https://hub.docker.com/search?q=eclipsebasyx).
2. Run the Robots server via the command `python robot_behavioral_computation_server/robots_server.py`.
3. Run the MQTT-to-ZMQ forwarder script via the command `python robot_behavioral_computation_server/mqtt_forwarder.py`.
4. Run the flex-cell Java application (based on Maven) via the command `mvn exec:java -Dexec.mainClass=dtflexcell.DTFlexCellMain`.
5. When the Java application starts sending the messages, run the Unity application. Note that the ZMQ subscriber can lead the Unity app to crash if there is no publisher.
