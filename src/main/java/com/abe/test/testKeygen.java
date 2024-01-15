package com.abe.test;

import com.abe.cpabeUtil;

public class testKeygen {
    public static void main(String[] args) throws Exception {
        String[] userAttList = {"1", "2","3"};
        String dir = "src/main/data/";
        String pkFileName = dir + "pk.properties";
        String mskFileName = dir + "msk.properties";
        String userSk = cpabeUtil.keygen(userAttList, pkFileName, mskFileName);
        System.out.println(userSk);
    }
}
