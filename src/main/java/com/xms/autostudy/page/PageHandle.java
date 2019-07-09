package com.xms.autostudy.page;

import com.xms.autostudy.constant.AutoStudyConstant;
import com.xms.autostudy.utils.SpringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
/**
 * xumengsi
 */
public class PageHandle {

    private static final Logger log = LoggerFactory.getLogger(PageHandle.class);

    private static String STATIC_DATE_TEMPLATE = "1993-11-09 10:%s";

    /**
     * 开始执行页面操作
     * @param pageProcess
     */
    public static void start(PageProcess pageProcess){
        List<String> filterLinkUrls = getLinks(pageProcess.getUrl(), pageProcess.getUserId(), pageProcess.getPattern(), pageProcess.getAutoKey());
        getCorrectLinks(pageProcess.getWebDriver(), pageProcess.getJsExecutor(), filterLinkUrls, pageProcess.getUserId(), pageProcess.getRegex(), pageProcess.getAutoKey());
    }

    /**
     * 获取页面数据源
     * @param url
     * @param userId
     * @param pattern
     * @param AutoKey
     * @return
     */
    @SuppressWarnings("unchecked")
	private static List<String> getLinks(String url, String userId, Pattern pattern, String AutoKey) {
        url = String.format(url, Math.floor(System.currentTimeMillis() / 1000));
        RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);
        ResponseEntity<Resource> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, Resource.class);
        InputStream ReadIn = null;
        try {
            ReadIn = responseEntity.getBody().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String readDataJsonStr = inputStream2String(ReadIn);
        List<String> readUrlList = new ArrayList<>();
        Matcher matcher = pattern.matcher(readDataJsonStr);
        while (matcher.find()) {
            readUrlList.add(matcher.group());
        }
        ListOperations<String,String> listOperations = SpringUtil.getBean(ListOperations.class);
        String key = AutoStudyConstant.formatKey(AutoKey, userId);
        Long keySize = listOperations.size(key);
        if(keySize != null && keySize > 0){
            List<String> userIsReadUrls = listOperations.range(key, 0, keySize - 1);
            readUrlList.removeAll(userIsReadUrls);
        }
        return readUrlList;
    }


    /**
     * 判断是否有视频
     * @param window
     * @param webDriver
     * @param jsExecutor
     * @param pageStopTime
     * @return
     */
    public static int isOpenVideo(String window, WebDriver webDriver, JavascriptExecutor jsExecutor, int pageStopTime) {
        webDriver.switchTo().window(window);
        Actions action = new Actions(webDriver);
            WebElement webElement = webDriver.findElement(By.xpath("//div[@class='outter']"));
            action.moveToElement(webElement).click().perform();
            String pagewatchTime = webDriver.findElement(By.xpath("//span[@class='duration']")).getText();
            log.info("执行自动打开视频，观看时间：{}", pagewatchTime);
            LocalDateTime localDateTime = LocalDateTime.parse(String.format(STATIC_DATE_TEMPLATE, pagewatchTime), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return localDateTime.getMinute() * 60 * 1000 + localDateTime.getSecond() * 1000;
    }


    /**
     * 过滤并且打开正确的数据源连接
     *
     * @param webDriver
     * @param links
     * @return
     */
    @SuppressWarnings("unchecked")
	private static void getCorrectLinks(WebDriver webDriver, JavascriptExecutor jsExecutor, List<String> links, String userId, String regex, String autoKey) {
        int filterStart = 0;
        int filterEnd = 6;
        List<String> selectLinkUrl = links.subList(filterStart, filterEnd);
        links = links.stream().skip(filterEnd + 1).collect(Collectors.toList());

        Boolean isLoop = Boolean.FALSE;
        Integer windowSize = webDriver.getWindowHandles().size();
        log.info("windowSize: {}", windowSize);
        for (String link : selectLinkUrl) {
            if (windowSize <= filterEnd) {
                //此处window窗口可能不符合要求被关闭，需要先切换下窗口在执行打开新窗口
                webDriver.switchTo().window(webDriver.getWindowHandles().iterator().next());
                jsExecutor.executeScript("window.open('" + link + "','_blank')");
                windowSize++;
            }
        }
        try {
            //休眠15秒等待页面加载完毕
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int isCloseWindowSize = 0;
        List<String> windows = webDriver.getWindowHandles().stream().collect(Collectors.toList());
        for (String window : windows) {
            webDriver.switchTo().window(window);
            String key = AutoStudyConstant.formatKey(autoKey, userId);
            ListOperations<String,String> listOperations = SpringUtil.getBean(ListOperations.class);
            listOperations.rightPush(key, webDriver.getCurrentUrl());
            if (webDriver.getPageSource().indexOf("系统正在维护中...") != -1 || !Pattern.matches(regex, webDriver.getCurrentUrl())) {
                log.warn("页面 {} 不符合要求，关闭该窗口", webDriver.getCurrentUrl());
                if( isCloseWindowSize + 1 != windows.size()){
                    webDriver.close();
                }
                isLoop = Boolean.TRUE;
                isCloseWindowSize ++ ;
                continue;
            }
        }
        if (isLoop) {
            getCorrectLinks(webDriver, jsExecutor, links, userId, regex, autoKey);
        }
    }

    /**
     * inputStream 转 string
     *
     * @param inputStream
     * @return
     */
    private static String inputStream2String(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i = -1;
        try {
            while ((i = inputStream.read()) != -1) {
                byteArrayOutputStream.write(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

    /**
     * xumengsi
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class PageProcess{

        /**
         * 模拟器驱动
         */
        private WebDriver webDriver;

        /**
         * JS模拟器
         */
        private JavascriptExecutor jsExecutor;

        /**
         * 用户ID
         */
        private String userId;

        /**
         * 数据源连接
         */
        private String url;

        /**
         * 连接规则
         */
        private Pattern pattern;

        /**
         * 用户已使用的连接redis的key
         */
        private String AutoKey;

        /**
         *  页面符合的正则表达式
         */
        private String regex;

    }
}
