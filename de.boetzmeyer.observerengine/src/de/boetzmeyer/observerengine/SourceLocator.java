package de.boetzmeyer.observerengine;

final class SourceLocator {
	private static boolean fileSource = true;

	public static boolean isFileSource() {
		return fileSource;
	}

	public static void setFileSource(final boolean inFileSource) {
		fileSource = inFileSource;
	}

	public static final ISource create() {
		if (fileSource) {
			return LocalSource.create();
		}
		return SQLSource.create();
	}

}
