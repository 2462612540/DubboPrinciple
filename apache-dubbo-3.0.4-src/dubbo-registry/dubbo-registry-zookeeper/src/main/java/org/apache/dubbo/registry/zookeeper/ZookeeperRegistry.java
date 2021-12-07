/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.registry.zookeeper;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.ConcurrentHashSet;
import org.apache.dubbo.common.utils.UrlUtils;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.registry.RegistryNotifier;
import org.apache.dubbo.registry.support.CacheableFailbackRegistry;
import org.apache.dubbo.remoting.Constants;
import org.apache.dubbo.remoting.zookeeper.ChildListener;
import org.apache.dubbo.remoting.zookeeper.StateListener;
import org.apache.dubbo.remoting.zookeeper.ZookeeperClient;
import org.apache.dubbo.remoting.zookeeper.ZookeeperTransporter;
import org.apache.dubbo.rpc.RpcException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

import static org.apache.dubbo.common.constants.CommonConstants.*;
import static org.apache.dubbo.common.constants.RegistryConstants.*;

/**
 * ZookeeperRegistry
 * 该类继承了CacheableFailbackRegistry类，该类就是针对注册中心核心的功能注册、订阅、取消注册、取消订阅，查询注册列表进行展开，基于zookeeper来实现
 */
public class ZookeeperRegistry extends CacheableFailbackRegistry {
    // 日志记录
    private final static Logger logger = LoggerFactory.getLogger(ZookeeperRegistry.class);
    // 默认zookeeper根节点
    private final static String DEFAULT_ROOT = "dubbo";
    // zookeeper根节点
    private final String root;
    // 服务接口集合
    private final Set<String> anyServices = new ConcurrentHashSet<>();
    // 监听器集合
    private final ConcurrentMap<URL, ConcurrentMap<NotifyListener, ChildListener>> zkListeners = new ConcurrentHashMap<>();
    // zookeeper客户端实例
    private ZookeeperClient zkClient;

    //ZookeeperRegistry 构造方法
    public ZookeeperRegistry(URL url, ZookeeperTransporter zookeeperTransporter) {
        super(url);
        if (url.isAnyHost()) {
            throw new IllegalStateException("registry address == null");
        }
        // 获得url携带的分组配置，并且作为zookeeper的根节点
        String group = url.getGroup(DEFAULT_ROOT);
        if (!group.startsWith(PATH_SEPARATOR)) {
            group = PATH_SEPARATOR + group;
        }
        this.root = group;
        // 创建zookeeper client
        zkClient = zookeeperTransporter.connect(url);
        // 添加状态监听器，当状态为重连的时候调用恢复方法
        zkClient.addStateListener((state) -> {
            if (state == StateListener.RECONNECTED) {
                logger.warn("Trying to fetch the latest urls, in case there're provider changes during connection loss.\n" +
                    " Since ephemeral ZNode will not get deleted for a connection lose, " +
                    "there's no need to re-register url of this instance.");
                ZookeeperRegistry.this.fetchLatestAddresses();
            } else if (state == StateListener.NEW_SESSION_CREATED) {
                logger.warn("Trying to re-register urls and re-subscribe listeners of this instance to registry...");
                try {
                    // 恢复
                    ZookeeperRegistry.this.recover();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            } else if (state == StateListener.SESSION_LOST) {
                logger.warn("Url of this instance will be deleted from registry soon. " +
                    "Dubbo client will try to re-register once a new session is created.");
            } else if (state == StateListener.SUSPENDED) {

            } else if (state == StateListener.CONNECTED) {

            }
        });
    }

    //是否连接
    @Override
    public boolean isAvailable() {
        return zkClient != null && zkClient.isConnected();
    }

    //销毁连接
    @Override
    public void destroy() {
        super.destroy();
        // Just release zkClient reference, but can not close zk client here for zk client is shared somewhere else.
        // See org.apache.dubbo.remoting.zookeeper.AbstractZookeeperTransporter#destroy()
        zkClient = null;
    }

    private void checkDestroyed() {
        if (zkClient == null) {
            throw new IllegalStateException("registry is destroyed");
        }
    }

    //注册
    @Override
    public void doRegister(URL url) {
        try {
            checkDestroyed();
            //创建URL节点，也就是URL层的节点
            zkClient.create(toUrlPath(url), url.getParameter(DYNAMIC_KEY, true));
        } catch (Throwable e) {
            throw new RpcException("Failed to register " + url + " to zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    //取消注册
    @Override
    public void doUnregister(URL url) {
        try {
            checkDestroyed();
            //// 删除节点
            zkClient.delete(toUrlPath(url));
        } catch (Throwable e) {
            throw new RpcException("Failed to unregister " + url + " to zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }
    //这个方法是订阅

    /**
     * @description Map computeIfAbsent方法说明  从map中根据key获取value操作 java8之后。上面的操作可以简化为一行，若key对应的value为空，会将第二个参数的返回值存入并返回
     * <p>
     * 所有Service层发起的订阅中的ChildListener是在在 Service 层发生变更时，才会做出解码，用anyServices属性判断是否是新增的服务，最后调用父类的subscribe订阅。
     * 而指定的Service层发起的订阅是在URL层发生变更的时候，调用notify，回调回调NotifyListener的逻辑，做到通知服务变更。
     * <p>
     * 所有Service层发起的订阅中客户端创建的节点是Service节点，该节点为持久节点，而指定的Service层发起的订阅中创建的节点是Type节点，该节点也是持久节点。
     * 这里补充一下zookeeper的持久节点是节点创建后，就一直存在，直到有删除操作来主动清除这个节点，不会因为创建该节点的客户端会话失效而消失。而临时节点的生命周期和客户端会话绑定。
     * 也就是说，如果客户端会话失效，那么这个节点就会自动被清除掉。注意，这里提到的是会话失效，而非连接断开。另外，在临时节点下面不能创建子节点。
     * <p>
     * 指定的Service层发起的订阅中调用了两次notify，第一次是增量的通知，也就是只是通知这次增加的服务节点，而第二个是全量的通知
     * @param: url
     * @param: listener
     * @date: 2021/12/7 21:11
     * @return: void
     * @author: xjl
     */
    @Override
    public void doSubscribe(final URL url, final NotifyListener listener) {
        try {
            checkDestroyed();
            // 处理所有Service层发起的订阅，例如监控中心的订阅
            if (ANY_VALUE.equals(url.getServiceInterface())) {
                //获得根目录
                String root = toRootPath();
                boolean check = url.getParameter(CHECK_KEY, false);
                // 获得url对应的监听器集合
                ConcurrentMap<NotifyListener, ChildListener> listeners = zkListeners.computeIfAbsent(url, k -> new ConcurrentHashMap<>());
                // 获得节点监听器
                ChildListener zkListener = listeners.computeIfAbsent(listener, k -> (parentPath, currentChilds) -> {
                    for (String child : currentChilds) {
                        child = URL.decode(child);
                        if (!anyServices.contains(child)) {
                            anyServices.add(child);
                            subscribe(url.setPath(child).addParameters(INTERFACE_KEY, child,
                                Constants.CHECK_KEY, String.valueOf(check)), k);
                        }
                    }
                });
                // 创建service节点，该节点为持久节点
                zkClient.create(root, false);
                // 向zookeeper的service节点发起订阅，获得Service接口全名数组
                List<String> services = zkClient.addChildListener(root, zkListener);
                if (CollectionUtils.isNotEmpty(services)) {
                    for (String service : services) {
                        service = URL.decode(service);
                        anyServices.add(service);
                        // 发起该service层的订阅
                        subscribe(url.setPath(service).addParameters(INTERFACE_KEY, service,
                            Constants.CHECK_KEY, String.valueOf(check)), listener);
                    }
                }
            } else {
                CountDownLatch latch = new CountDownLatch(1);
                try {
                    // 处理指定 Service 层的发起订阅，例如服务消费者的订阅
                    List<URL> urls = new ArrayList<>();
                    for (String path : toCategoriesPath(url)) {
                        // 遍历分类数组 获得监听器集合
                        ConcurrentMap<NotifyListener, ChildListener> listeners = zkListeners.computeIfAbsent(url, k -> new ConcurrentHashMap<>());
                        // 如果没有则创建
                        ChildListener zkListener = listeners.computeIfAbsent(listener, k -> new RegistryChildListenerImpl(url, path, k, latch));
                        if (zkListener instanceof RegistryChildListenerImpl) {
                            ((RegistryChildListenerImpl) zkListener).setLatch(latch);
                        }
                        zkClient.create(path, false);
                        List<String> children = zkClient.addChildListener(path, zkListener);
                        if (children != null) {
                            urls.addAll(toUrlsWithEmpty(url, path, children));
                        }
                    }
                    notify(url, listener, urls);
                } finally {
                    // 告诉监听器只有在主线程的同步通知完成后才运行
                    latch.countDown();
                }
            }
        } catch (Throwable e) {
            throw new RpcException("Failed to subscribe " + url + " to zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }
    //取消订阅

    /**
     * @description 所有的Service发起的取消订阅还是指定的Service发起的取消订阅。可以看到所有的Service发起的取消订阅就直接移除了根目录下所有的监听器，
     * 而指定的Service发起的取消订阅是移除了该Service层下面的所有Type节点监听器
     * @param: url
     * @param: listener
     * @date: 2021/12/7 21:18
     * @return: void
     * @author: xjl
     */
    @Override
    public void doUnsubscribe(URL url, NotifyListener listener) {
        checkDestroyed();
        // 获得监听器集合
        ConcurrentMap<NotifyListener, ChildListener> listeners = zkListeners.get(url);
        if (listeners != null) {
            // 获得子节点的监听器
            ChildListener zkListener = listeners.remove(listener);
            if (zkListener != null) {
                // 如果为全部的服务接口，例如监控中心
                if (ANY_VALUE.equals(url.getServiceInterface())) {
                    // 获得根目录
                    String root = toRootPath();
                    // 移除监听器
                    zkClient.removeChildListener(root, zkListener);
                } else {
                    // 遍历分类数组进行移除监听器
                    for (String path : toCategoriesPath(url)) {
                        zkClient.removeChildListener(path, zkListener);
                    }
                }
            }

            if (listeners.isEmpty()) {
                zkListeners.remove(url);
            }
        }
    }

    //该方法就是查询符合条件的已经注册的服务。调用了toUrlsWithoutEmpty方法
    @Override
    public List<URL> lookup(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("lookup url == null");
        }
        try {
            checkDestroyed();
            List<String> providers = new ArrayList<>();
            // 遍历分组类别
            for (String path : toCategoriesPath(url)) {
                // 获得子节点
                List<String> children = zkClient.getChildren(path);
                if (children != null) {
                    providers.addAll(children);
                }
            }
            // 获得 providers 中，和 consumer 匹配的 URL 数组
            return toUrlsWithoutEmpty(url, providers);
        } catch (Throwable e) {
            throw new RpcException("Failed to lookup " + url + " from zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    private String toRootDir() {
        if (root.equals(PATH_SEPARATOR)) {
            return root;
        }
        return root + PATH_SEPARATOR;
    }

    private String toRootPath() {
        return root;
    }

    //该方法是获得服务路径，拼接规则：Root + Type。
    private String toServicePath(URL url) {
        String name = url.getServiceInterface();
        if (ANY_VALUE.equals(name)) {
            return toRootPath();
        }
        return toRootDir() + URL.encode(name);
    }

    //第一个方法是获得分类数组，也就是url携带的服务下的所有Type节点数组。
    private String[] toCategoriesPath(URL url) {
        String[] categories;
        if (ANY_VALUE.equals(url.getCategory())) {
            categories = new String[]{PROVIDERS_CATEGORY, CONSUMERS_CATEGORY, ROUTERS_CATEGORY, CONFIGURATORS_CATEGORY};
        } else {
            categories = url.getCategory(new String[]{DEFAULT_CATEGORY});
        }
        String[] paths = new String[categories.length];
        for (int i = 0; i < categories.length; i++) {
            paths[i] = toServicePath(url) + PATH_SEPARATOR + categories[i];
        }
        return paths;
    }

    // 第二个是获得分类路径，分类路径拼接规则：Root + Service + Type
    private String toCategoryPath(URL url) {
        return toServicePath(url) + PATH_SEPARATOR + url.getCategory(DEFAULT_CATEGORY);
    }

    //该方法是获得URL路径，拼接规则是Root + Service + Type + URL
    private String toUrlPath(URL url) {
        return toCategoryPath(url) + PATH_SEPARATOR + URL.encode(url.toFullString());
    }

    /**
     * When zookeeper connection recovered from a connection loss, it need to fetch the latest provider list.
     * re-register watcher is only a side effect and is not mandate.
     */
    private void fetchLatestAddresses() {
        // subscribe
        Map<URL, Set<NotifyListener>> recoverSubscribed = new HashMap<URL, Set<NotifyListener>>(getSubscribed());
        if (!recoverSubscribed.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("Fetching the latest urls of " + recoverSubscribed.keySet());
            }
            for (Map.Entry<URL, Set<NotifyListener>> entry : recoverSubscribed.entrySet()) {
                URL url = entry.getKey();
                for (NotifyListener listener : entry.getValue()) {
                    removeFailedSubscribed(url, listener);
                    addFailedSubscribed(url, listener);
                }
            }
        }
    }

    @Override
    protected boolean isMatch(URL subscribeUrl, URL providerUrl) {
        return UrlUtils.isMatch(subscribeUrl, providerUrl);
    }

    private class RegistryChildListenerImpl implements ChildListener {
        private RegistryNotifier notifier;
        private long lastExecuteTime;
        private volatile CountDownLatch latch;

        public RegistryChildListenerImpl(URL consumerUrl, String path, NotifyListener listener, CountDownLatch latch) {
            this.latch = latch;
            notifier = new RegistryNotifier(getUrl(), ZookeeperRegistry.this.getDelay()) {
                @Override
                public void notify(Object rawAddresses) {
                    long delayTime = getDelayTime();
                    if (delayTime <= 0) {
                        this.doNotify(rawAddresses);
                    } else {
                        long interval = delayTime - (System.currentTimeMillis() - lastExecuteTime);
                        if (interval > 0) {
                            try {
                                Thread.sleep(interval);
                            } catch (InterruptedException e) {
                                // ignore
                            }
                        }
                        lastExecuteTime = System.currentTimeMillis();
                        this.doNotify(rawAddresses);
                    }
                }

                @Override
                protected void doNotify(Object rawAddresses) {
                    ZookeeperRegistry.this.notify(consumerUrl, listener, ZookeeperRegistry.this.toUrlsWithEmpty(consumerUrl, path, (List<String>) rawAddresses));
                }
            };
        }

        public void setLatch(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void childChanged(String path, List<String> children) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                logger.warn("Zookeeper children listener thread was interrupted unexpectedly, may cause race condition with the main thread.");
            }
            notifier.notify(children);
        }
    }
}
