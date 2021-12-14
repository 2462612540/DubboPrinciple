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
package org.apache.dubbo.registry.etcd;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.support.AbstractRegistryFactory;
import org.apache.dubbo.remoting.etcd.EtcdTransporter;

public class EtcdRegistryFactory extends AbstractRegistryFactory {

    private EtcdTransporter etcdTransporter;
    /**
     * @description  直接返回etcd注册中心实例
      * @param: url
     * @date: 2021/12/13 22:32
     * @return: org.apache.dubbo.registry.Registry
     * @author: xjl
    */
    @Override
    protected Registry createRegistry(URL url) {
        return new EtcdRegistry(url, etcdTransporter);
    }
    // 自动注入 tranportep 扩展点
    public void setEtcdTransporter(EtcdTransporter etcdTransporter) {
        this.etcdTransporter = etcdTransporter;
    }
}
