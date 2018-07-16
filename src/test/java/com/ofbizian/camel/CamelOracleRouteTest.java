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
package com.ofbizian.camel;

import org.apache.camel.Exchange;
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

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.apache.camel.component.web3j.Web3jConstants.AT_BLOCK;
import static org.apache.camel.component.web3j.Web3jConstants.DATA;
import static org.apache.camel.component.web3j.Web3jConstants.ETH_CALL;
import static org.apache.camel.component.web3j.Web3jConstants.ETH_SEND_TRANSACTION;
import static org.apache.camel.component.web3j.Web3jConstants.FROM_ADDRESS;
import static org.apache.camel.component.web3j.Web3jConstants.OPERATION;
import static org.apache.camel.component.web3j.Web3jConstants.TO_ADDRESS;

public class CamelOracleRouteTest extends CamelTestSupport {

    @Produce(uri = "web3j://http://127.0.0.1:7545")
    protected ProducerTemplate template;

    @Test
    public void getBTCCap() throws Exception {
        Function function = new Function("getBTCCap", Arrays.<Type>asList(), Arrays.<TypeReference<?>>asList(new TypeReference<Uint>() {}));
        String encodedFunction = FunctionEncoder.encode(function);

        Exchange exchange = createExchangeWithHeader(OPERATION, ETH_CALL);
        exchange.getIn().setHeader(FROM_ADDRESS, "0xc8CDceCE5d006dAB638029EBCf6Dd666efF5A952");
        exchange.getIn().setHeader(TO_ADDRESS, "0x484982345fD584a0a16deC5F9ba330f6383af3d9");
        exchange.getIn().setHeader(AT_BLOCK, "latest");
        exchange.getIn().setHeader(DATA, encodedFunction);

        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue(body != null);

        Type result = FunctionReturnDecoder.decodeIndexedValue(body, new TypeReference<Uint>() {});
        List<Type> decode = FunctionReturnDecoder.decode(body, function.getOutputParameters());
        System.out.println("getBTCCap: " + decode.get(0).getValue());
    }

    @Test
    public void updateBTCCap() throws Exception {
        Function function = new Function("updateBTCCap", Arrays.<Type>asList(), Collections.<TypeReference<?>>emptyList());
        String encodedFunction = FunctionEncoder.encode(function);

        Exchange exchange = createExchangeWithHeader(OPERATION, ETH_SEND_TRANSACTION);
        exchange.getIn().setHeader(FROM_ADDRESS, "0xc8CDceCE5d006dAB638029EBCf6Dd666efF5A952");
        exchange.getIn().setHeader(TO_ADDRESS, "0x484982345fD584a0a16deC5F9ba330f6383af3d9");
        exchange.getIn().setHeader(AT_BLOCK, "latest");
        exchange.getIn().setHeader(DATA, encodedFunction);

        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue(body != null);

        List<Type> decode = FunctionReturnDecoder.decode(body, function.getOutputParameters());
        System.out.println("transaction hash: " + body);
        System.out.printf("transaction result  " + decode);
    }

    @Test
    public void setBTCCap() throws Exception {
        Function function = new Function("setBTCCap", Arrays.<Type>asList(new Uint(BigInteger.valueOf(100))), Collections.<TypeReference<?>>emptyList());
        String encodedFunction = FunctionEncoder.encode(function);

        Exchange exchange = createExchangeWithHeader(OPERATION, ETH_SEND_TRANSACTION);
        exchange.getIn().setHeader(FROM_ADDRESS, "0xc8CDceCE5d006dAB638029EBCf6Dd666efF5A952");
        exchange.getIn().setHeader(TO_ADDRESS, "0x484982345fD584a0a16deC5F9ba330f6383af3d9");
        exchange.getIn().setHeader(AT_BLOCK, "latest");
        exchange.getIn().setHeader(DATA, encodedFunction);

        template.send(exchange);
        String body = exchange.getIn().getBody(String.class);
        assertTrue(body != null);

        List<Type> decode = FunctionReturnDecoder.decode(body, function.getOutputParameters());
        System.out.println("transaction hash: " + body);
        System.out.printf("transaction result  " + decode.toString());
    }

    protected Exchange createExchangeWithHeader(String key, Object value) {
        DefaultExchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader(key, value);
        return exchange;
    }
}
