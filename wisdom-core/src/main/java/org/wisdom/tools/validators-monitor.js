const program = require('commander')
const axios = require('axios')
const RPCClient = require('@alicloud/pop-core').RPCClient;

program
    .option("--rpc_host_port <string>", "rpc host port")

program.parse(process.argv)


const params = {
    "RegionId": "cn-hangzhou",
    "PhoneNumbers": "15861828708",
    "SignName": "error",
    "TemplateCode": "error",
    "TemplateParam": "error",
    "OutId": "error",
    "SmsUpExtendCode": "error"
}


const validators = {
    '08f74cb61f41f692011a5e66e3c038969eb0ec75':'wisdom://1pQfDX4fvz7uzBQuM9FbuoKWohmhg9TmY@120.76.101.153:19585',
    '12acb24a3bbc5b9eaa32b6f8ae5e6c66c8c152aa':'wisdom://12hk3cWr28BJWjASCy9Diw4bqH8SnWvSpP@47.74.183.249:19585',
    '552f6d4390367de2b05f4c9fc345eeaaf0750db9':'wisdom://18mRFaYHguJyCWtAA9ZV1PZuGAb6UzAijE@47.74.216.251:19585',
    '5b0a4c7e31c3123db40a4c14200b54b8e358294b':'wisdom://19JNq2jAprkxVrpkgBiRaa1m47WcUMXtCb@47.96.67.155:19585',
    '15f581858068ed39f7e8cf8e9fdec5dfdae9cf15':'wisdom://1317J5fZb8kVrACnfi3PXN1T21573hYata@47.74.86.106:19585'
}

const validatorProposals = {}

axios
    .get(`http://${program.rpc_host_port}/block/-1`)
    .then(resp => resp.data.nHeight)
    .then(h => axios.get(`http://${program.rpc_host_port}/consensus/blocks?start=-1&stop=${h}&clipFromStop=true`))
    .then(resp => resp.data)
    .then(blocks => {
        for(let b of blocks){
            const key = b.body[0]['to']
            validatorProposals[key] || (validatorProposals[key] = 0)
            validatorProposals[key] ++
        }
        for(let v in validators){
            if(!validatorProposals[v]){
                console.error(validators[v] + " not proposal any block in previous 50 block")

            }else{
                console.log(`${validators[v]} has proposed ${validatorProposals[v]} blocks in previous 50 blocks`)
            }
        }
    })
    .catch(console.error)
