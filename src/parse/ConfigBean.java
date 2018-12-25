package parse;

import java.util.LinkedHashMap;
import java.util.List;

public class ConfigBean {
    private String stringsFloderPath;
    private String compareLanFolder;
    private List<String> placeHolderWhiteList;
    private List<String> surplusWhiteList;
    private List<String> lackWhiteList;
    private LinkedHashMap<String, String> stringsFolderMap;

    public String getStringsFloderPath() {
        return stringsFloderPath;
    }

    public void setStringsFloderPath(String stringsFloderPath) {
        this.stringsFloderPath = stringsFloderPath;
    }

    public String getCompareLanFolder() {
        return compareLanFolder;
    }

    public void setCompareLanFolder(String compareLanFolder) {
        this.compareLanFolder = compareLanFolder;
    }

    public List<String> getPlaceHolderWhiteList() {
        return placeHolderWhiteList;
    }

    public void setPlaceHolderWhiteList(List<String> placeHolderWhiteList) {
        this.placeHolderWhiteList = placeHolderWhiteList;
    }

    public List<String> getSurplusWhiteList() {
        return surplusWhiteList;
    }

    public void setSurplusWhiteList(List<String> surplusWhiteList) {
        this.surplusWhiteList = surplusWhiteList;
    }

    public List<String> getLackWhiteList() {
        return lackWhiteList;
    }

    public void setLackWhiteList(List<String> lackWhiteList) {
        this.lackWhiteList = lackWhiteList;
    }

    public LinkedHashMap<String, String> getStringsFolderMap() {
        return stringsFolderMap;
    }

    public void setStringsFolderMap(LinkedHashMap<String, String> stringsFolderMap) {
        this.stringsFolderMap = stringsFolderMap;
    }
}
