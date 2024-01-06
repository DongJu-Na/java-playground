package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.setProperty("webdriver.chrome.driver", "/home/app/chromedriver-window/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--window-size=1920x1080");
        options.addArguments("--log-level=3");
        options.addArguments("--incognito");
        options.addArguments("--mute_audio");
        options.addArguments("--no-gpu");
        options.setAcceptInsecureCerts(true);
        ChromeDriver chromeDriver = new ChromeDriver(options);

        List<String> imageList = new ArrayList<>();

        try {
            chromeDriver.get("https://dabagirl.co.kr/product/detail.html?product_no=55707");

            Thread.sleep(5000);

            WebElement element = chromeDriver.findElement(By.tagName("body"));

            element.sendKeys(Keys.END);

            Thread.sleep(200);

            element.sendKeys(Keys.HOME);

            Thread.sleep(5000);

            List<WebElement> contents = chromeDriver.findElements(By.tagName("img"));

            imageList = contents.stream()
                    .map(item -> String.valueOf(item.getAttribute("src")))
                    .filter(item -> !item.contains("btn")
                            && !item.contains("txt")
                            && !item.contains("icon")
                            && !item.contains("ico")
                            && !item.contains("loading")
                            && !item.contains("member")
                            && !item.contains("null")
                            && !item.contains("gif")
                            && !item.contains("svg")
                            && !ObjectUtils.isEmpty(item))
                    .collect(Collectors.toList());

            List<String> imageDataList = contents.stream()
                    .map(item -> String.valueOf(item.getAttribute("data-src")).contains("http") ? String.valueOf(item.getAttribute("data-src")) : "https:" + item.getAttribute("data-src"))
                    .filter(item -> !item.contains("btn")
                            && !item.contains("txt")
                            && !item.contains("icon")
                            && !item.contains("ico")
                            && !item.contains("loading")
                            && !item.contains("member")
                            && !item.contains("null")
                            && !item.contains("gif")
                            && !item.contains("svg")
                            && !ObjectUtils.isEmpty(item))
                    .collect(Collectors.toList());

            chromeDriver.quit();

            if (!ObjectUtils.isEmpty(imageDataList)) {
                imageList.addAll(imageDataList);
            }

            imageList = imageList.stream().filter(item -> {
                BufferedImage bimg = null;
                try {
                    bimg = ImageIO.read(new URL(item).openStream());
                } catch (Exception e) {
                    return false;
                } finally {
                    if (ObjectUtils.isEmpty(bimg)) {
                        try {
                            bimg = ImageIO.read(new URL(item.replaceAll("https:", "http:")).openStream());
                        } catch (Exception e) {
                            return false;
                        }
                    }
                    if (!ObjectUtils.isEmpty(bimg) && bimg.getWidth() > 300 && bimg.getHeight() > 300) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());


        } catch (Exception e){
            e.printStackTrace();
            chromeDriver.quit();
        }

        imageList = imageList.stream().distinct().collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        long totalTimeInMillis = endTime - startTime;

        long minutes = (totalTimeInMillis / (1000 * 60));
        long seconds = (totalTimeInMillis / 1000) % 60;

        System.out.println("총 소요 시간: " + minutes + "분 " + seconds + "초");
    }
}