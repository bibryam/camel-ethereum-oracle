# Camel-Ethereum-Oracle
Ethereum Oracle with Apache Camel and Web3J

### Deploy Oracle.sol contract 
Start a local Ganache instance first. Then compile and deploy the contract.

    truffle compile
    truffle migrate --reset

### Start Camel oracle route 
And leave it to listen for CallbackGetBTCCap events to update btc price with a random value.

    mvn spring-boot:run
    
### Run Camel client to check btc price
On a separate terminal check the current btc value in the contract

    mvn test -Dtest=CamelOracleRouteTest#getBTCCap
    
### Trigger CallbackGetBTCCap event 
The event will be picket by the Camel route running above. The route then will update the btc price with a random value.

    mvn test -Dtest=CamelOracleRouteTest#updateBTCCap

Then check btc price again to see it set in the contract:

    mvn test -Dtest=CamelOracleRouteTest#getBTCCap

Rather than using the oracle, you can also set the btc price with a client:
    
    mvn test -Dtest=CamelOracleRouteTest#setBTCCap    
    
### You can also run Javascript based oracle and client for the same scenario
The complete Javascript based example are from [here](https://kndrck.co/posts/ethereum_oracles_a_simple_guide/)
    
    node oracle.js

On a separate terminal

    node client.js

