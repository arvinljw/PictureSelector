package net.arvin.pictureselectordemo;

/**
 * created by arvin on 16/8/31 23:13
 * email：1035407623@qq.com
 */
public class Test {
    public static void main(String[] args) {
        System.out.print(checkDifferent("D-5H0F6T%Z?QM9,\72:[A8X! ;YJ#"));
    }

    public static boolean checkDifferent(String iniString) {
        boolean isAllNotSame = true;
        for (int i = 0; i < iniString.length(); i++) {
            for (int j = 0; j < iniString.length(); j++) {
                if (iniString.charAt(i) == iniString.charAt(j)) {
                    isAllNotSame = false;
                    break;
                }
            }
            if (!isAllNotSame) {
                break;
            }
        }
        return isAllNotSame;
    }
}
