package com.dnake.v700;

public class ioctl {
	private static int hooter_onoff = 100;

	public static void hooter(int onoff) {
		if (hooter_onoff != onoff) {
			hooter_onoff = onoff;

			dmsg req = new dmsg();
			dxml p = new dxml();
			p.setInt("/params/onoff", onoff);
			req.to("/control/hooter", p.toString());
		}
	}
}
