/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.camel;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;

import java.util.Arrays;

import static org.apache.camel.component.web3j.Web3jConstants.AT_BLOCK;
import static org.apache.camel.component.web3j.Web3jConstants.DATA;
import static org.apache.camel.component.web3j.Web3jConstants.FROM_ADDRESS;
import static org.apache.camel.component.web3j.Web3jConstants.TO_ADDRESS;

public class ClientTriggerEventTest extends CamelTestSupport {

    public ClientTriggerEventTest() {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
    }

    @Produce(uri = "web3j://http://127.0.0.1:7545?operation=ETH_SEND_TRANSACTION")
    protected ProducerTemplate template;

    @Test
    public void ethTriggerEventTest() throws Exception {
        Function function = new Function("updateBTCCap", Arrays.asList(), Arrays.asList());
        String encodedFunction = FunctionEncoder.encode(function);

        DefaultExchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader(FROM_ADDRESS, "0xc8CDceCE5d006dAB638029EBCf6Dd666efF5A952");
        exchange.getIn().setHeader(TO_ADDRESS, "0x484982345fD584a0a16deC5F9ba330f6383af3d9");
        exchange.getIn().setHeader(AT_BLOCK, "latest");
        exchange.getIn().setHeader(DATA, encodedFunction);

        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue(body != null);
        Type result = FunctionReturnDecoder.decodeIndexedValue(body, new TypeReference<Uint>() {});
        System.out.println(result.getValue());
    }
}
