package gexfWebservice.Tests;

import static org.junit.Assert.*;
import gexfWebservice.*;
import org.junit.Test;

public class Tests {
	@Test public void testHash() {
		Servlet serv = new Servlet();
		String result = serv.hashCodeSHA256(Settings.testfile);
		String expected = "8ff1326e5523d796c89eb85028357c6d73a4e0b9f920a6b40521e8445aaf3a29";
		assertTrue(expected.equals(result));
	}
}
