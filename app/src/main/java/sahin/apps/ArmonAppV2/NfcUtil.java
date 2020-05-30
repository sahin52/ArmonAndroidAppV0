package sahin.apps.ArmonAppV2;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

public class NfcUtil {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static boolean isTag(Intent intent){
        return intent.hasExtra(NfcAdapter.EXTRA_TAG) && intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED);
    }
    public static String Read(Intent intent){
        String CardId="";
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            if(intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)){
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] tagIdBytes = tag.getId();
                CardId = bytesToHex(tagIdBytes);
            }
        }
        return CardId;
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            int place = bytes.length-j-1;
            hexChars[place * 2] = HEX_ARRAY[v >>> 4];
            hexChars[place * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
