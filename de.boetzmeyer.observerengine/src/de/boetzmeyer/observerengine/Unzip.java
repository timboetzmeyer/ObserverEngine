package de.boetzmeyer.observerengine;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

final class Unzip {

	private Unzip() {
	}

	public static final List<String> doIt(String strZipFilePath, String strUnzipFolder) {
		List<String> listFilePaths = new ArrayList<String>();

		try {
			final Unzip unzip = new Unzip();

			final ZipFile zipFile = new ZipFile(strZipFilePath);

			listFilePaths = unzip.unzipFolder(zipFile, strUnzipFolder);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return listFilePaths;
	}

	private List<String> unzipFolder(ZipFile zipFile, String strUnzipFolder) {
		List<String> listFilePaths = new ArrayList<String>();

		Enumeration e = zipFile.entries();

		int nSize = zipFile.size();

		while (e.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) e.nextElement();

			if (this.createUnzippedFile(zipFile, zipEntry, strUnzipFolder)) {
				listFilePaths.add(zipEntry.getName());
			}
		}

		return listFilePaths;
	}

	private boolean createUnzippedFile(ZipFile zipFile, ZipEntry zipEntry, String strUnzipFolder) {
		boolean bUnzipped = false;

		if ((zipFile != null) && (zipEntry != null)) {
			try {
				File fileZipEntry = new File(zipEntry.getName());

				File fileOutput = new File(strUnzipFolder + File.separatorChar + zipEntry.getName());

				if (!zipEntry.isDirectory()) {
					BufferedInputStream inStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));

					File dir = new File(fileOutput.getParent());

					dir.mkdirs();

					BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(fileOutput));

					byte[] buffer = new byte[0xFFFF];
					int len = inStream.read(buffer);
					while (len != -1) {
						outStream.write(buffer, 0, len);
						len = inStream.read(buffer);
					}

					outStream.close();
				} else {
					fileOutput.mkdirs();
				}

				long lUncompressedLength = zipEntry.getSize();

				long lFileLength = fileOutput.length();

				bUnzipped = (lUncompressedLength == lFileLength);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		return bUnzipped;
	}
}
