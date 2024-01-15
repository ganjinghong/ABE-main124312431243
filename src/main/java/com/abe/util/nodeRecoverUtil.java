package com.abe.util;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class nodeRecoverUtil {
    public static boolean nodeRecover(Map<String, TreeNode> accessTree, String n, String[] atts, Pairing bp) {
        if (!accessTree.get(n).isLeaf()) {
            // 对于内部节点，维护一个子节点索引列表，用于秘密恢复。
            List<Integer> validChildrenList = new ArrayList<>();
            int[] validChildren;
            // 遍历每一个子节点
            for (int j=0; j<accessTree.get(n).children.length; j++){
                int childID = accessTree.get(n).children[j];
                String childName = Integer.toString(childID);
                String s = accessTree.get(childName).isLeaf()? accessTree.get(childName).att : Arrays.toString(accessTree.get(childName).gate);
                // 递归调用，恢复子节点的秘密值
                if (nodeRecover(accessTree, childName, atts, bp)){

//                    System.out.println("The node with index " + childName + ": " + s  + " is satisfied!");

                    validChildrenList.add(accessTree.get(n).children[j]);
                    // 如果满足条件的子节点个数已经达到门限值，则跳出循环，不再计算剩余的节点
                    if (validChildrenList.size() == accessTree.get(n).gate[0]) {
                        accessTree.get(n).valid = true;
                        break;
                    }
                }
                else {
//                    System.out.println("The node with index " + childName + ": " + s  + " is not satisfied!");
                }
            }
            // 如果可恢复的子节点个数等于门限值，则利用子节点的秘密分片恢复当前节点的秘密。
            if (validChildrenList.size() == accessTree.get(n).gate[0]){
                validChildren = validChildrenList.stream().mapToInt(i->i).toArray();
                // 利用拉格朗日差值恢复秘密
                // 注意，此处是在指数因子上做拉格朗日差值
                Element secret = bp.getGT().newOneElement().getImmutable();
                for (int i : validChildren) {
                    Element delta = lagrangeUtil.lagrange(i, validChildren, 0, bp);  //计算拉格朗日插值因子
                    secret = secret.mul( accessTree.get(Integer.toString(i)).secretShare.duplicate().powZn(delta) ); //基于拉格朗日因子进行指数运算，然后连乘
                }
                accessTree.get(n).secretShare = secret;
            }
        }
        else {
            // 判断叶子节点的属性值是否属于属性列表
            // 判断一个String元素是否属于String数组，注意String类型和int类型的判断方式不同
            if (Arrays.asList(atts).contains(accessTree.get(n).att)){
                accessTree.get(n).valid = true;
            }
        }
        return accessTree.get(n).valid;
    }
}
