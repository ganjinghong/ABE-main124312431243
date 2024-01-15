package com.abe.util;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class qxUtil {
    public static Element qx(Element index, Element[] coef, Pairing bp){
        Element res = coef[0].getImmutable();
        for (int i = 1; i < coef.length; i++){
            Element exp = bp.getZr().newElement(i).getImmutable();
            //index一定要使用duplicate复制使用，因为index在每一次循环中都要使用，如果不加duplicte，index的值会发生变化
            res = res.add(coef[i].mul(index.duplicate().powZn(exp)));
        }
        return res;
    }
}
