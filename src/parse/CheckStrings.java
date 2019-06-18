package parse;

import com.google.gson.Gson;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 解析 Android 中 strings.xml 多语言文件，打印出不符合规范和缺少/多余 的 key
 */
public class CheckStrings {
    //要检查的语言
    private static LinkedHashMap<String, String> stringsFolderMap = new LinkedHashMap<>();
    //白名单（占位符有问题的key）
    private static List<String> placeHolderWhiteList = new ArrayList<>();
    //白名单（多余的key）
    private static List<String> surplusWhiteList = new ArrayList<>();
    //白名单（缺少的key）
    private static List<String> lackWhiteList = new ArrayList<>();

    static {
//        placeHolderWhiteList.add("deep_clean_des_dialog");
//        placeHolderWhiteList.add("battery_running_title1");
//        placeHolderWhiteList.add("permission_desc_accessibility");
//        placeHolderWhiteList.add("permission_desc_selfstart");
//
//
//        lackWhiteList.add("default_notification_channel_id");
//        lackWhiteList.add("clean_app_percent");
//        lackWhiteList.add("spalash_skip");


        stringsFolderMap.put("values", "英语");
        stringsFolderMap.put("values-ar", "阿拉伯语");
//        stringsFolderMap.put("values-cs-rCZ", "捷克");
//        stringsFolderMap.put("values-de-rDE", "德语");
        stringsFolderMap.put("values-es", "西班牙语");
//        stringsFolderMap.put("values-fr-rFR", "法语");
        stringsFolderMap.put("values-in-rID", "印尼语");
//        stringsFolderMap.put("values-ja-rJP", "日语");
//        stringsFolderMap.put("values-ko-rKR", "韩语");
//        stringsFolderMap.put("values-pl-rPL", "波兰");
        stringsFolderMap.put("values-pt", "葡萄牙语");
        stringsFolderMap.put("values-ru-rRU", "俄语");
//        stringsFolderMap.put("values-sv", "瑞典");
        stringsFolderMap.put("values-th-rTH", "泰语");
        stringsFolderMap.put("values-vi-rVN", "越南语");
        stringsFolderMap.put("values-zh-rCN", "中文");
        stringsFolderMap.put("values-zh-rTW", "繁体中文");

    }

    //作为对比的语言解析结果
    private static LinkedHashMap<String, String> compareLan = new LinkedHashMap<>();
    private static LinkedHashMap<String, Integer> placeHolderCountMap = new LinkedHashMap<>();
    //作为对比的语言所在的目录
    public static String COMPARE_LAN = "values";

    public static String PATH_START = "D:\\Repositories\\NoxSecurity\\app\\src\\main\\res\\";
    public static final String PATH_END = "\\strings.xml";
    public static final String PATH_END2 = "\\string.xml";
    public static final String PATH_CONFIG = "checkstrings.config";

    public static void main(String[] args) {
//        String jsonStr = readConfig();
//        System.out.println(jsonStr);
        lackWhiteList.add("app_name");
        lackWhiteList.add("whatsapp");
        lackWhiteList.add("line");
        lackWhiteList.add("wechat");
        lackWhiteList.add("qq");
        lackWhiteList.add("private_noti_msg_count");
        lackWhiteList.add("like_us_on_facebook");

        placeHolderWhiteList.add("battery_running_title1");
        placeHolderWhiteList.add("permission_desc_accessibility");
        placeHolderWhiteList.add("permission_desc_selfstart");
        placeHolderWhiteList.add("deep_clean_des_dialog");
        placeHolderWhiteList.add("game_permission_desc_accessibility");

        compareLan = parseXml2(PATH_START + COMPARE_LAN + PATH_END, COMPARE_LAN, true);
        for (String key : stringsFolderMap.keySet()) {
            if (!COMPARE_LAN.equals(key)) {
                parseXml2(PATH_START + key + PATH_END, key, false);
                parseXml2(PATH_START + key + PATH_END2, key, false);
            }
        }
    }




    private static String readConfig() {
        File cacheFile = new File(PATH_CONFIG);
        System.out.println("cacheFile:" + cacheFile.getAbsolutePath());
        String jsonStr = readStringFromFile(cacheFile);
        if (!isEmpty(jsonStr)) {
            ConfigBean configBean = new Gson().fromJson(jsonStr, ConfigBean.class);
            if (configBean != null) {
                String stringsFloderPath = configBean.getStringsFloderPath();
                String compareLanFolder = configBean.getCompareLanFolder();
                List<String> lackWhiteList = configBean.getLackWhiteList();
                List<String> placeHolderWhiteList = configBean.getPlaceHolderWhiteList();
                List<String> surplusWhiteList = configBean.getSurplusWhiteList();
                LinkedHashMap<String, String> stringsFolderMap = configBean.getStringsFolderMap();
                if (!isEmpty(stringsFloderPath)) {
                    PATH_START = stringsFloderPath;
                }
                if (!isEmpty(compareLanFolder)) {
                    COMPARE_LAN = compareLanFolder;
                }
                if (!isEmpty(lackWhiteList)) {
                    CheckStrings.lackWhiteList = lackWhiteList;
                }
                if (!isEmpty(placeHolderWhiteList)) {
                    CheckStrings.placeHolderWhiteList = placeHolderWhiteList;
                }
                if (!isEmpty(surplusWhiteList)) {
                    CheckStrings.surplusWhiteList = surplusWhiteList;
                }
                if (stringsFolderMap != null && !stringsFolderMap.isEmpty()) {
                    CheckStrings.stringsFolderMap = stringsFolderMap;
                }

            }
        }
        return jsonStr;
    }

    public static LinkedHashMap parseXml2(String filePath, String lan, boolean isCompareLan) {
        try {
            if (filePath != null && !filePath.isEmpty() && new File(filePath).exists()) {
                System.out.println();
                System.out.println("=====================================================================");
                System.out.println("当前的语言是：" + lan + ":" + stringsFolderMap.get(lan));
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath));
                NodeList childNodes = document.getElementsByTagName("string");
                System.out.println("总个数:" + childNodes.getLength());
                LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
                List<String> invalideKey = new ArrayList<>();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) node;
                        String key = eElement.getAttribute("name");
                        String value = node.getTextContent();
                        if (!isValide(key, value, isCompareLan) && !placeHolderWhiteList.contains(key)) {
                            invalideKey.add(key);
                        }
                        hashMap.put(key, value);
                    }
                }

                if (!COMPARE_LAN.equals(lan)) {
                    for (String key : compareLan.keySet()) {
                        if (!hashMap.containsKey(key) && !lackWhiteList.contains(key)) {
                            System.out.println("缺少key:==============================" + key);
                        }
                    }

                    for (String key : hashMap.keySet()) {
                        if (!compareLan.containsKey(key) && !surplusWhiteList.contains(key)) {
                            System.out.println("多余的key:==============================" + key);
                        }
                    }
                }

                for (String key : invalideKey) {
                    System.out.println("占位符有问题的key：==============================" + key);
                }

                return hashMap;
            }
        } catch (Exception e) {
            System.out.println("e:" + e.getLocalizedMessage());
        }

        return null;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean isValide(String key, String value, boolean isCompareLan) {
        boolean result = false;
        int count = 0;
        if (!isEmpty(value)) {
            if (value.contains("%")) {
                counter = 0;
                int countS = countStr(value, "%");
                counter = 0;
                int countS1 = countStr(value, "%s");
                counter = 0;
                int countS2 = 0;
                for (int i = 0; i < 10; i++) {
                    counter = 0;
                    countS2 += countStr(value, "%" + i + "s");
                }
                int countS3 = 0;
                for (int i = 0; i < 10; i++) {
                    counter = 0;
                    countS3 += countStr(value, "%" + i + "$s");
                }

                counter = 0;
                int countD1 = countStr(value, "%d");
                counter = 0;
                int countD2 = 0;
                for (int i = 0; i < 10; i++) {
                    counter = 0;
                    countD2 += countStr(value, "%" + i + "d");
                }
                int countD3 = 0;
                for (int i = 0; i < 10; i++) {
                    counter = 0;
                    countD3 += countStr(value, "%" + i + "$d");
                }

                if (countS != (countS1 + countS2 + countS3 + countD1 + countD2 + countD3)) {
                    result = false;
                }
                count = countS;

            }
            result = true;
        }
        result = false;
        if (isCompareLan) {
            placeHolderCountMap.put(key, count);
        } else {
            int compLanCount = placeHolderCountMap.get(key);
            if (count != compLanCount) {
                result = false;
            }
        }
        return result;
    }

    static int counter = 0;

    public static int countStr(String str1, String str2) {
        if (str1.indexOf(str2) == -1) {
            return 0;
        } else if (str1.indexOf(str2) != -1) {
            counter++;
            countStr(str1.substring(str1.indexOf(str2) + str2.length()), str2);
            return counter;
        }
        return 0;
    }

    public static String readStringFromFile(File cacheFile) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(cacheFile)));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                builder.append(line).append("\n");
            }
        } catch (Exception e) {
        }
        return builder.toString();
    }
}
