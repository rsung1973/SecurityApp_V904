package com.dnake.special;

import com.dnake.v700.dxml;

public class ipc {
	public static int MAX = 16;
	public static String name[] = new String[MAX];
	public static String rtsp[] = new String[MAX];
	public static int idx = 0;

	public static String url = "/dnake/cfg/ipc.xml";

	public static void load() {
		dxml p = new dxml();
		if (p.load(url)) {
			idx = p.getInt("/sys/max", 0);
			for (int i = 0; i < idx; i++) {
				name[i] = p.getText("/sys/r" + i + "/name");
				rtsp[i] = p.getText("/sys/r" + i + "/url");
			}
		} else
			save();
	}

	public static void save() {
		dxml p = new dxml();
		p.setInt("/sys/max", idx);
		for (int i = 0; i < idx; i++) {
			p.setText("/sys/r" + i + "/name", name[i]);
			p.setText("/sys/r" + i + "/url", rtsp[i]);
		}
//		p.save(url);
	}
}
