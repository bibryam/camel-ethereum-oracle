var CMCOracle = artifacts.require("./Oracle.sol");

module.exports = function(deployer) {
    deployer.deploy(CMCOracle);
};