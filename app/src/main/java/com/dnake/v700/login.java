package com.dnake.v700;

public class login {
	public static long ts = 0;
	public static Boolean ok = false;
	public static int timeout = 30*1000;

	public static Boolean passwd(String s) {
		if (s.equals(security.passwd)) {
			ts = System.currentTimeMillis();
			ok = true;
			return true;
		}
		return false;
	}

	public static Boolean ok() {
		if (ok && Math.abs(System.currentTimeMillis()-ts) < timeout)
			return true;
		ok = false;
		return false;
	}

	public static void refresh() {
		if (ok && Math.abs(System.currentTimeMillis()-ts) >= timeout)
			ok = false;
		ts = System.currentTimeMillis();
	}

	public static Boolean timeout() {
		if (ok && Math.abs(System.currentTimeMillis()-ts) < timeout)
			return false;
		ok = false;
		return true;
	}
}
