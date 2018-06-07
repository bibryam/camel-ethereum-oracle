pragma solidity ^0.4.23;

contract Oracle {
    // Contract owner
    address public owner;

    // BTC Marketcap Storage
    uint public btcMarketCap;

    // Callback function
    event CallbackGetBTCCap();

    constructor() public {
        owner = msg.sender;
    }

    function updateBTCCap() public {
        // Calls the callback function
        emit CallbackGetBTCCap();
    }

    function setBTCCap(uint cap) public {
        // If it isn't sent by a trusted oracle
        // a.k.a ourselves, ignore it
        require(msg.sender == owner);
        btcMarketCap = cap;
    }

    function getBTCCap() constant public returns (uint) {
        return btcMarketCap;
    }
}
