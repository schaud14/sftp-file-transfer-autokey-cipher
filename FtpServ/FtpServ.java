//package edu.cs;
/**
 * @author Saurabh Chaudhari
 *
 */


import java.net.*;
import java.io.*;

public class FtpServ {
	public static void main(String[] args) throws Exception {
		String fromClient;
		String toClient = null;
		boolean result;
		ServerSocket localSocket = new ServerSocket(Integer.parseInt(args[0]));
		System.out.println("Waiting for Client to Connect on port "+ args[0]);
		while (true) {
			Socket Connected = localSocket.accept();
			OutputStream os = Connected.getOutputStream();
			System.out.println("Client " + Connected.getLocalAddress() + " connected at port " + Connected.getPort());
			BufferedReader inputFromUser = new BufferedReader(new InputStreamReader(System.in));
			BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(Connected.getInputStream()));
			PrintWriter outputToClient = new PrintWriter(Connected.getOutputStream(), true);
			while (true) {
				if (toClient == null) {
					fromClient = inputFromClient.readLine();
					if (fromClient.equals("quit")) {
						{
							System.out.println("Client Closed the connection.");
							System.exit(0);
							Connected.close();
							break;
						}
					} else {
						System.out.println("From Client: " + fromClient);
						String[] splitInput = fromClient.split(" ");
						switch (splitInput[0]) {
						case "cd": {
							if (splitInput[1].equals("..")) {
								String parentDir = changeDirectorytoParent();
								outputToClient.println("Server Directory Changed:");
							} else {
								changeDirectory(splitInput[1]);
								outputToClient.println("Server Directory Changed:");
							}
							break;
						}
						case "ls": {
							String[] fileList = getFilelist();
							Integer len = fileList.length;
							System.out.println(len);
							outputToClient.println(len);
							for (String file : fileList) {
								//System.out.println(file);
								outputToClient.println(file);
							}
							break;
						}
						case "mkdir": {
							createDirectory(splitInput[1]);
							outputToClient.println("Directory Created");
							break;
						}
						case "pwd": {
							String currDir = getCurrentDirectory();
							//System.out.println(currDir);
							outputToClient.println("Current Path is: " + currDir);
							break;
						}
						case "get": {
							String fileName = splitInput[1];
							String path = getCurrentDirectory().concat("/".concat(fileName));
							File nf = new File(path);
							String fileName_encrypted = fileName.concat("_se");
							File ennf = new File(path.concat("_se"));
							String key = "Security";
							String plainText = null;
							String cipherText = null;

							try {

								FileReader fileReader = new FileReader(nf);
								BufferedReader bufferedReader = new BufferedReader(fileReader);

								FileWriter fileWriterEn = new FileWriter(ennf);
								BufferedWriter bufferedWriterEn = new BufferedWriter(fileWriterEn);

								while ((plainText = bufferedReader.readLine()) != null) {
									char[][] matrix = genenrateMatrix();
									cipherText = encryptDataSpaces(plainText, key, matrix);
									bufferedWriterEn.write(cipherText);
									bufferedWriterEn.newLine();
									// System.out.println(cipherText);
								}
								bufferedReader.close();
								bufferedWriterEn.close();
							} catch (FileNotFoundException ex) {
								System.out.println("Unable to open file '" + fileName + "'");
							} catch (IOException ex) {
								System.out.println("Error reading file '" + fileName + "'");
							}

							SendFile(Connected, path.concat("_se"));
							break;
						}

						}

					}
				}
			}

		}

	}

	public static void SendFile(Socket Connected, String fileName) throws Exception {
		DataInputStream din = new DataInputStream(Connected.getInputStream());
		DataOutputStream dout = new DataOutputStream(Connected.getOutputStream());
		fileName = din.readUTF();
		String path = getCurrentDirectory();
		path = path.concat("/");
		path = path.concat(fileName);

		File f = new File(path);
		if (!f.exists()) {
			dout.writeUTF("File Not Found");
			return;
		} else {
			dout.writeUTF("READY");
			FileInputStream fin = new FileInputStream(f);
			int ch;
			do {
				ch = fin.read();
				dout.writeUTF(String.valueOf(ch));
			} while (ch != -1);
			fin.close();
			dout.writeUTF("File Receive Successfully");
		}
	}

	public static String[] getFilelist() {
		File currentDirectory = new File(getCurrentDirectory());
		String fileList[] = currentDirectory.list();
		return fileList;
	}
	/*
	 * Function to Change Directory.
	 */

	public static void changeDirectory(String directoryName) {
		String current = getCurrentDirectory();
		String changedDir = current.concat("/");
		changedDir = changedDir.concat(directoryName);
		current = System.setProperty("user.dir", changedDir);
	}

	public static String changeDirectorytoParent() {
		String currentPath = getCurrentDirectory();
		String[] currDir = currentPath.split("/");
		String parentPath = "";
		Integer len = currDir.length;
		for (Integer i = 0; i < len - 1; i++) {
			parentPath = parentPath.concat(currDir[i]);
			if (i < len - 2) {
				parentPath = parentPath.concat("/");
			}
		}
		//System.out.println(parentPath);
		parentPath = System.setProperty("user.dir", parentPath);
		return parentPath;
	}

	/*
	 * Function to get current Directory.
	 */
	public static String getCurrentDirectory() {
		String current = System.getProperty("user.dir");
		return current;
	}

	/*
	 * Function to Create new Directory.
	 */

	public static void createDirectory(String newDirectoryName) {
		boolean result = false;
		String path = getCurrentDirectory().concat("/".concat(newDirectoryName));
		File theDir = new File(path);
		if (!theDir.exists()) {
			System.out.println("Creating New directory: " + newDirectoryName);
			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
				// handle it
			}
			if (result) {
				System.out.println("Directoty named " + newDirectoryName + " created.");
			}
		} else {
			System.out.println("Directoty named " + newDirectoryName + " already exist.");
		}
	}

	public static String encryptDataSpaces(String plainText, String key, char[][] matrix) {
		String cipher = null;
		char[] cipherchar = new char[plainText.length()];
		Integer length = plainText.length();
		plainText = plainText.toUpperCase();
		String newkey = key.toUpperCase();
		newkey = newkey.concat(plainText);
		Integer row;
		for (Integer i = 0; i < length; i++) {
			char plainChar = plainText.charAt(i);
			if (plainChar == ' ') {
				cipherchar[i] = ' ';
			}

			else {
				if (newkey.charAt(i) == ' ') {
					row = 0;
					Integer column = (int) plainText.charAt(i);
					cipherchar[i] = matrix[row][column - 65];
				} else {
					row = (int) newkey.charAt(i);
					Integer column = (int) plainText.charAt(i);
					cipherchar[i] = matrix[row - 65][column - 65];
				}
			}
		}
		cipher = new String(cipherchar);
		return cipher;
	}

	public static char[][] genenrateMatrix() {
		char[][] lookupTable = new char[27][27];
		Integer i, j, count;
		for (i = 0; i < 26; i++) {
			count = 65 + i;
			for (j = 0; j < 26; j++) {
				// System.out.print((char)(0+count) + " ");
				lookupTable[i][j] = (char) (0 + count);
				count++;
				if (count == 91) {
					count = 65;
				}
			}
			// System.out.println();
		}
		return lookupTable;
	}

}