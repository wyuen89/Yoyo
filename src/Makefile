JFLAGS = -g -d
CLASSFLAG = -classpath
JC = javac
CLASSPATH = ../build
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $(CLASSPATH) $*.java

CLASSES = \
	GIFManipulator.java\
        Yoyo.java\
        GIFData.java\
        Filter.java\
        OriginalFilter.java\
        GrayscaleFilter.java\
        ColorIsoFilter.java\
        SepiaFilter.java\
        NegativeFilter.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) $(CLASSPATH)/*.class