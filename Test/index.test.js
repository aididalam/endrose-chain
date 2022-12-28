const Blockchain = require("../Blockchain/index");
const Block = require("../Blockchain/Block");

describe("Blockchain", () => {
  let bc, bc2;

  beforeEach(() => {
    bc = new Blockchain();
    bc2 = new Blockchain();
  });

  it("Starts with genesis block", () => {
    expect(bc.chain[0]).toEqual(Block.genesis());
  });

  it("adds new block", () => {
    const data = "foo";
    bc.addBlock(data);
    expect(bc.chain[bc.chain.length - 1].data).toEqual(data);
  });

  it("Validate a valid chain", () => {
    bc2.addBlock("foo");

    expect(bc.isValidChain(bc2.chain)).toBe(true);
  });

  it("Invalidate a chain with corrupted data", () => {
    bc2.chain[0].data = "Bad data";

    expect(bc.isValidChain(bc2.chain)).toBe(false);
  });

  it("Invalidate a corrupt chain", () => {
    bc2.addBlock("foo");
    bc2.chain[1].data = "Not foo";

    expect(bc.isValidChain(bc2.chain)).toBe(false);
  });

  it("Replace the chain with a valid chain", () => {
    bc2.addBlock("goo");
    bc.replaceChain(bc2.chain);

    expect(bc.chain).toEqual(bc2.chain);
  });

  it("doesnot change the chain with one of less than or equal to length", () => {
    bc.addBlock("foo");
    bc.replaceChain(bc2.chain);

    expect(bc.chain).not.toEqual(bc2.chain);
  });
});
