package com.swatt.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IoUtilities {
	private static final int BLOCK_SIZE = 2048;
	
	private static FileFilter directoryFileFilter = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory();
		}
	};

	public static int copyStream(InputStream in, OutputStream out) throws IOException { return copyStream(in, out, 0); }
	
	public static int copyStream(InputStream in, OutputStream out, int len) throws IOException {
		byte buf[] = new byte[BLOCK_SIZE];
		
		int count = 0;

		if (len <= 0)
			len = Integer.MAX_VALUE;

		for (;len > 0;) {
			int n = Math.min(BLOCK_SIZE, len);

			n = in.read(buf, 0, n);

			if (n < 0)
				break;

			out.write(buf, 0, n);

			count += n;
			len -= n;
		}

		return count;
	}

	public static String fileToString(String fileName) throws IOException { return fileToString(new File(fileName)); }
	
	public static String fileToString(File file) throws IOException { return new String(fileToBytes(file)); }
	
	public static byte[] fileToBytes(String fileName) throws IOException { return fileToBytes(new File(fileName)); }
	
	public static byte[] fileToBytes(File file) throws IOException {
		InputStream in = null;
		
		try {
			in = new FileInputStream(file);
			return streamToBytes(in);
		} finally {
			close(in);
			
		}
	}

	public static byte[] streamToBytes(InputStream in) throws IOException {
		try {
			byte buf[]   = new byte[BLOCK_SIZE];
			int size = 0;

			for(;;) {
				int nRead = in.read(buf, size, BLOCK_SIZE);

				if (nRead < 0)
						break;

				size += nRead;

				if ( (size + BLOCK_SIZE) > buf.length) {
						byte temp[] = new byte[2*buf.length];
						System.arraycopy(buf, 0, temp, 0, buf.length);
						buf = temp;
				}
			}
			
			
			if (size == buf.length)
				return buf;
			else {
				byte answer[] = new byte[size];
				System.arraycopy(buf, 0, answer, 0, size);
				return answer;
			}
		} finally {
			in.close();			
		}
	}

	public static byte[] streamToBytes(InputStream in, int length) throws IOException {
		byte buf[] = new byte[length];
		streamToBytes(in, buf);
		return buf;
	}

	public static void streamToBytes(InputStream in, byte buf[]) throws IOException {
		streamToBytes(in, buf, 0, buf.length);
	}
	
	public static void  streamToBytes(InputStream in, byte buf[], int length) throws IOException {
		streamToBytes(in, buf, 0, length);

	}
	
	public static void streamToBytes(InputStream in, byte buf[], int pos, int length) throws IOException {
		long lastZero = -1;
		
		while(length > 0) {				// Fix-Me: GS - We probably need timeout logic also, but we may go to NIO first
			int n = in.read(buf, pos, length);
			
			if (n == 0) {
				long now = System.currentTimeMillis();
				
				if (lastZero < 0)
					lastZero = now;
				
				long diff = now - lastZero;
				
				if (diff < 10)
					Thread.yield();
				else
					ConcurrencyUtilities.sleep(10);
				
				continue;
			}
			
			lastZero = -1;
			
			pos += n;
			length -= n;
		}
	}
	
	public static String streamToString(InputStream in) throws IOException {
		byte buf[] = streamToBytes(in);
		return new String(buf);
	}

	public static String streamToString(InputStream in, int length) throws IOException {
		byte buf[] = streamToBytes(in, length);
		return new String(buf);
	}
	
	public static InputStream bytesToStream(byte buf[]) {
		return new ByteArrayInputStream(buf);
	}

	public static void stringToFile(String fileName, String contents) throws IOException { stringToFile(new File(fileName), contents); }
	public static void stringToFile(File file, String contents) throws IOException { bytesToFile(file, contents.getBytes()); }
	public static void bytesToFile(String fileName, byte buf[]) throws IOException { bytesToFile(new File(fileName), buf); }

	public static void bytesToFile(File file, byte buf[]) throws IOException {
		FileOutputStream fout = new FileOutputStream(file);
		fout.write(buf);
		fout.close();
	}
	
	public static int available(InputStream in) {
		if (in == null) 
			return -1;
		else {
			try {
				return in.available();
			} catch (IOException e) {
				return -1;
			}
		}
	}
	
	public static boolean readyToRead(InputStream in) {
		return (available(in) > 0);
	}
	
	public static void close(InputStream in) {
		try {
			if (in != null) 
				in.close();
		} catch (IOException e) { }
	}

	public static void close(OutputStream out) {
		try {
			if (out != null)
				out.close();
		} catch (IOException e) { }
	}
	
	public static void close(RandomAccessFile randomAccessFile) {
		try {
			if (randomAccessFile != null) 
				randomAccessFile.close();
		} catch (IOException e) { }
	}

	
	public static void close(Channel channel) {
		try {
			if (channel != null)
				channel.close();
		} catch (IOException e) { }
	}



	static final void deleteDirectoryAndContents(File dir) {
		if (!dir.isDirectory() || !dir.exists())
			return;

		File files[] = dir.listFiles();

		for (int i=0; i < files.length; i++) {
			File file = files[i];
			
			if (file.isDirectory())
				deleteDirectoryAndContents(file);
			else
				file.delete();
		}

		dir.delete();
	}
	
	public static PrintStream getPrintStream(OutputStream out) {
		if (out instanceof PrintStream)
			return (PrintStream) out;
		else
			return new PrintStream(out);
	}
	
	public static OutputStream getOutputStream(final DataOutput dout) {
		if (dout instanceof OutputStream)
			return (OutputStream) dout;
		
		return new OutputStream() {

			public void write(int b) throws IOException {
				dout.write(b);				
			}
			
			public void write(byte[] buf) throws IOException {
				dout.write(buf);
			}

			public void write(byte[] buf, int off, int len) throws IOException {
				dout.write(buf, off, len);
			}
		};
	}
	
	public static PrintStream getPrintStream(DataOutput dout) {
		return getPrintStream(getOutputStream(dout));
	}

	public static DataInputStream getDataInputStream(InputStream in) {
		if (in instanceof DataInputStream)
			return (DataInputStream) in;
		else
			return new DataInputStream(in);
	}
	
	public static OutputStream toOutputStream(String fileName) throws FileNotFoundException {
		return new FileOutputStream(new File(fileName));
	}
	
	public static ArrayList readTrimmedLines(String fileName) throws IOException {
		return readLines(new File(fileName), true);
	}

	public static ArrayList readLines(String fileName, boolean trim) throws IOException {
		return readLines(new File(fileName), trim, false);
	}
	
	public static ArrayList readLines(String fileName, boolean trim, boolean omitEmptyLines) throws IOException {
		return readLines(new File(fileName), trim, omitEmptyLines);
	}

	public static ArrayList readTrimmedLines(File file) throws IOException {
		return readLines(new FileInputStream(file), true);
	}

	public static ArrayList readLines(File file, boolean trim) throws IOException {
		return readLines(new FileInputStream(file), trim, false);
	}
	
	public static ArrayList readLines(File file, boolean trim, boolean omitEmptyLines) throws IOException {
		return readLines(new FileInputStream(file), trim, omitEmptyLines);
	}

	
	public static ArrayList readTrimmedLines(InputStream in) throws IOException {
		return readLines(in, true);
	}

	public static ArrayList readLines(InputStream in, boolean trim) throws IOException {
		return readLines(in, trim, false);
	}
	
	public static ArrayList readLines(InputStream in, boolean trim, boolean omitEmptyLines) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		ArrayList lines = new ArrayList();
		
		for (;;) {
			String line = reader.readLine();
			
			if (line != null) {
				if (trim)
					line = line.trim();
				
				if (omitEmptyLines && (line.length() == 0))
					continue;
				else
					lines.add(line);
				
			} else
				break;
		}
		
		lines.trimToSize();
		
		return lines;
	}
	
    public static String getExtension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) 
            ext = s.substring(i+1).toLowerCase();

        return ext;
    }
    
    public static File[] getSubDirectories(File dir) {
		return dir.listFiles(new FileFilter() {
			
			public boolean accept(File file) {
				if (file.isDirectory()) 
					return true;
				else
					return false;
			}
		});
    }

    public static File[] getFilesByExtension(File dir, String extensions[]) {
    	return dir.listFiles(createExtensionFileFilter(extensions, false));
    }

    
    public static File[] getFilesByExtension(File dir, String extension) {
    	return dir.listFiles(createExtensionFileFilter(extension, false));
    }
    
    public static File[] getFilesByExtension(File dir, String extension, boolean recursive) {
    	if (recursive) {
    		FileFilter fileFilter = createExtensionFileFilter(extension, false);
    		ArrayList files = new ArrayList();
    		addFilesByExtension(dir, fileFilter, files);
    		return (File[]) files.toArray(new File[files.size()]);
    	} else 
    		return getFilesByExtension(dir, extension);
    	
    }
    
    public static File[] getFilesRecursive(File dir) {
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File pathname) {
				return true;
			}
		};
		ArrayList files = new ArrayList();
		addFilesByExtension(dir, fileFilter, files);
		return (File[]) files.toArray(new File[files.size()]);
    	
    }
    
    private static void addFilesByExtension(File dir, FileFilter fileFilter, Collection files) {
    	CollectionsUtilities.add(files, dir.listFiles(fileFilter));
    	
    	File subDirs[] = dir.listFiles(directoryFileFilter);
    	
    	for (int i=0; i < subDirs.length; i++)
    		addFilesByExtension(subDirs[i], fileFilter, files);
    }
    
    private static class ExtensionFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter, java.io.FilenameFilter {
    	String extensions[];
    	boolean includeDirectories;

    	ExtensionFilter(String extensions[]) { this(extensions, true); }

    	ExtensionFilter(String extensions[], boolean includeDirectories) {
    		this.extensions = extensions;
    		this.includeDirectories = includeDirectories;
    	}
    	
		public boolean accept(File file) {
			if (file.isDirectory()) 
				return includeDirectories;

			String extension = getExtension(file);
			
			if (extension != null) {
				for(int i=0; i < extensions.length; i++) {
					if (extension.equals(extensions[i]))
						return true;
				}
			} 
			
			return false;
		}

		public String getDescription() {
			String result = "Extensions (";
			for (int i=0; i < extensions.length; i++) {
				result += extensions[i];
				
				if ((i+1) != extensions.length)
					result += ",";
			}
			
			result += ")";
			
			return result;
		}

		public boolean accept(File dir, String name) {
			return accept(new File(dir, name));
		}
	}

	public static javax.swing.filechooser.FileFilter createExtensionFileChooserFilter(String extensions[]) {
		return new ExtensionFilter(extensions);
	}
	
	public static java.io.FileFilter createExtensionFileFilter(String extensions[]) {
		return new ExtensionFilter(extensions);
	}
	
	public static java.io.FileFilter createExtensionFileFilter(String extensions[], boolean includeDirectories) {
		return new ExtensionFilter(extensions, includeDirectories);
	}
	
	public static FilenameFilter createExtensionFilenameFilter(String extensions[]) {
		return new ExtensionFilter(extensions);
	}
	
    public static javax.swing.filechooser.FileFilter createExtensionFileChooserFilter(String extension) {
    	return createExtensionFileChooserFilter(new String[] { extension }); 
    }
    
	public static java.io.FileFilter createExtensionFileFilter(String extension, boolean includeDirectories) {
		return new ExtensionFilter(new String[] { extension }, includeDirectories); 
	}

	public static java.io.FileFilter createExtensionFileFilter(String extension) {
		return new ExtensionFilter(new String[] { extension }); 
	}
	
	public static FilenameFilter createExtensionFilenameFilter(String extension) {
		return new ExtensionFilter(new String[] { extension });
	}
    public static javax.swing.filechooser.FileFilter createExtensionFileChooserFilter(Collection extensions) {
    	return createExtensionFileChooserFilter((String[]) extensions.toArray(new String[extensions.size()])); 
    }
    
	public static java.io.FileFilter createExtensionFileFilter(Collection extensions) {
		return new ExtensionFilter((String[]) extensions.toArray(new String[extensions.size()])); 
	}
	
	public static FilenameFilter createExtensionFilenameFilter(Collection extensions) {
		return new ExtensionFilter((String[]) extensions.toArray(new String[extensions.size()])); 
	}
	

//	public static void serializableToStream(Serializable serializable, OutputStream out) throws IOException {
//		ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
//		objectOutputStream.writeObject(serializable);
//	}
	
	public static void writeSerializable(Serializable serializable, OutputStream out) throws IOException {
		byte buf[] = serializableToBytes(serializable);
		writeInt(out, buf.length);
		out.write(buf);
	}
	
	public static byte[] serializableToBytes(Serializable serializable) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		if (serializable != null) {
			IoUtilities.writeBoolean(bout, true);
			ObjectOutputStream oout = new ObjectOutputStream(bout);
			oout.writeObject(serializable);
		} else  	
			IoUtilities.writeBoolean(bout, false);

		bout.close();
		
		byte result[] = bout.toByteArray();
		return result;
	}



	
	public static Serializable readSerializable(InputStream in) throws IOException, ClassNotFoundException {
		int len = IoUtilities.readInt(in);
		byte buf[] = new byte[len];
		streamToBytes(in, buf);
		return bytesToSerializable(buf);
	}
	
	public static Serializable bytesToSerializable(byte buf[]) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bin = new ByteArrayInputStream(buf);
		
		if (IoUtilities.readBoolean(bin)) {
			ObjectInputStream oin = new ObjectInputStream(bin);
			return (Serializable) oin.readObject();
		} else 
			return null;
	}
	
	public static void writeBoolean(OutputStream out, boolean value) throws IOException {
		if (value)
			out.write(1);
		else
			out.write(0);
	}
	
	public static boolean readBoolean(InputStream in) throws IOException {
		return (in.read() != 0);
	}
	
	public static void writeInt(OutputStream out, int value) throws IOException {
		out.write((value >>> 24) & 0xFF);
		out.write((value >>> 16) & 0xFF);
		out.write((value >>> 8) & 0xFF);
		out.write(value & 0xFF);
	}
	
	public static int readInt(InputStream in) throws IOException {
		int b1 = in.read();
		int b2 = in.read();
		int b3 = in.read();
		int b4 = in.read();
		        
		return ((b1 << 24) + (b2 << 16) + (b3 << 8) + (b4 << 0));
	}
	
	public static Iterator getAllLines(String fileName) throws IOException { return readAllLines(new File(fileName)).iterator(); }
	public static Iterator getAllLines(File file) throws IOException { return readAllLines(new FileInputStream(file)).iterator(); }
	public static Iterator getAllLines(InputStream in) throws IOException { return readAllLines(in).iterator(); }

	public static List readAllLines(String fileName) throws IOException { return readAllLines(new File(fileName)); }
	public static List readAllLines(File file) throws IOException { 
		InputStream in = null;
		try { 
			in = new FileInputStream(file);
			return readAllLines(in);
		} finally {
			close(in);
		}
	}

	public static List readAllLines(InputStream in) throws IOException {
		ArrayList lines = new ArrayList();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		for(;;) {
			String line = reader.readLine();
			
			if (line == null)
				break;
			
			lines.add(line);			
		}
		
		return lines;
	}
	

	public static void writeAllLines(String fileName, List lines) throws IOException {  writeAllLines(new File(fileName), lines.iterator()); }
	public static void writeAllLines(File file, List lines) throws IOException {  writeAllLines(file, lines.iterator()); }
	public static void writeAllLines(OutputStream out, List lines) throws IOException {  writeAllLines(out, lines.iterator()); }
	public static void writeAllLines(String fileName, Iterator lines) throws IOException {  writeAllLines(new File(fileName), lines); }
	
	public static void writeAllLines(File file, Iterator lines) throws IOException {  
		FileOutputStream fout = new FileOutputStream(file);
		writeAllLines(fout, lines); 
		fout.close();
	}

	public static void writeAllLines(OutputStream out, Iterator lines) throws IOException {
		PrintStream pout = new PrintStream(out);
		
		for (Iterator i = lines; i.hasNext();) {
			String line = (String) i.next();
			pout.println(line);
		}
	}
	
	public static File[] listFiles(File baseDir, final String patternText) {
		final Pattern pattern = Pattern.compile(patternText);

		FilenameFilter fileNameFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				Matcher matcher = pattern.matcher(name);
				return matcher.find(); 
			}
		};
		
		return baseDir.listFiles(fileNameFilter);
	}
	
	public static File getCurrentDirectory() { 
		return new File(System.getProperty("user.dir"));
	}
	
	public static final BufferedOutputStream createBufferedOutputFileStream(String fileName, int bufSize) throws FileNotFoundException {
		return createBufferedOutputFileStream(new File(fileName), bufSize);
	}
	
	public static final BufferedOutputStream createBufferedOutputFileStream(File file, int bufSize) throws FileNotFoundException {
		FileOutputStream fout = new FileOutputStream(file);
		return new BufferedOutputStream(fout, bufSize);
	}
	
	public static final BufferedInputStream createBufferedInputFileStream(String fileName, int bufSize) throws FileNotFoundException {
		return createBufferedInputFileStream(new File(fileName), bufSize);
	}
	
	public static final BufferedInputStream createBufferedInputFileStream(File file, int bufSize) throws FileNotFoundException {
		FileInputStream fin = new FileInputStream(file);
		return new BufferedInputStream(fin, bufSize);
	}
}