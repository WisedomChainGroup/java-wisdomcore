const axios = require('axios');
const sendServiceAlert = require('./send-service-alert');
const schedule = require('node-schedule');
const program = require('commander');

let j;

const task = function(){
    axios.get("http://localhost:19585/getPoolAddress?address="+program.pool_address)
        .then((resp) => {
            if (resp.data.data.length === 0) {
                sendServiceAlert(program.access_key_id, program.access_key_secret, {
                    PhoneNumbers: program.phone_numbers,
                    SignName: program.sign_name,
                    TemplateCode: program.template_code,
                    Content: "Pool is empty"
                });
                j.cancel();
            }
        }).catch((error)=> {
        console.log(error);
        j.cancel();
    })
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
            j = schedule.scheduleJob('10 * * * * *', task);
        });
    program.parse(process.argv);
}
