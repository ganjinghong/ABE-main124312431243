package com.abe.util;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class randompUtil {
    public static Element[] randomP(int d, Element s, Pairing bp) {
        Element[] coef = new Element[d];
        coef[0] = s;
        for (int i = 1; i < d; i++){
            coef[i] = bp.getZr().newRandomElement().getImmutable();
        }
        return  coef;
    }
}
