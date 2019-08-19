#!/usr/bin/env node

const program = require('commander')
const calcInterval = require('./calc-interval')
const countProposal = require('./count-proposals')
const sendServiceAlert = require('./send-service-alert')
const env = process.env
program.version('0.0.1')

program
    .command('interval')
    .description('calculate block interval')
    .option('-f, --from <number>', 'start block height', parseInt)
    .option('-t, --to <number>', 'end block height', parseInt)
    .option('-r, --rpc_host_port <string>', 'rpc host port')
    .action((opts) => {
        calcInterval(opts.rpc_host_port, opts.from, opts.to)
            .catch(console.error)
    })

program
    .command('count_proposals')
    .description('count proposals')
    .option('-f, --from <number>', 'start block height', parseInt)
    .option('-t, --to <number>', 'end block height', parseInt)
    .option('-r, --rpc_host_port <string>', 'rpc host port')
    .action((opts) => {
        countProposal(opts['rpc_host_port'], opts['from'], opts['to'])
            .catch(console.error)
    })

program.command('alert_offline')
    .description('send sms when error node found')
    .option('-r, --rpc_host_port <string>', 'rpc host port', env.RPC_HOST_PORT)
    .option('-i, --access_key_id <string>', 'sms access key id', env.ACCESS_KEY_ID)
    .option('-k, --access_key_secret <string>', 'access secret key', env.ACCESS_KEY_SECRET)
    .option('-p, --phone_numbers <string>', 'phone numbers', env.PHONE_NUMBERS)
    .option('-s, --sign_name <string>', 'signature', env.SIGN_NAME)
    .option('-t, --template_code <string>', 'template code', env.TEMPLATE_CODE)
    .action(opts => {
        countProposal(opts.rpc_host_port, 1, -1)
            .then(errors => {
                if (errors && errors.length) {
                    sendServiceAlert(opts['access_key_id'], opts['access_key_secret'], {
                        PhoneNumbers: opts.phone_numbers,
                        SignName: opts.sign_name,
                        TemplateCode: opts.template_code,
                        Content: "blockchain"
                    })
                }
            })
            .catch(console.error)
    })

program.parse(process.argv)
