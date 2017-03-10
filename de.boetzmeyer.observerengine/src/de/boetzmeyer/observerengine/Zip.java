package de.boetzmeyer.observerengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

final class Zip {

	private final Vector m_vecFilesToZip = new Vector();

	private Zip() {
	}

	public static final int doIt(String strZipFilePath, String strFolderToZip) {
		final Zip zip = new Zip();
		return zip.zipFolder(strZipFilePath, strFolderToZip);
	}

	public static final int doIt(String strZipFilePath, String[] strFilesToZip) {
		final Zip zip = new Zip();
		return zip.zipFiles(strZipFilePath, strFilesToZip);
	}

	public static final int doIt(String strZipFilePath, File[] filesToZip) {
		if ((strZipFilePath != null) && (filesToZip != null)) {
			final Zip zip = new Zip();

			String[] strFilesToZip = new String[filesToZip.length];

			for (int i = 0; i < strFilesToZip.length; i++) {
				if (filesToZip[i] != null) {
					strFilesToZip[i] = filesToZip[i].getAbsolutePath();
				}
			}

			return zip.zipFiles(strZipFilePath, strFilesToZip);
		}

		return 0;
	}

	private void findFilesToZip(final File fileRoot) {
		if (fileRoot != null) {
			if (fileRoot.isDirectory()) {
				final File[] files = fileRoot.listFiles();

				this.addFileToZip(fileRoot.getAbsolutePath());

				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						// recursive call
						// ==============
						this.findFilesToZip(files[i]);
					}
				}
			} else if (fileRoot.isFile()) {
				this.addFileToZip(fileRoot.getAbsolutePath());
			}
		}
	}

	private void addFileToZip(String strFilePath) {
		this.m_vecFilesToZip.addElement(strFilePath);
	}

	private void resetFilesToZip() {
		this.m_vecFilesToZip.removeAllElements();
	}

	private int zipFolder(String strZipFilePath, String strFolderToZip) {
		int nZipped = 0;

		ZipOutputStream outStream = null;

		try {
			outStream = new ZipOutputStream(new FileOutputStream(strZipFilePath));

			outStream.setMethod(ZipOutputStream.DEFLATED);

			this.resetFilesToZip();

			this.findFilesToZip(new File(strFolderToZip));

			final Enumeration enumFilesToZip = this.m_vecFilesToZip.elements();

			while (enumFilesToZip.hasMoreElements()) {
				String strFileEntry = (String) enumFilesToZip.nextElement();

				if (strFileEntry != null) {
					File fileNew = new File(strFileEntry);

					if (fileNew.exists() && fileNew.isFile() && (fileNew.length() > 0)) {
						FileInputStream inStream = null;

						ZipEntry entry = null;

						try {
							strFileEntry = this.removeSourcePath(strFolderToZip, strFileEntry);

							entry = new ZipEntry(strFileEntry);

							outStream.putNextEntry(entry);

							final int BUFFER_SIZE = 1024;

							final byte[] bytes = new byte[BUFFER_SIZE];

							int read = 0;

							inStream = new FileInputStream(strFileEntry);

							while ((read = inStream.read(bytes, 0, BUFFER_SIZE)) != -1) {
								outStream.write(bytes, 0, read);
							}

							nZipped++;
						} catch (final Exception e) {
							e.printStackTrace();
						} finally {
							outStream.closeEntry();

							FileSystem.close(inStream);
						}
					}
				}
			}
		} catch (final FileNotFoundException e) {
			// ignore
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			FileSystem.close(outStream);
		}

		return nZipped;
	}

	private String extractFileName(String strFilePath) {
		String strFileName = "";

		int nPos = strFilePath.lastIndexOf(File.separatorChar);

		if (nPos > -1) {
			strFileName = strFilePath.substring(nPos + 1, strFilePath.length());
		}

		return strFileName;
	}

	private String removeSourcePath(String strSourcePath, String strFilePath) {
		int nPos = strSourcePath.length();

		if ((nPos > 0) && (nPos < strFilePath.length())) {
			strFilePath = strFilePath.substring(nPos, strFilePath.length());
		}

		return strFilePath;
	}

	private int zipFiles(String strZipFilePath, String[] strFilesToZip) {
		int nZipped = 0;

		ZipOutputStream outStream = null;

		try {
			outStream = new ZipOutputStream(new FileOutputStream(strZipFilePath));

			outStream.setMethod(ZipOutputStream.DEFLATED);

			this.resetFilesToZip();

			for (int i = 0; i < strFilesToZip.length; i++) {
				if (strFilesToZip[i] != null) {
					File fileNew = new File(strFilesToZip[i]);

					if (fileNew.exists() && fileNew.isFile() && (fileNew.length() > 0)) {
						FileInputStream inStream = null;

						ZipEntry entry = null;

						try {
							String strZipEntry = extractFileName(strFilesToZip[i]);

							entry = new ZipEntry(strZipEntry);

							outStream.putNextEntry(entry);

							final int BUFFER_SIZE = 1024;

							byte[] bytes = new byte[BUFFER_SIZE];

							int read = 0;

							inStream = new FileInputStream(strFilesToZip[i]);

							while ((read = inStream.read(bytes, 0, BUFFER_SIZE)) != -1) {
								outStream.write(bytes, 0, read);
							}

							nZipped++;
						} catch (final FileNotFoundException e) {
							// ignore
						} catch (final Exception e) {
							e.printStackTrace();
						} finally {
							FileSystem.close(inStream);
							outStream.closeEntry();
						}
					}
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			FileSystem.close(outStream);
		}

		return nZipped;
	}

}
