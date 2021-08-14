package com.example;

import java.io.File;
import java.io.FileWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class App {
    public static void main(String[] args) {
        // create Options object
        Options options = new Options();

        // add t option
        options.addOption("d", false, "create directory by novel name");
        options.addOption(new Option("t", "milliseconds wait before loading next page"));
        options.addOption(Option.builder("url").hasArg().required(false).build());
        int wait = 1000;
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            try {
                wait = Integer.parseInt(cmd.getOptionValue("t", "1000"));
            } catch (NumberFormatException e) {
                System.err.println("please input decimal number");
            }
            String url;
            Scanner sc;
            if (!cmd.hasOption("url")) {

                System.out.print("Paste ESJ link:");
                sc = new Scanner(System.in);
                url = sc.nextLine();
            } else {
                url = cmd.getOptionValue("url");
            }

            final String catalog_regex = "http(s)?:\\/\\/www\\.esjzone\\.cc\\/detail\\/\\d+\\.html";
            final String novel_regex = "https:\\/\\/www\\.esjzone\\.cc\\/forum\\/\\d+\\/\\d+\\.html";
            ArrayList<String> links = new ArrayList<>();

            String save_folder = System.getProperty("user.dir");

            if (url.matches(catalog_regex)) {
                WebDriver driver = new ChromeDriver();
                driver.manage().timeouts().implicitlyWait(5, TimeUnit.MINUTES);
                driver.get(url);
                List<WebElement> els = driver.findElements(By.xpath("//*[@id=\"chapterList\"]/a"));
                for (WebElement webElement : els) {
                    String href = webElement.getAttribute("href");
                    if (href.matches(novel_regex)) {
                        links.add(href);
                    }
                }

                if (cmd.hasOption("d")) {
                    String name = driver.findElement(By.xpath("/html/body/div[3]/section/div/div[1]/div[1]/div[2]/h2"))
                            .getText();
                    File folder = new File(name);
                    if (!folder.mkdir() || !folder.exists()) {
                        return;
                    }
                    save_folder = folder.getAbsolutePath();
                }

                for (String link : links) {
                    driver.get(link);
                    String ch_name = driver.findElement(By.xpath("/html/body/div[3]/section/div/div[1]/h2")).getText();
                    System.out.println("Now backup: " + ch_name);
                    String content = driver.findElement(By.xpath("//*[contains(@class,'forum-content')]"))
                            .getAttribute("innerHTML");
                    String text = driver.findElement(By.xpath("//*[contains(@class,'forum-content')]")).getText();
                    File novel_html = new File(save_folder + File.separator + ch_name + ".html");
                    File novel_txt = new File(save_folder + File.separator + ch_name + ".txt");
                    try {
                        if (novel_html.createNewFile()) {
                            FileWriter fw = new FileWriter(novel_html);
                            fw.write(content);
                            fw.close();
                        }

                    } catch (Exception e) {

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
                        Thread.sleep(wait);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                driver.quit();
            } else {
                System.err.println("Incorrct url.");
            }

        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        }

    }
}
