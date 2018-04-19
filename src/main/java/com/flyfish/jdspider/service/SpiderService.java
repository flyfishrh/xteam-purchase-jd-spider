package com.flyfish.jdspider.service;

import com.flyfish.jdspider.util.FileUtil;
import com.flyfish.jdspider.util.HttpUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IDEA by tiaotiao
 *
 * @author: shixiongzhou
 * @date: 2018/3/23 下午11:47
 */
public class SpiderService {
    private static final Logger logger = LoggerFactory.getLogger(SpiderService.class);

    private static final Pattern TITLE_PATTERN = Pattern.compile( "<span\\s*class=\"(\\s*title-text\\s*|\\s*jx-title-text\\s*){1,4}\"\\s*>(.*?)</span>");
    private static final Pattern BIG_PRICE_PATTERN = Pattern.compile("<span\\s*class=\"\\s*big-price\\s*\"\\s*>(.*?)</span>");
    private static final Pattern SMALL_PRICE_PATTERN = Pattern.compile("<span\\s*class=\"\\s*small-price\\s*\"\\s*>(.*?)</span>");
    private static final Pattern IMAGE_CONTENT_PATTERN = Pattern.compile("<div\\s*class=\"\\s*scroll-imgs\\s*\"\\s*>([\\S\\s]*?)</ul>");
    private static final Pattern IMAGE_PATTERN = Pattern.compile("<img([\\s\\S]*?)src=\"(.*?)\"([\\s\\S]*?)>");
    private static final Pattern HIDE_IMAGE_PATTERN = Pattern.compile("imgsrc=\"(.*?)\"");
    private static final Pattern ITEM_ID_PATTERN = Pattern.compile("(\\d*).html");

//    public static final Pattern PC_TITLE_PATTERN = Pattern.compile("<div\\s*class=\"\\s*sku-name\\s*\">([\\S\\s].*)</div>");
//    public static final Pattern PC_PRICE_PATTERN = Pattern.compile("<span\\s*class=\"(\\s*price\\s*|\\s*J-p-(.*)\\s*){1,4}\"\\s*>(.*?)</span>");
//    public static final String PC_IMAGE_URL_PREFIX = "https://img14.360buyimg.com/n1/s800x800_";


    /**
     * 爬虫逻辑
     * @param url
     * @param folder
     */
    public static boolean seeker(String url, String folder, String imageSize) {
        if (StringUtils.isNotBlank(url)
                && (url.startsWith("http://") || url.startsWith("https://"))) {

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("user-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");

            String html = HttpUtil.httpGet(url, null, headers);

            // 商品名称 先按照移动端处理
            String title = "";
            Matcher matcher = TITLE_PATTERN.matcher(html);
            if (matcher.find()) {
                title = matcher.group(2);
            } else {
                matcher = ITEM_ID_PATTERN.matcher(url);
                if (matcher.find()) {
                    String itemId = matcher.group(1);
                    url = String.format("https://item.m.jd.com/product/%s.html", itemId);
                    html = HttpUtil.httpGet(url, null, headers);
                    matcher = TITLE_PATTERN.matcher(html);
                    if (matcher.find()) {
                        title = matcher.group(2);
                    }
                }
            }

            if (StringUtils.isNotBlank(title)) {
                String path = title.replaceAll(File.separator, "");
                String goodFolder = folder + File.separator + path;
                FileUtil.createFolder(goodFolder);

                // 获取价格
                String bigPrice = "";
                String smallPrice = "";
                matcher = BIG_PRICE_PATTERN.matcher(html);
                if (matcher.find()) {
                    bigPrice = matcher.group(1);
                }
                matcher = SMALL_PRICE_PATTERN.matcher(html);
                if (matcher.find()) {
                    smallPrice = matcher.group(1);
                }
                String price = bigPrice + smallPrice;

                // 获取图片
                List<String> images = new ArrayList<String>();
                matcher = IMAGE_CONTENT_PATTERN.matcher(html);
                if (matcher.find()) {
                    String content = matcher.group(1);
                    matcher = IMAGE_PATTERN.matcher(content);
                    while (matcher.find()) {
                        String image = matcher.group();
                        if (image.contains("imgsrc")) {
                            Matcher matcher2 = HIDE_IMAGE_PATTERN.matcher(image);
                            if (matcher2.find()) {
                                image = matcher2.group(1);
                            }
                        } else {
                            image = matcher.group(2);
                        }
                        images.add(image);
                    }
                }

                String content = title + "\n" + price + "\n";

                int i = 0;
                for (String image : images) {
                    i++;

                    image = image.replaceAll("/jfs/", "/s" + imageSize + "_jfs/");
                    byte[] bytes = HttpUtil.httpGetAsBytes(image, null, headers);
                    try {
                        FileUtils.writeByteArrayToFile(new File(goodFolder + File.separator + i + ".jpg"), bytes);
                        content += image + "\n";
                    } catch (IOException e) {
                        logger.error(String.format("拉取[%s]的图片[%s]时发生异常.", title, image), e);
                    }
                }

                FileUtil.createFile(goodFolder + File.separator + "goods_info.txt", content);
                return true;
            } else {

                logger.error("商品[{}]的信息无法爬取！", url);
            }
        } else {
            // 按照PC版处理


            logger.error("请输入有效的商品url！以http://或https://开头。");
        }
        return false;
    }
}
