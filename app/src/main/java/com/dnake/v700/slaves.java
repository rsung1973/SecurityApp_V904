package com.dnake.v700;

public class slaves {
	public static String url[] = new String[10];

	public static Boolean cMark[] = new Boolean[10];
	public static Boolean dMark[] = new Boolean[10];

	public static void start() {
		for(int i=0; i<10; i++) {
			cMark[i] = false;
			dMark[i] = false;
			url[i] = null;
		}
	}

	public static void load(dxml p) {
		for(int i=0; i<10; i++) {
			String u = url[i];
			url[i] = p.getText("/params/url"+i);
			if (sys.talk.dcode == 0 && (u == null || u.equals(url[i]) == false)) {
				setCfgMark(i);
				setDataMark(i);
			}
		}
	}

	public static void setMarks(int m) {
		for(int i=0; i<10; i++) {
			if ((m & 0x01) != 0)
				setCfgMark(i);
			if ((m & 0x02) != 0)
				setDataMark(i);
		}
	}

	public static void setCfgMark(int n) {
		cMark[n] = true;
	}

	public static void setDataMark(int n) {
		dMark[n] = true;
	}

	public static void process() {
		if (sys.talk.dcode != 0) { //副分机
			if (cMark[0] && url[0] != null) {
				cMark[0] = false;
				syncConf(url[0]);
			}
		} else { //主分机
			for(int i=1; i<10; i++) {
				if (url[i] != null) {
					if (cMark[i]) {
						cMark[i] = false;
						syncConf(url[i]);
					}
					if (dMark[i]) {
						dMark[i] = false;
						syncData(url[i]);
					}

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void syncConf(String url) {
		for (int n=0; n<security.MAX; n += 4) {
			dxml p = new dxml();
			int max;

			if (url != null)
				p.setText("/params/slave_url", url);

			if (n+4 >= security.MAX) {
				p.setInt("/params/defence", security.defence);
				p.setInt("/params/timeout", security.timeout);
				max = security.MAX-n;
			} else
				max = 4;

			for (int i=0; i<max; i++) {
				String s = "/params/zone"+(n+i);;
				p.setInt(s+"/defence", security.zone[n+i].defence);
				p.setInt(s+"/type", security.zone[n+i].type);
				p.setInt(s+"/delay", security.zone[n+i].delay);
				p.setInt(s+"/sensor", security.zone[n+i].sensor);
				p.setInt(s+"/mode", security.zone[n+i].mode);
				for (int j=0; j<4; j++)
					p.setInt(s+"/scene"+j, security.zone[n+i].scene[j]);
			}
			dmsg req = new dmsg();
			req.to("/talk/slave/security", p.toString());
		}
	}

	public static void syncData(String url) {
		dxml p = new dxml();

		if (url != null)
			p.setText("/params/slave_url", url);
		for (int i=0; i<security.mIoSt.length; i++) {
			if (security.mIoSt[i]) {
				int d = 0x01;
				if (security.zone[i].mode == security.M_NO)
					d = 0x02;
				else if (security.zone[i].mode == security.M_NC)
					d = 0x01;
				p.setInt("/params/io"+i, d);
			} else
				p.setInt("/params/io"+i, 0x10);
		}

		dmsg req = new dmsg();
		req.to("/talk/slave/mcu_io", p.toString()); //副机联动报警
	}
}
