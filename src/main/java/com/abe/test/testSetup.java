package com.abe.test;

import com.abe.cpabeUtil;

public class testSetup {
    public static void main(String[] args) throws Exception {
        String dir = "src/main/data/";
        String pairingParametersFileName = dir + "a.properties";
        String pkFileName = dir + "pk.properties";
        String mskFileName = dir + "msk.properties";
        cpabeUtil.setup(pairingParametersFileName, pkFileName, mskFileName);
    }
}
