package com.dnake.v700;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.dnake.handler.DefenceHelper;
import com.dnake.security.AlarmLabel;
import com.dnake.security.WakeTask;

@SuppressLint({"HandlerLeak", "NewApi"})
public class security extends Service {
    public static String url = "/dnake/cfg/security.xml";
    public static String url2 = "/dnake/data/security.xml";
    public static final int MAX = 8;
    public static final int MAX_SCENE = 4;

    public static final int WITHDRAW = 0;
    public static final int OUT = 1;
    public static final int HOME = 2;
    public static final int SLEEP = 3;

    public static final int M_3C = 0;
    public static final int M_NO = 1;
    public static final int M_NC = 2;
    public static final int M_BELL = 3;

    public static String passwd = "1234";

    public static int defence = 0;
    public static zone_c zone[] = null;
    public static int timeout = 100; // 布防时间
    public static long defenceStart = 0;

    public static final boolean __EnableSecureSwitch = true;
	public static final boolean __EnableSECOM = !__EnableSecureSwitch && true;

    public static class zone_c {
        public static final int NORMAL = 0;
        public static final int EMERGENCY = 1;
        public static final int H24 = 2;
		public static final int SECURE_SWITCH = 3;

        public int defence = 0;
        public int type = H24;
        public int delay = 0;
        public int sensor = 0;
        public int mode = 0; // 接口模式
        public int[] scene = new int[MAX_SCENE];
        public int currentStatus = -1;	//M_3C;
    }

    public static void load() {
        if (zone == null) {
            zone = new zone_c[MAX];
            for (int i = 0; i < MAX; i++) {
                zone[i] = new zone_c();
                zone[i].scene[0] = 0;
                zone[i].scene[1] = 0;
                zone[i].scene[2] = 0;
                zone[i].scene[3] = 0;

                if (i < 8)
                    zone[i].scene[0] = 1;
                if (i < 2)
                    zone[i].scene[1] = 1;
                if (i < 4)
                    zone[i].scene[2] = 1;
            }

            zone[0].sensor = 3;
            zone[0].type = zone_c.NORMAL;
            zone[0].mode = M_NC;
            zone[0].delay = 2;
            zone[1].sensor = 4;
            zone[1].type = zone_c.NORMAL;
            zone[1].mode = M_NC;
            zone[2].sensor = 4;
            zone[2].type = zone_c.NORMAL;
            zone[2].mode = M_NC;
            zone[3].sensor = 4;
            zone[3].type = zone_c.NORMAL;
            zone[3].mode = M_NC;
            zone[4].sensor = 4;
            zone[4].mode = M_NC;
            zone[4].type = zone_c.NORMAL;
            zone[5].sensor = 5;
            zone[5].mode = M_NO;
			zone[5].type = zone_c.H24;
            zone[6].sensor = 1;
            zone[6].mode = M_NO;
			zone[6].type = zone_c.H24;
            zone[7].sensor = 0;
            zone[7].mode = M_NO;
            zone[7].type = zone_c.H24;

        }

        boolean ok1 = false, ok2 = false;
        dxml p = new dxml();
        ok1 = p.load(url);
        if (!ok1) {
            ok2 = p.load(url2);
        }
        if (ok1 || ok2) {
            passwd = p.getText("/security/passwd", passwd);
            timeout = p.getInt("/security/timeout", 100);
            defence = p.getInt("/security/defence", 0);
            for (int i = 0; i < MAX; i++) {
                String s = "/security/zone" + i;
                zone[i].defence = p.getInt(s + "/defence", 0);
                zone[i].type = p.getInt(s + "/type", 0);
                zone[i].delay = p.getInt(s + "/delay", 0);
                zone[i].sensor = p.getInt(s + "/sensor", 0);
                if (zone[i].sensor < 0 || zone[i].sensor > 8) {
                    zone[i].sensor = 0;
                }
                zone[i].mode = p.getInt(s + "/mode", 0);
                for (int j = 0; j < MAX_SCENE; j++)
                    zone[i].scene[j] = p.getInt(s + "/scene" + j, 0);
            }
        }
        if (!ok1) {
            save();
        }
        sys.httpPasswd();
    }

    public static void save() {
        dxml p = new dxml();

        p.setText("/security/passwd", passwd);
        p.setInt("/security/defence", defence);
        p.setInt("/security/timeout", timeout);
        for (int i = 0; i < MAX; i++) {
            String s = new String("/security/zone" + i);
            p.setInt(s + "/defence", zone[i].defence);
            p.setInt(s + "/type", zone[i].type);
            p.setInt(s + "/delay", zone[i].delay);
            p.setInt(s + "/sensor", zone[i].sensor);
            p.setInt(s + "/mode", zone[i].mode);
            for (int j = 0; j < MAX_SCENE; j++)
                p.setInt(s + "/scene" + j, zone[i].scene[j]);
        }
        p.save(url);
        p.save(url2);

        sys.httpPasswd();
        security.dBroadcast();
    }

    public static void load(dxml p) {
        if (zone == null)
            return;

        for (int i = 0; i < MAX; i++) {
            String s = "/params/zone" + i;
            zone[i].defence = p.getInt(s + "/defence", zone[i].defence);
            zone[i].type = p.getInt(s + "/type", zone[i].type);
            zone[i].delay = p.getInt(s + "/delay", zone[i].delay);
            zone[i].sensor = p.getInt(s + "/sensor", zone[i].sensor);
            zone[i].mode = p.getInt(s + "/mode", zone[i].mode);
            for (int j = 0; j < MAX_SCENE; j++)
                zone[i].scene[j] = p.getInt(s + "/scene" + j, zone[i].scene[j]);
        }

        if (p.getInt("/params/defence", -1) != -1) {
            int d = timeout;
            int st = p.getInt("/params/defence", defence);
            timeout = p.getInt("/params/timeout", timeout);

            if (st == 0)
                security.withdraw();

            setDefence(st);
            if (sys.talk.dcode == 0) // 主分机需要同步状态
                slaves.setMarks(0x01);
            if (timeout != d)
                save();
        }
    }

    //布防延时处理
    public static class dSound {
        public static long mTs = 0;
        public static dSound mSound = null;

        public static void stop() {
            mSound = null;
            mTs = 0;
        }

        public dSound() {
            mTs = System.currentTimeMillis();
            idx = 0;
            if (timeout < 60)
                max = 50;
        }

        private int max = 80;
        private int idx = 0;

        public void process() {
            idx++;
            if (idx >= max) {
                idx = 0;
                if (max > 20)
                    max--;
                security.soundEvent(0);
            }

            if (mTs != 0 && Math.abs(System.currentTimeMillis() - mTs) >= timeout * 1000) {
                dSound.stop();
                security.soundEvent(1);
            }
            if (AlarmLabel.intent != null)
                dSound.stop();
        }
    }

    public static void setDefence(int st) {
        Boolean ok = (defence != st ? true : false);

        switch (st) {
            case WITHDRAW:
                for (int i = 0; i < MAX; i++)
                    zone[i].defence = 0;
                break;

            case OUT:
                for (int i = 0; i < MAX; i++)
                    zone[i].defence = zone[i].scene[0];
                break;

            case HOME:
                for (int i = 0; i < MAX; i++)
                    zone[i].defence = zone[i].scene[1];
                break;

            case SLEEP:
                for (int i = 0; i < MAX; i++)
                    zone[i].defence = zone[i].scene[2];
                break;
        }
        defence = st;

        if (ok) {
            dSound.stop();

            if (st > 0) {
                security.soundEvent(0);
                dSound.mSound = new dSound();
            } else
                security.soundEvent(2);

            nBroadcast();
            CMS.sendDefence();
            CMS.d600SetZone();
            save();

            if(mContext !=null) {
                Intent it = new Intent("com.dnake.defenceStatus");
                it.addFlags((int)0x01000000 /*Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND*/);
                it.putExtra("status", defence);
                mContext.sendBroadcast(it);
            }
        }

        if (st == WITHDRAW)
            security.withdraw();
    }

	public static long[] mZoneTs = new long[security.MAX]; // 延时
	public static Boolean[] mSend = new Boolean[security.MAX]; // 是否网络发送

	public static Boolean mHave = false; //是否有报警触发
	public static long mAts = 0; // 最后一个报警触发时间
	public static int mIO[] = new int[MAX]; // 最新IO状态
	public static Boolean[] mIoSt = new Boolean[security.MAX]; // IO触发标记

	public static void process(int io[], int length) {
		if (length > security.MAX)
			length = security.MAX;

		for (int i = 0; i < length; i++) {
			if (io[i] == 0x10)
				continue;

			if (io[i] != 0) {
				if (zone[i].type == zone_c.EMERGENCY) { // 紧急报警
//					CMS.sendAlarm(i, 0);
                    CMS.sendAlarmWithSensor(i, 0, zone[i].sensor);
				} else if (zone[i].type == zone_c.SECURE_SWITCH) { // 刷卡保全設定
					if (zone[i].mode != io[i]) {
						if (security.defence == security.WITHDRAW) {
							DefenceHelper.setDefence(security.OUT);
						}
					} else {
						if (security.defence != security.WITHDRAW) {
							DefenceHelper.setWithdraw();
						}
					}
				} else if (zone[i].type == zone_c.H24 || (zone[i].defence > 0 && dSound.mTs == 0)) { // 报警
					if (mZoneTs[i] == 0)
						mZoneTs[i] = System.currentTimeMillis();
					if (mSend[i])
						mSend[i] = false;

					mIO[i] = io[i];
					mHave = true;
					mIoSt[i] = true;
					mAts = System.currentTimeMillis();
				}
			} else
				mIO[i] = 0;
		}
		if (security.mHave) {
			WakeTask.acquire();
			if (sys.talk.dcode == 0)
				slaves.setMarks(0x02); // 同步安防状态
		}
	}

    public static void withdraw() {
        mHave = false;
        for (int i = 0; i < security.MAX; i++) {
            mIO[i] = 0;
            mIoSt[i] = false;
            mSend[i] = false;
            mZoneTs[i] = 0;
        }
        ioctl.hooter(0);

        CMS.d600AlarmCancel();
    }

    public static long zoneDelayTs[] = {0, 5, 15, 20, 25, 40, 60};

	public static Boolean isZoneAlarm(int idx) {
		if (mIoSt[idx] && zone[idx].type == zone_c.EMERGENCY)
			return true;

		if (mIoSt[idx] && mZoneTs[idx] != 0) {
			long ts = Math.abs(System.currentTimeMillis() - mZoneTs[idx]);
			if (ts >= zoneDelayTs[zone[idx].delay] * 1000)
				return true;
		}
		return false;
	}

    //管理软件接口
    public static class CMS {
        public static void sendAlarm(int io, int force) {
            if (force == 0 && sys.talk.dcode != 0)
                return;

            dmsg req = new dmsg();
            dxml p = new dxml();
            p.setInt("/params/sp_io", 0);
            p.setInt("/params/io" + io, 1);
            req.to("/control/d600/alarm_trigger", p.toString()); // 600协议报警上报

            dxml p2 = new dxml();
            p2.setText("/params/event_url", "/msg/alarm/trigger");
            p2.setInt("/params/zone", io);
            p2.setInt("/params/data", 1);
            req.to("/talk/center/to", p2.toString()); // 700协议报警上报

            security.broadcast(io);
            security.mBroadcast(); // 取消静音

//			NowIP.alarm(io);

            security.broadcastAlarm(io);
        }

        public static void sendAlarmWithSensor(int io, int force, int sensor) {
            Log.i("aaa", "_______________sendAlarmWithSensor_______________io_____" + io + "________sensor______" + sensor);
            if (force == 0 && sys.talk.dcode != 0)
                return;

            dmsg req = new dmsg();
            dxml p = new dxml();
            p.setInt("/params/sp_io", 0);
            p.setInt("/params/io" + io, 1);
            req.to("/control/d600/alarm_trigger", p.toString()); // 600协议报警上报

            dxml p2 = new dxml();
            p2.setText("/params/event_url", "/msg/alarm/trigger");
            p2.setInt("/params/zone", io);
            p2.setInt("/params/data", 1);
            p2.setInt("/params/sensor", sensor);
            req.to("/talk/center/to", p2.toString()); // 700协议报警上报

            security.broadcast(io);
            security.mBroadcast(); // 取消静音

//			NowIP.alarm(io);

			security.broadcastAlarm(io);
        }

        public static void sendDefence() {
            if (sys.talk.dcode != 0)
                return;

            dmsg req = new dmsg();
            dxml p = new dxml();

            p.setText("/params/event_url", "/msg/alarm/defence");
            p.setInt("/params/defence", security.defence);
            p.setInt("/params/total", security.MAX);
            for (int i = 0; i < security.MAX; i++) {
                String s = "/params/io" + i;
                p.setInt(s, security.zone[i].defence);
            }
            req.to("/talk/center/to", p.toString());
        }

        public static void d600SetZone() { //600管理软件防区布撤防状态
            dmsg req = new dmsg();
            dxml p = new dxml();
            p.setInt("/params/defence", defence);
            for (int i = 0; i < MAX; i++) {
                p.setInt("/params/zone" + i, zone[i].defence);
            }
            req.to("/control/d600/zone", p.toString());
        }

        public static void d600AlarmCancel() {
            if (sys.talk.dcode == 0) {
                dmsg req = new dmsg();
                req.to("/control/d600/alarm_cancel", null);
            }
        }
    }

    public static void broadcast(int io) {
        dmsg req = new dmsg();
        dxml p = new dxml();
        p.setText("/event/broadcast_url", "security");
        p.setText("/event/data/uuid", utils.getUUID());
        p.setInt("/event/data/io", io);
        if (io == -1) {
            p.setInt("/event/data/type", -1);
            p.setInt("/event/data/sensor", 13);
        } else {
            p.setInt("/event/data/type", security.zone[io].type);
            p.setInt("/event/data/sensor", security.zone[io].sensor);
        }
        p.setInt("/event/talk/build", sys.talk.building);
        p.setInt("/event/talk/unit", sys.talk.unit);
        p.setInt("/event/talk/floor", sys.talk.floor);
        p.setInt("/event/talk/family", sys.talk.family);
        req.to("/talk/broadcast/data", p.toString());
    }

    public static void nBroadcast() {
        if (sys.talk.dcode != 0)
            return;

        dmsg req = new dmsg();
        dxml p = new dxml();
        p.setText("/event/broadcast_url", "/security/notify");
        p.setInt("/event/build", sys.talk.building);
        p.setInt("/event/unit", sys.talk.unit);
        p.setInt("/event/floor", sys.talk.floor);
        p.setInt("/event/family", sys.talk.family);
        p.setInt("/event/defence", security.defence);
        req.to("/talk/broadcast/data", p.toString());
    }

	public static void authorizeCardNo(String cardNo,int mode) {
		dmsg req = new dmsg();
		dxml p = new dxml();
		p.setText("/event/broadcast_url", "/card/auth");
		p.setText("/event/data", cardNo);
		p.setInt("/event/mode", mode);
		req.to("/talk/broadcast/data", p.toString());
	}

    public static void run() {
        if (mHave) {
            for (int i = 0; i < security.MAX; i++) {
                if (mSend[i] == false && mIoSt[i] && isZoneAlarm(i)) {
//					CMS.sendAlarm(i, 0);
                    CMS.sendAlarmWithSensor(i, 0, zone[i].sensor);
                    mSend[i] = true;
                }
            }
            if (mPm != null && mPm.isScreenOn()) {
                if (Math.abs(System.currentTimeMillis() - AlarmLabel.mTs) >= 10 * 1000) {
                    //定时器没刷新，intent出现异常
                    AlarmLabel.intent = null;
                }
            }
            if (AlarmLabel.intent == null && Math.abs(System.currentTimeMillis() - mAts) < AlarmLabel.TIMEOUT) {
                WakeTask.acquire();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                security.alarmEvent();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ProcessThread pt;

    public static void start() {
        mHave = false;
        for (int i = 0; i < security.MAX; i++) {
            mIoSt[i] = false;
            mSend[i] = false;
            mZoneTs[i] = 0;
        }

        pt = new ProcessThread();
        Thread thread = new Thread(pt);
        thread.start();
    }

    public static class ProcessThread implements Runnable {

		private long checkMessageGuardCycleCount = System.currentTimeMillis();
		private long checkSyncIO = System.currentTimeMillis();
        String messageGuardService = "crc648040af3feb41865b.MessageGuardService";
        private void checkMessageGuard() {
            try {
                boolean isRunning = false;
                ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//				Log.d("desktop:list service", service.service.getClassName());
                    if (messageGuardService.equals(service.service.getClassName())) {
                        isRunning = true;
                        Log.d("security:check", "MessageGuardService is running...");
                    }
                }

//				if (!isRunning) {
					ComponentName componentName = new ComponentName("com.awtek.messageGuard", messageGuardService);
					ServiceInfo info = mContext.getPackageManager().getServiceInfo(componentName,
							PackageManager.GET_META_DATA);
					if (info != null) {
						Intent intent = new Intent();
						intent.setComponent(componentName);
						mContext.startService(intent);
						Log.d("security:launch", messageGuardService);
					}
//				}
            } catch (Exception ex) {
                Log.d("security", ex.toString());
            }
        }

        @Override
        public void run() {
            while (true) {
                security.run();
                slaves.process();

                if (dSound.mSound != null)
                    dSound.mSound.process();

//                checkMessageGuardCycleCount++;
//                Log.d("security:check", "MessageGuard check cycle " + checkMessageGuardCycleCount);

                if (Math.abs(System.currentTimeMillis() - checkMessageGuardCycleCount) > 60000) {
					checkMessageGuardCycleCount = System.currentTimeMillis();
					checkMessageGuard();
				}

				if (Math.abs(System.currentTimeMillis() - checkSyncIO) > 5000) {
					checkSyncIO = System.currentTimeMillis();
					security.invokeIO(false);
				}

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public static Context mContext = null;
    private static PowerManager mPm = null;
    private static Handler e_defence = null;
    private static Handler e_alarm = null;
    private static int s_defence = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        mPm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

        dmsg.start("/security");
        devent.setup();
        sys.load();
//		ipc.load();
        slaves.start();
        security.load();
        security.start();

        sound.load();
        ioctl.hooter(0);
        slaves.setMarks(0x03);

        dmsg req = new dmsg();
        req.to("/talk/slave/reset", null);

        e_defence = new Handler() {
            private MediaPlayer player = null;

            class PlayerOnCompletionListener implements OnCompletionListener {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            }

            private void stopPlayer() {
                if (player != null) {
                    player.stop();
                    player.release();
                    player = null;
                }
            }

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (mContext != null) {
                    // 模拟触摸事件，确保不会关屏
                    dmsg req = new dmsg();
                    dxml p = new dxml();
                    p.setInt("/params/data", 0);
                    p.setInt("/params/apk", 1);
                    req.to("/ui/touch/event", p.toString());

                    if (s_defence == 0) { // 布防延时
                        if (player == null) {
                            player = sound.play(sound.defence_delay, false, new PlayerOnCompletionListener());
                        } else if (!player.isPlaying()) {
                            stopPlayer();
                        }
                    } else if (s_defence == 1) { // 布防成功
                        stopPlayer();
                        player = sound.play(sound.defence_on, false, new PlayerOnCompletionListener());
						invokeIO(true);
                    } else if (s_defence == 2) {
                        stopPlayer();
                        player = sound.play(sound.defence_cancel, false, new PlayerOnCompletionListener());
                    }
                }
            }
        };

        e_alarm = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.i("aaa", "_______________e_alarm_______!!!!!!!!!!!!_________");
                dmsg req = new dmsg();
                if (AlarmLabel.intent == null && req.to("/talk/active", null) == 200) {
                    dxml p = new dxml();
                    p.parse(req.mBody);
                    if (p.getInt("/params/data", 0) == 0) {
                        AlarmLabel.mTs = System.currentTimeMillis();
                        AlarmLabel.intent = new Intent(security.this, AlarmLabel.class);
                        AlarmLabel.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(AlarmLabel.intent);
                    }
                }
            }
        };

        security.dBroadcast();
        CMS.d600SetZone();

        devent.boot = true;
    }

    public static void soundEvent(int s) {
        s_defence = s;
        if (e_defence != null)
            e_defence.sendMessage(e_defence.obtainMessage());
    }

    public static void alarmEvent() {
        if (e_alarm != null)
            e_alarm.sendMessage(e_alarm.obtainMessage());
    }

    public static void dBroadcast() {
        if (mContext != null) {
            Intent it = new Intent("com.dnake.broadcast");
            it.addFlags((int)0x01000000 /*Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND*/);
            it.putExtra("event", "com.dnake.security.data");
            it.putExtra("defence", defence);
            mContext.sendBroadcast(it);
        }
    }

    public static void mBroadcast() {
        if (mContext != null) {
            Intent it = new Intent("com.dnake.broadcast");
            it.addFlags((int)0x01000000 /*Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND*/);
            it.putExtra("event", "com.dnake.talk.smute");
            it.putExtra("data", 0);
            mContext.sendBroadcast(it);
        }
    }

    public static void broadcastAlarm(int io) {
        if (mContext != null && io >= 0) {
            Intent it = new Intent("com.awtek.messageGuard.Alarm");
            it.addFlags((int) 0x01000000 /*Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND*/);
            it.putExtra("io", io);
            it.putExtra("sensor", zone[io].sensor);
            mContext.sendBroadcast(it);
        }
    }

	public static void clearAlarm() {
		if (mContext != null) {
			Intent it = new Intent("com.awtek.messageGuard.Alarm");
//            it.setPackage("com.awtek.messageGuard");
            it.addFlags((int)0x01000000 /*Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND*/);
			it.putExtra("io", -1);
			it.putExtra("sensor", -1);
			mContext.sendBroadcast(it);
		}
	}

    public static void broadcastDefence() {
		if (mContext != null) {
			int io = 0;
			for (int i = 0; i < 8; i++) {
				if (security.zone[i].defence == 1) {
					io += (1 << i);
				}
			}
			Intent it = new Intent("com.awtek.messageGuard.Alarm");
//            it.setPackage("com.awtek.messageGuard");
            it.addFlags((int)0x01000000 /*Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND*/);
			it.putExtra("io", io);
			it.putExtra("sensor", -1);
			mContext.sendBroadcast(it);
		}
    }


	public static boolean forcedCheck = false;
    public static void invokeIO(boolean forcedCheck) {
        security.forcedCheck = forcedCheck;
        if (security.defence == security.WITHDRAW || forcedCheck) {
            dmsg req = new dmsg();
            req.to("/control/io/sync", null);
        }
    }

    public static boolean checkSecurity() {
        if (zone == null)
            return false;

		for (int i = 0; i < 8; i++) {
			if (zone[i] != null && zone[i].mode != security.M_BELL && zone[i].type != zone_c.SECURE_SWITCH) {
				if (zone[i].currentStatus != zone[i].mode) {
					return false;
				}
			}
		}

        return true;
    }

	public static boolean checkSecurityWithDefence(int defence) {
		if (zone == null)
			return false;

		for (int i = 0; i < 8; i++) {
			if (zone[i] != null && zone[i].mode != security.M_BELL && zone[i].type != zone_c.SECURE_SWITCH) {
				if (zone[i].currentStatus != zone[i].mode && zone[i].scene[defence - 1] == 1) {
					if (i == 0 && security.timeout > 0) {
						continue;
					}
					return false;
				}
			}
		}

		return true;
	}

//	public static int checkDelay() {
//
//		if (zone == null)
//			return 0;
//
//		int delay = 0;
//		for (int i = 0; i < 8; i++) {
//			if (zone[i].delay > delay) {
//				delay = zone[i].delay;
//			}
//		}
//		return delay;
//	}

    public static void sos() {
//		for (int i = 0; i < security.MAX; i++) {
//			CMS.sendAlarm(i, 1);
//		}
//		CMS.sendAlarm(15, 1);
        CMS.sendAlarmWithSensor(-1, 1, 13);
    }
}
