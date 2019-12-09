#!/usr/bin/env node

/** 安装依赖
 * npm init
 * npm install axios -D
 * node status.js
 */


const axios = require('axios')

const SEEDS = [

    '120.76.101.153',
    '47.74.183.249',
    '47.74.216.251',
    '47.96.67.155',
    '47.74.86.106',
    '47.56.67.236'
]



Promise.all(
    SEEDS.map(h =>
        axios
            .get(`http://${h}:19585/block/-1`)
            .then(resp => resp.data)
            .then(data => console.log(
                `${h} \t height = ${data.nHeight} \t hash = ${data.blockHash} \t prev hash = ${data.hashPrevBlock}`
            ))
            .catch(e => console.error(`cannot connect to ${h}`))
        )
    )
    .then(() => {})
