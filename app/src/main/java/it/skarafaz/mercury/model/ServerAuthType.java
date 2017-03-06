package it.skarafaz.mercury.model;

import com.jcraft.jsch.KeyPair;

public enum ServerAuthType {
    DSA512, DSA1024, DSA2048, DSA4096, DSA8192,
    ECDSA128, ECDSA256, ECDSA521,
    RSA512, RSA1024, RSA2048, RSA4096, RSA8192;

    public static String appendDefaultLength(String authType) {
        authType = authType.toUpperCase();
        if (authType.equals("DSA")) {
            authType.concat("2048");
        } else if (authType.equals("ECDSA")) {
            authType.concat("256");
        } else if (authType.equals("RSA")) {
            authType.concat("2048");
        }
        return authType;
    }

    public int getKeyType() {
        switch (this) {
            case DSA512:
            case DSA1024:
            case DSA2048:
            case DSA4096:
            case DSA8192:
                return KeyPair.DSA;
            case ECDSA128:
            case ECDSA256:
            case ECDSA521:
                return KeyPair.ECDSA;
            case RSA512:
            case RSA1024:
            case RSA2048:
            case RSA4096:
            case RSA8192:
                return KeyPair.RSA;
            default:
                throw new RuntimeException("invalid ServerAuthType");
        }
    }

    public int getKeySize() {
        switch (this) {
            case DSA512:
            case RSA512:
                return 512;
            case DSA1024:
            case RSA1024:
                return 1024;
            case DSA2048:
            case RSA2048:
                return 2048;
            case DSA4096:
            case RSA4096:
                return 4096;
            case DSA8192:
            case RSA8192:
                return 8192;
            case ECDSA128:
                return 128;
            case ECDSA256:
                return 256;
            case ECDSA521:
                return 521;
            default:
                throw new RuntimeException("invalid ServerAuthType");
        }
    }
}
