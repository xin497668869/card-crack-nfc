package com.example.xin.card;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.RED;

public class MainActivity extends AppCompatActivity {

    /**
     * 云埔7-2
     * I/System.out: 0x7a79d2528308040001b3787d2bebcf1d 0x00000000000000000000000000000000 0x00000000000000000000000000000000 0x000000000000ff078069ffffffffffff
     * I/System.out: Sector 1:验证成功
     * I/System.out: 0x0006d818051500000001000023590000 0x0000c081ff0700000000000000000000 0x00000000000000000000000000000000 0x000000000000ff078069ffffffffffff
     */
    public static final String jiada72 = "0006d818051500000001000023590000";
    public static final String jiada42 = "0006d818051500000001000023590000";
    public static final String EMPTY = "00000000000000000000000000000000";

    private IntentFilter[] mWriteTagFilters;
    private NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    private Mode mode = Mode.READ;

    public enum Mode {
        READ, JIADA72,JIADA71, JIADA42, WRITE_EMPTY
    }

    String[][] mTechLists;
    private TextView mainContent;
    private Button readBtn;
    private Button writeja72Btn;
    private Button writeja71Btn;
    private Button writeja42Btn;
    private Button writeEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        mode = Mode.READ;

        readBtn = (Button) findViewById(R.id.readBtn);
        writeja71Btn = (Button) findViewById(R.id.writeja71Btn);
        writeja72Btn = (Button) findViewById(R.id.writeja72Btn);
        writeja42Btn = (Button) findViewById(R.id.writeja42Btn);
        writeEmpty = (Button) findViewById(R.id.writeEmpty);

        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode(Mode.READ);
            }
        });

        writeja72Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode(Mode.JIADA72);
            }
        });
        writeja71Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode(Mode.JIADA71);
            }
        });
        writeja42Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode(Mode.JIADA42);
            }
        });
        writeEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode(Mode.WRITE_EMPTY);
                mainContent.setText("");
            }
        });
    }

    private void changeMode(Mode mode) {
        this.mode = mode;
        switch (mode) {

            case READ:
                readBtn.setTextColor(RED);
                writeja72Btn.setTextColor(BLACK);
                writeja42Btn.setTextColor(BLACK);
                writeEmpty.setTextColor(BLACK);
                break;
            case JIADA71:
                readBtn.setTextColor(BLACK);
                writeja71Btn.setTextColor(RED);
                writeja72Btn.setTextColor(BLACK);
                writeja42Btn.setTextColor(BLACK);
                writeEmpty.setTextColor(BLACK);
                break;
            case JIADA72:
                readBtn.setTextColor(BLACK);
                writeja72Btn.setTextColor(RED);
                writeja42Btn.setTextColor(BLACK);
                writeEmpty.setTextColor(BLACK);
                writeja71Btn.setTextColor(BLACK);
                break;
            case JIADA42:
                readBtn.setTextColor(BLACK);
                writeja72Btn.setTextColor(BLACK);
                writeja42Btn.setTextColor(RED);
                writeEmpty.setTextColor(BLACK);
                writeja71Btn.setTextColor(BLACK);
                break;
            case WRITE_EMPTY:
                readBtn.setTextColor(BLACK);
                writeja72Btn.setTextColor(BLACK);
                writeja42Btn.setTextColor(BLACK);
                writeja71Btn.setTextColor(BLACK);
                writeEmpty.setTextColor(RED);
                break;
        }
    }

    private void init() {
        //  editText = (EditText) findViewById(R.id.editText);

        mainContent = (TextView) findViewById(R.id.mainContent);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        ndef.addCategory("*/*");
        mWriteTagFilters = new IntentFilter[]{ndef};
        mTechLists = new String[][]{new String[]{NfcA.class.getName()},
                new String[]{NfcF.class.getName()},
                new String[]{NfcB.class.getName()},
                new String[]{NfcV.class.getName()}};
    }


    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent,
                mWriteTagFilters, mTechLists);
    }


    /**
     * 读取NFC标签数据的操作
     */
    private MifareClassic getMfc(Intent intent) {
        //取出封装在intent中的TAG
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        for (String tech : tagFromIntent.getTechList()) {
            System.out.println(tech);
        }

        //读取TAG
        MifareClassic mfc = MifareClassic.get(tagFromIntent);
        return mfc;
    }

    public boolean writeToTag(Intent intent, int sectorCount, List<String> hexData) throws IOException {
        MifareClassic mfc = getMfc(intent);
        String metaInfo = "";
        mfc.connect();
//        int maxSectorCount = mfc.getSectorCount();//获取TAG中包含的扇区数
        boolean auth;
        //Authenticate a sector with key A.
        byte[] nokey =
                {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        auth = mfc.authenticateSectorWithKeyA(sectorCount,
                MifareClassic.KEY_DEFAULT);

        System.out.println("准备写入啦");
        if (auth) {
            System.out.println("验证成功写入");
            // 读取扇区中的块
            int bCount = mfc.getBlockCountInSector(sectorCount);
            int bIndex = mfc.sectorToBlock(sectorCount);
            for (int i = 0; i < 3 && i < hexData.size(); i++) {
                System.out.println("Sector " + sectorCount + ":验证成功\n" + "  bCount: " + bCount + "   bIndex" + bIndex + "  写入 " + hexData.get(i));
                metaInfo += "Sector " + sectorCount + ":验证成功\n" + "  bCount: " + bCount + "   bIndex" + bIndex;

                mfc.writeBlock(bIndex, hexStringToByteArray(hexData.get(i)));
                bIndex++;
            }
            metaInfo += "Sector " + sectorCount + " blockCount: " + bCount + ":写入成功  " + hexData + "\n";
        } else {
            metaInfo += "Sector " + sectorCount + ":验证失败\n";
        }
        System.out.println(metaInfo);
        mfc.close();
        return true;

    }

    List<String> zones = new ArrayList<>();

    {
        String zone = "";
        for (int i = 0; i < 20; i++) {
            zones.add(i, zone);
            zone += "0";
        }

    }


    public String bu0(String a) {
        return zones.get(12 - a.length()) + a;
    }

    private void readFromTag(Intent intent) throws IOException {
        MifareClassic mfc = getMfc(intent);
        String metaInfo = "";
        mfc.connect();
        int type = mfc.getType();//获取TAG的类型
        int sectorCount = mfc.getSectorCount();//获取TAG中包含的扇区数
        String typeS = "";
        switch (type) {
            case MifareClassic.TYPE_CLASSIC:
                typeS = "TYPE_CLASSIC";
                break;
            case MifareClassic.TYPE_PLUS:
                typeS = "TYPE_PLUS";
                break;
            case MifareClassic.TYPE_PRO:
                typeS = "TYPE_PRO";
                break;
            case MifareClassic.TYPE_UNKNOWN:
                typeS = "TYPE_UNKNOWN";
                break;
        }

        boolean auth = false;
        System.out.println("卡片类型：" + typeS + "\n共" + sectorCount + "个扇区\n共" + mfc.getBlockCount() + "个块\n存储空间: " + mfc.getSize() + "B\n");
        metaInfo += "卡片类型：" + typeS + "\n共" + sectorCount + "个扇区\n共" + mfc.getBlockCount() + "个块\n存储空间: " + mfc.getSize() + "B\n";

        for (int j = 0; j < sectorCount; j++) {
            auth = mfc.authenticateSectorWithKeyA(j,
                    MifareClassic.KEY_DEFAULT);

//            mfc.transceive(hexStringToByteArray(Long.toHexString(i)));

            int bCount;
            int bIndex;
            if (auth) {

                // 读取扇区中的块
                bCount = mfc.getBlockCountInSector(j);
                bIndex = mfc.sectorToBlock(j);
                System.out.println("Sector " + j + ":验证成功" + "  bCount: " + bCount + "   bIndex" + bIndex + "\n");
                metaInfo += "Sector " + j + ":验证成功" + "  bCount: " + bCount + "   bIndex" + bIndex + "\n";
                for (int i = 0; i < bCount; i++) {
                    byte[] data = mfc.readBlock(bIndex);
                    System.out.print(bytesToHexString(data) + "  ");
                    metaInfo += bytesToHexString(data) + " ";
                    bIndex++;
                }

            } else {
                metaInfo += "Sector " + j + ":验证失败\n";
            }
            metaInfo += "\n";
        }

        mfc.close();
        mainContent.setText(metaInfo);
        System.out.println(metaInfo);
    }

    //字符序列转换为16进制字符串
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
//            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    /**
     * 16进制转成byte数组
     *
     * @param str
     * @return
     */
    public static byte[] hexStringToByteArray(String str) {
        byte[] byteArray = new byte[str.length() / 2];
        int len = byteArray.length;
        int j = 0;
        for (int i = 0; i < len; i++) {
            j = (i << 1);
            byteArray[i] = 0;
            char c = str.charAt(j);
            if ('0' <= c && c <= '9') {
                byteArray[i] |= ((c - '0') << 4);
            } else if ('A' <= c && c <= 'F') {
                byteArray[i] |= ((c - 'A' + 10) << 4);
            } else if ('a' <= c && c <= 'f') {
                byteArray[i] |= ((c - 'a' + 10) << 4);
            } else {
// TODO: Exception
            }
            j++;
            c = str.charAt(j);
            if ('0' <= c && c <= '9') {
                byteArray[i] |= (c - '0');
            } else if ('A' <= c && c <= 'F') {
                byteArray[i] |= (c - 'A' + 10);
            } else if ('a' <= c && c <= 'f') {
                byteArray[i] |= (c - 'a' + 10);
            } else {

            }
        }
        return byteArray;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                    NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
                switch (mode) {
        /*
        0x00091250123100000001000023590000 0x00003880ff0700000000000000000000
         */
                    case READ:
                        readFromTag(intent);
                        break;
                    case JIADA71:
                        List<String> jiada71s = new ArrayList<>();
                        jiada71s.add("00091250123100000001000023590000");
                        jiada71s.add("00003880ff0700000000000000000000");
                        writeToTag(intent, 1, jiada71s);
                        Toast.makeText(getApplicationContext(), "写入成功",
                                Toast.LENGTH_LONG).show();
                        readFromTag(intent);
                        break;
                    case JIADA72:
                        //0x0006d818051500000001000023590000 0x0000c081ff0700000000000000000000 0x00000000000000000000000000000000 0x000000000000ff078069ffffffffffff
                        List<String> jiada72s = new ArrayList<>();
                        jiada72s.add("0006d818051500000001000023590000");
                        jiada72s.add("0000c081ff0700000000000000000000");
//                          jiada72s.add("7a79d252800804006263646566676869");
//                          jiada72s.add("00000000000000000000000000000000");
                        writeToTag(intent, 1, jiada72s);
                        Toast.makeText(getApplicationContext(), "写入成功",
                                Toast.LENGTH_LONG).show();
                        readFromTag(intent);
                        break;
                    case JIADA42:
                        List<String> jiada42s = new ArrayList<>();
                        jiada42s.add("0000cd18032800000001000023590000");
                        jiada42s.add("000e0080ff0700000000000000000000");
                        writeToTag(intent, 1, jiada42s);
                        Toast.makeText(getApplicationContext(), "写入成功",
                                Toast.LENGTH_LONG).show();
                        readFromTag(intent);
                        break;
                    case WRITE_EMPTY:
                        List<String> emptys = new ArrayList<>();
                        emptys.add(EMPTY);
                        emptys.add(EMPTY);
                        writeToTag(intent, 1, emptys);
                        Toast.makeText(getApplicationContext(), "清空成功",
                                Toast.LENGTH_LONG).show();
                        readFromTag(intent);
                }
                readFromTag(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mainContent.setText("出现异常 重新刷卡 " + e.getMessage());
        }
    }
}
