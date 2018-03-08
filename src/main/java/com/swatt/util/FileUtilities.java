package com.swatt.util;

/* Copyright 2005 Gerry Seidman, Inc. All rights reserved.  GERRY SEIDMAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class FileUtilities {

	public static byte[] getFileAsBytes(String filename) throws IOException {
		InputStream in = null; 

		try {
			File file = new File(filename);
			long length = file.length();
			byte[] bytes = new byte[(int)length];
			in = new FileInputStream(file);
			int bytesRead = in.read(bytes);
			if (bytesRead != length)
				throw new IOException("Error: Cannot read all, "+bytesRead+" != "+length);
			return bytes;
		} finally {
			IoUtilities.close(in);
		}
	}
	
	public static String fileToString(String fileName) throws IOException { return fileToString(new File(fileName)); }

	public static String fileToString(File file) throws IOException { 
		InputStream in = null;

		try {
			in = new FileInputStream(file);
			int length = (int) file.length();	// Fix-Me: this will fail for HUGH files
	
			return IoUtilities.streamToString(in, length);
		} finally {
			IoUtilities.close(in);
		}
	}

	public static void stringToFile(String text, String fileName) throws IOException { stringToFile(text, new File(fileName)); }

	public static void stringToFile(String text, File file) throws IOException { 
		OutputStream out = null;

		try {
			guaranteePath(file);
			out = new FileOutputStream(file);
			out.write(text.getBytes());
		} finally {
			IoUtilities.close(out);
		}
	}
	
	public static boolean guaranteePath(File file) {
		File parent = new File(file.getParent());
		if (parent.exists())
			return true;
		
		return parent.mkdirs();
	}

	public static boolean guaranteePath(String file) {
		return guaranteePath(new File(file));
	}

	public static boolean guaranteeDirectory(File file) {
		if (file.exists())
			return true;
		
		return file.mkdirs();
	}

	public static boolean guaranteeDirectory(String directoryPath) {
		return guaranteeDirectory(new File(directoryPath));
	}

	public static void copyStream(InputStream in, OutputStream out) throws IOException {
		copyStream(in, out, -1);
	}


	public static void copyStream(InputStream in, OutputStream out, long limit) throws IOException {
		byte buf[] = new byte[4096];
		int nRead;
	
		for (long total = 0; limit < 0 || total < limit;) {
			nRead = in.read(buf, 0, buf.length);
			if (nRead < 0)
				break;

			out.write(buf, 0, nRead);
			
			total += nRead;
		}

	}

	public static void copy(File from, File to) throws IOException {
		FileInputStream fin = null;
		FileOutputStream fout = null;

		try {
			guaranteePath(to);
			
			fin = new FileInputStream(from);
			fout = new FileOutputStream(to);
			
			copyStream(fin, fout);
		} finally {
			IoUtilities.close(fin);
			IoUtilities.close(fout);
		}
	}
	
	public static void copy(File from, OutputStream out) throws IOException {
		FileInputStream fin = null;
		fin = new FileInputStream(from);
		copyStream(fin, out);
	}
	
	public static Collection getFiles(String dirName) {
		return getFiles(new File(dirName), false);
	}
	
	public static Collection getFiles(File dir) {
		return getFiles(dir, false);
	}

	public static Collection getFiles(String dirName, boolean deep) {
		return getFiles(new File(dirName), deep);
	}
	
		
	public static Collection getFiles(File dir, boolean deep) {
		ArrayList files = new ArrayList();
		
		File dirFiles[] = dir.listFiles();
		
		for (File file : dirFiles) {
			if (file.isFile()) {
				files.add(file);
			} else if (deep) {
				files.addAll(getFiles(file, deep));
			}
		}
		
		return files;
	}
	
	public static Collection getDirectories(String dirName) {
		return getDirectories(new File(dirName));
	}

	
	public static Collection getDirectories(File dir) {
		ArrayList files = new ArrayList();
		CollectionsUtilities.add(files, dir.listFiles());
		
		for (Iterator i = files.iterator(); i.hasNext();) {
			File file = (File) i.next();
			
			if (file.isFile())
				i.remove();
		}
		
		return files;
	}
	
	public static int getNextUnusedCountNumber(File root, String ext) {
		int next = 0;
		
		for (File file: root.listFiles()) {
			String fileName = file.getName();
			if (fileName.endsWith(ext)) {
				fileName = fileName.substring(0, fileName.length() - ext.length());
				try {
					int val = Integer.parseInt(fileName);
					next = Math.max(next, val+1);
				} catch (Exception e) { } // probably not an int
			}
		}
		
		return next;
	}

	
	public static String getDosScriptAbsolutePath(File file) {
		String fileName = file.getAbsolutePath();
		
		String extras = ".!#$%-@^_`{}~" + "/\\:*?\"<>|";   // Note /\\:*?\"<>| are there because they are not valid DOS file names anyway (but appear in c:/...)
		
		for(int i=0; i < fileName.length(); i++) {
			char c = fileName.charAt(i);
			
			if (Character.isLetterOrDigit(c) || (extras.indexOf(c) >= 0))
				continue;
			else
				return '"' + fileName + '"';
		}
		
		return fileName;
	}

	public static final boolean isSameFile(File file1, File file2) {
		return isSameFile(file1, file2, 5000);
	}

	public static final boolean isSameFile(File file1, File file2, long threshold) {
		if (!file1.getName().equals(file2.getName()))
			return false;

		if (file1.isFile() && file2.isFile()) {
			long diff = file1.lastModified() - file2.lastModified();
			
			if (diff > threshold) {
//System.out.println(diff  + " " + file1.lastModified() + " " + file2.lastModified() + " " + file1 );
				return false;
			}

			if (file1.length() != file2.length())
				return false;

		}
		
		return true;
	}

	public static String getSimpleName(File file) {
		return getSimpleName(file.getName()); 
	}
	
	public static String getSimpleName(String fileName) {
		String ext = getExtension(fileName);
		
		if (ext != null) {
			return fileName.substring(0,  fileName.length() - ext.length() - 1);
		} else
			return fileName;
	}
	

	public static String getExtension(File file) { return getExtension(file.getName()); }

	public static String getExtension(String fileName) {
		String extension = null;
		
		int pos = fileName.lastIndexOf('.');
		
		if ((pos >= 0) && (pos != fileName.length())) {
			extension = fileName.substring(pos+1).trim();
			
			if (extension.length() == 0)
				return null;
			else
				return extension;
		}
		else
			return null;
	}
	
	public static boolean isAllowedToCreateFiles(String directory) {
		File file = new File(directory);
		return isAllowedToCreateFiles(file);
	}

	public static boolean isAllowedToCreateFiles(File file) {
		if(!file.isDirectory())
			return false;

		File tempFile = null;
		try {
			tempFile = File.createTempFile("iam", "tmp", file);
		} catch (Exception e) {
			return false;
		}

		tempFile.delete();
		return true;		
	}	
	
	public static File getUserDir() {
		return new File(System.getProperty("user.dir"));
	}
	
	public static boolean hasExtension(File file, String extension) {
		return hasSuffix(file, '.' + extension);
	}

	public static boolean hasSuffix(File file, String suffix) {
		String name = file.getName();
		int pos = name.indexOf(suffix);
		return (pos >= 0);
	}
	
	public static File stripExtension(File file, String extension) {
		return stripSuffix(file, '.' + extension);
	}
	
	public static File stripSuffix(File file, String suffix) {
		File parent = file.getParentFile();
		String name = file.getName();
		int pos = name.indexOf(suffix);
		name = name.substring(0, pos);
		return new File(parent, name);
	}
	
	public static File appendSuffix(File file, String suffix) {
		File parent = file.getParentFile();
		String name = file.getName() + suffix;
		return new File(parent, name);
	}
	
	public static File appendExtension(File file, String extension) {
		File parent = file.getParentFile();
		String name = file.getName() + '.' + extension;
		return new File(parent, name);
	}
}
