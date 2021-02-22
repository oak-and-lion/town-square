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
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SquareKeyPair implements ISquareKeyPair {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private IUtility utility;

    public SquareKeyPair() {
    }

    public SquareKeyPair(IUtility utility) {
        setPrivateKeyFromBase64(utility.readFile(Constants.PRIVATE_KEY_FILE));
        setPublicKeyFromBase64(utility.readFile(Constants.PUBLIC_KEY_FILE));
        this.utility = utility;
    }

    public SquareKeyPair(PublicKey publicKey, PrivateKey privateKey, IUtility utility) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.utility = utility;
    }

    public void setPublicKeyFromBase64(String b64Key) {
        if (b64Key.equals(Constants.EMPTY_STRING)) {
            return;
        }
        try {
            byte[] key = Base64.getDecoder().decode(b64Key.getBytes());
            KeyFactory keyFactory = KeyFactory.getInstance(Constants.RSA);
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(key);
            publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException e) {
            utility.logError(e.getMessage());
        }
        catch (InvalidKeySpecException e1) {
            utility.logError(e1.getMessage());
        }
    }

    public void setPrivateKeyFromBase64(String b64Key) {
        if (b64Key.equals(Constants.EMPTY_STRING)) {
            return;
        }
        try {
            byte[] key = Base64.getDecoder().decode(b64Key.getBytes());
            KeyFactory keyFactory = KeyFactory.getInstance(Constants.RSA);
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(key);
            privateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException e) {
            utility.logError(e.getMessage());
        }
        catch (InvalidKeySpecException e1) {
            utility.logError(e1.getMessage());
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
            Cipher cipher = Cipher.getInstance(Constants.ENCRYPTION_SCHEME);
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
            return cipher.doFinal(data.getBytes());
        } catch (NoSuchAlgorithmException nsae) {
            utility.logError(nsae.getMessage());
        } catch (NoSuchPaddingException nspe) {
            utility.logError(nspe.getMessage());
        } catch (InvalidKeyException ike) {
            utility.logError(ike.getMessage());
        } catch (IllegalBlockSizeException ibse) {
            utility.logError(ibse.getMessage());
        } catch (BadPaddingException bpe) {
            utility.logError(bpe.getMessage());
        }

        return new byte[0];
    }

    public String decryptFromBase64(String data) {
        return decrypt(convertFromBase64(data));
    }

    public String decrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(Constants.ENCRYPTION_SCHEME);
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
            byte[] bytes = cipher.doFinal(data);
            return new String(bytes);
        } catch (IllegalBlockSizeException ibse) {
            utility.logError(utility.concatStrings(ibse.getMessage(), Constants.NEWLINE, Arrays.toString(ibse.getStackTrace())));
        } catch (InvalidKeyException ike) {
            utility.logError(utility.concatStrings(ike.getMessage(), Constants.NEWLINE, Arrays.toString(ike.getStackTrace())));
        } catch (BadPaddingException bpe) {
            utility.logError(utility.concatStrings(bpe.getMessage(), Constants.NEWLINE, Arrays.toString(bpe.getStackTrace())));
        } catch (NoSuchAlgorithmException nsae) {
            utility.logError(utility.concatStrings(nsae.getMessage(), Constants.NEWLINE, Arrays.toString(nsae.getStackTrace())));
        } catch (NoSuchPaddingException nspe) {
            utility.logError(utility.concatStrings(nspe.getMessage(), Constants.NEWLINE, Arrays.toString(nspe.getStackTrace())));
        }

        return Constants.EMPTY_STRING;
    }
}
