# Camel-Ethereum-Oracle
Ethereum Oracle with Apache Camel and Web3j

![Oracle](https://raw.githubusercontent.com/bibryam/camel-ethereum-oracle/master/oracle.png)

### Deploy Oracle.sol contract 
Compile the contract.

    truffle compile
    
Start a local Ganache instance first. Then deploy the contract.
    
    truffle migrate --reset

### Start Camel oracle route 
And leave it to listen for CallbackGetBTCCap events to update btc price with a random value.

    mvn spring-boot:run
    
### Run Camel client to check btc price
On a separate terminal check the current btc value in the contract (will be 0)

    mvn test -Dtest=CamelOracleRouteTest#getBTCCap
    
### Trigger CallbackGetBTCCap event 
CallbackGetBTCCap event will be picket by the Camel route running above. Then the route will update the btc price with a random value.

    mvn test -Dtest=CamelOracleRouteTest#updateBTCCap

Then check btc price again to see it set in the contract with random value:

    mvn test -Dtest=CamelOracleRouteTest#getBTCCap

Rather than using the oracle, you can also set the btc price directly to 100:
    
    mvn test -Dtest=CamelOracleRouteTest#setBTCCap    
    
### You can also run Javascript based oracle and client for the same scenario
The complete Javascript based example are from [here](https://kndrck.co/posts/ethereum_oracles_a_simple_guide/)
    
    node oracle.js

On a separate terminal

    node client.js



