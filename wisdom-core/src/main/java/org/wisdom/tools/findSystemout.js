const glob = require("glob")
const fs = require('fs')

const files = glob.sync("../../../**/*.java", {})
const reg = /.*System.out.println.*/m

for(let f of files){
    let data = fs.readFileSync(f).toString()
    if(reg.test(data)){
        console.log(f)
    }
}