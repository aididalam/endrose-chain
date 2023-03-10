const Block = require("..//Blockchain/Block");

describe("Block", () => {
  let data, lastBlock, block;
  beforeEach(() => {
    data = "bar";
    lastBlock = Block.genesis();
    block = Block.mineBlock(lastBlock, data);
  });

  it("Sets the `data` to match the input", () => {
    expect(block.data).toEqual(data);
  });

  it("Sets the `lastHash` to match the hash of the last block", () => {
    console.log(block);
    expect(block.lastHash).toEqual(lastBlock.hash);
  });
});
