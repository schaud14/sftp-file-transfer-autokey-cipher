CS558:

Assignment 1:

Submitted by:
Saurabh Chaudhari
	email: schaud14@binghamton.edu


Instructions:

To run this code the hostname and port address is hardcoded.
hostname: bingsuns.binghamton.edu
port : 8792

To Compile the code Extract tar using "tar -xvf p1-schaud14.tar.gz"
then Navigate to "p1-schaud14" and type make.

To Initialize Server on bingsuns:
1. cd FtpServ
2. java FtpServ <port_no>

To Run Client on bingsuns:
1. cd FtpCli
2. java FtpCli bingsuns.binghamton.edu <port_no>


Functionalities of program:

Commands Run on Server:

get <filename> 
	will create a file filename_se with encrypted text at same directory of original file on server.
	Server will send filename_se to client which will save filename_ce on client directory from witch it is invoked.
	client will decrypt the file back and save it as filename_cd in same directory of filename_ce.
pwd
	will give the current server path.
ls
	will give the list of file in current server directory.
mkdir <new_directory_name>
	will create a folder/directory in current server directory.
cd <directory_name>
	will change the server's currnent working directory to that mentioned in command if its exists.
cd ..
	will change server's current working directory to its parent directory

Commands run on Client:

lls
	will give the list of files in current client directory
quit
	will close the connetions from both server and client end.

