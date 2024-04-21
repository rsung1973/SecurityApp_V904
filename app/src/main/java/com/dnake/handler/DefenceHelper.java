package com.dnake.handler;

import com.dnake.v700.security;
import com.dnake.v700.slaves;
import com.dnake.v700.sound;

public class DefenceHelper {

    public  static  boolean setDefence(int defence) {
        if(security.checkSecurityWithDefence(defence)) {
            security.setDefence(defence);
            security.save();
            security.nBroadcast();
            slaves.setMarks(0x01);
            security.broadcastDefence();
//            security.broadcast(-1);
            return true;
        } else {
            sound.play(sound.passwd_err, false);
            return false;
        }
    }

    public static void setWithdraw() {
        security.setDefence(security.WITHDRAW);
        security.withdraw();
        security.save();
        security.nBroadcast();
        slaves.setMarks(0x01);
        security.clearAlarm();
//        security.broadcast(999);
    }

}
