package de.boetzmeyer.observerengine;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

final class FileSystem {

	private FileSystem() {
	}

	public static final void close(final InputStream in) {
		try {
			in.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static final void close(final OutputStream out) {
		try {
			out.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static final int zipFiles(String strZipFilePath, String[] strFilesToZip) {
		return Zip.doIt(strZipFilePath, strFilesToZip);
	}

	public static final int zipFolder(String strZipFilePath, String strFolderToZip) {
		return Zip.doIt(strZipFilePath, strFolderToZip);
	}

	public static final List<String> unzipFile(String strZipFilePath, String strDestinationFolder) {
		return Unzip.doIt(strZipFilePath, strDestinationFolder);
	}

}
