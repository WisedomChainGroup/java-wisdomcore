const axios = require('axios')

module.exports = (hostport, from, to) => {
    const validators = {
        '08f74cb61f41f692011a5e66e3c038969eb0ec75': 'wisdom://1pQfDX4fvz7uzBQuM9FbuoKWohmhg9TmY@120.76.101.153:19585',
        '12acb24a3bbc5b9eaa32b6f8ae5e6c66c8c152aa': 'wisdom://12hk3cWr28BJWjASCy9Diw4bqH8SnWvSpP@47.74.183.249:19585',
        '552f6d4390367de2b05f4c9fc345eeaaf0750db9': 'wisdom://18mRFaYHguJyCWtAA9ZV1PZuGAb6UzAijE@47.74.216.251:19585',
        '5b0a4c7e31c3123db40a4c14200b54b8e358294b': 'wisdom://19JNq2jAprkxVrpkgBiRaa1m47WcUMXtCb@47.96.67.155:19585',
        '15f581858068ed39f7e8cf8e9fdec5dfdae9cf15': 'wisdom://1317J5fZb8kVrACnfi3PXN1T21573hYata@47.74.86.106:19585'
    }

    const validatorProposals = new Map()

    return axios
        .get(`http://${hostport}/consensus/status`)
        .then(resp => resp.data.currentHeight)
        .then(h => {
            if (to < 0) {
                to = h
            }
            return axios.get(`http://${hostport}/consensus/blocks?start=${from}&stop=${to}&clipFromStop=true`)
        }
        )
        .then(resp => resp.data)
        .then(blocks => {
            const errors = []
            for (let b of blocks) {
                const key = b.body[0]['to']
                validatorProposals[key] || (validatorProposals[key] = 0)
                validatorProposals[key]++
            }
            for (let v in validators) {
                if (!validatorProposals[v]) {
                    const err = validators[v] + " not proposal any block in previous 50 block"
                    errors.push(err)
                    console.error(errors);
                } else {
                    console.log(`${validators[v]} has proposed ${validatorProposals[v]} blocks from ${blocks[0].nHeight} to ${blocks[blocks.length - 1].nHeight}`)
                }
            }
            return errors
        })
}
