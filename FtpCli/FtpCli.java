//package edu.cs;
/**
 * @author Saurabh Chaudhari
 *
 */

import java.net.*;
import java.io.*;

public class FtpCli {
	public static void main(String[] args) throws Exception {
		String fromServer = null;
		String toServer;
		String hostname = args[0];
		String port = args[1];

		Socket clientSocket = new Socket(args[0], Integer.parseInt(args[1]));
		// InputStream is = clientSocket.getInputStream();
		BufferedReader inputFromUser = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter outputToServer = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader inputFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		while (true) {
			System.out.print("ftp>");
			fromServer = null;
			if (fromServer == null) {
				toServer = inputFromUser.readLine();
				String splitInput[] = toServer.split(" ");
				switch (splitInput[0]) {
				case "ls": {
					outputToServer.println(toServer);
					fromServer = inputFromServer.readLine();
					Integer count = Integer.parseInt(fromServer);
					for (Integer i = 0; i < count; i++) {
						// outputToServer.println("Server Closed the
						// connection");
						fromServer = inputFromServer.readLine();
						System.out.println(fromServer);
					}
					break;
				}
				case "get": {
					String fileName = splitInput[1];
					outputToServer.println(toServer);
					ReceiveFile(clientSocket, fileName.concat("_se"));
					String plainText = null;
					String key = "Security";
					String SplitFile[] = fileName.split("_ce");
					String fileNameDe = SplitFile[0].concat("_cd");
					String cipherText = null;
					try {
						FileReader fileReader = new FileReader(fileName.concat("_ce"));
						BufferedReader bufferedReader = new BufferedReader(fileReader);
						FileWriter fileWriterDe = new FileWriter(fileNameDe);
						BufferedWriter bufferedWriterDe = new BufferedWriter(fileWriterDe);

						while ((cipherText = bufferedReader.readLine()) != null) {
							char[][] matrix = genenrateMatrix();

							plainText = decryptDataSpaces(cipherText, key, matrix);
							bufferedWriterDe.write(plainText);
							bufferedWriterDe.newLine();
							// System.out.println(plainText);

						}
						bufferedReader.close();
						bufferedWriterDe.close();
					} catch (FileNotFoundException ex) {
						System.out.println("Unable to open file '" + fileName + "'");
					} catch (IOException ex) {
						System.out.println("Error reading file '" + fileName + "'");
					}

					break;
				}
				case "lls": {
					String[] fileList = getFilelist();
					for (String file : fileList) {
						System.out.println(file);
					}
					break;
				}
				case "cd": {
				}
				case "mkdir": {
				}
				case "pwd": {
					outputToServer.println(toServer);
					fromServer = inputFromServer.readLine();
					System.out.println(fromServer);
					break;
				}
				case "quit": {

					outputToServer.println("quit");
					clientSocket.close();
					System.exit(0);
					break;
				}
				}
			}
		}
	}

	public static String[] getFilelist() {
		File currentDirectory = new File(getCurrentDirectory());
		String fileList[] = currentDirectory.list();
		return fileList;
	}

	public static String getCurrentDirectory() {
		String current = System.getProperty("user.dir");
		return current;
	}

	public static void ReceiveFile(Socket clientSocket, String fileName) throws Exception {
		DataInputStream din = new DataInputStream(clientSocket.getInputStream());
		;
		DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String SplitIn[] = fileName.split("_se");
		String NewFile = SplitIn[0].concat("_ce");
		File newFile = new File(NewFile);
		dout.writeUTF(fileName);
		String msgFromServer = din.readUTF();

		if (msgFromServer.compareTo("File Not Found") == 0) {
			System.out.println("File not found on Server ...");
			return;
		} else if (msgFromServer.compareTo("READY") == 0) {
			System.out.println("Receiving File ...");
			File f = new File(SplitIn[0].concat("_ce"));
			if (f.exists()) {
				String Option;
				System.out.println("File Already Exists. Want to OverWrite (Y/N) ?");
				Option = br.readLine();
				if (Option == "N") {
					dout.flush();
					return;
				}
			}
			FileOutputStream fout = new FileOutputStream(f);
			int ch;
			String temp;
			do {
				temp = din.readUTF();
				ch = Integer.parseInt(temp);
				if (ch != -1) {
					fout.write(ch);
				}
			} while (ch != -1);
			fout.close();
			System.out.println(din.readUTF());

		}
	}

	public static String decryptDataSpaces(String cipherText, String key, char[][] matrix) {
		String plainText = null;
		char[] plainChar = new char[cipherText.length()];
		Integer length = cipherText.length();
		cipherText = cipherText.toUpperCase();
		String newKey = key.toUpperCase();
		// char [] newKey1 = new char [cipherText.length()];
		// newKey1[0]= 'S';newKey1[1]= 'E';newKey1[2]= 'C';newKey1[3]=
		// 'U';newKey1[4]= 'R';newKey1[5]= 'I';newKey1[6]= 'T';newKey1[7]= 'Y';

		for (Integer i = 0; i < length; i++) {
			char cipherChar = cipherText.charAt(i);
			if (cipherChar == ' ') {
				plainChar[i] = ' ';
				// newKey1[i+key.length()]=plainChar[i];
				newKey = newKey + plainChar[i];
			} else {
				if (newKey.charAt(i) == ' ') {
					int row = 0;// (((int) newKey.charAt(i)) - 65);
					int j = 0;
					while (j < 26) {
						if (matrix[row][j] == cipherText.charAt(i)) {
							break;
						}
						j++;
					}
					int column = j;
					plainChar[i] = (char) (column + 65);

					newKey = newKey + plainChar[i];
					// newKey1[i+key.length()]=plainChar[i];
				} else {
					int row = (((int) newKey.charAt(i)) - 65);
					int j = 0;
					while (j < 26) {
						if (matrix[row][j] == cipherText.charAt(i)) {
							break;
						}
						j++;
					}
					int column = j;
					plainChar[i] = (char) (column + 65);
					newKey = newKey + plainChar[i];
					// newKey1[i+key.length()]=plainChar[i];
				}
			}
		}
		plainText = new String(plainChar);
		return plainText;
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
