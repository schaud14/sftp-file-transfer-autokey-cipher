# sftp-file-transfer-autokey-cipher
The ftp server is invoked as
java FtpServ <server_port> (Java)
<server_port> specifies the port at which ftp server accepts connection requests.
The sftp client is invoked as
java FtpCli <server_domain> <server_port> (Java)

Upon connecting to the server, the client prints out ftp >, which allows the user to execute the
following commands.
ftp > ls //lists contents of the directory where the executable server code is
ftp> lls //lists contents of the directory where the executable client code is
ftp > mkdir <dir> //create a directory <dir>
ftp > cd <dir-relative-path> //change the current working directory to a path that is relative to
//the current directory. E.g. cd path
ftp > cd .. //move up one folder
ftp > pwd //print the working directory of the server
ftp> get <filename> //transfer <filename> from the server to the client
ftp> quit

Implement the Auto key cipher (key: security). When the client requests to transfer the file
<filename> from the server using get <filename>, the server encrypts <filename> using
Autokey cipher and stores the encrypted file in <filename>_se. The server then transfers the
encrypted file to the client. After the client receives the file, the client saves the received file in
<filename>_ce and then decrypts the file. The decrypted file is stored in <filename>_cd.
