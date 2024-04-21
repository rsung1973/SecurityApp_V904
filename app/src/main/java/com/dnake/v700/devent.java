package com.dnake.v700;

import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.dnake.handler.DefenceHelper;
import com.dnake.special.ipc;

public class devent {
    private static List<devent> elist = null;
    public static Boolean boot = false;

    public String url;

    public devent(String url) {
        this.url = url;
    }

    public void process(String xml) {
    }

    public static void event(String url, String xml) {
        Boolean err = true;
        if (boot && elist != null) {
            devent e;

            Iterator<devent> it = elist.iterator();
            while (it.hasNext()) {
                e = it.next();
                if (url.equals(e.url)) {
                    e.process(xml);
                    err = false;
                    break;
                }
            }
        }
        if (err) dmsg.ack(480, null);
    }

    public static void setup() {
        elist = new LinkedList<devent>();

        devent de;
        de = new devent("/security/run") {
            @Override
            public void process(String body) {
                dmsg.ack(200, null);
            }
        };
        elist.add(de);

        de = new devent("/security/version") {
            @Override
            public void process(String body) {
                dxml p = new dxml();
                String v = String.valueOf(sys.version_major) + "." + sys.version_minor + "." + sys.version_minor2;
                v = v + " " + sys.version_date + " " + sys.version_ex;
                p.setText("/params/version", v);
                dmsg.ack(200, p.toString());
            }
        };
        elist.add(de);

        de = new devent("/security/io") {
            @Override
            public void process(String body) {
                dmsg.ack(200, null);

                dxml p = new dxml();
                p.parse(body);
                int mcu = p.getInt("/params/mcu", 0);

//				Log.d("security=>io",body);

				int io[] = new int[security.MAX];
				boolean syncStatus = true;

                if (mcu == 1) {
					for (int i = 0; i < security.MAX; i++) {
						io[i] = p.getInt("/params/io" + i, 0x10);
						if (io[i] == 0x10) {
							syncStatus = false;
						}
					}

					if (syncStatus && security.zone != null) {
						for (int i = 0; i < security.MAX; i++) {
							if (security.zone[i] != null) {
								security.zone[i].currentStatus = io[i];
							}
						}
					}
				}

				if (syncStatus && !security.forcedCheck) {
					return;
				}
				security.forcedCheck = false;

//                if (security.defence == security.WITHDRAW)
//                    return;
//
//				if (security.defenceStart == 0 || Math.abs(System.currentTimeMillis() - security.defenceStart) < security.timeout * 1000) {
//					return;
//				}

                if (mcu != 0 && sys.talk.dcode != 0) {
                    //副机IO报警，直接同步到主分机
                    dmsg req = new dmsg();
                    p.setInt("/params/mcu", 0);
                    req.to("/talk/slave/mcu_io", p.toString());
                } else {
                    // io状态:  0: 正常状态    1:断开    2:闭合    0x10: 状态未发生变化
//                    int io[] = new int[security.MAX];
                    Boolean bell = false;
                    for (int i = 0; i < security.MAX; i++) {
                        io[i] = p.getInt("/params/io" + i, 0x10);
                        if (security.zone[i].mode == security.M_NO && io[i] == 0x01) //常开，io=1为正常状态
                            io[i] = 0x00;
                        else if (security.zone[i].mode == security.M_NC && io[i] == 0x02) //常闭，io=2为正常状态
                            io[i] = 0x00;
                        else if (security.zone[i].mode == security.M_BELL && io[i] != 0x00 && io[i] != 0x10) {
                            io[i] = 0x00;
                            bell = true;
                        }
                    }
                    security.process(io, security.MAX);

                    if (bell && Math.abs(System.currentTimeMillis() - sys.bell) >= 4000) {
                        sys.bell = System.currentTimeMillis();
                        sound.play(sound.bell, false);
                    }
                }
            }
        };
        elist.add(de);

        de = new devent("/security/conf") {
            @Override
            public void process(String body) {
                dmsg.ack(200, null);

                dxml p = new dxml();
                p.parse(body);
                security.load(p);
            }
        };
        elist.add(de);

        de = new devent("/security/defence") {
            @Override
            public void process(String body) {
                dxml p = new dxml();
                p.parse(body);
                int d = p.getInt("/params/defence", -1);
                if (d != -1) security.setDefence(d);

                dxml p2 = new dxml();
                p2.setInt("/params/defence", security.defence);
                dmsg.ack(200, p2.toString());
            }
        };
        elist.add(de);

        de = new devent("/security/set_id") {
            @Override
            public void process(String body) {
                dmsg.ack(200, null);

                sys.load();
            }
        };
        elist.add(de);

        de = new devent("/security/slaves") {
            @Override
            public void process(String body) {
                dmsg.ack(200, null);

                dxml p = new dxml();
                p.parse(body);
                slaves.load(p);
            }
        };
        elist.add(de);

        de = new devent("/security/slave/device") {
            @Override
            public void process(String body) {
                dmsg.ack(200, null);

                dxml p = new dxml();
                p.parse(body);
                slaves.load(p);
            }
        };
        elist.add(de);

        de = new devent("/security/alarm") {
            @Override
            public void process(String body) {
                dxml p = new dxml();
                if (security.mHave) p.setInt("/params/have", 1);
                else p.setInt("/params/have", 0);
                dmsg.ack(200, p.toString());
            }
        };
        elist.add(de);

        de = new devent("/security/sos") {
            @Override
            public void process(String body) {
                dmsg.ack(200, null);
                security.sos();
            }
        };
        elist.add(de);

        /*de = new devent("/security/broadcast/data") {
            @Override
            public void process(String body) {
                dmsg.ack(200, null);

                dxml p = new dxml();
                p.parse(body);
                int build = p.getInt("/event/build", 0);
                int unit = p.getInt("/event/unit", 0);
                int floor = p.getInt("/event/floor", 0);
                int family = p.getInt("/event/family", 0);
                int mode = p.getInt("/event/mode", 0);
                if (sys.talk.dcode == 0 && security.mHave == false && build == sys.talk.building && unit == sys.talk.unit && floor == sys.talk.floor && family == sys.talk.family) {
                    if (utils.eHome) {
                        //中华电信eHome模式，不处理我们自己的流程。
                        if (mode == 2) //小门口机
                            utils.eHomeCard(security.mContext, p.getText("/event/card"));
                        return;
                    }

                    if (mode == 2) { //小门口机
                        if (security.defence == security.WITHDRAW)
                            security.setDefence(security.OUT);
                        else security.setDefence(security.WITHDRAW);
                        security.save();
                    } else { //大门口机、围墙机只撤防
                        if (security.defence != security.WITHDRAW) {
                            security.setDefence(security.WITHDRAW);
                            security.save();
                        }
                    }
                    slaves.setMarks(0x01);
                }
            }
        };
        elist.add(de);*/

        de = new devent("/security/web/ipc/read") {
            @Override
            public void process(String body) {
                dxml p = new dxml();
                p.setInt("/params/max", ipc.idx);
                for (int i = 0; i < ipc.idx; i++) {
                    p.setText("/params/r" + i + "/name", ipc.name[i]);
                    p.setText("/params/r" + i + "/url", ipc.rtsp[i]);
                }
                dmsg.ack(200, p.toString());
            }
        };
        elist.add(de);

        de = new devent("/security/web/ipc/write") {
            @Override
            public void process(String body) {
                dmsg.ack(200, null);

                dxml p = new dxml();
                p.parse(body);
                ipc.idx = p.getInt("/params/max", 0);
                for (int i = 0; i < ipc.idx; i++) {
                    ipc.name[i] = p.getText("/params/r" + i + "/name");
                    ipc.rtsp[i] = p.getText("/params/r" + i + "/url");
                }
                ipc.save();
            }
        };
        elist.add(de);

        de = new devent("/security/slaves_setMarks_def") {
            @Override
            public void process(String body) {
                dmsg.ack(200, null);
                slaves.setMarks(0x01);
            }
        };
        elist.add(de);

        de = new devent("/security/defence_read") {
            @Override
            public void process(String body) {
                dxml p = new dxml();
                p.setInt("/params/defence", security.defence);
                dmsg.ack(200, p.toString());
            }
        };
        elist.add(de);

        de = new devent("/security/is_login_ok") {
            @Override
            public void process(String body) {
                dxml p = new dxml();
                p.setInt("/params/is_ok", login.ok() ? 1 : 0);
                dmsg.ack(200, p.toString());
            }
        };
        elist.add(de);
        de = new devent("/security/is_password_ok") {
            @Override
            public void process(String body) {
                dxml p = new dxml();
                p.parse(body);
                String password = p.getText("/params/password", "");
                p.setInt("/params/is_ok", login.passwd(password) ? 1 : 0);
                dmsg.ack(200, p.toString());
            }
        };
        elist.add(de);

        de = new devent("/security/web/zone/read") {
            @Override
            public void process(String body) {
                dxml p = new dxml();
                for (int i = 0; i < security.MAX; i++) {
                    String s = "/params/zone_" + i;
                    p.setInt(s + "_type", security.zone[i].type);//防区类型，参数范围：0-2， 参数说明：0：Normal，1：Emergency，2：24 Hour
                    p.setInt(s + "_mode", security.zone[i].mode);//防区模式，参数范围：0-3，参数说明：0：3C，1：NO，2：NC，3：BELL
                    p.setInt(s + "_delay", security.zone[i].delay);//报警触发延时时间，参数范围：0-60s
                    p.setInt(s + "_sensor", security.zone[i].sensor);//传感器类型，参数范围：0-6，参数说明：0: Smoke, 1: Gas, 2: PIR, 3: Door, 4: Window, 5: Panic, 6: Flood
                }
                Log.i("aaa", "_________________/security/web/zone/read__________________" + p.toString());
                dmsg.ack(200, p.toString());
            }
        };
        elist.add(de);
        de = new devent("/security/web/zone/write") {
            @Override
            public void process(String body) {
                dmsg.ack(200, null);
                dxml p = new dxml();
                p.parse(body);
                for (int i = 0; i < security.MAX; i++) {
                    String s = "/params/zone_" + i;
                    security.zone[i].type = p.getInt(s + "_type", security.zone_c.H24);//防区类型，参数范围：0-2， 参数说明：0：Normal，1：Emergency，2：24 Hour
                    security.zone[i].mode = p.getInt(s + "_mode", 0);//防区模式，参数范围：0-3，参数说明：0：3C，1：NO，2：NC，3：BELL
                    security.zone[i].delay = p.getInt(s + "_delay", 0);//报警触发延时时间，参数范围：0-60s
                    security.zone[i].sensor = p.getInt(s + "_sensor", 0);//传感器类型，参数范围：0-6，参数说明：0: Smoke, 1: Gas, 2: PIR, 3: Door, 4: Window, 5: Panic, 6: Flood
                }
                security.save();
            }
        };
        elist.add(de);
        de = new devent("/security/web/scene/read") {
            @Override
            public void process(String body) {
                dxml p = new dxml();
                p.setInt("/params/delay", security.timeout); //Activation time，参数范围：0-300s
                for (int i = 0; i < security.MAX; i++) {
                    String s_out = "/params/out_" + i;
                    String s_home = "/params/home_" + i;
                    String s_sleep = "/params/sleep_" + i;
                    p.setInt(s_out, security.zone[i].scene[0]);//外出模式
                    p.setInt(s_home, security.zone[i].scene[1]);//在家模式
                    p.setInt(s_sleep, security.zone[i].scene[2]);//睡眠模式
                }
                dmsg.ack(200, p.toString());
            }
        };
        elist.add(de);
        de = new devent("/security/web/scene/write") {
            @Override
            public void process(String body) {
                dmsg.ack(200, null);
                dxml p = new dxml();
                p.parse(body);
                security.timeout = p.getInt("/params/delay", 100); //Activation time，参数范围：0-300s
                for (int i = 0; i < security.MAX; i++) {
                    String s_out = "/params/out_" + i;
                    String s_home = "/params/home_" + i;
                    String s_sleep = "/params/sleep_" + i;
                    security.zone[i].scene[0] = p.getInt(s_out, 0);//外出模式
                    security.zone[i].scene[1] = p.getInt(s_home, 0);//在家模式
                    security.zone[i].scene[2] = p.getInt(s_sleep, 0);//睡眠模式
                }
                security.save();
            }
        };
        elist.add(de);

        de = new devent("/security/sync_user_passwd") {
            @Override
            public void process(String body) {
                dmsg.ack(200, null);
                dxml p = new dxml();
                p.parse(body);
                security.passwd = p.getText("/params/password", security.passwd);
                security.save();
            }
        };
        elist.add(de);

		de = new devent("/security/broadcast/data") {
			@Override
			public void process(String body) {
				dmsg.ack(200, null);

				dxml p = new dxml();
				p.parse(body);
				int build = p.getInt("/event/build", 0);
				int unit = p.getInt("/event/unit", 0);
				int floor = p.getInt("/event/floor", 0);
				int family = p.getInt("/event/family", 0);
				int mode = p.getInt("/event/mode", 0);
				if (sys.talk.dcode == 0 &&
//						security.mHave == false &&
						build == sys.talk.building &&
						unit == sys.talk.unit &&
						floor == sys.talk.floor &&
						family == sys.talk.family) {
/*					if (utils.eHome) {
						//中华电信eHome模式，不处理我们自己的流程。
						if (mode == 2) //小门口机
							utils.eHomeCard(security.mContext, p.getText("/event/card"));
						return;
					}*/

					if (mode == 2) { //小门口机
						if (security.defence == security.WITHDRAW) {
//							if(security.checkSecurity() || security.timeout>0 /*回路状态正常*/)//增加io回路状态判断
//								security.setDefence(security.OUT);
							if(DefenceHelper.setDefence(security.OUT)) {
//								security.save();
//								security.nBroadcast();
							} else {
//                                dmsg req = new dmsg();
//                                req.to("/talk/slave/deny", null);
                            }
						}
						else {
							DefenceHelper.setWithdraw();
//							security.withdraw();
//							security.setDefence(security.WITHDRAW);
//							security.save();
//							security.nBroadcast();
						}
					} else { //大门口机、围墙机只撤防
						if (security.defence != security.WITHDRAW) {
							DefenceHelper.setWithdraw();
//							security.setDefence(security.WITHDRAW);
//							security.save();
//							security.nBroadcast();
						}
					}
					slaves.setMarks(0x01);
				}
			}
		};
		elist.add(de);
    }
}
