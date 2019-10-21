#!/usr/bin/env node

const axios = require('axios')

SEEDS = [
    '120.76.101.153',
    '47.74.183.249',
    '47.74.216.251',
    '47.96.67.155',
    '47.74.86.106',
    '47.56.67.236'
]

const asyncs = []

for (let h of SEEDS) {
    asyncs.push(
        axios.get(`http://${h}:19585/block/-1`)
            .then(resp => resp.data)
            .then(data => console.log(
                `${h} \t height = ${data.nHeight} \t hash = ${data.blockHash}`
            ))
    )
}

Promise.all(asyncs)
    .then(() => console.log('success'))
    .catch(console.error)