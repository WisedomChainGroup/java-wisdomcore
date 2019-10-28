const baseX = require('./base-x');

class base58
{
    constructor() {
        this.BASE58 = '123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz';
        this.bs58 = baseX(this.BASE58);
    }

    decode(code) {
        return this.bs58.decode(code);
    }

    encode(code) {
        return this.bs58.encode(code);
    }
}

module.exports = base58;