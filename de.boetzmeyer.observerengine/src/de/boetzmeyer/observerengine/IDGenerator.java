package de.boetzmeyer.observerengine;

final class IDGenerator {

	private IDGenerator() {
	}

	public static final long createPrimaryKey() {
		final long OFFSET = 1000000;

		final long lTimeInMillis = System.currentTimeMillis();

		final long lRandom = (long) (OFFSET * Math.random());

		return (long) (lRandom + OFFSET * lTimeInMillis);
	}

}
