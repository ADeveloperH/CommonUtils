package parse;

public class PlaceholderBean {

    //%s 个数
    public int count_type1 = 0;
    //%d 个数
    public int count_type2 = 0;
    //%1s 类型个数
    public int count_type3 = 0;
    //%1$s 类型个数
    public int count_type4 = 0;
    //%1d 类型个数
    public int count_type5 = 0;
    //%1$d 类型个数
    public int count_type6 = 0;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlaceholderBean) {
            PlaceholderBean compareBean = (PlaceholderBean) obj;
            return compareBean.count_type1 == count_type1 && compareBean.count_type2 == count_type2 && compareBean.count_type3 == count_type3 && compareBean.count_type4 == count_type4;
        }
        return false;
    }
}
