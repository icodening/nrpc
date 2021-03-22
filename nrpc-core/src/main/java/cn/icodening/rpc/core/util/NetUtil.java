package cn.icodening.rpc.core.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author icodening
 * @date 2021.03.14
 */
public class NetUtil {

    public static final String LOOPBACK_ADDRESS = "127.0.0.1";

    private NetUtil() {
    }

    public static List<String> getLocalhost() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> netAddresses;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                netAddresses = networkInterface.getInetAddresses();
                while (netAddresses.hasMoreElements()) {
                    inetAddress = netAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address &&
                            !LOOPBACK_ADDRESS.equals(inetAddress.getHostAddress())) {
                        ip = inetAddress.getHostAddress();
                        ipList.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipList;
    }
}
