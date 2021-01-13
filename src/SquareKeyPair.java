import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SquareKeyPair {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private static final String ENCRYPTION_SCHEME = "RSA/ECB/PKCS1Padding";
    private static final String EMPTY_STRING = "";

    public SquareKeyPair() {

    }

    public SquareKeyPair(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public void setPublicKeyFromBase64(String b64Key) {
        try {
            byte[] key = Base64.getDecoder().decode(b64Key.getBytes());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(key);
            publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (InvalidKeySpecException e1) {
            e1.printStackTrace();
        }
    }

    public void setPrivateKeyFromBase64(String b64Key) {
        try {
            byte[] key = Base64.getDecoder().decode(b64Key.getBytes());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(key);
            privateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (InvalidKeySpecException e1) {
            e1.printStackTrace();
        }
    }

    public String getPublicKeyBase64() {
        return convertToBase64(publicKey.getEncoded());
    }

    public String getPrivateKeyBase64() {
        return convertToBase64(privateKey.getEncoded());
    }

    private String convertToBase64(byte[] bytes) {
        byte[] encodedBytes = Base64.getEncoder().encode(bytes);
        return new String(encodedBytes);
    }

    private byte[] convertFromBase64(String data) {
        return Base64.getDecoder().decode(data);
    } 

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public Key getPrivateKey() {
        return privateKey;
    }

    public String encryptToBase64(String data) {
        return convertToBase64(encrypt(data));
    }

    public byte[] encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_SCHEME);
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
            return cipher.doFinal(data.getBytes());
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        } catch (NoSuchPaddingException nspe) {
            nspe.printStackTrace();
        } catch (InvalidKeyException ike) {
            ike.printStackTrace();
        } catch (IllegalBlockSizeException ibse) {
            ibse.printStackTrace();
        } catch (BadPaddingException bpe) {
            bpe.printStackTrace();
        }

        return new byte[0];
    }

    public String decryptFromBase64(String data) {
        return decrypt(convertFromBase64(data));
    }

    public String decrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_SCHEME);
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
            byte[] bytes = cipher.doFinal(data);
            return new String(bytes);
        } catch (IllegalBlockSizeException ibse) {
            ibse.printStackTrace();
        } catch (InvalidKeyException ike) {
            ike.printStackTrace();
        } catch (BadPaddingException bpe) {
            bpe.printStackTrace();
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        } catch (NoSuchPaddingException nspe) {
            nspe.printStackTrace();
        }

        return EMPTY_STRING;
    }
}
