package com.flyfish.jdspider;

import com.flyfish.jdspider.service.SpiderService;
import com.flyfish.jdspider.util.DateUtil;
import com.flyfish.jdspider.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Created with IDEA by tiaotiao
 *
 * @author: shixiongzhou
 * @date: 2018/3/23 下午11:39
 */
public class Starter {
    private static final Logger logger = LoggerFactory.getLogger(Starter.class);

    public static void main(String[] args) throws Exception {

        // 创建今日商品目录
        String dirPath = "商品列表_" + DateUtil.getToday(DateUtil.NS_DAY_ALL_NUM);
        FileUtil.createFolder(dirPath);

        if (args != null && args.length > 0) {
            String filename = args[0];
            String urls = FileUtil.readTxt(filename, "UTF-8");
            if (StringUtils.isNotBlank(urls)) {
                logger.info("开始处理[{}]…", filename);
                String[] urlArray = urls.split(" ");

                int counter = 0;
                for (String url : urlArray) {
                    if (SpiderService.seeker(url, dirPath)) {
                        counter++;
                    }
                }
                logger.info("本次共处理商品{}个，成功{]个.", urlArray.length, counter);
            } else {
                System.out.println("找不到url列表文件或文件为空！");
            }
        } else {
            while (true) {
                System.out.println("请输入要爬取的京东商品URL：");
                Scanner scanner = new Scanner(System.in);
                if (scanner.hasNextLine()) {
                    String url = scanner.nextLine();
                    if ("exit".equalsIgnoreCase(url)) {
                        System.out.println("GoodBye!");
                        System.exit(0);
                    }

                    SpiderService.seeker(url, dirPath);
                }
            }
        }

    }
}
