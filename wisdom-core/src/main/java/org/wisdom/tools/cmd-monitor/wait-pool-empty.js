const axios = require('axios');
const sendServiceAlert = require('./send-service-alert');
const schedule = require('node-schedule');
const program = require('commander');

let j;

const task = (id, secret, params) => {
    axios.get("http://localhost:19585/getPoolAddress?address="+params.PoolAddress)
        .then((resp) => {
            if (resp.data.data.length === 0) {
                sendServiceAlert(id, secret, {
                    PhoneNumbers: params.PhoneNumbers,
                    SignName: params.SignName,
                    TemplateCode: params.TemplateCode,
                    Content: "Pool is empty"
                });
                j.cancel();
            }
        }).catch(console.error)
};

if (require.main === module) {
    program
        .option('-i, --access_key_id <string>', 'access id')
        .option('-k, --access_key_secret <string>', 'access ket secret')
        .option('-c, --content <string>', 'sms content')
        .option('-p, --phone_numbers <string>', 'phone numbers')
        .option('-s, --sign_name <string>', 'signature')
        .option('-t, --template_code <string>', 'template code')
        .option('-a, --pool_address <string>','address')
        .action(opts => {
            j = schedule.scheduleJob('10 * * * * *', task(opts.access_key_id, opts.access_key_secret, {
                PhoneNumbers: opts.phone_numbers,
                SignName: opts.sign_name,
                TemplateCode: opts.template_code,
                PoolAddress: opts.pool_address
            }));
        });
    program.parse(process.argv);
}
