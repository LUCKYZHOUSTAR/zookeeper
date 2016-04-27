package com.zkProject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IpUtil {
    public IpUtil() {
    }

    public static String getRealIp() throws SocketException {
        String localip = null;
        String netip = null;
        Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        boolean finded = false;
        label0: do {
            if (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                Enumeration address = ni.getInetAddresses();
                do {
                    do {
                        if (!address.hasMoreElements())
                            continue label0;
                        ip = (InetAddress) address.nextElement();
                        if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1) {
                            netip = ip.getHostAddress();
                            finded = true;
                            continue label0;
                        }
                    } while (!ip.isSiteLocalAddress() || ip.isLoopbackAddress()
                             || ip.getHostAddress().indexOf(":") != -1);
                    localip = ip.getHostAddress();
                } while (true);
            }
            if (netip != null && !"".equals(netip))
                return netip;
            return localip;
        } while (true);
    }
}
