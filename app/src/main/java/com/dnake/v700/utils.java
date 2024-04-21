package com.dnake.v700;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

public class utils {

	public static Boolean eHome = false;

	public static String getLocalIp() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress())
						return inetAddress.getHostAddress().toString();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getLocalMac() {
		String mac_s = "";
		try {
			NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(utils.getLocalIp()));
			if (ne != null) {
				byte[] mac = ne.getHardwareAddress();
				if (mac != null)
					mac_s = String.format("%02X:%02X:%02X:%02X:%02X:%02X", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mac_s;
	}

	public static void eHomeCard(Context ctx, String card) {
		Intent it = new Intent("com.dnake.broadcast");
		it.addFlags((int)0x01000000 /*Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND*/);
		it.putExtra("event", "com.dnake.eHome.card");
		it.putExtra("card", card);
		ctx.sendBroadcast(it);
	}

	public static Drawable path2Drawable(Context context, String file) {
		if (file == null || file.isEmpty()) {
			return null;
		}
		Drawable drawable = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			Bitmap bitmap = BitmapFactory.decodeStream(fis);
			drawable = new BitmapDrawable(context.getResources(), bitmap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return drawable;
	}

	public static  String getDesktopBgPath() {
		String path = "";
		try {
			FileInputStream in = new FileInputStream("/dnake/cfg/desktop_bg_path");
			int length;
			byte[] bytes = new byte[1024];
			while ((length = in.read(bytes)) != -1) {
				path = new String(bytes, 0, length);
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			path = "";
		} catch (IOException e) {
			e.printStackTrace();
			path = "";
		}
		if (TextUtils.isEmpty(path)) {
			path = "/dnake/data/bg/bg_default.webp";
			return path;
		} else {
			return path;
		}
	}

	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		String uuidStr = str.replace("-", "");
		return uuidStr;
	}
}
