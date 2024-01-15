import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class testKeygen {
    public static void main(String[] args) throws Exception {
        String[] userAttList = {"3", "1","2"};
        String dir = "data/";
        String pkFileName = dir + "pk.properties";
        String mskFileName = dir + "msk.properties";

        String userSk = cpabe_NewUtil.keygen(userAttList, pkFileName, mskFileName);
//        String userSk_differentsystem = "userAttList=[0, 1, 2, 3, 4, 10401]D=oGqpjD0nJg2N+IkKs3x5BLwW2zwsiQwyQY4fRIf5G24DcZUINGFNNBTgOQZXCUF4Skp0FBoOo5znZfWI2wmu1ZFx+Yh4ie0yXjncWC7aopbzFKHirk3KcOlCmh8eUBrU38haLQJtLs+7SYtQhqNM4eyTelSgpoY2cCB7Lf8dSgcD0=ge+7JdFQuZ5NKXVOTqV9/yr7vaiR3ALiRNRlK7ni3zxr1+RtlN0ikIRw50eJ4pTkV2Xyk02XvqsL5KYBZZnJjFlE6AHdKjZ4zayrJOS9CadW2TjxE7EDAYHzPQ+Q8+pLBrCaXOeUUy+2llWXK0H2Zs3+T4LWopFmehv8zvLngOAD-0=ab6b7pDQbj1jwEMC05WC9O49pYM/92Ry1rcVxHojcG0tS7XTkr1wpTKDphOQsDdKHZ7WUbPSt1Ew+jpzHmJH3EAhOOlmWnYA32LrBGTW2U3x2FHnosPTPrGoS7sjsKo6daIhXtITZqgFe0M2VvohvaX1FG8ivQTQI1WM5XV26+wD-1=E1yrRt+FP7BjcVYXvD/6OwQZ+76NqwcPrYXru2Rv4UD2uIyT+cM9PCg4cbt+Ds5z20hp92OvT7qjMh7YsKF/X5xqD3qSxq/yLvlQB/GyVs7epR7yWidQF1utzvMOKgDCi0P2jWmiRa1s4tuNBGu9/J4Xq2Dh1yAE5AVh7fCJFUoD-2=YqHGXczHlNjzqNu6SzvO3PIhwRew3scNw1IV09GYRi7Lbgl5WqkrSqQlop35jARQcdEmC2Q8kaTIKnzEAI1ICXDRyu7aVpWpClR9qXiCJg6e1vto3s9dKbkDVH9eCmwMsMSIABetBvt9GCGrShIyoWea76vY9qALwrpdkdKaKloD-3=k65GZD2+jqbBGVS6f3p+SRNd1UmMzQQkIP5cAWTqN0QqEn8F6V6idZyeM98EHkjjCjsuKAR2B3x5BgzSlMzbz4+6TffZ0tfwEbAnXEYl1/WE6cCbS1EkXVE4QP7q7nJHcrjjRpcQGTCVQehoPQlPyBO0JSPj9XCYuMtXHg3j+bUD-4=KyusdKmDfaJAbR7sp5CF6maiZjk3fDK6PhJ7OEPHBfL7gO0iEuXyD74GZQCCmuZNGZLv75V9BAtzoQLAR0yd9jOWuJjtxj6psGst5reKwRT9H7+VfPrpFG6hodZObuog+lAqod+WsZDVRW00/MN/UgphIYimRPLfY17K+nB8CX4D-10401=dW437PS6flVoTPMBCK59ZgtkEG0Cf5z5jP/K3qeIfcpzSQPk9jSTLFxbk4G0rsYP23tn7/wHOCU5B22tGiza5pchaINs6yOz6SvRDKaxq2YhNnhbh985U32g3gpaWWZ1p/IGuqmLC1SrCZaxpkoiWUkNmQFihKFCC/7SlIicx9A";
        System.out.println(userSk);




    }
}
