SRCS = $(wildcard FtpServ/*.java FtpCli/*.java)
CLS  = $(SRCS:.java=.class)

default:
	javac -classpath . $(SRCS)

clean:
	$(RM) $(CLS)
