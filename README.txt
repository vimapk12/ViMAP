Jars needed in classpath:

http://ccl.northwestern.edu/netlogo/download.shtml (NetLogo 5.1)
NetLogo.jar 
bitmap.jar 
qtj.jar 
sample-scala.jar
scala-library.jar

http://xstream.codehaus.org/download.html
xstream-1.4.2.jar

http://sourceforge.net/projects/jfreechart/files/
jfreechart-1.0.13-demo.jar

http://sourceforge.net/projects/kxml/files/kxml2/
kxml2-2.3.0.jar

http://mvnrepository.com/artifact/junit/junit/4.8.2
junit-4.8.2.jar

http://code.google.com/p/flying-saucer/downloads/detail?name=flyingsaucer-R8.zip&can=2&q=
core-renderer.jar


Development environment:
Java 1.7.0
Eclipse 3.7 (Indigo)

Checkstyle 5.5
http://eclipse-cs.sourceforge.net/update
Eclipse Project -> Properties -> Checkstyle -> Sun Checks (Eclipse) - (Global)

Optionally, to silence Checkstyle warnings about JavaDoc:
Eclipse -> Preferences -> Checkstyle
Select Sun Checks (Eclipse) -> Copy??? -> Name: NoJavadoc -> OK
Select NoJavadoc -> Configure??? -> JavaDoc comments 
    -> uncheck all 5 checkboxes under "Enabled" -> OK -> OK
Select your project in Package Explorer -> Properties 
    -> Checkstyle -> select NoJavadoc -> OK


Findbugs 2.0.1
http://findbugs.cs.umd.edu/eclipse
Eclipse Project -> Properties -> Findbugs -> Minimum Rank to Report 20, Mark all as Warning

Eclipse editor style:
Eclipse Preferences -> Java -> Code Style -> Formatter -> Java Conventions/Edit -> Tab policy: Spaces only

Eclipse compiler warnings:
Code style: all are warnings except Unqualified access, Non-externalized, Can be static, Can potentially be static (ignored)
Potential programming problems: All are warnings except Boxing and unboxing (ignored)
Name shadowing: All are warnings
Deprecated: All are warnings, but Forbidden reference is an error
Unnecessary code: All are warnings except Unnecessary 'else' (ignored)
Generic types: All are warnings
Annotations: All are warnings

NetLogo file notes:
world size must not exceed 400 x 400 patches, or a heap space error may occur
