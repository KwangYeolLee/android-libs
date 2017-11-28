package com.rayworld.androidlibs;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

public class FTPUtil {
	public static final String TAG = "FTPUtill";

	/**
	 * FTP 파일 다운로드
	 * @param server : 호스트
	 * @param portNumber : 포트
	 * @param user : 아이디
	 * @param password : 패스워드
	 * @param workDir : 서버 작업 디렉토리(루트 이후 상대경로)
	 * @param filename : 서버 파일명
	 * @param localFile : 저장 될 로컬파일
	 * @param mode : Passive(true), Active(false)
	 * @return : 성공여부
     * @throws IOException
     */
	public static boolean downloadAndSaveFile(String server, int portNumber,
			String user, String password, String workDir, String filename, File localFile, boolean mode)
					throws IOException {
		FTPClient ftp = null;

		try {
			ftp = new FTPClient();
			ftp.connect(server, portNumber);
			Log.d(TAG, "FTP Connected. Reply: " + ftp.getReplyString());

			ftp.login(user, password);
			Log.d(TAG, "FTP Logged in");
			ftp.changeWorkingDirectory(workDir);
			ftp.setFileType(FTP.BINARY_FILE_TYPE);

			if(mode) {
				ftp.enterLocalPassiveMode();
			}

			Log.d(TAG, "FTP Downloading");
			OutputStream outputStream = null;
			boolean success = false;
			try {
				outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
				success = ftp.retrieveFile(filename, outputStream);
			} finally {
				if (outputStream != null) {
					outputStream.close();
				}
			}

			return success;
		} finally {
			if (ftp != null) {
				ftp.logout();
				ftp.disconnect();
				Log.d(TAG, "FTP Disconnected");
			}
		}
	}
	
	public static String readFromFile(String server, int portNumber,
			String user, String password, String workDir, String filename, boolean mode)
					throws IOException {
		FTPClient ftp = null;
		String text = null;
		try {
			ftp = new FTPClient();
			ftp.connect(server, portNumber);
			Log.d(TAG, "FTP Connected. Reply: " + ftp.getReplyString());

			ftp.login(user, password);
			Log.d(TAG, "FTP Logged in");
			ftp.changeWorkingDirectory(workDir);
			ftp.setFileType(FTP.BINARY_FILE_TYPE);

			if(mode) {
				ftp.enterLocalPassiveMode();
			}

			Log.d(TAG, "FTP Downloading");
			InputStream inputStream = null;
			BufferedReader br = null;
			String line = null;
			try {
				inputStream = ftp.retrieveFileStream(filename);
				br = new BufferedReader(new InputStreamReader(inputStream, "euc-kr"));
	            while ((line = br.readLine())!= null) {
	            	text = line;
	            }
			} finally {
				if (br != null) {
					br.close();
				}
			}

			return text;
		} finally {
			if (ftp != null) {
				ftp.logout();
				ftp.disconnect();
				Log.d(TAG, "FTP Disconnected");
			}
		}
	}


	public static Properties readFromVersionFile(String server, int portNumber, String user, String password,
												 String workDir, String filename, boolean mode) throws IOException {
		FTPClient ftp = null;
		Properties pros = null;
		InputStream is = null;
		try {
			ftp = new FTPClient();
			ftp.connect(server, portNumber);
			Log.d(TAG, "FTP Connected. Reply: " + ftp.getReplyString());

			ftp.login(user, password);
			Log.d(TAG, "FTP Logged in");
			ftp.changeWorkingDirectory(workDir);

			ftp.setFileType(FTP.BINARY_FILE_TYPE);

			if(mode) {
				ftp.enterLocalPassiveMode();
			}

			Log.d(TAG, "FTP Downloading");
			is = ftp.retrieveFileStream(filename);
			if(is != null) {
				pros = new Properties();
				pros.load(is);
			}
		} finally {
			if (is != null) {
				is.close();
			}

			if (ftp != null) {
				ftp.logout();
				ftp.disconnect();
				Log.d(TAG, "FTP Disconnected");
			}
		}
		return pros;
	}
	
	public static boolean uploadFile(String server, int portNumber, String user, String password, 
								String workDir, String filename, File localFile, boolean mode) throws IOException {
		
		FTPClient ftp = null;
		
		try {
			ftp = new FTPClient();
			ftp.connect(server, portNumber);
			Log.d(TAG, "FTP Connected. Reply: " + ftp.getReplyString());

			ftp.login(user, password);
			Log.d(TAG, "FTP Logged in");
			ftp.changeWorkingDirectory(workDir);
			ftp.setFileType(FTP.BINARY_FILE_TYPE);

			if(mode) {
				ftp.enterLocalPassiveMode();
			}

			Log.d(TAG, "FTP Uploading");
			InputStream inputStream = null;
			boolean success = false;
			try {
				inputStream = new BufferedInputStream(new FileInputStream(localFile));
				success = ftp.storeFile(filename, inputStream);
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}

			return success;
		} finally {
			if (ftp != null) {
				ftp.logout();
				ftp.disconnect();
				Log.d(TAG, "FTP Disconnected");
			}
		}
		
	}
}
