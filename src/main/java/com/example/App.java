package com.example;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class App {
    public static void main(String[] args) {
        System.out.print("Paste ESJ link:");
        var sc = new Scanner(System.in);
        var url = sc.nextLine();

        final String catalog_regex = "http(s)?:\\/\\/www\\.esjzone\\.cc\\/detail\\/\\d+\\.html";
        final String novel_regex = "https:\\/\\/www\\.esjzone\\.cc\\/forum\\/\\d+\\/\\d+\\.html";
        ArrayList<String> links = new ArrayList<>();

        if (url.matches(catalog_regex)) {
            WebDriver driver = new ChromeDriver();
            driver.get(url);
            var els = driver.findElements(By.xpath("//*[@id=\"chapterList\"]/a"));
            for (WebElement webElement : els) {
                var href = webElement.getAttribute("href");
                if (href.matches(novel_regex)) {
                    links.add(href);
                }
            }

            for (String link : links) {
                driver.get(link);
                var ch_name = driver.findElement(By.xpath("/html/body/div[3]/section/div/div[1]/h2")).getText();
                var content = driver.findElement(By.xpath("//*[contains(@class,'forum-content')]"))
                        .getAttribute("innerHTML");
                var text = driver.findElement(By.xpath("//*[contains(@class,'forum-content')]")).getText();
                File novel_html = new File(ch_name + ".html");
                File novel_txt = new File(ch_name + ".txt");
                try {
                    if (novel_html.createNewFile()) {
                        FileWriter fw = new FileWriter(novel_html);
                        fw.write(content);
                        fw.close();
                    }
                    
                } catch (Exception e) {
                    // TODO: handle exception
                } finally {

                }

                try {
                    if (novel_txt.createNewFile()) {
                        FileWriter fw = new FileWriter(novel_txt);
                        fw.write(text);
                        fw.close();
                    }
                    
                } catch (Exception e) {
                    // TODO: handle exception
                } finally {

                }

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        } else {
            System.err.println("Incorrct url.");
        }

        sc.close();
    }
}
