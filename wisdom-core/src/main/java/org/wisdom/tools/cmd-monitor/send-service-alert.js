#!/usr/bin/env node

const Core = require('@alicloud/pop-core');
const program = require('commander')

const sendServiceAlert = (id, secret, params) => {
    const client = new Core({
        accessKeyId: id,
        accessKeySecret: secret,
        endpoint: 'https://dysmsapi.aliyuncs.com',
        apiVersion: '2017-05-25'
    });

    const requestOption = {
        method: 'POST'
    };

    client.request('SendSms', {
        "RegionId": "cn-hangzhou",
        "PhoneNumbers": params.PhoneNumbers,
        "SignName": params.SignName,
        "TemplateCode": params.TemplateCode,
        "TemplateParam": `{"service": "${params.Content}"}`,
        "SmsUpExtendCode": "",
        "OutId": "",
    }, requestOption).then((result) => {
        console.log(JSON.stringify(result));
    }, (ex) => {
        console.log(ex);
    })

}

if (require.main === module) {
    program
        .option('-i, --access_key_id <string>', 'access id')
        .option('-k, --access_key_secret <string>', 'access ket secret')
        .option('-c, --content <string>', 'sms content')
        .option('-p, --phone_numbers <string>', 'phone numbers')
        .option('-s, --sign_name <string>', 'signature')
        .option('-t, --template_code <string>', 'template code')
        .action(opts => {
            sendServiceAlert(opts.access_key_id, opts.access_key_secret, {
                PhoneNumbers: opts.phone_numbers,
                SignName: opts.sign_name,
                TemplateCode: opts.template_code,
                Content: opts.content
            })
        })
    program.parse(process.argv)
}

module.exports = sendServiceAlert






