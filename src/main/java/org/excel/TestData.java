package org.excel;

import java.io.*;
import java.util.*;

/**
 * Hello world!
 * @author Administrator
 */
public class TestData {

    public static void main(String[] args) throws IOException {

        Map<String, Object> datas = new HashMap<>(10);
        InputStream is = new FileInputStream(new File("C:/Users/Administrator/Desktop/阿尔塞斯.jpg"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int n = 0;
        while (-1 != (n = is.read(buffer))) {
            output.write(buffer, 0, n);
        }

        List<Map<String, String>> listMap = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("iqcDate", "检验时间" + i);
            map.put("manufacturerName", "供应商" + i);
            map.put("productNo", "产品编号" + i);
            map.put("productName", "产品名称" + i);
            map.put("productSpec", "规格型号" + i);
            map.put("totalQuantity", "测量单位aaaaaaaaaa" + i);
            map.put("result", "批量数" + i);
            map.put("sampleQuantity", "检验结果" + i);
            map.put("badQuantity", "实际抽样数" + i);
            map.put("state", "不良数" + i);
            map.put("iqcNo", "不良率" + i);
            map.put("prodTypeName", "检验状态" + i);
            map.put("prodUnit", "评审状态" + i);
            map.put("reviewState", "检验单号" + i);
            map.put("batchQuantity", "检验员" + i);
            map.put("badRate", "审核人" + i);
            listMap.add(map);
        }
        datas.put("listMap", listMap);


        File file = new File("D:/A临时/excel/test.xlsx");
        file.createNewFile();
        FileOutputStream os = new FileOutputStream(file);
        os.flush();
        os.close();
    }

}