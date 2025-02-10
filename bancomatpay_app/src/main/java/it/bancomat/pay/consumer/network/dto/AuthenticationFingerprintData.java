package it.bancomat.pay.consumer.network.dto;

import com.google.gson.Gson;

import java.io.CharArrayReader;
import java.util.Arrays;

import it.bancomat.pay.consumer.storage.model.FingerprintData;

public class AuthenticationFingerprintData implements AppAuthenticationInterface {

    private byte[] seed;
    private byte[] hmacKey;

    public AuthenticationFingerprintData(byte[] data) {

        //Non viene più usata una stringa per deparsificare il json tramite gson.fromJson()
        // perchè l'oggetto String è un tipo di oggetto immutabile, e una volta inizializzato rimane nell'heap
        // senza poter essere eliminato. Nel caso attuale viene utilizzato un array di char che dopo l'utilizzo viene vuotato

        char[] chars = new char[data.length];
        for(int i=0; i<data.length; i++) {
            chars[i] = (char) data[i];
        }

        Gson gson = new Gson();
        CharArrayReader reader = new CharArrayReader(chars);
        FingerprintData fingerprintData = gson.fromJson(reader, FingerprintData.class);
        reader.close();
        seed = fingerprintData.getSeed();
        hmacKey = fingerprintData.getHmacKey();

        Arrays.fill(data, (byte) 0x00);
        Arrays.fill(chars, '0');
    }

    @Override
    public byte[] getSeed() {
        return seed;
    }

    @Override
    public byte[] getHmacKey() {
        return hmacKey;
    }

}
