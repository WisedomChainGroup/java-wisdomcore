const axios = require('axios')
const program = require('commander')
const context = {}


program
    .option("--last_block_height <number>", "last block height", parseInt)
    .option("--blocks <number>", "number of blocks", parseInt)
    .option("--rpc_host_port <string>", "rpc url")
program.parse(process.argv)


axios.get(`http://${program.rpc_host_port}/block/${program['last_block_height']}`)
    .then(resp => resp.data
    ).then((block) => {
    context.t0 = block.nTime
    context.h0 = block.nHeight
    return context
})
.then((ctx) =>{
    return axios.get(`http://${program.rpc_host_port}/block/${ctx.h0 - parseInt(program.blocks)}`)
})
.then(resp => resp.data)
.then((block) => {
    context.t1 = block.nTime
    context.h1 = block.nHeight
    const interval = (context.t1 - context.t0)/(context.h1 - context.h0)
    console.log(`the block interval from height ${context.h1} to ${context.h0} is ${interval}`)
})
.catch(console.error)
