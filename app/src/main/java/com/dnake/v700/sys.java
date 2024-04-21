package com.dnake.v700;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public final class sys {
	public static int version_major = 1; // 主版本
	public static int version_minor = 0; // 次版本
	public static int version_minor2 = 5; // 次版本2

	public static String version_date = "20170412"; // 日期

	public static String version_ex = "(std)"; // 扩展标注

	public static String url = "/dnake/cfg/sys.xml";

	public static float scaled = 1.0f;

	public static long bell = 0; // 门铃播放时间

	public static final class talk {
		public static int building = 1;
		public static int unit = 1;
		public static int floor = 11;
		public static int family = 11;

		public static int dcode = 0;
		public static String sync = new String("123456");

		public static String server = new String("192.168.12.40");
	}

	public static void load() {
		dxml p = new dxml();
		if (p.load(url)) {
			talk.building = p.getInt("/sys/talk/building", 1);
			talk.unit = p.getInt("/sys/talk/unit", 1);
			talk.floor = p.getInt("/sys/talk/floor", 1);
			talk.family = p.getInt("/sys/talk/family", 1);
			talk.dcode = p.getInt("/sys/talk/dcode", 0);
			talk.sync = p.getText("/sys/talk/sync", talk.sync);
			talk.server = p.getText("/sys/talk/server", talk.server);
		}
	}

	public static void httpPasswd() {
		try {
			FileOutputStream out = new FileOutputStream("/var/etc/http_security");
			String s = "user:" + security.passwd + "\n";
			out.write(s.getBytes());
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int sLimit = -1;

	public static int limit() {
		if (sLimit != -1)
			return sLimit;

		int limit = 0;
		try {
			FileInputStream in = new FileInputStream("/dnake/bin/limit");
			byte[] data = new byte[256];
			int ret = in.read(data);
			if (ret > 0) {
				String s = new String();
				char[] d = new char[1];
				for (int i = 0; i < ret; i++) {
					if (data[i] >= '0' && data[i] <= '9') {
						d[0] = (char) data[i];
						s += new String(d);
					} else
						break;
				}
				limit = Integer.parseInt(s);
			}
			in.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		sLimit = limit;
		return limit;
	}
}
