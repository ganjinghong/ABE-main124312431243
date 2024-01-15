import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.Map;

public class nodeShareUtil {
    public static void nodeShare(Map<String, TreeNode> accessTree, String n, Pairing bp){
        // 如果是叶子节点，则不需要再分享
        if (!accessTree.get(n).isLeaf()){
            // 如果不是叶子节点，则先生成一个随机多项式，多项式的常数项为当前节点的秘密值（这个值将被用于分享）
            // 多项式的次数，由节点的gate对应的threshold决定
            Element[] coef = randompUtil.randomP(accessTree.get(n).gate[0], accessTree.get(n).secretShare, bp);
            for (int j=0; j<accessTree.get(n).children.length; j++ ){
                // 父节点的children数组中存储的子节点的id是int类型，但从map中取子节点的时候要将int类型转换为String类型
                int childID = accessTree.get(n).children[j];
                String childName = Integer.toString(childID);
                // 对于每一个子节点，以子节点的索引为横坐标，计算子节点的多项式值（也就是其对应的秘密分片）
                accessTree.get(childName).secretShare = qxUtil.qx(bp.getZr().newElement(childID), coef, bp);
                // 递归，将该子节点的秘密继续共享下去
                nodeShare(accessTree, childName, bp);
            }
        }
    }

}
