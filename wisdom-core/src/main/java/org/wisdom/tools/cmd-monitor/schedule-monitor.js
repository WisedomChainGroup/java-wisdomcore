const countProposal = require('./count-proposals')
const sendServiceAlert = require('./send-service-alert')
const schedule = require('node-schedule')
const task = () => {
    countProposal(process.env.RPC_HOST_PORT, 1, -1)
    .then(errors => {
        if(errors && errors.length){
            sendServiceAlert(process.env.ACCESS_KEY_ID, process.env.ACCESS_KEY_SECRET, {
                PhoneNumbers: process.env.PHONE_NUMBERS,
                SignName: process.env.SIGN_NAME,
                TemplateCode: process.env.TEMPLATE_CODE,
                Content: "blockchain"
            })
        }
    })
    .catch(console.error)
}


schedule.scheduleJob(process.env.cron, task);

