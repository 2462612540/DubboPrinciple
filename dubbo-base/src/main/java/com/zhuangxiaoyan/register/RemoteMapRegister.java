package com.zhuangxiaoyan.register;

import com.zhuangxiaoyan.framework.URL;

import java.io.*;
import java.util.*;

/**
 * @Classname RemoteMapRegister
 * @Description 模拟远程注册中心
 * @Date 2021/12/14 21:54
 * @Created by xjl
 */
public class RemoteMapRegister {

    static String zookeeperPath = "dubbo-base/src/main/resources/temp.txt";

    private static Map<String, List<URL>> REGISTER = new HashMap<>();

    public static void regist(String interfaceName, URL url) {
        List<URL> list = Collections.singletonList(url);
        REGISTER.put(interfaceName, list);

        // 将provider中的注册数据共享出来，持久化到一个文件中
        saveFile();
    }

    // 模拟注册中心的Zookeeper 存放注册中心数据
    private static void saveFile() {
        try {
            File file;
            FileOutputStream fileOutputStream = new FileOutputStream(zookeeperPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(REGISTER);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 模拟注册中心的Zookeeper 拿到注册中心数据
    private static Map<String, List<URL>> getFile() {
        try {
            FileInputStream fileInputStream = new FileInputStream(zookeeperPath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (Map<String, List<URL>>) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<URL> get(String interfaceName) {
        return REGISTER.get(interfaceName);
    }

    public static URL random(String interfaceName) {
        REGISTER = getFile();
        List<URL> list = REGISTER.get(interfaceName);
        Random random = new Random();
        int n = random.nextInt(list.size());
        return list.get(n);
    }
}
