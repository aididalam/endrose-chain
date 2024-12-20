// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract ProductLifecycle {
    struct Product {
        string name;
        uint256 expiryDate;
        address currentOwner;
        string details;
        address[] history;
    }

    mapping(uint256 => Product) public products; // Maps product ID to product details
    uint256 public productCounter;
    
    address public deadWallet = address(0xdead);

    event ProductMinted(uint256 productId, string name, uint256 expiryDate, address owner);
    event ProductTransferred(uint256 productId, address from, address to);
    event ProductConsumed(uint256 productId, address consumer);

    // Mint a new product (e.g., collecting milk and cocoa powder)
    function mintProduct(
        string memory _name,
        uint256 _expiryDate,
        string memory _details
    ) public {
        productCounter++;
        Product storage newProduct = products[productCounter];
        newProduct.name = _name;
        newProduct.expiryDate = _expiryDate;
        newProduct.currentOwner = msg.sender;
        newProduct.details = _details;
        newProduct.history.push(msg.sender);

        emit ProductMinted(productCounter, _name, _expiryDate, msg.sender);
    }

    // Transfer product ownership to the next stage
    function transferProduct(uint256 _productId, address _to) public {
        Product storage product = products[_productId];
        require(msg.sender == product.currentOwner, "Only the current owner can transfer the product.");
        require(_to != address(0), "Invalid recipient address.");

        address previousOwner = product.currentOwner;
        product.currentOwner = _to;
        product.history.push(_to);

        emit ProductTransferred(_productId, previousOwner, _to);
    }

    // Mark the product as consumed (transferred to dead wallet)
    function consumeProduct(uint256 _productId) public {
        Product storage product = products[_productId];
        require(msg.sender == product.currentOwner, "Only the current owner can consume the product.");

        address previousOwner = product.currentOwner;
        product.currentOwner = deadWallet;

        emit ProductConsumed(_productId, msg.sender);
    }

    // Get the history of the product
    function getProductHistory(uint256 _productId) public view returns (address[] memory) {
        return products[_productId].history;
    }

    // Get product details
    function getProductDetails(uint256 _productId)
        public
        view
        returns (
            string memory name,
            uint256 expiryDate,
            address currentOwner,
            string memory details,
            address[] memory history
        )
    {
        Product storage product = products[_productId];
        return (
            product.name,
            product.expiryDate,
            product.currentOwner,
            product.details,
            product.history
        );
    }
}
