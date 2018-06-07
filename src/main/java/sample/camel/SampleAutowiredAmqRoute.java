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

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Uint;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import static org.apache.camel.component.web3j.Web3jConstants.AT_BLOCK;
import static org.apache.camel.component.web3j.Web3jConstants.BLOCK_HASH;
import static org.apache.camel.component.web3j.Web3jConstants.DATA;
import static org.apache.camel.component.web3j.Web3jConstants.ETH_BLOCK_HASH_OBSERVABLE;
import static org.apache.camel.component.web3j.Web3jConstants.ETH_GET_BLOCK_BY_HASH;
import static org.apache.camel.component.web3j.Web3jConstants.ETH_LOG_OBSERVABLE;
import static org.apache.camel.component.web3j.Web3jConstants.ETH_SEND_RAW_TRANSACTION;
import static org.apache.camel.component.web3j.Web3jConstants.ETH_SEND_TRANSACTION;
import static org.apache.camel.component.web3j.Web3jConstants.FROM_ADDRESS;
import static org.apache.camel.component.web3j.Web3jConstants.FULL_TRANSACTION_OBJECTS;
import static org.apache.camel.component.web3j.Web3jConstants.OPERATION;
import static org.apache.camel.component.web3j.Web3jConstants.SIGNED_TRANSACTION_DATA;
import static org.apache.camel.component.web3j.Web3jConstants.TO_ADDRESS;

@Component
public class SampleAutowiredAmqRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
//        from("activemq:foo")
//            .to("log:sample");
//
//        from("timer:bar")
//            .setBody(constant("Hello from Camel"))
//            .to("activemq:foo");
//

//
//        from("web3j://http://127.0.0.1:7545?" + OPERATION.toLowerCase() + "=" + ETH_BLOCK_HASH_OBSERVABLE)
//                .to("log:blockHash?showBody=true&showAll=false")
//
//                .setHeader(FULL_TRANSACTION_OBJECTS, constant("true"))
//                .setHeader(BLOCK_HASH, body())
//                .to("web3j://http://127.0.0.1:7545?" + OPERATION.toLowerCase() + "=" + ETH_GET_BLOCK_BY_HASH)
//                .to("log:block?showBody=true&showAll=false");





        //oracle
        from("web3j://http://127.0.0.1:7545?"
                + OPERATION.toLowerCase() + "=" + ETH_LOG_OBSERVABLE
                + "&fromBlock=earliest"
                + "&toBlock=latest"
                + "&topics=0xbc990f6ed9f9b01a5c7a8bc19e4a3c811b42c39a9407311f3101e4c4ba7f13e3")
                .to("log:log_recieved?showAll=true&multiline=true")



                //prep transaction
                .setHeader(FROM_ADDRESS, constant("0xc8CDceCE5d006dAB638029EBCf6Dd666efF5A952"))
                .setHeader(TO_ADDRESS, constant("0x484982345fD584a0a16deC5F9ba330f6383af3d9"))
                .setHeader(AT_BLOCK, constant("latest"))
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Function function = new Function("setBTCCap",
                                Arrays.asList(new Uint(BigInteger.valueOf(10))), Collections.<TypeReference<?>>emptyList());
                        String encodedFunction = FunctionEncoder.encode(function);
                        exchange.getIn().setHeader(DATA, encodedFunction);
                    }
                })
                .to("web3j://http://127.0.0.1:7545?" + OPERATION.toLowerCase() + "=" + ETH_SEND_TRANSACTION)
                .to("log:transaction_sent?showBody=true&showAll=false");
    }
}
