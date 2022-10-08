package chronoMods.network.steam;

import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamNetworking;
import com.codedisaster.steamworks.SteamNetworkingCallback;
import com.codedisaster.steamworks.SteamUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SteamworksAdapter {

    private static Boolean isVersion16 = null;

    public static boolean steamUtilsGetImageRGBA(SteamUtils steamUtils, int image, ByteBuffer dest, int length) throws SteamException {
        if (isVersion16 == null || isVersion16) {
            try {
                Method method = steamUtils.getClass().getMethod("getImageRGBA", int.class, ByteBuffer.class, int.class);
                return (boolean) method.invoke(steamUtils, image, dest, length);
            } catch (NoSuchMethodException ignored) {
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                isVersion16 = true;
            }
        }

        isVersion16 = false;

        try {
            Method method = steamUtils.getClass().getMethod("getImageRGBA", int.class, ByteBuffer.class);
            return (boolean) method.invoke(steamUtils, image, dest);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static SteamNetworking newSteamNetworking(SteamNetworkingCallback callbacks) {
        if (isVersion16 == null || isVersion16) {
            try {
                Class<?> apiEnum = Class.forName(SteamNetworking.class.getName() + "$API");
                Object[] constants = apiEnum.getEnumConstants();
                Object client = Arrays.stream(constants).filter(c -> c.toString().equals("Client")).findFirst();

                Constructor<SteamNetworking> constructor = SteamNetworking.class.getConstructor(SteamNetworkingCallback.class, apiEnum);
                return constructor.newInstance(callbacks, client);
            } catch (NoSuchMethodException | ClassNotFoundException ignored) {
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                isVersion16 = true;
            }
        }

        isVersion16 = false;

        try {
            Constructor<SteamNetworking> constructor = SteamNetworking.class.getConstructor(SteamNetworkingCallback.class);
            return constructor.newInstance(callbacks);
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static int steamNetworkingIsP2PPacketAvailable(SteamNetworking net, int channel) {
        if (isVersion16 == null || isVersion16) {
            try {
                Method method = net.getClass().getMethod("isP2PPacketAvailable", int.class);
                return (int) method.invoke(net, channel);
            } catch (NoSuchMethodException ignored) {
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                isVersion16 = true;
            }
        }

        isVersion16 = false;

        try {
            Method method = net.getClass().getMethod("isP2PPacketAvailable", int.class, int[].class);
            int[] size = new int[1];
            method.invoke(net, channel, size);
            return size[0];
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
