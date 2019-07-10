const axios = require('axios')

// 1. interval --from 1 --to 2000 --rpc_host_port localhost:8888
// 2. proposals --from 1 --to 50 --rpc_host_port localhost:8888

module.exports = (hostport, from, to) => {
    const context = new Map()
    return axios.get(`http://${hostport}/block/${from}`)
        .then(resp => resp.data
        ).then((block) => {
            context['t0'] = block.nTime
            context['h0'] = block.nHeight
        })
        .then(() => {
            return axios.get(`http://${hostport}/block/${to}`)
        })
        .then(resp => resp.data)
        .then((block) => {
            context['t1'] = block.nTime
            context['h1'] = block.nHeight
            const interval = (context['t1'] - context['t0']) / (context['h1'] - context['h0'])
            console.log(`the block interval from height ${context['h1']} to ${context['h0']} is ${interval}`)
        })
}
