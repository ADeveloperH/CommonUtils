package com.adeveloperh.plugin;

import com.google.gson.Gson;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import parse.ConfigBean;
import parse.PlaceholderBean;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CheckAndroidStrings {

    /**
     * key:要检查的语言文件夹名称
     * value:对应的中文名称
     */
    private static LinkedHashMap<String, String> stringsFolderMap = new LinkedHashMap<>();


    /**
     * 需要忽略的：占位符有问题的 key
     */
    private static List<String> placeHolderWhiteList = new ArrayList<>();

    /**
     * 需要忽略的：多余的 key
     */
    private static List<String> surplusWhiteList = new ArrayList<>();

    /**
     * 需要忽略的：缺少的 key
     */
    private static List<String> lackWhiteList = new ArrayList<>();


    /**
     * 作为对比的语言解析结果:
     * key:key
     * value:value
     */
    private static LinkedHashMap<String, String> compareLanMap = new LinkedHashMap<>();

    /**
     * 作为对比的语言占位符的个数结果:
     * key:key
     * value:占位符的个数信息的Bean
     */
    private static LinkedHashMap<String, PlaceholderBean> comparePlaceHolderCountMap = new LinkedHashMap<>();

    //作为对比的语言所在的目录
    private static String COMPARE_LAN = "values";

    private static String PATH_START = "D:\\Repositories\\NoxSecurity\\app\\src\\main\\res\\";
    private static final String PATH_END = File.separator + "strings.xml";

    public static final String PATH_CONFIG = "checkstrings.config";

    private static StringBuilder sbResult = new StringBuilder();

    //    static {
//        stringsFolderMap.put("values", "英语");
//        stringsFolderMap.put("values-ar", "阿拉伯语");
////        stringsFolderMap.put("values-cs-rCZ", "捷克");
//        stringsFolderMap.put("values-de-rDE", "德语");
//        stringsFolderMap.put("values-es", "西班牙语");
////        stringsFolderMap.put("values-fr-rFR", "法语");
//        stringsFolderMap.put("values-in-rID", "印尼语");
////        stringsFolderMap.put("values-ja-rJP", "日语");
//        stringsFolderMap.put("values-ko-rKR", "韩语");
////        stringsFolderMap.put("values-pl-rPL", "波兰");
//        stringsFolderMap.put("values-pt", "葡萄牙语");
//        stringsFolderMap.put("values-ru-rRU", "俄语");
////        stringsFolderMap.put("values-sv", "瑞典");
//        stringsFolderMap.put("values-th-rTH", "泰语");
//        stringsFolderMap.put("values-vi-rVN", "越南语");
//        stringsFolderMap.put("values-zh-rCN", "中文");
//        stringsFolderMap.put("values-zh-rTW", "繁体中文");
//
//
//        lackWhiteList.add("app_name");
//        lackWhiteList.add("whatsapp");
//        lackWhiteList.add("line");
//        lackWhiteList.add("wechat");
//        lackWhiteList.add("qq");
//        lackWhiteList.add("private_noti_msg_count");
//        lackWhiteList.add("like_us_on_facebook");
//        lackWhiteList.add("default_notification_channel_id");
//
//        placeHolderWhiteList.add("battery_running_title1");
//        placeHolderWhiteList.add("permission_desc_accessibility");
//        placeHolderWhiteList.add("permission_desc_selfstart");
//        placeHolderWhiteList.add("deep_clean_des_dialog");
//        placeHolderWhiteList.add("game_permission_desc_accessibility");
//        placeHolderWhiteList.add("msg_memory_tip");
//        placeHolderWhiteList.add("msg_battery_tip");
//    }
    public static void main(String[] args) {

        String result = startCheck("D:\\Repositories\\NoxIClean");
        System.out.println(result);

    }

    public static String startCheck(String basePath) {
        sbResult = new StringBuilder();
        readConfig(basePath);
        //先解析作为对比的语言文件
        compareLanMap = parseXml(PATH_START + COMPARE_LAN + PATH_END, COMPARE_LAN, true);

        for (String key : stringsFolderMap.keySet()) {
            if (!COMPARE_LAN.equals(key)) {
                parseXml(PATH_START + key + PATH_END, key, false);
            }
        }
        return sbResult.toString();
    }


    private static String readConfig(String basePath) {
        File cacheFile = new File(basePath + "/checkstrings.config");
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
                    PATH_START = basePath + stringsFloderPath;
                }
                if (!isEmpty(compareLanFolder)) {
                    COMPARE_LAN = compareLanFolder;
                }
                if (!isEmpty(lackWhiteList)) {
                    CheckAndroidStrings.lackWhiteList = lackWhiteList;
                }
                if (!isEmpty(placeHolderWhiteList)) {
                    CheckAndroidStrings.placeHolderWhiteList = placeHolderWhiteList;
                }
                if (!isEmpty(surplusWhiteList)) {
                    CheckAndroidStrings.surplusWhiteList = surplusWhiteList;
                }
                if (stringsFolderMap != null && !stringsFolderMap.isEmpty()) {
                    CheckAndroidStrings.stringsFolderMap = stringsFolderMap;
                }

            }
        }
        return jsonStr;
    }

    private static String readStringFromFile(File cacheFile) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(cacheFile), "UTF-8"));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                builder.append(line).append("\n");
            }
        } catch (Exception var4) {
        }
        return builder.toString();
    }


    private static LinkedHashMap parseXml(String filePath, String lan, boolean isCompareLan) {
        try {
            if (filePath != null && !filePath.isEmpty() && new File(filePath).exists()) {
                sbResult.append("\n");
                sbResult.append("=====================================================================").append("\n").append("\n");
                sbResult.append("当前的语言是：").append(lan).append(":").append(stringsFolderMap.get(lan)).append("\n");
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath));
                NodeList childNodes = document.getElementsByTagName("string");
                sbResult.append("总个数:").append(childNodes.getLength()).append("\n");
                LinkedHashMap<String, String> curLanHashMap = new LinkedHashMap<>();
                List<String> invalideKey = new ArrayList<>();
                int noTranslateCount = 0;
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) node;
                        String key = eElement.getAttribute("name");
                        if (isCompareLan) {
                            //过滤掉不需要翻译的
                            if ("false".equals(eElement.getAttribute("translatable")) && !lackWhiteList.contains(key)) {
                                lackWhiteList.add(key);
                                noTranslateCount++;
                            }
                        }
                        String value = node.getTextContent();
                        if (!placeHolderWhiteList.contains(key) && !isValide(key, value, isCompareLan)) {
                            invalideKey.add(key);
                        }
                        curLanHashMap.put(key, value);
                    }
                }

                if (noTranslateCount > 0) {
                    sbResult.append("translatable 为 false 个数：").append(noTranslateCount).append("\n");
                }

                if (!COMPARE_LAN.equals(lan)) {
                    for (String key : compareLanMap.keySet()) {
                        if (!curLanHashMap.containsKey(key) && !lackWhiteList.contains(key)) {
                            sbResult.append("缺少key:==============================").append(key).append("\n");
                        }
                    }

                    for (String key : curLanHashMap.keySet()) {
                        if (!compareLanMap.containsKey(key) && !surplusWhiteList.contains(key)) {
                            sbResult.append("多余的key:==============================").append(key).append("\n");
                        }
                    }

                    for (String key : invalideKey) {
                        sbResult.append("占位符有问题的key：==============================").append(key).append("\n");
                    }
                }

                return curLanHashMap;
            } else {
                sbResult.append("\n")
                        .append("***********************************************************************************************************")
                        .append("\n").append("未找到对应文件:").append(filePath).append("  语言为：").append(lan)
                        .append("\n")
                        .append("***********************************************************************************************************")
                        .append("\n");
            }
        } catch (Exception e) {
            sbResult.append("e:").append(e.getLocalizedMessage()).append("\n");
        }
        return null;
    }

    private static boolean isValide(String key, String value, boolean isCompareLan) {
        PlaceholderBean curBean = new PlaceholderBean();
        if (!isEmpty(value)) {
            if (value.contains("%")) {
                curBean.count_type1 = getCountStr(value, "%s");

                int countS2 = 0;
                for (int i = 1; i < 10; i++) {
                    countS2 += getCountStr(value, "%" + i + "s");
                }
                curBean.count_type3 = countS2;

                int countS3 = 0;
                for (int i = 1; i < 10; i++) {
                    countS3 += getCountStr(value, "%" + i + "$s");
                }
                curBean.count_type4 = countS3;


                curBean.count_type2 = getCountStr(value, "%d");

                int countD2 = 0;
                for (int i = 1; i < 10; i++) {
                    countD2 += getCountStr(value, "%" + i + "d");
                }
                curBean.count_type5 = countD2;


                int countD3 = 0;
                for (int i = 1; i < 10; i++) {
                    countD3 += getCountStr(value, "%" + i + "$d");
                }
                curBean.count_type6 = countD3;

            }
        }
        if (isCompareLan) {
            comparePlaceHolderCountMap.put(key, curBean);
            return true;
        } else {
            PlaceholderBean compareBean = comparePlaceHolderCountMap.get(key);
            return curBean.equals(compareBean);
        }
    }

    private static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    private static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }


    private static int getCountStr(String str1, String str2) {
        counter = 0;
        return countStr(str1, str2);
    }

    private static int counter = 0;

    private static int countStr(String str1, String str2) {
        if (!str1.contains(str2)) {
            return 0;
        } else if (str1.contains(str2)) {
            counter++;
            countStr(str1.substring(str1.indexOf(str2) + str2.length()), str2);
            return counter;
        }
        return 0;
    }
}

