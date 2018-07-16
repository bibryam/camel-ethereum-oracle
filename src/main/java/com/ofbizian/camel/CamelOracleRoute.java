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
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
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
import java.util.Random;

import static org.apache.camel.component.web3j.Web3jConstants.AT_BLOCK;
import static org.apache.camel.component.web3j.Web3jConstants.DATA;
import static org.apache.camel.component.web3j.Web3jConstants.ETH_SEND_TRANSACTION;
import static org.apache.camel.component.web3j.Web3jConstants.FROM_ADDRESS;
import static org.apache.camel.component.web3j.Web3jConstants.OPERATION;
import static org.apache.camel.component.web3j.Web3jConstants.TO_ADDRESS;

/**
 * A Camel route that listens for CallbackGetBTCCap events and calls setBTCCap with a random number
 */
@Component
public class CamelOracleRoute extends RouteBuilder {

    Random rand = new Random();

    @Override
    public void configure() throws Exception {
        String topics = EventEncoder.buildEventSignature("CallbackGetBTCCap()");

        from("web3j://http://127.0.0.1:7545?operation=ETH_LOG_OBSERVABLE&topics=" + topics)
                .to("log:received event")

                .setHeader(OPERATION, constant(ETH_SEND_TRANSACTION))
                .setHeader(FROM_ADDRESS, constant("0xc8CDceCE5d006dAB638029EBCf6Dd666efF5A952"))
                .setHeader(TO_ADDRESS, constant("0x484982345fD584a0a16deC5F9ba330f6383af3d9"))
                .setHeader(AT_BLOCK, constant("latest"))

                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        int random = rand.nextInt(50);
                        Function function = new Function("setBTCCap", Arrays.<Type>asList(new Uint(BigInteger.valueOf(random))), Collections.<TypeReference<?>>emptyList());
                        String setBTCCap = FunctionEncoder.encode(function);
                        exchange.getIn().setHeader(DATA, setBTCCap);
                    }
                })
                .to("web3j://http://127.0.0.1:7545")

                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        String body = exchange.getIn().getBody(String.class);
                        System.out.println("transaction hash: " + body);
                    }
                })
                .to("log:processed event ");
    }
}
