package parse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
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
    public static String COMPARE_LAN = "values";

    public static String PATH_START = "D:\\Repositories\\NoxSecurity\\app\\src\\main\\res\\";
    public static final String PATH_END = "\\strings.xml";
    public static final String PATH_END2 = "\\string.xml";


    public static final String PATH_CONFIG = "checkstrings.config";

    static {
        stringsFolderMap.put("values", "英语");
        stringsFolderMap.put("values-ar", "阿拉伯语");
//        stringsFolderMap.put("values-cs-rCZ", "捷克");
        stringsFolderMap.put("values-de-rDE", "德语");
        stringsFolderMap.put("values-es", "西班牙语");
//        stringsFolderMap.put("values-fr-rFR", "法语");
        stringsFolderMap.put("values-in-rID", "印尼语");
//        stringsFolderMap.put("values-ja-rJP", "日语");
        stringsFolderMap.put("values-ko-rKR", "韩语");
//        stringsFolderMap.put("values-pl-rPL", "波兰");
        stringsFolderMap.put("values-pt", "葡萄牙语");
        stringsFolderMap.put("values-ru-rRU", "俄语");
//        stringsFolderMap.put("values-sv", "瑞典");
        stringsFolderMap.put("values-th-rTH", "泰语");
        stringsFolderMap.put("values-vi-rVN", "越南语");
        stringsFolderMap.put("values-zh-rCN", "中文");
        stringsFolderMap.put("values-zh-rTW", "繁体中文");


        lackWhiteList.add("app_name");
        lackWhiteList.add("whatsapp");
        lackWhiteList.add("line");
        lackWhiteList.add("wechat");
        lackWhiteList.add("qq");
        lackWhiteList.add("private_noti_msg_count");
        lackWhiteList.add("like_us_on_facebook");
        lackWhiteList.add("default_notification_channel_id");

        placeHolderWhiteList.add("battery_running_title1");
        placeHolderWhiteList.add("permission_desc_accessibility");
        placeHolderWhiteList.add("permission_desc_selfstart");
        placeHolderWhiteList.add("deep_clean_des_dialog");
        placeHolderWhiteList.add("game_permission_desc_accessibility");
        placeHolderWhiteList.add("msg_memory_tip");
        placeHolderWhiteList.add("msg_battery_tip");
    }

    public static void main(String[] args) {
        //先解析作为对比的语言文件
        compareLanMap = parseXml(PATH_START + COMPARE_LAN + PATH_END, COMPARE_LAN, true);

        for (String key : stringsFolderMap.keySet()) {
            if (!COMPARE_LAN.equals(key)) {
                parseXml(PATH_START + key + PATH_END, key, false);
            }
        }
    }


    public static LinkedHashMap parseXml(String filePath, String lan, boolean isCompareLan) {
        try {
            if (filePath != null && !filePath.isEmpty() && new File(filePath).exists()) {
                System.out.println();
                System.out.println("=====================================================================");
                System.out.println("当前的语言是：" + lan + ":" + stringsFolderMap.get(lan));
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath));
                NodeList childNodes = document.getElementsByTagName("string");
                System.out.println("总个数:" + childNodes.getLength());
                LinkedHashMap<String, String> curLanHashMap = new LinkedHashMap<>();
                List<String> invalideKey = new ArrayList<>();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) node;
                        String key = eElement.getAttribute("name");
                        String value = node.getTextContent();
                        if (!placeHolderWhiteList.contains(key) && !isValide(key, value, isCompareLan)) {
                            invalideKey.add(key);
                        }
                        curLanHashMap.put(key, value);
                    }
                }

                if (!COMPARE_LAN.equals(lan)) {
                    for (String key : compareLanMap.keySet()) {
                        if (!curLanHashMap.containsKey(key) && !lackWhiteList.contains(key)) {
                            System.out.println("缺少key:==============================" + key);
                        }
                    }

                    for (String key : curLanHashMap.keySet()) {
                        if (!compareLanMap.containsKey(key) && !surplusWhiteList.contains(key)) {
                            System.out.println("多余的key:==============================" + key);
                        }
                    }
                }

                for (String key : invalideKey) {
                    System.out.println("占位符有问题的key：==============================" + key);
                }

                return curLanHashMap;
            }
        } catch (Exception e) {
            System.out.println("e:" + e.getLocalizedMessage());
        }
        return null;
    }

    public static boolean isValide(String key, String value, boolean isCompareLan) {
        PlaceholderBean curBean = new PlaceholderBean();
        if (!isEmpty(value)) {
            if (value.contains("%")) {
                curBean.count_type1 = getCountStr(value, "%s");

                int countS2 = 0;
                for (int i = 0; i < 10; i++) {
                    countS2 += getCountStr(value, "%" + i + "s");
                }
                curBean.count_type3 = countS2;

                int countS3 = 0;
                for (int i = 0; i < 10; i++) {
                    countS3 += countStr(value, "%" + i + "$s");
                }
                curBean.count_type4 = countS3;


                curBean.count_type2 = getCountStr(value, "%d");

                int countD2 = 0;
                for (int i = 0; i < 10; i++) {
                    countD2 += getCountStr(value, "%" + i + "d");
                }
                curBean.count_type5 = countD2;


                int countD3 = 0;
                for (int i = 0; i < 10; i++) {
                    countD3 += countStr(value, "%" + i + "$d");
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

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }


    private static int getCountStr(String str1, String str2) {
        counter = 0;
        return countStr(str1, str2);
    }

    static int counter = 0;

    private static int countStr(String str1, String str2) {
        if (str1.indexOf(str2) == -1) {
            return 0;
        } else if (str1.indexOf(str2) != -1) {
            counter++;
            countStr(str1.substring(str1.indexOf(str2) + str2.length()), str2);
            return counter;
        }
        return 0;
    }
}

