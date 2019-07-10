#!/usr/bin/env node

const countProposal = require('./count-proposals')
const sendServiceAlert = require('./send-service-alert')
const schedule = require('node-schedule')
const env = process.env
const task = () => {
    countProposal(env.RPC_HOST_PORT, 1, -1)
        .then(errors => {
            if (errors && errors.length) {
                sendServiceAlert(env.ACCESS_KEY_ID, env.ACCESS_KEY_SECRET, {
                    PhoneNumbers: env.PHONE_NUMBERS,
                    SignName: env.SIGN_NAME,
                    TemplateCode: env.TEMPLATE_CODE,
                    Content: "blockchain"
                })
            }
        })
        .catch(console.error)
}


schedule.scheduleJob(env.CRON, task);

