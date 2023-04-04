package me.tim.util;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.util.Session;

public class SessionUtil {
    public static Session createCrackedSession(String user) {
        return new Session(user, "", "", "mojang");
    }

    public static Session createMSSession(String email, String password) {
        try {
            MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
            MicrosoftAuthResult result = authenticator.loginWithCredentials(email, password);
            return new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "mojang");
        } catch (MicrosoftAuthenticationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
