package test;

import util.NetworkScanner;

public class Main {

	public static void main(final String... args) {
		NetworkScanner scanner = new NetworkScanner("127.0.0.1", "127.0.0.3", 0, 60000);
		scanner.scan();

	}
}