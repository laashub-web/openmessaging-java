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

package org.apache.openmessaging.samples.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.openmessaging.KeyValue;
import org.apache.openmessaging.MessagingAccessPoint;
import org.apache.openmessaging.MessagingAccessPointManager;
import org.apache.openmessaging.ServiceEndPoint;
import org.apache.openmessaging.samples.service.api.HelloService;

public class SimpleConsumer {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        try {
            KeyValue properties = MessagingAccessPointManager.buildKeyValue();
            properties.put("protocol.name", "mvp");
            final MessagingAccessPoint messagingAccessPoint = MessagingAccessPointManager.getMessagingAccessPoint("openmessaging:rocketmq:100.81.2.5:8443", properties);

            final ServiceEndPoint serviceEndPoint = messagingAccessPoint.createServiceEndPoint();

            messagingAccessPoint.start();

            serviceEndPoint.start();

            HelloService helloServiceGen = serviceEndPoint.bind(HelloService.class);

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    messagingAccessPoint.shutdown();
                    serviceEndPoint.shutdown();
                }
            }));

            for (int i = 0; i < 1000; i++) {
                CallRequest req = new CallRequest();
                req.setValue("hello ");
                try {
                    final CallResponse response = helloServiceGen.sayHello(req);
                    System.out.println(response);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                TimeUnit.MILLISECONDS.sleep(10);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
