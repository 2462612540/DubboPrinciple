# Springboot-dubbo_demo详细命令的步骤

## 1 构建Zookeeper注册中心

   ### 1.1 Zookeeper的下载
   
   - 下载的地址：https://downloads.apache.org/zookeeper/
   
   ### 1.2 Zookeeper的配置
   - 解压缩 zookeeper-3.4.10.tar.gz
   
   ```shell
       [root@master ~]# tar -zxf zookeeper-3.4.10.tar.gz -C /usr/zookeeper
   ```
  
   - 进入到 /usr/zookeeper/zookeeper-3.4.10/conf 目录中并复制 zoo_sample.cfg 文件的并命名为为 zoo.cfg
   
   ```shell
      [root@master ~]# cd zookeeper-3.4.10/conf/
      [root@master conf]# cp zoo_sample.cfg zoo.cfg
   ```
   
   - 编辑zoo.cfg文件修改并添加如下内容
   
   ```shell
      dataDir=/usr/zookeeper/zookeeper-3.4.10/data
      dataLogDir=//usr/zookeeper/zookeeper-3.4.10/logs
   ```
  
   - 编辑环境变量并生效
   ```shell
      [root@master ~]# vi /etc/profile
          export ZOOKEEPER_HOME=/usr/zookeeper/zookeeper-3.4.10/
          export PATH=$ZOOKEEPER_HOME/bin:$PATH
          export PATH
      [root@master ~]# source /etc/profile
   ```
   
   ### 1.3 Zookeeper的运行和关闭与检查
   
   - 启动 zookeeper 服务,查询 zookeeper 状态
   ```shell
      [root@master ~]# cd /usr/zookeeper/zookeeper-3.4.10/
      [root@master bin]# zkServer.sh start
      [root@master bin]# zkServer.sh status

      如打印如下信息则表明启动成功。
      
      ZooKeeper JMX enabled by default
      Using config: /usr/zookeeper/zookeeper-3.4.9/bin/…/conf/zoo.cfg
      Starting zookeeper … STARTED
   ```

   - 关闭 zookeeper 服务
   ```shell
      zkServer.sh stop

      如打印如下信息则表明成功关闭。
      
      ZooKeeper JMX enabled by default
      Using config: /usr/zookeeper/zookeeper-3.4.10/bin/…/conf/zoo.cfg
      Stopping zookeeper … STOPPED

   ```
   - 重启 zookeeper 服务
   ```shell
      zkServer.sh restart 
   ```

   - jps查看进程
   ```shell
      [root@host_jdk bin]# jps
      3332 QuorumPeerMain
      5976 Jps
   ```
   

## 2 构建dubbo监控中心

   ### 2.1 dubbo-admin下载
   - https://github.com/apache/dubbo-admin
   ### 2.2 dubbo-admin配置
   
   ```shell
    Specify registry address in dubbo-admin-server/src/main/resources/application.properties
        dubbo.registry.address=zookeeper://127.0.0.1:2181 修改你的Zookeeper的地址
    
    mvn clean package 进行的编译打包
        mvn clean package -Dmaven.test.skip=true
    
   ```
    
   ### 2.3 dubbo-admin运行
 
   ```shell
    Start 
    
    mvn --projects dubbo-admin-server spring-boot:run OR
    cd dubbo-admin-distribution/target; java -jar dubbo-admin-0.1.jar
    
    Visit http://localhost:8080
    
    Default username and password is root
   ```

   ### 2.4 使用的docker 构建dubbo-admin 服务
   ```shell
        systemctl start docker
        docker pull apache/dubbo-admin
        docker run -d --name dubbo-admin -p 9090:8080 -e admin.registry.address=zookeeper://192.168.25.140:2181 -e admin.config-center=zookeeper://192.168.25.140:2181 -e admin.metadata-report.address=zookeeper://192.168.25.140:2181 --restart=always docker.io/apache/dubbo-admin
        http://IP地址:8080
   ```

## 3 构建Springboot生产者

   ### 3.1 Springboot的构建
   
   ### 3.2 producer生产者的编写
   
   

## 4 构建Springboot消费者

   ### 4.1 Springboot的构建
   
   ### 4.2 producer生产者的编写

```shell

```