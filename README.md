# ViMAP, a programming language designed for K--12

## How to build from source (for Mac and Windows)

### ECLIPSE

1. [Download Eclipse](https://www.eclipse.org/downloads/).
2. Open Eclipse and create or choose a workspace (i.e. a directory). The workspace is where you will house your main project folder, which also serves as your Git repository.
3. Download Java SE7, if you don't already have it.
  * Java SE7 includes JDK 7 and JRE 7.
  * Download it [here](http://docs.oracle.com/javase/7/docs/webnotes/install/index.html).
4. Eclipse -> Preferences -> Java -> Compiler
  * Deselect the checkbox that says "Use default compliance settings"
  * Set the compiler compliance level to 1.7.
  * Set "Generated .class files compatibility" and "Source compatibility also to 1.7
  * Remember to hit 'Apply' to apply your changes.  Don't just hit 'OK'.
5. Eclipse -> Preferences -> Java -> Installed JREs
  * From the list of Installed JRE's choose Java SE7 (recommended) or above. Java SE7 is needed to contribute to the ViMAP open source project.
6. Eclipse -> Preferences -> Ant (General Ant Settings)
  * In 'Names:' enter 'build.xml'
  * In 'Separate JRE timeout (ms):' enter '20000'
  * Select "show error dialog when Ant build fails"
  * Click Apply, followed by OK.
  * Close Eclipse


### GIT
1. Install the [latest version of Git](http://git-scm.com/downloads).
2. With the command line, navigate to the directory you want to use for the project; this must be the same directory as the Eclipse workspace.  At the command prompt, enter the following: 
  * ```$ git clone https://github.com/vimapk12/ViMAP.git ```
  * This will create a new repository called 'ViMAP' and clone of all the files in the online repository to this new local repository.


### ECLIPSE
1. Launch Eclipse and choose the workspace that contains the newly created ViMAP repository, and then click OK.
2. File -> Import -> Git -> Projects from Git:
  * Select 'Projects from Git' and hit 'Next'.
3. Choose 'Existing local repository' and hit 'Next'.
4. Click 'Add...' in the top right corner, browse for ViMAP repository, select the ViMAP repository folder.
5. Now the new repository is recognized so hit 'Finish' or 'Next'.
6. Select 'Use the New Project Wizard' and hit Finish.
7. Choose 'Java Project' and hit 'Next'.
  * Select execution environment to be JavaSE-1.7
  * Title the Project "ViMAP" 
  * Click 'Finish' (Hooray!)
8. There are only 3 folders that should be set as source folders (i.e. in the build path): 
  * src/main
  * src/test
  * resources   
9. To add a folder that is currently not set as a source folder:
  * right-click on that folder -> Build path -> Use as Source Folder
10. To remove a folder that is currently set as a source folder but should not be:
  * right click on that folder -> Build path -> Remove from Build Path


### NETLOGO
1. [Download NetLogo version 5.1.0](https://ccl.northwestern.edu/netlogo/download.shtml) and open the DMG file.
2. Drag the NetLogo 5.1.0 icon to your dock and/or to your Applications folder. You will be using it quite a bit during development.


### JAR FILES
1. You need to obtain eight JAR files and add them in Eclipse as external libraries. Four of the JARs are located in the NetLogo disk image file (.dmg) that you just downloaded. They are:
  * NetLogo.jar 
  * qtj.jar (extensions -> qtj) 
  * bitmap.jar (extensions -> bitmap) 
  * scala-library.jar (lib) 
2. The rest of the JAR files can be found here:
  * [xstream-1.4.7.jar](http://xstream.codehaus.org/download.html) (click on Binary distribution to download)
  * [kxml2-2.3.0.jar](http://sourceforge.net/projects/kxml/files/kxml2/2.3.0/)
  * [junit-4.8.2.jar](http://mvnrepository.com/artifact/junit/junit/4.8.2) (click on "Download (Jar)")
  * [core-renderer.jar](http://code.google.com/p/flying-saucer/downloads/detail?name=flyingsaucer-R8.zip&can=2&q=)
3. Once you have all of the JAR files, you must add them to your project. First, move the JAR files to a new directory in your Eclipse project.
4. Select the ViMAP folder in Eclipse.
  * Project -> Properties -> Java Build Path
  * Click on the "Libraries" tab, then click Add External JARs.
  * Find and select each JAR and click "Open"


### FINAL STEPS
1. Save/Refresh the project.
2. Open the "ViMAP.java" file and scroll down to the init( ) function. Make sure only one netLogoFile String is selected and comment the other strings out. 
3. Save the Project and Run to launch the project from source.

*Copyright Mind, Matter & Media Lab, Vanderbilt University. Our research is supported by several grants from the National Science Foundation and Vanderbilt University.*
